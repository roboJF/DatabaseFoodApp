package model;

public class Administrator {
    private int adminId;
    private String username;
    private String email;
    private String password;

    public Administrator(int adminId, String username, String email, String password) {
        this.adminId = adminId;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getAdminId() { return adminId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "Administrator{id=" + adminId + ", username=" + username + "}";
    }
}
