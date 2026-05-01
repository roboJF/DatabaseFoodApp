package model;

public class FoodOrder {
    private int foodOrderId;
    private String orderStatus;
    private int customerId;
    private int foodBusinessId;
    private Integer deliveryPersonnelId;

    // this constructor would be used if reading in from db
    public FoodOrder(int foodOrderId, String orderStatus, int customerId, int foodBusinessId,
            Integer deliveryPersonnelId) {
        this.foodOrderId = foodOrderId;
        this.orderStatus = orderStatus;
        this.customerId = customerId;
        this.foodBusinessId = foodBusinessId;
        this.deliveryPersonnelId = deliveryPersonnelId;
    }

    // this constructor would be used if placing a new order
    public FoodOrder(String orderStatus, int customerId, int foodBusinessId) {
        this.orderStatus = orderStatus;
        this.customerId = customerId;
        this.foodBusinessId = foodBusinessId;
        this.deliveryPersonnelId = null;
    }

    public int getFoodOrderId() {
        return foodOrderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getFoodBusinessId() {
        return foodBusinessId;
    }

    public Integer getDeliveryPersonnelId() {
        return deliveryPersonnelId;
    }

    public boolean isAssigned() {
        return deliveryPersonnelId != null;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setDeliveryPersonnelId(Integer personnelId) {
        this.deliveryPersonnelId = personnelId;
    }

    @Override
    public String toString() {
        return "FoodOrder{id=" + foodOrderId + ", status=" + orderStatus + "}";
    }
}