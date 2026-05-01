package model;

public class DeliveryPersonnel {
    private int deliveryPersonnelId;
    private String firstName;
    private String lastName;
    private String contactInfo;
    private String vehicleDetails;
    private String username;
    private String email;
    private String password;

    public DeliveryPersonnel(int deliveryPersonnelId, String firstName,String lastName, String contactInfo, String vehicleDetails,
            String username, String email, String password) {
        this.deliveryPersonnelId = deliveryPersonnelId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactInfo = contactInfo;
        this.vehicleDetails = vehicleDetails;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getDeliveryPersonnelId() {
        return deliveryPersonnelId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFullName() {
        return firstName.trim() + " " + lastName.trim();
    }
    
    public String getLastName() {
        return lastName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getVehicleDetails() {
        return vehicleDetails;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public void setVehicleDetails(String vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "DeliveryPersonnel{id=" + deliveryPersonnelId + ", name=" + getFullName() + "}";
    }
}