public abstract class LibraryUser {
    private final String id; // Assigned permanently during object creation and cannot be changed
    private String username;
    private String password;
    private String name;
    private String address;

    // Parameterized Constructor
    public LibraryUser(String id, String username, String password, String name, String address) {
        this.id = id; 
        this.username = username;
        this.password = password;
        this.name = name;
        this.address = address;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getAddress() { return address; }

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    
    // Abstract method to be implemented by child classes (e.g., Patron, Librarian)
    public abstract String getRole();
}