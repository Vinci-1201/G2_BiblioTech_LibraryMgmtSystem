import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LibrarianPanel extends JPanel implements ActionListener {
    // Structural desktop pane container and top navigation menu bar
    private JDesktopPane contentPanel = new JDesktopPane();
    private JMenuBar menuBar = new JMenuBar();
    private JMenuItem manageBooksMenu = new JMenuItem("Manage Books");
    private JMenuItem managePatronsMenu = new JMenuItem("Manage Patrons");
    private JMenuItem circulationMenu = new JMenuItem("Book Check-In/Out");
    private JMenuItem finesMenu = new JMenuItem("Fine Management"); 
    private JMenuItem logoutMenu = new JMenuItem("Logout");
    
    public LibrarianPanel() {
        // Set layout FIRST before adding components
        this.setLayout(new BorderLayout());
        this.setBounds(0, 0, SystemConstants.FRAME_WIDTH, SystemConstants.FRAME_HEIGHT);
        
        // Configure content panel
        contentPanel.setBounds(0, 0, SystemConstants.FRAME_WIDTH, SystemConstants.FRAME_HEIGHT - 50);
        this.add(contentPanel, BorderLayout.CENTER);
        
        // Add items to menu bar & register listeners
        menuBar.add(manageBooksMenu);
        menuBar.add(managePatronsMenu);
        menuBar.add(circulationMenu);
        menuBar.add(finesMenu); 
        menuBar.add(logoutMenu);
        
        manageBooksMenu.addActionListener(this);
        managePatronsMenu.addActionListener(this);
        circulationMenu.addActionListener(this);
        finesMenu.addActionListener(this); 
        logoutMenu.addActionListener(this);
        
        // Configure and add menu bar
        menuBar.setBounds(0, 0, SystemConstants.FRAME_WIDTH, 50);
        this.add(menuBar, BorderLayout.NORTH);
        
        // Automatically default to circulation screen
        showCirculationPanel();
    }
    
    // Initialization panel of librarian login
    private void showCirculationPanel() {
        setContent(new CirculationPanel());
    }
    
    // Set content
    public void setContent(JInternalFrame internalFrame) {
        // Clear background layers and append the target internal sub-window component
        contentPanel.removeAll();
        contentPanel.add(internalFrame);
        internalFrame.setVisible(true);

        // Attempt to maximize the internal workspace frame to fit parent layout limits
        try {
            internalFrame.setMaximum(true);
        } catch (java.beans.PropertyVetoException pve) {
            // Fallback safety bounds mapping strategy if platform UI manager halts full expansion
            internalFrame.setSize(SystemConstants.FRAME_WIDTH - 20, SystemConstants.FRAME_HEIGHT - 100);
            pve.printStackTrace();
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    } 
    
    // Menu Action
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == manageBooksMenu) {
            setContent(new BookCatalogPanel()); 
        } 
        else if (source == managePatronsMenu) {
            setContent(new PatronManagementPanel()); 
        } 
        else if (source == circulationMenu) {
            setContent(new CirculationPanel()); 
        } 
        else if (source == finesMenu) {
            setContent(new FineManagementPanel()); 
        }
        else if (source == logoutMenu) {
            logout();
        }
    }

    public static void logout() {
        MainFrame.user = null;
        MainFrame.setContent(
                new LoginPanel(),
                SystemConstants.LOGIN_WIDTH,
                SystemConstants.LOGIN_HEIGHT
        );
    }
}