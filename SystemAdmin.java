public class SystemAdmin extends LibraryUser{
    // Parameterized Constructor
    public SystemAdmin(String id, String username, String password, String name, String address) {
        super(id, username, password, name, address);
    }
    
    @Override
    public String getRole() { return "System Admin"; }
}
