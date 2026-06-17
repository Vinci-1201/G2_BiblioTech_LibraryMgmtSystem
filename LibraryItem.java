import java.util.Date;
import javax.swing.JOptionPane;

public abstract class LibraryItem {
    private final String itemId; 
    private String title;
    private Patron borrower;
    private Date borrowDate; 

    // No default constructor because item can be created without an ID
    // Parameterized Constructor
    public LibraryItem(String itemId, String title) {
        this.itemId = itemId;
        this.title = title;
        this.borrower = null;
        this.borrowDate = null;
    }

    // Getters and Setters
    public String getItemId() { return itemId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Patron getBorrower() { return borrower; }
    public Date getBorrowDate() { return borrowDate; }
    
    
    // Behaviours
    // Return the available state of an item
    public boolean isAvailable() {
        return this.borrower == null;
    }

    // Set borrower and checkout date
    public void checkOut(Patron patron) {
        if (isAvailable()) {
            this.borrower = patron;
            this.borrowDate = new Date(); 
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "System Alert: Item " + itemId + " is already checked out.",
                    "Transaction Denied",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    public void returnItem() {
        this.borrower = null;
        this.borrowDate = null;
    }
}