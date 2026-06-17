import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class SystemAdminPanel extends JPanel implements ActionListener {
    // Structural layout components and administrative menu items
    private JDesktopPane contentPanel = new JDesktopPane();
    private JMenuBar menuBar = new JMenuBar();
    private JMenuItem managePatronMenu = new JMenuItem("Manage Patrons");
    private JMenuItem manageLibrarianMenu = new JMenuItem("Manage Librarians");
    private JMenuItem manageBookMenu = new JMenuItem("Manage Books");
    private JMenuItem logoutMenu = new JMenuItem("Logout");
    
    public SystemAdminPanel() {
        // Set layout FIRST before adding components
        this.setLayout(new BorderLayout());
        this.setBounds(0, 0, SystemConstants.FRAME_WIDTH, SystemConstants.FRAME_HEIGHT);
        
        // Configure content panel
        contentPanel.setBounds(0, 20, SystemConstants.FRAME_WIDTH, SystemConstants.FRAME_HEIGHT - 50);
        this.add(contentPanel, BorderLayout.CENTER);
        
        // Add items to menu bar & register listeners
        menuBar.add(managePatronMenu);
        menuBar.add(manageLibrarianMenu);
        menuBar.add(manageBookMenu);
        menuBar.add(logoutMenu);
        
        managePatronMenu.addActionListener(this);
        manageLibrarianMenu.addActionListener(this);
        manageBookMenu.addActionListener(this);
        logoutMenu.addActionListener(this);
        
        // Configure and add menu bar
        menuBar.setBounds(0, 0, SystemConstants.FRAME_WIDTH, 50);
        this.add(menuBar, BorderLayout.NORTH);

        // Display the home dashboard right away on initialization
        showAdminDashboard();
    }

    private void showAdminDashboard() {
        contentPanel.removeAll();

        // Create a container panel for the dashboard matching contentPanel dimensions
        JPanel adminDashboard = new JPanel(new BorderLayout(20, 20));
        adminDashboard.setBorder(new EmptyBorder(40, 40, 40, 40));
        adminDashboard.setBackground(new Color(245, 247, 250)); // Soft modern light gray background
        adminDashboard.setBounds(0, 0, SystemConstants.FRAME_WIDTH, SystemConstants.FRAME_HEIGHT - 70);

        // ---- TOP SECTION: Heading ----
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headerPanel.setOpaque(false);
        
        JLabel welcomeTitle = new JLabel("Library Management System Central Command");
        welcomeTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeTitle.setForeground(new Color(44, 62, 80)); // Deep slate corporate tone
        
        JLabel welcomeSubtitle = new JLabel("System Administrator Session Active.");
        welcomeSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        welcomeSubtitle.setForeground(new Color(127, 140, 141));

        headerPanel.add(welcomeTitle);
        headerPanel.add(welcomeSubtitle);
        adminDashboard.add(headerPanel, BorderLayout.NORTH);

        // ---- CENTER SECTION: Action Status Cards ----
        JPanel centerGrid = new JPanel(new GridLayout(1, 3, 20, 20));
        centerGrid.setOpaque(false);

        // Quick mock status metrics cards to occupy the space meaningfully
        centerGrid.add(createMetricCard("Patron Management", "Active Accounts", new Color(46, 204, 113)));
        centerGrid.add(createMetricCard("Librarian Management", "Duty Operators", new Color(52, 152, 219)));
        centerGrid.add(createMetricCard("Global Book Catalog", "System Inventory", new Color(155, 89, 182)));
        
        adminDashboard.add(centerGrid, BorderLayout.CENTER);

        // ---- BOTTOM SECTION: Quick Tips / System Status Footer ----
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        footerPanel.setBackground(Color.WHITE);
        
        JLabel statusLabel = new JLabel("System Core Status: Operational");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        statusLabel.setForeground(new Color(39, 174, 96)); // Green alert color

        JLabel guideLabel = new JLabel("Use the top navigation menu header navigation options to manage core components.");
        guideLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        guideLabel.setForeground(Color.GRAY);

        footerPanel.add(statusLabel, BorderLayout.WEST);
        footerPanel.add(guideLabel, BorderLayout.EAST);
        adminDashboard.add(footerPanel, BorderLayout.SOUTH);

        // Attach admin dashboard layout wrapper inside desktop engine
        contentPanel.add(adminDashboard);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Generate elements for admin dashboard
    private JPanel createMetricCard(String metricTitle, String subtitle, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createMatteBorder(4, 0, 0, 0, accentColor)); // Top colored highlight line

        JPanel internalPadding = new JPanel(new GridLayout(2, 1, 5, 5));
        internalPadding.setOpaque(false);
        internalPadding.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel titleLbl = new JLabel(metricTitle);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLbl.setForeground(new Color(52, 73, 94));

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subLbl.setForeground(Color.GRAY);

        internalPadding.add(titleLbl);
        internalPadding.add(subLbl);
        card.add(internalPadding, BorderLayout.CENTER);

        return card;
    }
    
    // Set content for the frame
    public void setContent(JInternalFrame internalFrame) {
        contentPanel.removeAll();

        // Listen for when the internal window closes to restore the dashboard
        internalFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent e) {
                showAdminDashboard();
            }
        });

        // Establish parent container relationship hierarchy first
        contentPanel.add(internalFrame);
        internalFrame.setVisible(true);

        // Safely call maximum sizing without risking NullPointerExceptions
        try {
            internalFrame.setMaximum(true);
        } catch (java.beans.PropertyVetoException pve) {
            // Dynamic fail-safe scaling fallback layout strategy
            internalFrame.setSize(SystemConstants.FRAME_WIDTH - 20, SystemConstants.FRAME_HEIGHT - 100);
            pve.printStackTrace();
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    } 
    
    // Menu action
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == managePatronMenu) {
            setContent(new PatronManagementPanel()); 
        } 
        else if (source == manageLibrarianMenu) {
            setContent(new LibrarianManagementPanel()); 
        } 
        else if (source == manageBookMenu) {
            setContent(new BookCatalogPanel()); 
        } 
        else if (source == logoutMenu) {
            logout();
        }
    }

    // Logout method: global user = null
    // Display login panel
    public static void logout() {
        MainFrame.user = null;
        MainFrame.setContent(
                new LoginPanel(),
                SystemConstants.LOGIN_WIDTH,
                SystemConstants.LOGIN_HEIGHT
        );
    }
}