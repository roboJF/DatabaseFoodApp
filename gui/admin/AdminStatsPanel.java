package gui.admin;

import dao.FoodOrderDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminStatsPanel extends JPanel {

    private FoodOrderDAO orderDAO = new FoodOrderDAO();

    private DefaultTableModel orderCountModel;
    private DefaultTableModel revenueModel;
    private DefaultTableModel avgModel;

    public AdminStatsPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buildPanel();
        loadStats();
    }

    private void buildPanel() {
        //orders per customer
        JLabel orderCountLabel = new JLabel("Total Orders Per Customer");
        orderCountLabel.setFont(new Font("Arial", Font.BOLD, 13));

        String[] orderCountCols = {"Customer Name", "Total Orders"};
        orderCountModel = new DefaultTableModel(orderCountCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable orderCountTable = new JTable(orderCountModel);
        orderCountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel orderCountPanel = new JPanel(new BorderLayout(5, 5));
        orderCountPanel.add(orderCountLabel, BorderLayout.NORTH);
        orderCountPanel.add(new JScrollPane(orderCountTable), BorderLayout.CENTER);

        //business revenue stuff
        JLabel revenueLabel = new JLabel("Total Revenue Per Business (Delivered Orders, Highest First)");
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 13));

        String[] revenueCols = {"Business Name", "Total Revenue ($)"};
        revenueModel = new DefaultTableModel(revenueCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable revenueTable = new JTable(revenueModel);
        revenueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel revenuePanel = new JPanel(new BorderLayout(5, 5));
        revenuePanel.add(revenueLabel, BorderLayout.NORTH);
        revenuePanel.add(new JScrollPane(revenueTable), BorderLayout.CENTER);

        //avg order value per business
        JLabel avgLabel = new JLabel("Average Order Value Per Business");
        avgLabel.setFont(new Font("Arial", Font.BOLD, 13));

        String[] avgCols = {"Business Name", "Avg Order Value ($)"};
        avgModel = new DefaultTableModel(avgCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable avgTable = new JTable(avgModel);
        avgTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel avgPanel = new JPanel(new BorderLayout(5, 5));
        avgPanel.add(avgLabel, BorderLayout.NORTH);
        avgPanel.add(new JScrollPane(avgTable), BorderLayout.CENTER);

        //putting the tables in a grid
        JPanel tablesPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        tablesPanel.add(orderCountPanel);
        tablesPanel.add(revenuePanel);
        tablesPanel.add(avgPanel);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadStats());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(refreshButton);

        add(tablesPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadStats() {
        loadOrderCounts();
        loadRevenue();
        loadAvgOrderValues();
    }

    //load the amount of orders each customer has made
    private void loadOrderCounts() {
        orderCountModel.setRowCount(0);
        try {
            ResultSet rs = orderDAO.getOrderCountPerCustomer();
            while (rs.next()) {
                orderCountModel.addRow(new Object[]{
                    rs.getString("customer_name"),
                    rs.getInt("total_orders")
                });
            }
        } catch (SQLException e) {
            showError("Error loading order counts: " + e.getMessage());
        }
    }

    //load revenue per business
    private void loadRevenue() {
        revenueModel.setRowCount(0);
        try {
            ResultSet rs = orderDAO.getRevenuePerBusiness();
            while (rs.next()) {
                revenueModel.addRow(new Object[]{
                    rs.getString("business_name"),
                    rs.getDouble("total_revenue")
                });
            }
        } catch (SQLException e) {
            showError("Error loading revenue stats: " + e.getMessage());
        }
    }

    //load average order values (hard to tell by the function name, right?)
    private void loadAvgOrderValues() {
        avgModel.setRowCount(0);
        try {
            ResultSet rs = orderDAO.getAvgOrderValuePerBusiness();
            while (rs.next()) {
                avgModel.addRow(new Object[]{
                    rs.getString("business_name"),
                    rs.getDouble("avg_order_value")
                });
            }
        } catch (SQLException e) {
            showError("Error loading avg order values: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
