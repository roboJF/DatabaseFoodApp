package dao;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public void insert(Customer customer) throws SQLException {
        String sql = """
                INSERT INTO customer (first_name, last_name, address, contact_info, username, email, password)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, customer.getFirstName());
        ps.setString(2, customer.getLastName());
        ps.setString(3, customer.getAddress());
        ps.setString(4, customer.getContactInfo());
        ps.setString(5, customer.getUsername());
        ps.setString(6, customer.getEmail());
        ps.setString(7, customer.getPassword());
        ps.executeUpdate();
    }

    public Customer getById(int customerId) throws SQLException {
        String sql = "SELECT * FROM customer WHERE customer_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return mapRow(rs);
        return null;
    }

    public Customer getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM customer WHERE username = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return mapRow(rs);
        return null;
    }

    public List<Customer> getAll() throws SQLException {
        String sql = "SELECT * FROM customer";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Customer> customers = new ArrayList<>();
        while (rs.next())
            customers.add(mapRow(rs));
        return customers;
    }

    public void update(Customer customer) throws SQLException {
        String sql = """
                UPDATE customer
                SET first_name = ?, last_name = ?, address = ?, contact_info = ?, username = ?, email = ?, password = ?
                WHERE customer_id = ?
                """;

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ps.setString(1, customer.getFirstName());
        ps.setString(2, customer.getLastName());
        ps.setString(3, customer.getAddress());
        ps.setString(4, customer.getContactInfo());
        ps.setString(5, customer.getUsername());
        ps.setString(6, customer.getEmail());
        ps.setString(7, customer.getPassword());
        ps.setInt(8, customer.getCustomerId());

        ps.executeUpdate();
    }

    public void delete(int customerId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            String deleteOrderItems = "DELETE oi FROM order_item oi " +
                                      "JOIN food_order fo ON oi.food_order_id = fo.food_order_id " +
                                      "WHERE fo.customer_id = ?";
            PreparedStatement ps1 = conn.prepareStatement(deleteOrderItems);
            ps1.setInt(1, customerId);
            ps1.executeUpdate();

            String deleteOrders = "DELETE FROM food_order WHERE customer_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(deleteOrders);
            ps2.setInt(1, customerId);
            ps2.executeUpdate();

            String deleteManages = "DELETE FROM admin_manages_customer WHERE customer_id = ?";
            PreparedStatement ps3 = conn.prepareStatement(deleteManages);
            ps3.setInt(1, customerId);
            ps3.executeUpdate();

            String deleteCustomer = "DELETE FROM customer WHERE customer_id = ?";
            PreparedStatement ps4 = conn.prepareStatement(deleteCustomer);
            ps4.setInt(1, customerId);
            ps4.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // helper to put a resultset into a customer object
    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("customer_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("address"),
                rs.getString("contact_info"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password"));
    }
}
