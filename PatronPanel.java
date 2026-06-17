import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class PatronPanel extends JPanel implements ActionListener {
    // Structural panel container to cleanly swap sub-panels
    private static JPanel centerContentContainer = new JPanel(new BorderLayout());
    private JMenuBar menuBar = new JMenuBar();
    
    // Navigation Menu Items
    private JMenuItem viewBorrowedMenu = new JMenuItem("View Borrowed Books & Fines");
    private JMenuItem browseCatalogMenu = new JMenuItem("Browse & Hold Catalog");
    private JMenuItem logoutMenu = new JMenuItem("Logout");
    
    private Patron currentPatron;
    
    public PatronPanel() {
        // Set layout FIRST before adding components
        this.setLayout(new BorderLayout());
        this.setBounds(0, 0, SystemConstants.FRAME_WIDTH, SystemConstants.FRAME_HEIGHT);
        try {
            if (MainFrame.user instanceof Patron) {
                this.currentPatron = (Patron) MainFrame.user;
            }
        } catch (Exception e) {
            System.err.println("Error mapping authenticated user: " + e.getMessage());
        }
        
        // Configure internal center container view panel
        this.add(centerContentContainer, BorderLayout.CENTER);
        
        // Add items to menu bar & register listeners
        menuBar.add(viewBorrowedMenu);
        menuBar.add(browseCatalogMenu);
        menuBar.add(logoutMenu);
        
        viewBorrowedMenu.addActionListener(this);
        browseCatalogMenu.addActionListener(this);
        logoutMenu.addActionListener(this);
        
        // Configure and add menu bar
        this.add(menuBar, BorderLayout.NORTH);

        // Auto-load catalog workspace panel on dashboard launch
        showCatalogPanel();
    }
    
    // Changes structural view panels
    public static void setContent(JPanel structuralPanel) {
        centerContentContainer.removeAll();
        centerContentContainer.add(structuralPanel, BorderLayout.CENTER);
        centerContentContainer.revalidate();
        centerContentContainer.repaint();
    } 
    
    // Menu action
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == viewBorrowedMenu) {
            showBorrowedAndFinesPanel();
        } else if (source == browseCatalogMenu) {
            showCatalogPanel();
        } else if (source == logoutMenu) {
            logout();
        }
    }

    // =========================================================================
    // PANEL 1: BORROWED BOOKS, ITEMIZED FINES & TOTAL OVERVIEW
    // =========================================================================
    private void showBorrowedAndFinesPanel() {
        JPanel workspace = new JPanel(new BorderLayout(10, 10));
        workspace.setBorder(BorderFactory.createTitledBorder("Active Loans & Fine Statements"));

        // Setup itemized data columns 
        String[] columns = {"Loan ID", "Book Title", "Checkout Date", "Due Date", "Accumulated Fine"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        double calculatedSumTotal = 0.0;

        // Populate table records line-by-line 
        try {
            if (currentPatron != null) {
                // If the librarian set the fine to 0 explicitly, 
                // do not recalculate and overwrite it with old overdue data values
                double currentProfileFine = currentPatron.getFine();

                for (Loan loan : currentPatron.getActiveLoans()) {
                    if (loan != null) {
                        double loanFine = 0.0;

                        // Only calculate runtime overdue fines if the overall balance hasn't been manually cleared/settled
                        if (currentProfileFine > 0) {
                            loanFine = loan.calculateOverdueFine();
                        }

                        calculatedSumTotal = calculatedSumTotal + loanFine;

                        model.addRow(new Object[]{
                            loan.getLoanId(),
                            loan.getBook() != null ? loan.getBook().getTitle() : "Unknown Asset",
                            loan.getCheckoutDate() != null ? loan.getCheckoutDate().toString() : "N/A",
                            loan.getDueDate() != null ? loan.getDueDate().toString() : "N/A",
                            "RM " + String.format("%.2f", loanFine)
                        });
                    }
                }

                // Sync overall system profile data variables safely
                currentPatron.setFine(calculatedSumTotal);
            }
        } catch (NullPointerException npe) {
            System.err.println("Null sub-node properties encountered during loop processing: " + npe.getMessage());
        } catch (Exception ex) {
            System.err.println("Unexpected execution fault inside Loan mapping track compilation: " + ex.getMessage());
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        workspace.add(scrollPane, BorderLayout.CENTER);

        // Footer Summary Panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        summaryPanel.setBackground(new Color(245, 247, 250));
        JLabel totalLabel = new JLabel("Total Balance Fine Due: RM " + String.format("%.2f", calculatedSumTotal));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        if (calculatedSumTotal > 0.0) {
            totalLabel.setForeground(new Color(231, 76, 60)); // Crimson alerting text highlight
        } else {
            totalLabel.setForeground(new Color(46, 204, 113)); // Clean stable emerald theme green
        }
        summaryPanel.add(totalLabel);
        workspace.add(summaryPanel, BorderLayout.SOUTH);

        setContent(workspace);
    }
    
    // =========================================================================
    // PANEL 2: VIEW-ONLY BOOK CATALOG, BOOK RESERVATION
    // =========================================================================
    private void showCatalogPanel() {
        // Initialize book repository browsing view interface grid components
        final JPanel workspace = new JPanel(new BorderLayout(10, 10));
        workspace.setBorder(BorderFactory.createTitledBorder("Library Main Catalog Window"));

        String[] columns = {"Book ID", "Title", "Author", "Genre", "Available Stock"};
        final DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { 
                return false; 
            }
        };

        // Pull live rows straight out from database reference nodes
        try {
            for (Book b : LibraryDatabase.getAllBooks()) {
                if (b != null) {
                    model.addRow(new Object[]{
                        b.getItemId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getGenre(),
                        b.getAmount()
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to display book catalog: " + e.getMessage());
        }

        final JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        workspace.add(scrollPane, BorderLayout.CENTER);

        // Set up button controls to proces of HOLD
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton holdBtn = new JButton("HOLD");
        holdBtn.setBackground(new Color(52, 152, 219));
        holdBtn.setForeground(Color.WHITE);

        
        // Button action
        holdBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Verify table index selection constraints before filing system hold transactions
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(workspace, "Please select a catalog item from table.",
                            "Selection Missing", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Commit reservation to backend node
                // Present formal confirmation constraints notice
                try {
                    // Convert selected row view index back safely to model reference indexes
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    String bookId = model.getValueAt(modelRow, 0).toString();
                    Book selectedBook = LibraryDatabase.searchBookById(bookId);

                    // Commit reservation to backend node
                    if (selectedBook != null && currentPatron != null) {
                        LibraryDatabase.addHoldToBook(selectedBook.getItemId(), currentPatron.getId());

                        // --- REQ POPUP REMINDER ALERTS ---
                        JOptionPane.showMessageDialog(workspace,
                                "Hold Confirmation successfully for: \"" + selectedBook.getTitle() + "\"\n\n"
                                + "IMPORTANT TIMELINE POLICY REMINDER:\n"
                                + "This asset reservation is locked for exactly 7 days (1 week).\n"
                                + "If you do not physically check out this item at the circulation counter \n"
                                + "within the week, the hold will automatically expire and be released back to the general public catalog.",
                                "Hold Book", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(workspace, "Operation unsuccessful.",
                                "System Execution Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IndexOutOfBoundsException ioobe) {
                    JOptionPane.showMessageDialog(workspace, "Selected index array is unvalid.",
                            "Parsing Execution Error", JOptionPane.ERROR_MESSAGE);
                } catch (NullPointerException npe) {
                    JOptionPane.showMessageDialog(workspace, "Null Pointer Exception.",
                            "Database Pointer Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(workspace, "An unexpected error occurred: " + ex.getMessage(),
                            "System Exception Node", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        actionPanel.add(holdBtn);
        workspace.add(actionPanel, BorderLayout.SOUTH);

        setContent(workspace);
    }

    private void logout() {
        MainFrame.user = null;
        MainFrame.setContent(
            new LoginPanel(), 
            SystemConstants.LOGIN_WIDTH, 
            SystemConstants.LOGIN_HEIGHT
        );
    }
}