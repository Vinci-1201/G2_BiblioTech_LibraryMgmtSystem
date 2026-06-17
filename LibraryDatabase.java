import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LibraryDatabase {

    private static ArrayList<Book> bookList = new ArrayList<>();
    private static ArrayList<Patron> patronList = new ArrayList<>();
    private static ArrayList<Librarian> librarianList = new ArrayList<>();
    private static ArrayList<SystemAdmin> adminList = new ArrayList<>();
    private static ArrayList<Loan> loanList = new ArrayList<>();

    static {
        // Seed default system administrator
        adminList.add(new SystemAdmin("A01", "admin", "123", "Root Admin", "System Core"));
        
        // Seed expanded librarian workforce for multi-department demos
        librarianList.add(new Librarian("L01", "lib1", "123", "Sarah Jenkins", "Main Desk"));
        librarianList.add(new Librarian("L02", "lib2", "456", "Mirabelle Kwong", "Archival Wing"));
        librarianList.add(new Librarian("L03", "faizal_h", "789", "Faizal Hassan", "Circulation Supervisor"));
        librarianList.add(new Librarian("L04", "anastasia_v", "adminpass", "Anastasia Vance", "Digital Cataloging"));
        librarianList.add(new Librarian("L05", "kumar_r", "lib999", "Rajesh Kumar", "Reference & Research"));
        
        // Seed realistic patron profiles spanning various residential zones
        Patron p1 = new Patron(new ArrayList<Loan>(), "P01", "p1", "123", "John Doe", "123 Elm Street");
        Patron p2 = new Patron(new ArrayList<Loan>(), "P02", "p2", "456", "Jane Smith", "456 Oak Avenue");
        Patron p3 = new Patron(new ArrayList<Loan>(), "P03", "p3", "abc", "Brian Tan", "78 Jalan Ampang");
        Patron p4 = new Patron(new ArrayList<Loan>(), "P04", "siti_a", "xyz", "Siti Aminah", "12 Lorong Kurau");
        Patron p5 = new Patron(new ArrayList<Loan>(), "P05", "david_k", "pwd", "David Kim", "88 Maple Drive");
        Patron p6 = new Patron(new ArrayList<Loan>(), "P06", "chloe_l", "pass1", "Chloe Lim", "154 Jalan Tun Razak");
        Patron p7 = new Patron(new ArrayList<Loan>(), "P07", "ammar_z", "pass2", "Ammar Zulkifli", "5 Key Valley Road");
        Patron p8 = new Patron(new ArrayList<Loan>(), "P08", "priya_m", "pass3", "Priya Mohan", "32 Pine Crest Slopes");
        Patron p9 = new Patron(new ArrayList<Loan>(), "P09", "marcus_w", "pass4", "Marcus Wong", "210 Boulevard Residency");
        Patron p10 = new Patron(new ArrayList<Loan>(), "P10", "fatimah_r", "pass5", "Fatimah Razak", "67 Kampung Baru");
        
        // Seed sample starting outstanding balance details onto individual patron profiles
        p1.setFine(12.50);
        p2.setFine(0.00);
        p3.setFine(5.00);
        p5.setFine(25.00);
        p8.setFine(0.00);

        patronList.add(p1);
        patronList.add(p2);
        patronList.add(p3);
        patronList.add(p4);
        patronList.add(p5);
        patronList.add(p6);
        patronList.add(p7);
        patronList.add(p8);
        patronList.add(p9);
        patronList.add(p10);

        // Seed comprehensive inventory list with diverse genres and controlled stock flags
        Book b1 = new Book("B01", "The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", "Scribner", 1925, 14.99, 5, "Classic Fiction");
        Book b2 = new Book("B02", "1984", "George Orwell", "9780451524935", "Signet Classic", 1949, 9.99, 0, "Dystopian Sci-Fi");
        Book b3 = new Book("B03", "To Kill a Mockingbird", "Harper Lee", "9780446310789", "Grand Central", 1960, 15.99, 4, "Classic Drama");
        Book b4 = new Book("B04", "Dune", "Frank Herbert", "9780441172719", "Ace Books", 1965, 10.99, 2, "Sci-Fi Fantasy");
        Book b5 = new Book("B05", "The Hobbit", "J.R.R. Tolkien", "9780547928227", "Houghton Mifflin", 1937, 14.99, 3, "Sci-Fi Fantasy");
        Book b6 = new Book("B06", "Thinking, Fast and Slow", "Daniel Kahneman", "9780374275631", "Farrar, Straus", 2011, 18.00, 6, "Psychology Non-Fiction");
        Book b7 = new Book("B07", "A Brief History of Time", "Stephen Hawking", "9780553380163", "Bantam", 1988, 17.50, 1, "Popular Science");
        Book b8 = new Book("B08", "Clean Code", "Robert C. Martin", "9780132350884", "Prentice Hall", 2008, 44.99, 3, "Computer Science");
        Book b9 = new Book("B09", "Design Patterns", "Erich Gamma", "9780201633610", "Addison-Wesley", 1994, 49.95, 2, "Computer Science");
        Book b10 = new Book("B10", "The Silent Patient", "Alex Michaelides", "9781250301697", "Celadon Books", 2019, 26.99, 0, "Thriller Mystery");
        Book b11 = new Book("B11", "Educated", "Tara Westover", "9780399590504", "Random House", 2018, 28.00, 4, "Biography Memoir");
        Book b12 = new Book("B12", "Sapiens: A Brief History of Humankind", "Yuval Noah Harari", "9780062316097", "Harper", 2015, 22.99, 7, "History Anthropology");
        Book b13 = new Book("B13", "Atomic Habits", "James Clear", "9780735211292", "Avery", 2018, 27.00, 0, "Self-Help Development");
        Book b14 = new Book("B14", "Neuromancer", "William Gibson", "9780441569595", "Ace Books", 1984, 8.99, 2, "Dystopian Sci-Fi");
        Book b15 = new Book("B15", "The Catcher in the Rye", "J.D. Salinger", "9780316769174", "Little, Brown", 1951, 12.99, 5, "Classic Fiction");

        bookList.add(b1);
        bookList.add(b2);
        bookList.add(b3);
        bookList.add(b4);
        bookList.add(b5);
        bookList.add(b6);
        bookList.add(b7);
        bookList.add(b8);
        bookList.add(b9);
        bookList.add(b10);
        bookList.add(b11);
        bookList.add(b12);
        bookList.add(b13);
        bookList.add(b14);
        bookList.add(b15);
        
        // Seed initial active reservation holds for zero-stock titles
        b2.getHoldList().add(p3);
        b2.getHoldList().add(p7);
        b10.getHoldList().add(p9);
        b13.getHoldList().add(p4);

        // SYSTEM FIX: Parsed String variables directly into native java.util.Date objects via overloaded constructor parameters
        addLoan(new Loan("LN01", b1, p1, parseDate("2026-06-01"), parseDate("2026-06-15")));
        addLoan(new Loan("LN02", b3, p4, parseDate("2026-06-05"), parseDate("2026-06-19")));
        addLoan(new Loan("LN03", b4, p2, parseDate("2026-06-10"), parseDate("2026-06-24")));
        addLoan(new Loan("LN04", b6, p3, parseDate("2026-06-12"), parseDate("2026-06-26")));
        addLoan(new Loan("LN05", b8, p5, parseDate("2026-05-20"), parseDate("2026-06-03")));
    }
    
    // Internal seeding utility to cleanly prevent throwing raw block-level parsing exceptions
    private static Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            return new Date(); 
        }
    }

    // Method to add a new loan into the list
    public static void addLoan(Loan loan) {
        if (loan == null) {
            return;
        }
        loanList.add(loan);
        Patron p = loan.getPatron();
        if (p != null) {
            p.borrowBook(loan);
        }
    }

    // ================================================
    // BOOK CRUD: ADD, DELETE, SEARCH, LIST
    // ================================================
    public static boolean addBook(Book book) {
        if (findBookById(book.getItemId()) == null) {
            bookList.add(book);
            return true;
        }
        return false;
    }

    public static boolean deleteBookById(String itemId) {
        Book book = findBookById(itemId);
        if (book != null) {
            bookList.remove(book);
            return true;
        }
        return false;
    }

    public static Book findBookById(String itemId) {
        for (Book b : bookList) {
            if (b.getItemId().equalsIgnoreCase(itemId)) {
                return b;
            }
        }
        return null;
    }

    public static Book searchBookById(String itemId) {
        return findBookById(itemId);
    }

    public static ArrayList<Book> searchBooksByTitle(String keyword) {
        ArrayList<Book> matches = new ArrayList<>();
        for (Book b : bookList) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                matches.add(b);
            }
        }
        return matches;
    }

    public static ArrayList<Book> getAllBooks() {
        return bookList;
    }

    // ================================================
    // PATRON CRUD: ADD, DELETE, SEARCH, LIST
    // ================================================
    public static void addPatron(Patron patron) {
        if (findPatronById(patron.getId()) == null) {
            patronList.add(patron);
        }
    }

    public static void removePatron(String patronId) {
        Patron patron = findPatronById(patronId);
        if (patron != null) {
            patronList.remove(patron);
        }
    }

    public static Patron findPatronById(String patronId) {
        for (Patron p : patronList) {
            if (p.getId().equalsIgnoreCase(patronId)) {
                return p;
            }
        }
        return null;
    }

    public static ArrayList<Patron> getAllPatrons() {
        return patronList;
    }

    // ================================================
    // LIBRARIAN CRUD: ADD, DELETE, SEARCH, LIST
    // ================================================
    public static void addLibrarian(Librarian librarian) {
        if (findLibrarianById(librarian.getId()) == null) {
            librarianList.add(librarian);
        }
    }
    
    public static void removeLibrarian(String librarianId) {
        Librarian librarian = findLibrarianById(librarianId);
        if (librarian != null) {
            librarianList.remove(librarian);
        }
    }
    
    public static Librarian findLibrarianById(String librarianId) {
        for (Librarian l : librarianList) {
            if (l.getId().equalsIgnoreCase(librarianId)) {
                return l;
            }
        }
        return null;
    }

    public static ArrayList<Librarian> getAllLibrarians() {
        return librarianList;
    }

    // ================================================
    // LIBRARY USER SEARCH & AUTHENTICATE
    // ================================================
    public static LibraryUser searchUserById(String id) {
        for (SystemAdmin sa : adminList) {
            if (sa.getId().equalsIgnoreCase(id)) return sa;
        }
        for (Librarian l : librarianList) {
            if (l.getId().equalsIgnoreCase(id)) return l;
        }
        for (Patron p : patronList) {
            if (p.getId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    public static LibraryUser authenticate(String username, String password) {
        for (SystemAdmin sa : adminList) {
            if (sa.getUsername().equals(username) && sa.getPassword().equals(password)) {
                return sa;
            }
        }
        for (Librarian l : librarianList) {
            if (l.getUsername().equals(username) && l.getPassword().equals(password)) {
                return l;
            }
        }
        for (Patron p : patronList) {
            if (p.getUsername().equals(username) && p.getPassword().equals(password)) {
                return p;
            }
        }
        return null; 
    }

    // ================================================
    // HOLD RELATED OPERATIONS
    // ================================================
    public static ArrayList<String> getHoldQueuePatronIds(String bookId) {
        ArrayList<String> patronIds = new ArrayList<>();
        Book book = findBookById(bookId);
        if (book != null && book.getHoldList() != null) {
            for (Patron p : book.getHoldList()) {
                patronIds.add(p.getId());
            }
        }
        return patronIds;
    }
    
    public static boolean addHoldToBook(String bookId, String patronId) {
        Book book = findBookById(bookId);
        Patron patron = findPatronById(patronId);
        if (book != null && patron != null) {
            if (!book.getHoldList().contains(patron)) {
                book.getHoldList().add(patron);
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Book> getBooksOnHoldForPatron(String patronId) {
        ArrayList<Book> patronHoldRecords = new ArrayList<>();
        for (Book book : bookList) {
            if (book.getHoldList() != null) {
                for (Patron waitingPatron : book.getHoldList()) {
                    if (waitingPatron.getId().equalsIgnoreCase(patronId)) {
                        patronHoldRecords.add(book);
                        break; 
                    }
                }
            }
        }
        return patronHoldRecords;
    }

    // ================================================
    // FINE RELATED OPERATIONS
    // ================================================
    public static boolean payPatronFine(String patronId, double paymentAmount) {
        Patron patron = findPatronById(patronId);
        if (patron != null && paymentAmount > 0) {
            double currentFine = patron.getFine();
            if (paymentAmount <= currentFine) {
                patron.setFine(currentFine - paymentAmount);
                return true;
            }
        }
        return false;
    }

    public static boolean chargePatronFine(String patronId, double chargeAmount) {
        Patron patron = findPatronById(patronId);
        if (patron != null && chargeAmount > 0) {
            patron.setFine(patron.getFine() + chargeAmount);
            return true;
        }
        return false;
    }
}