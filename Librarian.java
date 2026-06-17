public class Librarian extends LibraryUser{
    // Parameterized Constructor
    public Librarian(String id, String username, String password, String name, String address) {
        super(id, username, password, name, address);
    }
    
    @Override
    public String getRole() { return "Librarian"; }
}
