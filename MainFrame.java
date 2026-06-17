import javax.swing.*;

public class MainFrame {
    // Global frame and session tracking fields
    public static final JFrame frame = new JFrame("BiblioTech Library Management System");
    public static LibraryUser user; 
    
    // Main method as entry point
    public static void main(String[] args){
        // Initialize window settings, dimensions, and display the login panel
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SystemConstants.LOGIN_WIDTH, SystemConstants.LOGIN_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new LoginPanel());
        frame.setVisible(true);
    }
    
    // Swap content panels, adjust window size, and refresh the UI layout
    public static void setContent(JPanel panel, int width, int height){
        frame.setContentPane(panel);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.revalidate();
        frame.repaint();
    }
}