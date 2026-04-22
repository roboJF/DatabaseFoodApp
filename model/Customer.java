package model;

public class Customer {
    private int customerId;
    private String name;
    private String address;
    private String contactInfo;
    private String username;
    private String email;
    private String password;

    public Customer(int customerId, String name, String address, String contactInfo, String username, String email, String password) {
        this.customerId  = customerId;
        this.name = name;
        this.address = address;
        this.contactInfo = contactInfo;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getContactInfo() { return contactInfo; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "Customer{id=" + customerId + ", name=" + name + ", username=" + username + "}";
    }
}