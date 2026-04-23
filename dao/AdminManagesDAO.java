package dao;

import model.Customer;
import model.FoodBusiness;
import model.DeliveryPersonnel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//i decided to put this all into one DAO since splitting it into 3 seemed pointless
public class AdminManagesDAO {

    //----------CUSTOMERS----------

    public void assignAdminToCustomer(int adminId, int customerId) throws SQLException {
        String sql = "INSERT INTO admin_manages_customer (admin_id, customer_id) VALUES (?, ?)";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, adminId);
        ps.setInt(2, customerId);
        ps.executeUpdate();
    }

    public void removeAdminFromCustomer(int adminId, int customerId) throws SQLException {
        String sql = "DELETE FROM admin_manages_customer WHERE admin_id = ? AND customer_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, adminId);
        ps.setInt(2, customerId);
        ps.executeUpdate();
    }

    public List<Customer> getCustomersByAdmin(int adminId) throws SQLException {
        String sql = """
                SELECT c.* FROM customer c
                JOIN admin_manages_customer amc ON c.customer_id = amc.customer_id
                WHERE amc.admin_id = ?
                """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, adminId);
        ResultSet rs = ps.executeQuery();
        List<Customer> customers = new ArrayList<>();
        while (rs.next()) {
            customers.add(new Customer(
                rs.getInt("customer_id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("contact_info"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password")
            ));
        }
        return customers;
    }

    //----------FOOD BUSINESSES----------

    public void assignAdminToBusiness(int adminId, int businessId) throws SQLException {
        String sql = "INSERT INTO admin_manages_business (admin_id, food_business_id) VALUES (?, ?)";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, adminId);
        ps.setInt(2, businessId);
        ps.executeUpdate();
    }

    public void removeAdminFromBusiness(int adminId, int businessId) throws SQLException {
        String sql = "DELETE FROM admin_manages_business WHERE admin_id = ? AND food_business_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, adminId);
        ps.setInt(2, businessId);
        ps.executeUpdate();
    }

    public List<FoodBusiness> getBusinessesByAdmin(int adminId) throws SQLException {
        String sql = """
                SELECT fb.* FROM food_business fb
                JOIN admin_manages_business amb ON fb.food_business_id = amb.food_business_id
                WHERE amb.admin_id = ?
                """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, adminId);
        ResultSet rs = ps.executeQuery();
        List<FoodBusiness> businesses = new ArrayList<>();
        while (rs.next()) {
            businesses.add(new FoodBusiness(
                rs.getInt("food_business_id"),
                rs.getString("name"),
                rs.getString("location"),
                rs.getString("contact_info"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password")
            ));
        }
        return businesses;
    }

    //----------DELIVERY PERSONNEL----------
    
    public void assignAdminToDelivery(int adminId, int personnelId) throws SQLException {
        String sql = "INSERT INTO admin_manages_delivery (admin_id, delivery_personnel_id) VALUES (?, ?)";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, adminId);
        ps.setInt(2, personnelId);
        ps.executeUpdate();
    }

    public void removeAdminFromDelivery(int adminId, int personnelId) throws SQLException {
        String sql = "DELETE FROM admin_manages_delivery WHERE admin_id = ? AND delivery_personnel_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, adminId);
        ps.setInt(2, personnelId);
        ps.executeUpdate();
    }

    public List<DeliveryPersonnel> getDeliveryByAdmin(int adminId) throws SQLException {
        String sql = """
                SELECT dp.* FROM delivery_personnel dp
                JOIN admin_manages_delivery amd ON dp.delivery_personnel_id = amd.delivery_personnel_id
                WHERE amd.admin_id = ?
                """;
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, adminId);
        ResultSet rs = ps.executeQuery();
        List<DeliveryPersonnel> personnel = new ArrayList<>();
        while (rs.next()) {
            personnel.add(new DeliveryPersonnel(
                rs.getInt("delivery_personnel_id"),
                rs.getString("name"),
                rs.getString("contact_info"),
                rs.getString("vehicle_details"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password")
            ));
        }
        return personnel;
    }
}
