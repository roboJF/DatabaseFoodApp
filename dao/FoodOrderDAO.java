package dao;

import model.FoodOrder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodOrderDAO {

    // i set this to return the id since if someone is making an order, i assume you
    // would also want the id immediately
    public int insert(FoodOrder order) throws SQLException {
        String sql = """
                INSERT INTO food_order (order_status, customer_id, food_business_id)
                VALUES (?, ?, ?)
                """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, order.getOrderStatus());
        ps.setInt(2, order.getCustomerId());
        ps.setInt(3, order.getFoodBusinessId());
        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next())
            return keys.getInt(1);
        throw new SQLException("Failed to retrieve generated order ID.");
    }

    public FoodOrder getById(int foodOrderId) throws SQLException {
        String sql = "SELECT * FROM food_order WHERE food_order_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, foodOrderId);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return mapRow(rs);
        return null;
    }

    public List<FoodOrder> getByCustomer(int customerId) throws SQLException {
        String sql = "SELECT * FROM food_order WHERE customer_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();
        List<FoodOrder> orders = new ArrayList<>();
        while (rs.next())
            orders.add(mapRow(rs));
        return orders;
    }

    public List<FoodOrder> getByBusiness(int foodBusinessId) throws SQLException {
        String sql = "SELECT * FROM food_order WHERE food_business_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, foodBusinessId);
        ResultSet rs = ps.executeQuery();
        List<FoodOrder> orders = new ArrayList<>();
        while (rs.next())
            orders.add(mapRow(rs));
        return orders;
    }

    public List<FoodOrder> getByDeliveryPersonnel(int personnelId) throws SQLException {
        String sql = "SELECT * FROM food_order WHERE delivery_personnel_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, personnelId);
        ResultSet rs = ps.executeQuery();
        List<FoodOrder> orders = new ArrayList<>();
        while (rs.next())
            orders.add(mapRow(rs));
        return orders;
    }

    public List<FoodOrder> getUnassignedOrders() throws SQLException {
        List<FoodOrder> orders = new ArrayList<>();

        String sql = "SELECT * FROM food_order WHERE delivery_personnel_id IS NULL";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            orders.add(mapRow(rs));
        }

        return orders;
    }

    public List<FoodOrder> getAll() throws SQLException {
        String sql = "SELECT * FROM food_order";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<FoodOrder> orders = new ArrayList<>();
        while (rs.next())
            orders.add(mapRow(rs));
        return orders;
    }

    public void updateStatus(int foodOrderId, String newStatus) throws SQLException {
        String sql = "UPDATE food_order SET order_status = ? WHERE food_order_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, newStatus);
        ps.setInt(2, foodOrderId);
        ps.executeUpdate();
    }

    public void assignDeliveryPersonnel(int foodOrderId, int personnelId) throws SQLException {
        String sql = "UPDATE food_order SET delivery_personnel_id = ? WHERE food_order_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, personnelId);
        ps.setInt(2, foodOrderId);
        ps.executeUpdate();
    }

    // unassign delivery personnel
    public void unassignDeliveryPersonnel(int orderId) throws SQLException {
        String sql = "UPDATE food_order SET delivery_personnel_id = NULL WHERE food_order_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, orderId);
        ps.executeUpdate();
    }

    public void delete(int foodOrderId) throws SQLException {
        String sql = "DELETE FROM food_order WHERE food_order_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, foodOrderId);
        ps.executeUpdate();
    }

    // private FoodOrder mapRow(ResultSet rs) throws SQLException {
    // int personnelId = rs.getInt("delivery_personnel_id");
    // if (rs.wasNull()) personnelId = 0;

    // return new FoodOrder(
    // rs.getInt("food_order_id"),
    // rs.getString("order_status"),
    // rs.getInt("customer_id"),
    // rs.getInt("food_business_id"),
    // personnelId
    // );
    // }

    private FoodOrder mapRow(ResultSet rs) throws SQLException {
        Integer personnelId = (Integer) rs.getObject("delivery_personnel_id");

        return new FoodOrder(
                rs.getInt("food_order_id"),
                rs.getString("order_status"),
                rs.getInt("customer_id"),
                rs.getInt("food_business_id"),
                personnelId == null ? null : personnelId);
    }

    public String getOrderItemsText(int orderId) throws SQLException {
        String sql = """
                SELECT mi.name, oi.quantity
                FROM order_item oi
                JOIN menu_item mi ON oi.menu_item_id = mi.menu_item_id
                WHERE oi.food_order_id = ?
                """;

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, orderId);

        ResultSet rs = ps.executeQuery();
        StringBuilder items = new StringBuilder();

        while (rs.next()) {
            if (items.length() > 0) {
                items.append(", ");
            }

            items.append(rs.getString("name"))
                    .append(" x")
                    .append(rs.getInt("quantity"));
        }

        return items.toString();
    }

    public double getOrderTotal(int orderId) throws SQLException {
        String sql = """
                SELECT SUM(mi.price * oi.quantity) AS total
                FROM order_item oi
                JOIN menu_item mi ON oi.menu_item_id = mi.menu_item_id
                WHERE oi.food_order_id = ?
                """;

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, orderId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getDouble("total");
        }

        return 0.0;
    }

    public String getDeliveryDriverName(int orderId) throws SQLException {
        String sql = """
                SELECT dp.username
                FROM food_order fo
                LEFT JOIN delivery_personnel dp
                    ON fo.delivery_personnel_id = dp.delivery_personnel_id
                WHERE fo.food_order_id = ?
                """;

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, orderId);

        ResultSet rs = ps.executeQuery();

        if (rs.next() && rs.getString("username") != null) {
            return rs.getString("username");
        }

        return "Not assigned";
    }
}
