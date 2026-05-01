package model;

public class OrderItem {
    private int orderItemId;
    private int quantity;
    private int foodOrderId;
    private int menuItemId;

    //this constructor would be used for pulling existing order items out of the DB
    public OrderItem(int orderItemId, int quantity, int foodOrderId, int menuItemId) {
        this.orderItemId  = orderItemId;
        this.quantity     = quantity;
        this.foodOrderId  = foodOrderId;
        this.menuItemId   = menuItemId;
    }

    //this constructor would be used for inserting new order items, since they wouldn't have id's yet (i think?)
    public OrderItem(int quantity, int foodOrderId, int menuItemId) {
        this.quantity = quantity;
        this.foodOrderId = foodOrderId;
        this.menuItemId = menuItemId;
    }

    public int getOrderItemId() { return orderItemId; }
    public int getQuantity() { return quantity; }
    public int getFoodOrderId() { return foodOrderId; }
    public int getMenuItemId() { return menuItemId; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "OrderItem{orderId=" + foodOrderId + ", menuItemId=" + menuItemId + ", quantity=" + quantity + "}";
    }
}