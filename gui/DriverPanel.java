package gui;

import gui.MainFrame;
import dao.FoodOrderDAO;
import model.FoodOrder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DriverPanel extends JPanel {

    private int driverId;
    private MainFrame mainFrame;

    private JTable ordersTable;
    private DefaultTableModel model;

    public DriverPanel(MainFrame mainFrame, int driverId) {
        this.mainFrame = mainFrame;
        this.driverId = driverId;

        setLayout(new BorderLayout(10,10));

        JLabel title = new JLabel("Driver Dashboard");
        JButton logout = new JButton("Logout");

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);
        top.add(logout, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Order ID", "Status", "Assigned"}, 0
        );

        ordersTable = new JTable(model);

        add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel();

        JButton refresh = new JButton("Refresh");
        JButton takeOrder = new JButton("Take Order");
        JButton markDelivered = new JButton("Mark Delivered");

        buttons.add(refresh);
        buttons.add(takeOrder);
        buttons.add(markDelivered);

        add(buttons, BorderLayout.SOUTH);

        refresh.addActionListener(e -> loadOrders());

        takeOrder.addActionListener(e -> takeOrder());

        markDelivered.addActionListener(e -> markDelivered());

        logout.addActionListener(e -> mainFrame.showLoginPanel());

        loadOrders();
    }

    private void loadOrders() {
        try {
            model.setRowCount(0);

            FoodOrderDAO dao = new FoodOrderDAO();

            List<FoodOrder> unassigned = dao.getUnassignedOrders();
            List<FoodOrder> mine = dao.getByDeliveryPersonnel(driverId);

            for (FoodOrder o : unassigned) {
                model.addRow(new Object[]{
                        o.getFoodOrderId(),
                        o.getOrderStatus(),
                        "No"
                });
            }

            for (FoodOrder o : mine) {
                model.addRow(new Object[]{
                        o.getFoodOrderId(),
                        o.getOrderStatus(),
                        "Yes"
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void takeOrder() {
        int row = ordersTable.getSelectedRow();
        if (row == -1) return;

        int orderId = (int) model.getValueAt(row, 0);
        String assigned = (String) model.getValueAt(row, 2);

        if (assigned.equals("Yes")) {
            JOptionPane.showMessageDialog(this, "Already assigned.");
            return;
        }

        try {
            new FoodOrderDAO().assignDeliveryPersonnel(orderId, driverId);
            loadOrders();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void markDelivered() {
        int row = ordersTable.getSelectedRow();
        if (row == -1) return;

        int orderId = (int) model.getValueAt(row, 0);

        try {
            new FoodOrderDAO().updateStatus(orderId, "DELIVERED");
            loadOrders();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
