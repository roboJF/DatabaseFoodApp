package dao;

import model.Administrator;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdministratorDAO {

    public void insert(Administrator admin) throws SQLException {
        String sql = "INSERT INTO administrator (username, email, password) VALUES (?, ?, ?)";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, admin.getUsername());
        ps.setString(2, admin.getEmail());
        ps.setString(3, admin.getPassword());
        ps.executeUpdate();
    }

    public Administrator getById(int adminId) throws SQLException {
        String sql = "SELECT * FROM administrator WHERE admin_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, adminId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    public Administrator getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM administrator WHERE username = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    public List<Administrator> getAll() throws SQLException {
        String sql = "SELECT * FROM administrator";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Administrator> admins = new ArrayList<>();
        while (rs.next()) admins.add(mapRow(rs));
        return admins;
    }

    public void update(Administrator admin) throws SQLException {
        String sql = "UPDATE administrator SET username = ?, email = ?, password = ? WHERE admin_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, admin.getUsername());
        ps.setString(2, admin.getEmail());
        ps.setString(3, admin.getPassword());
        ps.setInt(4, admin.getAdminId());
        ps.executeUpdate();
    }

    public void delete(int adminId) throws SQLException {
        String sql = "DELETE FROM administrator WHERE admin_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, adminId);
        ps.executeUpdate();
    }

    //helper method to turn resultsets into admin objects
    private Administrator mapRow(ResultSet rs) throws SQLException {
        return new Administrator(
            rs.getInt("admin_id"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password")
        );
    }
}