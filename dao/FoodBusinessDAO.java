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
        String sql = "DELETE FROM food_business WHERE food_business_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, businessId);
        ps.executeUpdate();
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
