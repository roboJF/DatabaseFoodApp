package dao;

import model.DeliveryPersonnel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeliveryPersonnelDAO {

    public void insert(DeliveryPersonnel personnel) throws SQLException {
        String sql = """
                INSERT INTO delivery_personnel
                (first_name, last_name, contact_info, vehicle_details, username, email, password)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                        """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, personnel.getFirstName());
        ps.setString(2, personnel.getLastName());
        ps.setString(3, personnel.getContactInfo());
        ps.setString(4, personnel.getVehicleDetails());
        ps.setString(5, personnel.getUsername());
        ps.setString(6, personnel.getEmail());
        ps.setString(7, personnel.getPassword());
        ps.executeUpdate();
    }

    public DeliveryPersonnel getById(int personnelId) throws SQLException {
        String sql = "SELECT * FROM delivery_personnel WHERE delivery_personnel_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, personnelId);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return mapRow(rs);
        return null;
    }

    public DeliveryPersonnel getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM delivery_personnel WHERE username = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return mapRow(rs);
        return null;
    }

    public List<DeliveryPersonnel> getAll() throws SQLException {
        String sql = "SELECT * FROM delivery_personnel";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<DeliveryPersonnel> personnelList = new ArrayList<>();
        while (rs.next())
            personnelList.add(mapRow(rs));
        return personnelList;
    }

    public void update(DeliveryPersonnel personnel) throws SQLException {
        String sql = """
                UPDATE delivery_personnel
                SET first_name = ?, last_name = ?, contact_info = ?, vehicle_details = ?, username = ?, email = ?, password = ?
                WHERE delivery_personnel_id = ?
                """;

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, personnel.getFirstName());
        ps.setString(2, personnel.getLastName());
        ps.setString(3, personnel.getContactInfo());
        ps.setString(4, personnel.getVehicleDetails());
        ps.setString(5, personnel.getUsername());
        ps.setString(6, personnel.getEmail());
        ps.setString(7, personnel.getPassword());
        ps.setInt(8, personnel.getDeliveryPersonnelId());
        ps.executeUpdate();
    }

    public void delete(int personnelId) throws SQLException {
        String sql = "DELETE FROM delivery_personnel WHERE delivery_personnel_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, personnelId);
        ps.executeUpdate();
    }

    private DeliveryPersonnel mapRow(ResultSet rs) throws SQLException {
        return new DeliveryPersonnel(
                rs.getInt("delivery_personnel_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("contact_info"),
                rs.getString("vehicle_details"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password"));
    }
}
