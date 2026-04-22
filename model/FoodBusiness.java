package model;

public class FoodBusiness {
    private int foodBusinessId;
    private String name;
    private String location;
    private String contactInfo;
    private String username;
    private String email;
    private String password;

    public FoodBusiness(int foodBusinessId, String name, String location, String contactInfo, String username, String email, String password) {
        this.foodBusinessId = foodBusinessId;
        this.name = name;
        this.location = location;
        this.contactInfo = contactInfo;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getFoodBusinessId() { return foodBusinessId; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getContactInfo() { return contactInfo; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() { return name; }
}
