import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.util.ArrayList;

public class LibrarianManagementPanel extends JInternalFrame implements ActionListener {

    private JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    private DefaultTableModel tableModel;
    
    private JTable table = new JTable() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; 
        }
    };

    private JButton addBtn = new JButton("Add Librarian");
    private JButton updateBtn = new JButton("Update Selected");
    private JButton deleteBtn = new JButton("Delete Selected");

    private JTextField searchField = new JTextField(15);
    private JButton searchBtn = new JButton("Search Name");
    private JButton resetBtn = new JButton("Reset");

    public LibrarianManagementPanel() {
        super("Manage Librarian", true, true, true, true);
        this.setSize(SystemConstants.FRAME_WIDTH, SystemConstants.FRAME_HEIGHT);
        this.setLayout(new BorderLayout());

        try {
            refreshTableData(LibraryDatabase.getAllLibrarians());
        } catch (Exception e) {
            System.err.println("Failed to load librarian records: " + e.getMessage());
        }
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        searchBtn.addActionListener(this);
        resetBtn.addActionListener(this);
        
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);

        topPanel.add(addBtn);
        topPanel.add(updateBtn);
        
        topPanel.add(new JSeparator(JSeparator.VERTICAL));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(resetBtn);
        topPanel.add(deleteBtn);
        
        this.add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        this.setVisible(true);
    }

    // Method to refresh data after certain operation
    private void refreshTableData(ArrayList<Librarian> librarianSourceList) {
        try {
            String[] columnNames = {"Staff ID", "Username", "Full Name", "Assigned Station / Address"};
            tableModel = new DefaultTableModel(columnNames, 0);

            if (librarianSourceList != null) {
                for (Librarian lib : librarianSourceList) {
                    if (lib != null) {
                        tableModel.addRow(new Object[]{
                            lib.getId() != null ? lib.getId() : "N/A",
                            lib.getUsername() != null ? lib.getUsername() : "N/A",
                            lib.getName() != null ? lib.getName() : "Unknown",
                            lib.getAddress() != null ? lib.getAddress() : "Unassigned"
                        });
                    }
                }
            }
            table.setModel(tableModel);
        } catch (Exception e) {
            System.err.println("Error occured when display table: " + e.getMessage());
        }
    }

    // Button action
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == addBtn) {
            spawnLibrarianWizard(null); 
        } else if (source == updateBtn) {
            prepareSelectedUpdate();
        } else if (source == deleteBtn) {
            executeDeletion();
        } else if (source == searchBtn) {
            executeKeywordSearch();
        } else if (source == resetBtn) {
            searchField.setText("");
            refreshTableData(LibraryDatabase.getAllLibrarians());
        }
    }

    // Query to get records from database
    private void prepareSelectedUpdate() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a librarian record from the table list first.", 
                    "No Row Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            String targetId = tableModel.getValueAt(modelRow, 0).toString();
            Librarian targetedLibrarian = LibraryDatabase.findLibrarianById(targetId);

            if (targetedLibrarian != null) {
                spawnLibrarianWizard(targetedLibrarian);
            } else {
                JOptionPane.showMessageDialog(this, "The selected librarian profile could not be located in the database.",
                        "Data Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IndexOutOfBoundsException ioobe) {
            System.err.println("Invalid row selection boundary index mapped: " + ioobe.getMessage());
        } catch (NullPointerException npe) {
            System.err.println("Encountered null pointer exception: " + npe.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected exception when retrieving data: " + e.getMessage());
        }
    }

    // Method to search from database based on keyword
    private void executeKeywordSearch() {
        try {
            String keyword = searchField.getText().trim().toLowerCase();
            if (keyword.isEmpty()) {
                refreshTableData(LibraryDatabase.getAllLibrarians());
                return;
            }

            ArrayList<Librarian> filterResults = new ArrayList<Librarian>();
            ArrayList<Librarian> allLibrarians = LibraryDatabase.getAllLibrarians();

            if (allLibrarians != null) {
                for (Librarian lib : allLibrarians) {
                    if (lib != null) {
                        String name = lib.getName() != null ? lib.getName().toLowerCase() : "";
                        String username = lib.getUsername() != null ? lib.getUsername().toLowerCase() : "";

                        if (name.contains(keyword) || username.contains(keyword)) {
                            filterResults.add(lib);
                        }
                    }
                }
            }
            refreshTableData(filterResults);
        } catch (Exception e) {
            System.err.println("Error filtering collection array lists dynamically: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "An error occurred while compiling search results.",
                    "Search Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to display a form for editing a librarian
    private void spawnLibrarianWizard(Librarian selectedLibrarian) {
        boolean isUpdateMode = (selectedLibrarian != null);

        JTextField idTx = new JTextField(isUpdateMode ? selectedLibrarian.getId() : "");
        JTextField userTx = new JTextField(isUpdateMode ? selectedLibrarian.getUsername() : "");
        JTextField passTx = new JTextField(isUpdateMode ? selectedLibrarian.getPassword() : "");
        JTextField nameTx = new JTextField(isUpdateMode ? selectedLibrarian.getName() : "");
        JTextField addrTx = new JTextField(isUpdateMode ? selectedLibrarian.getAddress() : "");

        if (isUpdateMode) {
            idTx.setEditable(false);
            idTx.setBackground(Color.LIGHT_GRAY);
        }

        Object[] dialogFields = {
            "Staff ID:", idTx,
            "Username:", userTx,
            "Password:", passTx,
            "Full Name:", nameTx,
            "Station/Address:", addrTx
        };

        int option = JOptionPane.showConfirmDialog(this, dialogFields, 
                isUpdateMode ? "Modify Librarian Profile" : "Create Librarian Profile", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (option == JOptionPane.OK_OPTION) {
            String id = idTx.getText().trim();
            String user = userTx.getText().trim();
            String pass = passTx.getText().trim();
            String name = nameTx.getText().trim();
            String addr = addrTx.getText().trim();

            if (id.isEmpty() || user.isEmpty() || pass.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All input fields are required.", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (isUpdateMode) {
                selectedLibrarian.setUsername(user);
                selectedLibrarian.setPassword(pass);
                selectedLibrarian.setName(name);
                selectedLibrarian.setAddress(addr);
                JOptionPane.showMessageDialog(this, "Librarian profile updated successfully.");
            } else {
                if (LibraryDatabase.findLibrarianById(id) != null) {
                    JOptionPane.showMessageDialog(this, "Addition rejected: Staff ID collision encountered.", 
                            "Data Entry Collision", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Librarian finalizedLibrarianObject = new Librarian(id, user, pass, name, addr);
                LibraryDatabase.addLibrarian(finalizedLibrarianObject);
                JOptionPane.showMessageDialog(this, "Librarian profile successfully established.");
            }
            refreshTableData(LibraryDatabase.getAllLibrarians()); 
        }
    }

    private void executeDeletion() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a librarian record from the table.", 
                    "Selection Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String staffId = tableModel.getValueAt(modelRow, 0).toString();
        String staffName = tableModel.getValueAt(modelRow, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to completely erase staff profile: " + staffName + "?", 
                "Confirm Account Erasure", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            LibraryDatabase.removeLibrarian(staffId);
            JOptionPane.showMessageDialog(this, "Staff profile successfully terminated.");
            refreshTableData(LibraryDatabase.getAllLibrarians());
        }
    }
}