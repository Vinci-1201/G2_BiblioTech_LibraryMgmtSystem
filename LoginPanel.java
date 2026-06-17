import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginPanel extends JPanel implements ActionListener {
    // UI component fields for user input and credentials
    private JTextField nameField = new JTextField(15);
    private JPasswordField pwdField = new JPasswordField(15);
    private JRadioButton adminRadio = new JRadioButton("Admin");
    private JRadioButton librarianRadio = new JRadioButton("Librarian", true);
    private JRadioButton patronRadio = new JRadioButton("Patron");
    private ButtonGroup userType = new ButtonGroup();
    private JButton loginBtn = new JButton("Log In");
    
    public LoginPanel() {
        // Configure main layout and padding spacing properties
        setLayout(new BorderLayout(10, 15));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // Create and add header welcome title at the top
        JLabel titleLabel = new JLabel("Welcome Back to BiblioTech", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Assemble data input fields and text labels inside a structural grid
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        formPanel.add(new JLabel("Username:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(pwdField);
        
        // Group radio buttons logically
        // then append them into a horizontal row container
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        userType.add(adminRadio);
        userType.add(librarianRadio);
        userType.add(patronRadio);
        radioPanel.add(adminRadio);
        radioPanel.add(librarianRadio);
        radioPanel.add(patronRadio);
        formPanel.add(radioPanel);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Create login button at the bottom with panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginBtn.setPreferredSize(new Dimension(120, 30));
        loginBtn.addActionListener(this);
        buttonPanel.add(loginBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Collect credential tokens typed by the client user
        String username = nameField.getText().trim();
        String password = new String(pwdField.getPassword());

        // Validate user match existence against database records
        LibraryUser user = LibraryDatabase.authenticate(username, password);
        JPanel userPanel = null;

        // Verify account existence integrity via guard clause bounds
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Username or password incorrect", "System Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Map UI choice status flags into validation role tracker strings
        String selectedRole = "";
        if (adminRadio.isSelected()) {
            selectedRole = "System Admin";
        } else if (librarianRadio.isSelected()) {
            selectedRole = "Librarian";
        } else if (patronRadio.isSelected()) {
            selectedRole = "Patron";
        }

       // Enforce synchronization constraints matching profile records with requested UI type
        if (!user.getRole().equals(selectedRole)) {
            JOptionPane.showMessageDialog(this,
                    "Role Mismatch: Your account is assigned as a " + user.getRole(),
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Bind verified profile attributes to global session runtime tracker storage
        MainFrame.user = user;

        // Polymorphism applied
        // Instantiate corresponding specialized sub-panel system workspace dashboards
        if (user instanceof SystemAdmin) {
            userPanel = new SystemAdminPanel();
        } else if (user instanceof Librarian) {
            userPanel = new LibrarianPanel();
        } else if (user instanceof Patron) {
            userPanel = new PatronPanel();
        }

        // Display dedicated user panel
        if (userPanel != null) {
            MainFrame.setContent(userPanel, SystemConstants.FRAME_WIDTH, SystemConstants.FRAME_HEIGHT);
        }
    }
}