package gui.restaurant;

import dao.FoodOrderDAO;
import dao.CustomerDAO;
import model.FoodOrder;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantOrdersPanel extends JPanel {

    private int restaurantId;

    private JTable orderTable;
    private DefaultTableModel orderModel;

    private List<FoodOrder> orderList = new ArrayList<>();

    public RestaurantOrdersPanel(int restaurantId) {
        this.restaurantId = restaurantId;

        setLayout(new BorderLayout(10, 10));

        buildPanel();
        loadOrders();
    }

    // build panel
    private void buildPanel() {
        orderModel = new DefaultTableModel(
                new String[]{
                        "Order ID",
                        "Customer ID",
                        "Customer Name",
                        "Items",
                        "Total Price",
                        "Delivery Driver",
                        "Status"
                },
                0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(orderModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton pendingButton = new JButton("Mark Pending");
        JButton readyButton = new JButton("Mark Ready for Pickup");
        JButton refreshButton = new JButton("Refresh Orders");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(pendingButton);
        buttonPanel.add(readyButton);
        buttonPanel.add(refreshButton);

        add(new JScrollPane(orderTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pendingButton.addActionListener(e -> updateSelectedOrderStatus("PENDING"));
        readyButton.addActionListener(e -> updateSelectedOrderStatus("READY"));
        refreshButton.addActionListener(e -> loadOrders());
    }

    // load orders
    private void loadOrders() {
        try {
            orderList = new FoodOrderDAO().getByBusiness(restaurantId);
            orderModel.setRowCount(0);

            FoodOrderDAO orderDAO = new FoodOrderDAO();
            CustomerDAO customerDAO = new CustomerDAO();

            for (FoodOrder order : orderList) {
                String items = orderDAO.getOrderItemsText(order.getFoodOrderId());
                double total = orderDAO.getOrderTotal(order.getFoodOrderId());
                String driver = orderDAO.getDeliveryDriverName(order.getFoodOrderId());

                Customer customer = customerDAO.getById(order.getCustomerId());
                String customerName = customer != null ? customer.getFullName() : "Unknown";

                orderModel.addRow(new Object[]{
                        order.getFoodOrderId(),
                        order.getCustomerId(),
                        customerName,
                        items,
                        String.format("$%.2f", total),
                        driver,
                        order.getOrderStatus()
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders.");
        }
    }

    // update selected order status (RESTRICTED)
    private void updateSelectedOrderStatus(String status) {
        int row = orderTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return;
        }

        FoodOrder selectedOrder = orderList.get(row);
        String currentStatus = selectedOrder.getOrderStatus();

        // Only allow changes if still in restaurant control
        if (!currentStatus.equals("PENDING") && !currentStatus.equals("READY")) {
            JOptionPane.showMessageDialog(
                    this,
                    "This order has already been picked up by a driver.\nThe restaurant can no longer change its status."
            );
            return;
        }

        try {
            new FoodOrderDAO().updateStatus(selectedOrder.getFoodOrderId(), status);
            loadOrders();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating order status.");
        }
    }
}