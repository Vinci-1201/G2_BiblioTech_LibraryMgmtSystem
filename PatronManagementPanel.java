import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.util.ArrayList;

public class PatronManagementPanel extends JInternalFrame implements ActionListener {

    private JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    private DefaultTableModel tableModel;
    
    private JTable table = new JTable() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Disallow raw inline text editing inside table cell grids
        }
    };

    // Functional Action Components
    private JButton addBtn = new JButton("Add Patron");
    private JButton updateBtn = new JButton("Update Selected");
    private JButton deleteBtn = new JButton("Delete Selected");

    // Search Filtering Components
    private JTextField searchField = new JTextField(15);
    private JButton searchBtn = new JButton("Search Name");
    private JButton resetBtn = new JButton("Reset");

    public PatronManagementPanel() {
        super("Manage Patrons", true, true, true, true);
        this.setSize(SystemConstants.FRAME_WIDTH, SystemConstants.FRAME_HEIGHT);
        this.setLayout(new BorderLayout());

        refreshTableData(LibraryDatabase.getAllPatrons());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        searchBtn.addActionListener(this);
        resetBtn.addActionListener(this);

        topPanel.add(addBtn);
        topPanel.add(updateBtn);

        topPanel.add(new JSeparator(JSeparator.VERTICAL));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(resetBtn);

        // Restrict delete capability to SYSTEM ADMIN only
        if (MainFrame.user instanceof SystemAdmin) {
            deleteBtn.addActionListener(this);
            deleteBtn.setBackground(new Color(231, 76, 60)); 
            deleteBtn.setForeground(Color.WHITE);
            topPanel.add(deleteBtn);
        }

        this.add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        this.setVisible(true);
    }

    // Extracts Patrons from the data list and populates the UI table model grid view
    private void refreshTableData(ArrayList<Patron> patronSourceList) {
        String[] columnNames = {"Patron ID", "Username", "Full Name", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0);
        
        for (Patron p : patronSourceList) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getUsername(),
                p.getName(),
                p.getAddress()
            });
        }
        table.setModel(tableModel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == addBtn) {
            spawnPatronWizard(null); // Passing null initializes blank input fields
        } 
        else if (source == updateBtn) {
            prepareSelectedUpdate();
        } 
        else if (source == deleteBtn) {
            executeSelectedDeletion();
        } 
        else if (source == searchBtn) {
            executeKeywordSearch();
        } 
        else if (source == resetBtn) {
            searchField.setText("");
            // ERROR FIX: Cleans filter panel to show all Patrons cleanly
            refreshTableData(LibraryDatabase.getAllPatrons()); 
        }
    }

    // Locates the chosen table row reference and passes the target object to input wizards
    private void prepareSelectedUpdate() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an active patron row from the table list first.", 
                "Selection Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String targetId = tableModel.getValueAt(selectedRow, 0).toString();
        Patron targetedPatron = LibraryDatabase.findPatronById(targetId);
        if (targetedPatron != null) {
            spawnPatronWizard(targetedPatron);
        }
    }

    // Method to search patron
    private void executeKeywordSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshTableData(LibraryDatabase.getAllPatrons());
            return;
        }
        
        ArrayList<Patron> filterResults = new ArrayList<>();
        for (Patron p : LibraryDatabase.getAllPatrons()) {
            if (p.getName().toLowerCase().contains(keyword) || p.getUsername().toLowerCase().contains(keyword)) {
                filterResults.add(p);
            }
        }
        refreshTableData(filterResults);
    }

    // Display a form to update patorn record
    private void spawnPatronWizard(Patron selectedPatron) {
        boolean isUpdateMode = (selectedPatron != null);

        JTextField idTx = new JTextField(isUpdateMode ? selectedPatron.getId() : "");
        JTextField userTx = new JTextField(isUpdateMode ? selectedPatron.getUsername() : "");
        JTextField passTx = new JTextField(isUpdateMode ? selectedPatron.getPassword() : "");
        JTextField nameTx = new JTextField(isUpdateMode ? selectedPatron.getName() : "");
        JTextField addrTx = new JTextField(isUpdateMode ? selectedPatron.getAddress() : "");

        if (isUpdateMode) {
            idTx.setEditable(false);
            idTx.setBackground(Color.LIGHT_GRAY);
        }

        Object[] inputFieldsLayout = {
            "Unique Patron ID:", idTx,
            "System Username:", userTx,
            "Account Password:", passTx,
            "Patron Full Name:", nameTx,
            "Residential Address:", addrTx
        };

        int confirmationResult = JOptionPane.showConfirmDialog(this, inputFieldsLayout, 
                isUpdateMode ? "Modify Patron Record Details" : "Register New Patron Profile", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (confirmationResult == JOptionPane.OK_OPTION) {
            String id = idTx.getText().trim();
            String user = userTx.getText().trim();
            String pass = passTx.getText().trim();
            String name = nameTx.getText().trim();
            String address = addrTx.getText().trim();

            if (id.isEmpty() || user.isEmpty() || pass.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username, password and name cannot be empty.", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isUpdateMode) {
                selectedPatron.setUsername(user);
                selectedPatron.setPassword(pass);
                selectedPatron.setName(name);
                selectedPatron.setAddress(address);
                
                JOptionPane.showMessageDialog(this, "Patron profile updated successfully.");
            } else {
                // Safeguard against duplicate Patron IDs
                if (LibraryDatabase.findPatronById(id) != null) {
                    JOptionPane.showMessageDialog(this, "Addition rejected: Patron ID existed.", 
                            "Primary Key Collision", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Patron finalizedPatronObject = new Patron(new ArrayList<Loan>(), id, user, pass, name, address);
                LibraryDatabase.addPatron(finalizedPatronObject);
                JOptionPane.showMessageDialog(this, "Successfully added new patron.");
            }
            
            refreshTableData(LibraryDatabase.getAllPatrons()); // Instantly update view screen layout rows
        }
    }

    // Method to delete patron
    private void executeSelectedDeletion() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a patron record from the table first.", 
                "Selection Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String patronId = tableModel.getValueAt(selectedRow, 0).toString();
        String patronName = tableModel.getValueAt(selectedRow, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you absolutely sure you want to drop patron account: " + patronName + "?", 
            "Confirm Account Drop", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            LibraryDatabase.removePatron(patronId);
            JOptionPane.showMessageDialog(this, "Account removed successfully.");
            refreshTableData(LibraryDatabase.getAllPatrons());
        }
    }
}