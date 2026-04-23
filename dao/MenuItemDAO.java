package dao;

import model.MenuItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO {

    public void insert(MenuItem item) throws SQLException {
        String sql = """
                INSERT INTO menu_item (name, description, price, availability, food_business_id)
                VALUES (?, ?, ?, ?, ?)
                """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, item.getName());
        ps.setString(2, item.getDescription());
        ps.setDouble(3, item.getPrice());
        ps.setBoolean(4, item.isAvailable());
        ps.setInt(5, item.getFoodBusinessId());
        ps.executeUpdate();
    }

    public MenuItem getById(int menuItemId) throws SQLException {
        String sql = "SELECT * FROM menu_item WHERE menu_item_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, menuItemId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    //used to just get menu items from a business
    public List<MenuItem> getByBusiness(int foodBusinessId) throws SQLException {
        String sql = "SELECT * FROM menu_item WHERE food_business_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, foodBusinessId);
        ResultSet rs = ps.executeQuery();
        List<MenuItem> items = new ArrayList<>();
        while (rs.next()) items.add(mapRow(rs));
        return items;
    }

    //used to get AVALIABLE menu items from a business
    //i kept getting confused when writing these so i feel the need to make the distinction clear (i might just be stupid)
    public List<MenuItem> getAvailableByBusiness(int foodBusinessId) throws SQLException {
        String sql = "SELECT * FROM menu_item WHERE food_business_id = ? AND availability = TRUE";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, foodBusinessId);
        ResultSet rs = ps.executeQuery();
        List<MenuItem> items = new ArrayList<>();
        while (rs.next()) items.add(mapRow(rs));
        return items;
    }

    public void update(MenuItem item) throws SQLException {
        String sql = """
                UPDATE menu_item
                SET name = ?, description = ?, price = ?, availability = ?
                WHERE menu_item_id = ?
                """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, item.getName());
        ps.setString(2, item.getDescription());
        ps.setDouble(3, item.getPrice());
        ps.setBoolean(4, item.isAvailable());
        ps.setInt(5, item.getMenuItemId());
        ps.executeUpdate();
    }

    public void updateAvailability(int menuItemId, boolean available) throws SQLException {
        String sql = "UPDATE menu_item SET availability = ? WHERE menu_item_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setBoolean(1, available);
        ps.setInt(2, menuItemId);
        ps.executeUpdate();
    }

    public void delete(int menuItemId) throws SQLException {
        String sql = "DELETE FROM menu_item WHERE menu_item_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, menuItemId);
        ps.executeUpdate();
    }

    private MenuItem mapRow(ResultSet rs) throws SQLException {
        return new MenuItem(
            rs.getInt("menu_item_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getDouble("price"),
            rs.getBoolean("availability"),
            rs.getInt("food_business_id")
        );
    }
}
