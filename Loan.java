import java.util.Date;
import java.util.Calendar;

public class Loan {
    private String loanId;
    private Book book;
    private Patron patron;
    private Date checkoutDate; 
    private Date dueDate;      
    private Date returnDate;   
    private boolean isReturned;
    private boolean fineApplied; 

    // Default Constructor for live UI operations
    public Loan(String loanId, Book book, Patron patron) {
        this.loanId = loanId;
        this.book = book;
        this.patron = patron;
        this.checkoutDate = new Date(); 
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.checkoutDate);
        cal.add(Calendar.DAY_OF_MONTH, SystemConstants.LOAN_DURATION_DAYS);
        this.dueDate = cal.getTime();
        
        this.isReturned = false;
        this.fineApplied = false;
        this.returnDate = null;
    }

    // Overloaded Constructor to cleanly accept custom dates from seed data
    public Loan(String loanId, Book book, Patron patron, Date checkoutDate, Date dueDate) {
        this.loanId = loanId;
        this.book = book;
        Patron databaseLivePatron = LibraryDatabase.findPatronById(patron.getId());
        if (databaseLivePatron != null) {
            this.patron = databaseLivePatron;
        } else {
            this.patron = patron; // Fallback safety catch
        }
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
        this.isReturned = false;
        this.fineApplied = false;
        this.returnDate = null;
    }

    // Finalizes the return transaction and handles overdue fines
    public void processReturn() {
        this.returnDate = new Date(); 
        this.isReturned = true;
        
        if (!fineApplied) {
            double calculatedFine = calculateOverdueFine();
            if (calculatedFine > 0.0) {
                patron.setFine(patron.getFine() + calculatedFine);
            }
            this.fineApplied = true; 
        }
    }

    // Checks if the book is overdue based on execution time limits
    public boolean isOverdue() {
        Date endPoint = this.isReturned ? this.returnDate : new Date();
        return endPoint.after(this.dueDate); 
    }

    // Safely counts overdue days cleanly on midnight boundaries
    public double calculateOverdueFine() {
        if (this.isReturned && this.fineApplied) {
            return 0.0;
        }

        Date endPoint = this.isReturned ? this.returnDate : new Date();
        
        if (endPoint.after(this.dueDate)) {
            int overdueDays = 0;
            
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime(this.dueDate);
            
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endPoint);

            tempCal.set(Calendar.HOUR_OF_DAY, 0); 
            tempCal.set(Calendar.MINUTE, 0); 
            tempCal.set(Calendar.SECOND, 0); 
            tempCal.set(Calendar.MILLISECOND, 0);
            
            endCal.set(Calendar.HOUR_OF_DAY, 0); 
            endCal.set(Calendar.MINUTE, 0); 
            endCal.set(Calendar.SECOND, 0); 
            endCal.set(Calendar.MILLISECOND, 0);

            while (tempCal.before(endCal)) {
                overdueDays = overdueDays + 1;
                tempCal.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            return overdueDays * SystemConstants.FINE_PER_DAY;
        }
        
        return 0.0;
    }

    // Getters
    public String getLoanId() { return this.loanId; }
    public Book getBook() { return this.book; }
    public Patron getPatron() { return this.patron; }
    public Date getCheckoutDate() { return this.checkoutDate; } 
    public Date getDueDate() { return this.dueDate; }           
    public Date getReturnDate() { return this.returnDate; }     
    public boolean isReturned() { return this.isReturned; }
}