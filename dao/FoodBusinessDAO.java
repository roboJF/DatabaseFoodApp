package dao;

import model.FoodBusiness;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodBusinessDAO {

    public void insert(FoodBusiness business) throws SQLException {
        String sql = """
                INSERT INTO food_business (name, location, contact_info, username, email, password)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, business.getName());
        ps.setString(2, business.getLocation());
        ps.setString(3, business.getContactInfo());
        ps.setString(4, business.getUsername());
        ps.setString(5, business.getEmail());
        ps.setString(6, business.getPassword());
        ps.executeUpdate();
    }

    public FoodBusiness getById(int businessId) throws SQLException {
        String sql = "SELECT * FROM food_business WHERE food_business_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, businessId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    public FoodBusiness getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM food_business WHERE username = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    public List<FoodBusiness> getAll() throws SQLException {
        String sql = "SELECT * FROM food_business";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<FoodBusiness> businesses = new ArrayList<>();
        while (rs.next()) businesses.add(mapRow(rs));
        return businesses;
    }

    public void update(FoodBusiness business) throws SQLException {
        String sql = """
                UPDATE food_business
                SET name = ?, location = ?, contact_info = ?, username = ?, email = ?, password = ?
                WHERE food_business_id = ?
                """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, business.getName());
        ps.setString(2, business.getLocation());
        ps.setString(3, business.getContactInfo());
        ps.setString(4, business.getUsername());
        ps.setString(5, business.getEmail());
        ps.setString(6, business.getPassword());
        ps.setInt(7, business.getFoodBusinessId());
        ps.executeUpdate();
    }

    public void delete(int businessId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            String deleteOrderItems = "DELETE oi FROM order_item oi " +
                                  "JOIN food_order fo ON oi.food_order_id = fo.food_order_id " +
                                  "WHERE fo.food_business_id = ?";
            PreparedStatement ps1 = conn.prepareStatement(deleteOrderItems);
            ps1.setInt(1, businessId);
            ps1.executeUpdate();

            String deleteOrders = "DELETE FROM food_order WHERE food_business_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(deleteOrders);
            ps2.setInt(1, businessId);
            ps2.executeUpdate();

            String deleteMenuItems = "DELETE FROM menu_item WHERE food_business_id = ?";
            PreparedStatement ps3 = conn.prepareStatement(deleteMenuItems);
            ps3.setInt(1, businessId);
            ps3.executeUpdate();

            String deleteManages = "DELETE FROM admin_manages_business WHERE food_business_id = ?";
            PreparedStatement ps4 = conn.prepareStatement(deleteManages);
            ps4.setInt(1, businessId);
            ps4.executeUpdate();

            String deleteBusiness = "DELETE FROM food_business WHERE food_business_id = ?";
            PreparedStatement ps5 = conn.prepareStatement(deleteBusiness);
            ps5.setInt(1, businessId);
            ps5.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
    }
}

    //yet another helper for putting resultsets into POJO's, man im noticing a trend here!
    private FoodBusiness mapRow(ResultSet rs) throws SQLException {
        return new FoodBusiness(
            rs.getInt("food_business_id"),
            rs.getString("name"),
            rs.getString("location"),
            rs.getString("contact_info"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password")
        );
    }
}
