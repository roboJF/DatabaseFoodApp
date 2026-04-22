package model;

public class DeliveryPersonnel {
    private int deliveryPersonnelId;
    private String name;
    private String contactInfo;
    private String vehicleDetails;
    private String username;
    private String email;
    private String password;

    public DeliveryPersonnel(int deliveryPersonnelId, String name, String contactInfo, String vehicleDetails, String username, String email, String password) {
        this.deliveryPersonnelId = deliveryPersonnelId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.vehicleDetails = vehicleDetails;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getDeliveryPersonnelId() { return deliveryPersonnelId; }
    public String getName() { return name; }
    public String getContactInfo() { return contactInfo; }
    public String getVehicleDetails() { return vehicleDetails; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setName(String name) { this.name = name; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void setVehicleDetails(String vehicleDetails) { this.vehicleDetails = vehicleDetails; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "DeliveryPersonnel{id=" + deliveryPersonnelId + ", name=" + name + "}";
    }
}