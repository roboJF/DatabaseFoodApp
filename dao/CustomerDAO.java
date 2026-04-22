package dao;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public void insert(Customer customer) throws SQLException {
        String sql = """
                INSERT INTO customer (name, address, contact_info, username, email, password)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, customer.getName());
        ps.setString(2, customer.getAddress());
        ps.setString(3, customer.getContactInfo());
        ps.setString(4, customer.getUsername());
        ps.setString(5, customer.getEmail());
        ps.setString(6, customer.getPassword());
        ps.executeUpdate();
    }

    public Customer getById(int customerId) throws SQLException {
        String sql = "SELECT * FROM customer WHERE customer_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    public Customer getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM customer WHERE username = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    public List<Customer> getAll() throws SQLException {
        String sql = "SELECT * FROM customer";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Customer> customers = new ArrayList<>();
        while (rs.next()) customers.add(mapRow(rs));
        return customers;
    }

    public void update(Customer customer) throws SQLException {
        String sql = """
                UPDATE customer
                SET name = ?, address = ?, contact_info = ?, username = ?, email = ?, password = ?
                WHERE customer_id = ?
                """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, customer.getName());
        ps.setString(2, customer.getAddress());
        ps.setString(3, customer.getContactInfo());
        ps.setString(4, customer.getUsername());
        ps.setString(5, customer.getEmail());
        ps.setString(6, customer.getPassword());
        ps.setInt(7, customer.getCustomerId());
        ps.executeUpdate();
    }

    public void delete(int customerId) throws SQLException {
        String sql = "DELETE FROM customer WHERE customer_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, customerId);
        ps.executeUpdate();
    }

    //helper to put a resultset into a customer object
    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("customer_id"),
            rs.getString("name"),
            rs.getString("address"),
            rs.getString("contact_info"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password")
        );
    }
}
