package dao;

import model.OrderItem;
import model.MenuItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {

    public void insert(OrderItem item) throws SQLException {
        String sql = """
                INSERT INTO order_item (quantity, food_order_id, menu_item_id)
                VALUES (?, ?, ?)
                """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, item.getQuantity());
        ps.setInt(2, item.getFoodOrderId());
        ps.setInt(3, item.getMenuItemId());
        ps.executeUpdate();
    }

    //used to insert multiple order items at once, thought it might be useful so i threw it in
    public void insertBatch(List<OrderItem> items) throws SQLException {
        String sql = """
                INSERT INTO order_item (quantity, food_order_id, menu_item_id)
                VALUES (?, ?, ?)
                """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        for (OrderItem item : items) {
            ps.setInt(1, item.getQuantity());
            ps.setInt(2, item.getFoodOrderId());
            ps.setInt(3, item.getMenuItemId());
            ps.addBatch();
        }
        ps.executeBatch();
    }

    public List<OrderItem> getByOrder(int foodOrderId) throws SQLException {
        String sql = "SELECT * FROM order_item WHERE food_order_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, foodOrderId);
        ResultSet rs = ps.executeQuery();
        List<OrderItem> items = new ArrayList<>();
        while (rs.next()) items.add(mapRow(rs));
        return items;
    }

    //returns the menu items associated with a given food order
    public List<MenuItem> getMenuItemsByOrder(int foodOrderId) throws SQLException {
        String sql = """
                SELECT mi.*, oi.quantity FROM menu_item mi
                JOIN order_item oi ON mi.menu_item_id = oi.menu_item_id
                WHERE oi.food_order_id = ?
                """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, foodOrderId);
        ResultSet rs = ps.executeQuery();
        List<MenuItem> items = new ArrayList<>();
        while (rs.next()) items.add(new MenuItem(
            rs.getInt("menu_item_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getDouble("price"),
            rs.getBoolean("availability"),
            rs.getInt("food_business_id")
        ));
        return items;
    }

    public void delete(int orderItemId) throws SQLException {
        String sql = "DELETE FROM order_item WHERE order_item_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, orderItemId);
        ps.executeUpdate();
    }

    //can delete all of the order items associated with an order, like if its being cancelled or something
    public void deleteByOrder(int foodOrderId) throws SQLException {
        String sql = "DELETE FROM order_item WHERE food_order_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, foodOrderId);
        ps.executeUpdate();
    }

    private OrderItem mapRow(ResultSet rs) throws SQLException {
        return new OrderItem(
            rs.getInt("order_item_id"),
            rs.getInt("quantity"),
            rs.getInt("food_order_id"),
            rs.getInt("menu_item_id")
        );
    }
}
