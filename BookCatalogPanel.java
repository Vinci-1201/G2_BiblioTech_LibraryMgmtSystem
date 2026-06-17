import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.io.*;
import java.util.ArrayList;

public class BookCatalogPanel extends JInternalFrame implements ActionListener {
    // Primary structural panel layouts and underlying grid models
    private JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    private DefaultTableModel tableModel;
    
    private JTable table = new JTable() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; 
        }
    };

    // Buttons for CRUD
    private JButton addBtn = new JButton("Add Book");
    private JButton updateBtn = new JButton("Update Selected");
    private JButton deleteBtn = new JButton("Delete Book");
    private JButton importCsvBtn = new JButton("Import CSV");
    private JButton viewHoldsBtn = new JButton("View Hold Queue");

    private JTextField searchField = new JTextField(15);
    private JButton searchBtn = new JButton("Search Title");
    private JButton resetBtn = new JButton("Reset");

    public BookCatalogPanel() {
        // Construct standard layout hierarchy definitions for internal frame
        super("Book Catalog Management", true, true, true, true);
        this.setSize(SystemConstants.FRAME_WIDTH, SystemConstants.FRAME_HEIGHT);
        this.setLayout(new BorderLayout());

        refreshCatalogTable(LibraryDatabase.getAllBooks());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Map interaction tracking action listeners
        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        importCsvBtn.addActionListener(this); 
        viewHoldsBtn.addActionListener(this);
        searchBtn.addActionListener(this);
        resetBtn.addActionListener(this);

        importCsvBtn.setBackground(new Color(52, 152, 219));
        importCsvBtn.setForeground(Color.WHITE);
        viewHoldsBtn.setBackground(new Color(155, 89, 182)); 
        viewHoldsBtn.setForeground(Color.WHITE);

        topPanel.add(addBtn);
        topPanel.add(updateBtn);
        topPanel.add(importCsvBtn); 
        topPanel.add(viewHoldsBtn);
        
        topPanel.add(new JSeparator(JSeparator.VERTICAL));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(resetBtn);

        // Only show the DELETE button when system admin logged in
        if (MainFrame.user instanceof SystemAdmin) {
            deleteBtn.addActionListener(this);
            deleteBtn.setBackground(new Color(231, 76, 60));
            deleteBtn.setForeground(Color.WHITE);
            topPanel.add(deleteBtn);
        } else {
            deleteBtn.addActionListener(this);
            deleteBtn.setBackground(new Color(231, 76, 60));
            deleteBtn.setForeground(Color.WHITE);
            topPanel.add(deleteBtn);
            deleteBtn.setVisible(MainFrame.user instanceof SystemAdmin);
        }

        this.add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        this.setVisible(true);
    }

    // Display books in table
    private void refreshCatalogTable(ArrayList<Book> bookSourceList) {
        String[] columns = {"Item ID", "Title", "Author", "Genre", "ISBN", "Publisher", "Year", "Price", "Available Stock"};
        tableModel = new DefaultTableModel(columns, 0);

        for (Book b : bookSourceList) {
            tableModel.addRow(new Object[]{
                b.getItemId(),
                b.getTitle(),
                b.getAuthor(),
                b.getGenre(), 
                b.getIsbn(),
                b.getPublisher(),
                b.getPublishedYear(),
                "RM " + String.format("%.2f", b.getPrice()),
                b.getAmount() + " units"
            });
        }
        table.setModel(tableModel);
    }

    // Button action
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == addBtn) {
            spawnBookWizard(null); 
        } 
        else if (source == updateBtn) {
            prepareSelectedUpdate();
        } 
        else if (source == importCsvBtn) {
            handleCsvImport();
        }
        else if (source == viewHoldsBtn) {
            displayBookHoldQueue();
        }
        else if (source == deleteBtn) {
            if (MainFrame.user instanceof SystemAdmin) {
                processInventoryDeletion();
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied: Administrative Clearance Required.", 
                        "Security Fault", JOptionPane.ERROR_MESSAGE);
            }
        } 
        else if (source == searchBtn) {
            executeCatalogSearch();
        } 
        else if (source == resetBtn) {
            searchField.setText("");
            refreshCatalogTable(LibraryDatabase.getAllBooks()); 
        }
    }
    
    // Display the waiting list for a book
    private void displayBookHoldQueue() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the catalog grid to review waitlists.",
                    "Selection Pointer Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String bookId = tableModel.getValueAt(modelRow, 0).toString();
        String bookTitle = tableModel.getValueAt(modelRow, 1).toString();

        ArrayList<String> waitingPatronIds = LibraryDatabase.getHoldQueuePatronIds(bookId);

        JPanel holdDialogPanel = new JPanel(new BorderLayout(10, 10));
        holdDialogPanel.setPreferredSize(new Dimension(500, 300));

        JLabel summaryLabel = new JLabel("Total Active Holds: " + waitingPatronIds.size());
        summaryLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        holdDialogPanel.add(summaryLabel, BorderLayout.NORTH);

        String[] queueColumns = {"Queue Position", "Patron ID", "Full Name"};
        DefaultTableModel queueTableModel = new DefaultTableModel(queueColumns, 0);
        
        JTable queueTable = new JTable(queueTableModel) {
            @Override
            public boolean isCellEditable(int r, int c) { 
                return false; 
            }
        };

        if (waitingPatronIds.isEmpty()) {
            queueTableModel.addRow(new Object[]{"-", "No active holds detected", "-"});
        } else {
            int position = 1;
            for (String patronId : waitingPatronIds) {
                LibraryUser user = LibraryDatabase.searchUserById(patronId);
                if (user != null) {
                    queueTableModel.addRow(new Object[]{
                        "#" + position,
                        user.getId(),
                        user.getName()
                    });
                } else {
                    queueTableModel.addRow(new Object[]{"#" + position, patronId, "Unknown User Instance"});
                }
                position = position + 1;
            }
        }

        holdDialogPanel.add(new JScrollPane(queueTable), BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, holdDialogPanel,
                "Hold Waitlist: " + bookTitle, JOptionPane.PLAIN_MESSAGE);
    }

    private void handleCsvImport() {
        // Launch file processing interfaces and configure extension match filters
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Book Catalog CSV File to Import");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Comma Delimited Data Sheet (*.csv)", "csv"));
        
        int userChoice = fileChooser.showOpenDialog(this);
        if (userChoice != JFileChooser.APPROVE_OPTION) {
            return; 
        }

        File selectedFile = fileChooser.getSelectedFile();
        int recordsImported = 0;
        int duplicateSkips = 0;
        int formattingErrors = 0;

        // Drive transactional processing loops across streaming row records
        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            String rowLine;
            boolean isFirstRowHeader = true;

            while ((rowLine = reader.readLine()) != null) {
                if (rowLine.trim().isEmpty()) {
                    continue; 
                }

                String[] rawTokens = rowLine.split(",");

                if (isFirstRowHeader) {
                    isFirstRowHeader = false;
                    if (rawTokens[0].equalsIgnoreCase("Item ID") || rawTokens[0].equalsIgnoreCase("id")) {
                        continue; 
                    }
                }

                if (rawTokens.length < 9) {
                    formattingErrors = formattingErrors + 1;
                    continue;
                }

                // Reading the data from CSV file
                try {
                    String id = rawTokens[0].trim();
                    String title = rawTokens[1].trim();
                    String author = rawTokens[2].trim();
                    String isbn = rawTokens[3].trim();
                    String publisher = rawTokens[4].trim();
                    int year = Integer.parseInt(rawTokens[5].trim());
                    double price = Double.parseDouble(rawTokens[6].trim());
                    int stock = Integer.parseInt(rawTokens[7].trim());
                    String genre = rawTokens[8].trim();

                    if (id.isEmpty() || title.isEmpty() || author.isEmpty() || genre.isEmpty()) {
                        formattingErrors = formattingErrors + 1;
                        continue;
                    }
                    
                    // Create Book object for each line of data
                    Book batchBook = new Book(id, title, author, isbn, publisher, year, price, stock, genre);
                    boolean registrationStatus = LibraryDatabase.addBook(batchBook);
                    
                    if (registrationStatus) {
                        recordsImported = recordsImported + 1;
                    } else {
                        duplicateSkips = duplicateSkips + 1; 
                    }

                } 
                // Corrupted records
                catch (NumberFormatException nfe) {
                    formattingErrors = formattingErrors + 1; 
                }
            }

            refreshCatalogTable(LibraryDatabase.getAllBooks());

            String analyticsReport = String.format(
                "CSV File Import Completed!\n\n" +
                "Books Registered Successfully: %d items\n" +
                "Skipped (ID Already Exists in Database): %d entries\n" +
                "Corrupted Records Dropped: %d rows",
                recordsImported, duplicateSkips, formattingErrors
            );
            
            JOptionPane.showMessageDialog(this, analyticsReport, "Import Action", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception err) {
            JOptionPane.showMessageDialog(this, "Error occured while reading the file: " + err.getMessage(), 
                    "File IO Crash Warning", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to search a book in database
    private void executeCatalogSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshCatalogTable(LibraryDatabase.getAllBooks());
            return;
        }

        ArrayList<Book> searchResults = LibraryDatabase.searchBooksByTitle(keyword);
        refreshCatalogTable(searchResults);
    }

    // Method to update a book
    private void prepareSelectedUpdate() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table.", 
                    "Selection Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String itemId = tableModel.getValueAt(modelRow, 0).toString();
        Book targetBook = LibraryDatabase.searchBookById(itemId);
        
        if (targetBook != null) {
            spawnBookWizard(targetBook);
        }
    }

    // Method to initialize a form to update a book
    private void spawnBookWizard(Book selectedBook) {
        boolean isUpdateMode = (selectedBook != null);
        
        // Textfield to retrieve input
        JTextField idTx = new JTextField(isUpdateMode ? selectedBook.getItemId() : "");
        JTextField titleTx = new JTextField(isUpdateMode ? selectedBook.getTitle() : "");
        JTextField authorTx = new JTextField(isUpdateMode ? selectedBook.getAuthor() : "");
        JTextField genreTx = new JTextField(isUpdateMode ? selectedBook.getGenre() : "General"); 
        JTextField isbnTx = new JTextField(isUpdateMode ? selectedBook.getIsbn() : "");
        JTextField pubTx = new JTextField(isUpdateMode ? selectedBook.getPublisher() : "");
        JTextField yearTx = new JTextField(isUpdateMode ? String.valueOf(selectedBook.getPublishedYear()) : "");
        JTextField priceTx = new JTextField(isUpdateMode ? String.valueOf(selectedBook.getPrice()) : "");
        JTextField stockTx = new JTextField(isUpdateMode ? String.valueOf(selectedBook.getAmount()) : "");

        if (isUpdateMode) {
            idTx.setEditable(false);
            idTx.setBackground(Color.LIGHT_GRAY);
        }

        Object[] interfaceFields = {
            "Book ID:", idTx,
            "Book Title:", titleTx,
            "Primary Author Name:", authorTx,
            "Book Genre / Classification:", genreTx, 
            "International ISBN Code:", isbnTx,
            "Publisher Name:", pubTx,
            "Published Year:", yearTx,
            "Price (RM):", priceTx,
            "In-Stock Inventory Amount:", stockTx
        };

        int option = JOptionPane.showConfirmDialog(this, interfaceFields, 
                isUpdateMode ? "Modify Catalog Item Entry" : "Register New Catalog Title Assets", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String id = idTx.getText().trim();
                String title = titleTx.getText().trim();
                String author = authorTx.getText().trim();
                String genre = genreTx.getText().trim(); 
                String isbn = isbnTx.getText().trim();
                String publisher = pubTx.getText().trim();
                int year = Integer.parseInt(yearTx.getText().trim());
                double price = Double.parseDouble(priceTx.getText().trim());
                int stock = Integer.parseInt(stockTx.getText().trim());

                if (id.isEmpty() || title.isEmpty() || author.isEmpty() || isbn.isEmpty() || genre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Core descriptive string fields cannot remain unpopulated.", 
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Process transaction types based on form initialization profiles
                if (isUpdateMode) {
                    selectedBook.setTitle(title);
                    selectedBook.setAuthor(author);
                    selectedBook.setGenre(genre); 
                    selectedBook.setIsbn(isbn);
                    selectedBook.setPublisher(publisher);
                    selectedBook.setPublishedYear(year);
                    selectedBook.setPrice(price);
                    selectedBook.setAmount(stock);
                    JOptionPane.showMessageDialog(this, "Catalog record updated successfully.");
                } else {
                    Book conceptualBookObject = new Book(id, title, author, isbn, publisher, year, price, stock, genre);
                    boolean created = LibraryDatabase.addBook(conceptualBookObject);
                    if (created) {
                        JOptionPane.showMessageDialog(this, "New book title registered into database.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Registration failed: Item ID '" + id + "' already exists.", 
                                "Primary Key Collision", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                refreshCatalogTable(LibraryDatabase.getAllBooks()); 

            } catch (NumberFormatException error) {
                JOptionPane.showMessageDialog(this, "Input processing rejected: Ensure year, price, and units are valid numbers.", 
                        "Data Parsing Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Delete method for delete button
    // Only for SYSTEM ADMIN
    private void processInventoryDeletion() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a catalog row to drop from index database first.", 
                    "No Active Row Pointer", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String targetId = tableModel.getValueAt(modelRow, 0).toString();
        String targetTitle = tableModel.getValueAt(modelRow, 1).toString();

        int userConfirmationResponse = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to permanently erase book entry registration for:\n" + targetTitle + "?", 
                "Confirm Inventory Drop Request", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (userConfirmationResponse == JOptionPane.YES_OPTION) {
            boolean dropped = LibraryDatabase.deleteBookById(targetId);
            if (dropped) {
                JOptionPane.showMessageDialog(this, "Catalog asset references removed.");
                refreshCatalogTable(LibraryDatabase.getAllBooks());
            } else {
                JOptionPane.showMessageDialog(this, "Operation delete failed.", 
                        "Internal System Trace Bug", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Method to get read-only data
    private void executeReadOnlyLoadData(DefaultTableModel readOnlyModel, JTextField patronSearchField) {
        // Query baseline collections to populate specialized read-only view layers
        readOnlyModel.setRowCount(0);
        String kw = patronSearchField.getText().trim();
        
        ArrayList<Book> list;
        if (kw.isEmpty()) {
            list = LibraryDatabase.getAllBooks();
        } else {
            list = LibraryDatabase.searchBooksByTitle(kw);
        }

        for (Book b : list) {
            readOnlyModel.addRow(new Object[]{
                b.getItemId(), b.getTitle(), b.getAuthor(), b.getGenre(), b.getIsbn(),
                b.getPublisher(), b.getPublishedYear(),
                "RM " + String.format("%.2f", b.getPrice()),
                b.getAmount() + " units"
            });
        }
    }
    
    // Method to display book data
    public JPanel getReadOnlyCatalogView() {
        final JPanel container = new JPanel(new BorderLayout());

        JPanel patronSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        final JTextField patronSearchField = new JTextField(15);
        JButton patronSearchBtn = new JButton("Search Title");
        JButton patronResetBtn = new JButton("Reset");

        patronSearchPanel.add(new JLabel("Search Catalog:"));
        patronSearchPanel.add(patronSearchField);
        patronSearchPanel.add(patronSearchBtn);
        patronSearchPanel.add(patronResetBtn);

        final DefaultTableModel readOnlyModel = new DefaultTableModel(
                new String[]{"Item ID", "Title", "Author", "Genre", "ISBN", "Publisher", "Year", "Price", "Available Stock"}, 0
        );
        final JTable readOnlyTable = new JTable(readOnlyModel) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };
        readOnlyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        patronSearchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                executeReadOnlyLoadData(readOnlyModel, patronSearchField);
            }
        });

        patronResetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                patronSearchField.setText("");
                executeReadOnlyLoadData(readOnlyModel, patronSearchField);
            }
        });

        executeReadOnlyLoadData(readOnlyModel, patronSearchField);

        container.add(patronSearchPanel, BorderLayout.NORTH);
        container.add(new JScrollPane(readOnlyTable), BorderLayout.CENTER);

        JPanel actionButtonStrip = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton placeHoldBtn = new JButton("Place Hold on Selected Book");
        placeHoldBtn.setBackground(new Color(52, 152, 219)); 
        placeHoldBtn.setForeground(Color.WHITE);
        placeHoldBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        placeHoldBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selectedRow = readOnlyTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(container, "Please select a book from the catalog view first to request a hold.", 
                            "Selection Missing", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int modelRow = readOnlyTable.convertRowIndexToModel(selectedRow);
                String selectedBookId = readOnlyModel.getValueAt(modelRow, 0).toString();
                String selectedBookTitle = readOnlyModel.getValueAt(modelRow, 1).toString();
                
                if (MainFrame.user instanceof Patron) {
                    Patron currentPatron = (Patron) MainFrame.user;
                    boolean holdRegistered = LibraryDatabase.addHoldToBook(selectedBookId, currentPatron.getId());
                    
                    if (holdRegistered) {
                        JOptionPane.showMessageDialog(container, "Hold successfully for: \n" + selectedBookTitle, 
                                "Hold Request Successful", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(container, "Transaction Rejected: You are already active inside the waitlist queue for this item.", 
                                "Duplicate Request", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(container, "Access Error: Current runtime authorization scope restricted to Patrons only.", 
                            "Privilege Exception", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        actionButtonStrip.add(placeHoldBtn);
        container.add(actionButtonStrip, BorderLayout.SOUTH);

        return container;
    }
}