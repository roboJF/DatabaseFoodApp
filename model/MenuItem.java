package model;

public class MenuItem {
    private int menuItemId;
    private String name;
    private String description;
    private double price;
    private boolean availability;
    private int foodBusinessId;

    public MenuItem(int menuItemId, String name, String description, double price, boolean availability, int foodBusinessId) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.availability = availability;
        this.foodBusinessId = foodBusinessId;
    }

    public int getMenuItemId() { return menuItemId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return availability; }
    public int getFoodBusinessId() { return foodBusinessId; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setAvailability(boolean availability) { this.availability = availability; }

    @Override
    public String toString() { return name + " - $" + String.format("%.2f", price); }
}