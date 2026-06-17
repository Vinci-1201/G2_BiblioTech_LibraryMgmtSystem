import java.util.ArrayList;

public class Patron extends LibraryUser {
    private double fine; 
    private ArrayList<Loan> activeLoans;

    // Parameterized Constructors
    public Patron(String id, String username, String password, String name, String address) {
        super(id, username, password, name, address);
        this.fine = 0.0;
        this.activeLoans = new ArrayList<>();
    }
    
    public Patron(ArrayList<Loan> activeLoans, String id, String username, String password, String name, String address) {
        super(id, username, password, name, address);
        this.fine = 0.0; 
        
        // Fallback to empty list if activeLoans parameter is passed as null
        if (activeLoans != null) {
            this.activeLoans = activeLoans;
        } else {
            this.activeLoans = new ArrayList<>();
        }
    }

    @Override
    public String getRole() { 
        return "Patron"; 
    }
    
    // Method for Patron to add book into loan list
    public void borrowBook(Loan loan) {
        if (loan != null) {
            if (this.activeLoans.contains(loan) == false) {
                this.activeLoans.add(loan);
            }
        }
    }
    
    // Removes a book from loan list
    public void returnBook(Loan loan) {
        if (loan != null) {
            this.activeLoans.remove(loan);
        }
    }
    
    // Finds and removes an active loan assignment using a plain Book ID string match
    public boolean returnBookById(String bookId) {
        if (bookId == null) {
            return false;
        }
        
        Loan targetLoan = null;
        for (Loan loan : this.activeLoans) {
            if (loan.getBook() != null) {
                if (loan.getBook().getItemId().equals(bookId.trim())) {
                    targetLoan = loan;
                    break;
                }
            }
        }
        
        if (targetLoan != null) {
            boolean isRemoved = this.activeLoans.remove(targetLoan);
            return isRemoved;
        }
        
        return false;
    }
    
    // Pulls book hold entries matching this patron's primary key identifier
    public ArrayList<Book> getBooksOnHold() {
        return LibraryDatabase.getBooksOnHoldForPatron(this.getId());
    }

    // Getters and Setters
    public double getFine() { return this.fine; }
    public void setFine(double fine) { this.fine = fine; }
    public ArrayList<Loan> getActiveLoans() { return this.activeLoans; }
}