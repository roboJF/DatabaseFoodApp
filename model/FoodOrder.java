package model;

public class FoodOrder {
    private int foodOrderId;
    private String orderStatus;
    private int customerId;
    private int foodBusinessId;
    private int deliveryPersonnelId; 

    //this constructor would be used if reading in from db
    public FoodOrder(int foodOrderId, String orderStatus, int customerId, int foodBusinessId, int deliveryPersonnelId) {
        this.foodOrderId = foodOrderId;
        this.orderStatus = orderStatus;
        this.customerId = customerId;
        this.foodBusinessId = foodBusinessId;
        this.deliveryPersonnelId = deliveryPersonnelId;
    }

    //this constructor would be used if placing a new order
    public FoodOrder(String orderStatus, int customerId, int foodBusinessId) {
        this.orderStatus = orderStatus;
        this.customerId = customerId;
        this.foodBusinessId = foodBusinessId;
        this.deliveryPersonnelId = 0;
    }

    public int getFoodOrderId() { return foodOrderId; }
    public String getOrderStatus() { return orderStatus; }
    public int getCustomerId() { return customerId; }
    public int getFoodBusinessId() { return foodBusinessId; }
    public int getDeliveryPersonnelId() { return deliveryPersonnelId; }
    public boolean isAssigned() { return deliveryPersonnelId != 0; }

    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public void setDeliveryPersonnelId(int personnelId) { this.deliveryPersonnelId = personnelId; }

    @Override
    public String toString() {
        return "FoodOrder{id=" + foodOrderId + ", status=" + orderStatus + "}";
    }
}