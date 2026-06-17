import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;

public class FineManagementPanel extends JInternalFrame implements ActionListener {

    private JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    
    // CRITICAL FIX 1: Explicitly initialize the model container structure once
    private String[] columns = {"Patron ID", "Full Name", "Books Borrowed / Late Items", "Outstanding Fine"};
    private DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
    
    private JTable table = new JTable(tableModel) { // Pass the single model reference straight into the constructor
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Prevent direct inline editing inside the grid cells
        }
    };

    private JButton paidBtn = new JButton("Clear Fine / Settlement");
    private JButton refreshBtn = new JButton("Refresh List");

    public FineManagementPanel() {
        super("Outstanding Fines & Late Return Monitoring Desk", true, true, true, true);
        this.setSize(SystemConstants.FRAME_WIDTH - 100, SystemConstants.FRAME_HEIGHT - 100);
        this.setLayout(new BorderLayout());

        // 1. Build and map active outstanding fine balances and late return logs
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        refreshLateReturnTable(); // Populate data safely

        // 2. Button Action Wire Configurations
        paidBtn.addActionListener(this);
        refreshBtn.addActionListener(this);

        // Styling the Settlement button with a clean success theme
        paidBtn.setBackground(new Color(46, 204, 113));
        paidBtn.setForeground(Color.WHITE);

        topPanel.add(paidBtn);
        topPanel.add(refreshBtn);
        this.add(topPanel, BorderLayout.NORTH);

        // 3. Setup Scrollable Viewport grid area
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        this.setVisible(true);
    }

    /**
     * Finds Patrons inside LibraryDatabase who have an outstanding balance penalty fee
     * Maps out their currently active loan items dynamically.
     */
    private void refreshLateReturnTable() {
        tableModel.setRowCount(0);

        for (LibraryUser user : LibraryDatabase.getAllPatrons()) {
            if (user instanceof Patron) {
                Patron p = (Patron) user;
                double outstandingFine = p.getFine(); 

                // Show only patrons who have active outstanding penalties to clear
                if (outstandingFine > 0) {
                    
                    StringBuilder booksString = new StringBuilder();
                    
                    if (p.getActiveLoans().isEmpty()) {
                        booksString.append("[ No active logs / Pending return confirmation ]");
                    } else {
                        for (int i = 0; i < p.getActiveLoans().size(); i++) {
                            Loan loan = p.getActiveLoans().get(i);
                            if (loan != null && loan.getBook() != null) {
                                Book b = loan.getBook();
                                booksString.append(b.getTitle());
                                if (i < p.getActiveLoans().size() - 1) {
                                    booksString.append(" | "); 
                                }
                            }
                        }
                    }

                    // Directly add rows to our static table model reference link
                    tableModel.addRow(new Object[]{
                        p.getId(),
                        p.getName(),
                        booksString.toString(), 
                        "RM " + String.format("%.2f", outstandingFine)
                    });
                }
            }
        }
    }

    // Button action
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == paidBtn) {
            processFinePayment();
        } else if (source == refreshBtn) {
            refreshLateReturnTable();
        }
    }

    /**
     * Clears out fine obligations for selected account records
     */
    private void processFinePayment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please choose a record from the late return listings table first.", 
                    "No Patron Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Safety layer: Convert the view row index to model row index to prevent sorting mismatches
        int modelRow = table.convertRowIndexToModel(selectedRow);

        String patronId = tableModel.getValueAt(modelRow, 0).toString();
        String patronName = tableModel.getValueAt(modelRow, 1).toString();
        String fineAmountText = tableModel.getValueAt(modelRow, 3).toString();

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Confirm payment settlement transaction for " + patronName + " (" + fineAmountText + ")?", 
                "Settle Fine Transaction Account", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            LibraryUser user = LibraryDatabase.searchUserById(patronId);
            if (user instanceof Patron) {
                Patron p = (Patron) user;
                
                // Reset fine tracking back to 0 inside memory database
                p.setFine(0.00); 

                JOptionPane.showMessageDialog(this, "Payment registered successfully! Outstanding fine balanced down to RM 0.00.");
                
                refreshLateReturnTable(); 
            } else {
                JOptionPane.showMessageDialog(this, "Error locating patron data reference node.", "Execution Fault", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}