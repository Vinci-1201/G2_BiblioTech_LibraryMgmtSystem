import java.util.ArrayList;

public class Book extends LibraryItem {
    private String author;
    private String isbn;
    private String publisher;
    private int publishedYear;
    private double price;
    private int amount;
    private String genre;
    private ArrayList<Patron> holdList;

    // Parameterized Constructor
    public Book(String itemId, String title, String author, String isbn, String publisher, int publishedYear, double price, int amount, String genre) {
        super(itemId, title);
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.publishedYear = publishedYear;
        this.price = price;
        this.amount = amount;
        this.genre = genre; // Initialize genre assignment
        this.holdList = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getPublishedYear() { return publishedYear; }
    public void setPublishedYear(int publishedYear) { this.publishedYear = publishedYear; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    
    public ArrayList<Patron> getHoldList() {return holdList;}

    
    // Behaviours 
    public void addPatronToHold(Patron patron) {
        if (patron != null && !holdList.contains(patron)) {
            holdList.add(patron);
        }
    }

    public void removePatronFromHold(Patron patron) {
        if (patron != null) {
            holdList.remove(patron);
        }
    }
}