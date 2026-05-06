package gui.admin;

import dao.CustomerDAO;
import dao.FoodBusinessDAO;
import dao.FoodOrderDAO;

import model.Customer;
import model.FoodBusiness;
import model.FoodOrder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminOrdersPanel extends JPanel {
    // admin can view and manage all orders
    // combines restaurant + driver order controls
    private JTable orderTable;
    private DefaultTableModel orderModel;

    // store orders in same order as table rows
    private List<FoodOrder> orderList = new ArrayList<>();

    public AdminOrdersPanel() {
        setLayout(new BorderLayout(10, 10));
        buildPanel();
        loadOrders();
    }

    // build table and action buttons
    private void buildPanel() {
        orderModel = new DefaultTableModel(
                new String[] {
                        "Order ID",
                        "Restaurant",
                        "Customer",
                        "Driver ID",
                        "Status"
                },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(orderModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton cancelButton = new JButton("Cancel Order");
        JButton pendingButton = new JButton("Mark Pending");
        JButton preparingButton = new JButton("Mark Preparing");
        JButton readyButton = new JButton("Mark Ready");
        JButton unassignButton = new JButton("Unassign Driver");
        JButton pickupButton = new JButton("Mark Picked Up");
        JButton deliveredButton = new JButton("Mark Delivered");

        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        buttonPanel.add(cancelButton);
        buttonPanel.add(pendingButton);
        buttonPanel.add(preparingButton);
        buttonPanel.add(readyButton);
        buttonPanel.add(unassignButton);
        buttonPanel.add(pickupButton);
        buttonPanel.add(deliveredButton);
        buttonPanel.add(refreshButton);

        add(new JScrollPane(orderTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // actions
        cancelButton.addActionListener(e -> confirmCancelOrder());
        pendingButton.addActionListener(e -> updateSelectedOrderStatus("PENDING"));
        preparingButton.addActionListener(e -> updateSelectedOrderStatus("PREPARING"));
        readyButton.addActionListener(e -> updateSelectedOrderStatus("READY"));
        pickupButton.addActionListener(e -> updateSelectedOrderStatus("OUT_FOR_DELIVERY"));
        deliveredButton.addActionListener(e -> updateSelectedOrderStatus("DELIVERED"));
        unassignButton.addActionListener(e -> unassignDriver());
        refreshButton.addActionListener(e -> loadOrders());
    }

    // load all orders
    private void loadOrders() {
        try {
            orderList = new FoodOrderDAO().getAll();
            orderModel.setRowCount(0);
            FoodBusinessDAO businessDAO = new FoodBusinessDAO();
            CustomerDAO customerDAO = new CustomerDAO();
            for (FoodOrder order : orderList) {
                FoodBusiness business = businessDAO.getById(order.getFoodBusinessId());
                Customer customer = customerDAO.getById(order.getCustomerId());

                orderModel.addRow(new Object[] {
                        order.getFoodOrderId(),
                        business != null
                                ? business.getName()
                                : "Unknown",
                        customer != null
                                ? customer.getFullName()
                                : "Unknown",
                        order.getDeliveryPersonnelId() == null
                                ? "Not Assigned"
                                : order.getDeliveryPersonnelId(),
                        formatStatus(order.getOrderStatus())
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading orders.");
        }
    }

    // format database status into readable text
    private String formatStatus(String status) {
        if (status == null) {
            return "";
        }
        String[] words = status.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (word.equals("for")) {
                result.append("for");
            } else {
                result.append(
                        word.substring(0, 1).toUpperCase()
                                + word.substring(1));
            }

            if (i < words.length - 1) {
                result.append(" ");
            }
        }
        return result.toString();
    }

    // update selected order status
    // follows same workflow rules as restaurant + driver panels
    private void updateSelectedOrderStatus(String status) {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select an order first.");
            return;
        }
        FoodOrder selectedOrder = orderList.get(row);
        String currentStatus = selectedOrder.getOrderStatus();
        // cancelled orders cannot be modified

        if (currentStatus.equals("CANCELLED")) {
            JOptionPane.showMessageDialog(
                    this,
                    "This order has already been cancelled.");
            return;
        }

        // delivered orders cannot be modified
        if (currentStatus.equals("DELIVERED")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Delivered orders cannot be modified.");
            return;
        }

        // cancellation flow:
        // PENDING -> CANCELLED
        if (status.equals("CANCELLED")
                && !currentStatus.equals("PENDING")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Only pending orders can be cancelled.");

            return;
        }
        // restaurant flow:
        // PENDING -> PREPARING -> READY
        if (status.equals("PREPARING")
                && !currentStatus.equals("PENDING")
                && !currentStatus.equals("READY")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Order must be pending or ready before it can be marked preparing.");

            return;
        }
        if (status.equals("READY")
                && !currentStatus.equals("PREPARING")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Order must be preparing before it can be marked ready.");

            return;
        }
        if (status.equals("PENDING")
                && !currentStatus.equals("PREPARING")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Only preparing orders can be moved back to pending.");

            return;
        }
        // driver flow:
        // ASSIGNED -> OUT_FOR_DELIVERY -> DELIVERED
        if (status.equals("OUT_FOR_DELIVERY")
                && !currentStatus.equals("ASSIGNED")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Only assigned orders can be marked picked up.");

            return;
        }
        if (status.equals("DELIVERED")
                && !currentStatus.equals("OUT_FOR_DELIVERY")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Only out-for-delivery orders can be marked delivered.");

            return;
        }
        try {
            new FoodOrderDAO().updateStatus(
                    selectedOrder.getFoodOrderId(),
                    status);

            loadOrders();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error updating order status.");
        }
    }

    // confirm before cancelling selected order
    private void confirmCancelOrder() {
        int row = orderTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select an order first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Do you want to cancel this order?",
                "Confirm Cancel",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        updateSelectedOrderStatus("CANCELLED");
    }

    // unassign driver from order
    // only allowed before pickup
    private void unassignDriver() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select an order first.");
            return;
        }
        FoodOrder selectedOrder = orderList.get(row);
        if (selectedOrder.getOrderStatus().equals("DELIVERED")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Delivered orders cannot be unassigned.");
            return;
        }

        if (selectedOrder.getOrderStatus().equals("CANCELLED")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cancelled orders cannot be unassigned.");
            return;
        }

        if (!selectedOrder.getOrderStatus().equals("ASSIGNED")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Only assigned orders can be unassigned.");
            return;
        }
        try {
            new FoodOrderDAO().unassignDeliveryPersonnel(
                    selectedOrder.getFoodOrderId());

            loadOrders();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error unassigning driver.");
        }
    }
}