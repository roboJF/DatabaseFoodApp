package gui.customer;

import dao.FoodBusinessDAO;
import dao.FoodOrderDAO;
import dao.MenuItemDAO;
import dao.OrderItemDAO;

import model.FoodBusiness;
import model.FoodOrder;
import model.MenuItem;
import model.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerOrdersPanel extends JPanel {

    private int customerId;

    private JTable ordersTable;
    private DefaultTableModel ordersModel;

    public CustomerOrdersPanel(int customerId) {
        this.customerId = customerId;

        setLayout(new BorderLayout(10, 10));

        ordersModel = new DefaultTableModel(
                new String[]{"Order ID", "Restaurant", "Status", "Items", "Total"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ordersTable = new JTable(ordersModel);
        ordersTable.setRowHeight(24);

        JButton refreshButton = new JButton("Refresh Orders");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("My Orders"), BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        refreshButton.addActionListener(e -> loadOrders());

        loadOrders();
    }

    public void loadOrders() {
        try {
            ordersModel.setRowCount(0);

            List<FoodOrder> orders = new FoodOrderDAO().getByCustomer(customerId);

            for (FoodOrder order : orders) {
                FoodBusiness business = new FoodBusinessDAO().getById(order.getFoodBusinessId());
                List<OrderItem> orderItems = new OrderItemDAO().getByOrder(order.getFoodOrderId());

                StringBuilder itemsText = new StringBuilder();
                double total = 0;

                for (OrderItem orderItem : orderItems) {
                    MenuItem menuItem = new MenuItemDAO().getById(orderItem.getMenuItemId());

                    if (menuItem != null) {
                        if (itemsText.length() > 0) {
                            itemsText.append(", ");
                        }

                        itemsText.append(menuItem.getName())
                                .append(" x")
                                .append(orderItem.getQuantity());

                        total += menuItem.getPrice() * orderItem.getQuantity();
                    }
                }

                ordersModel.addRow(new Object[]{
                        order.getFoodOrderId(),
                        business != null ? business.getName() : "Unknown",
                        order.getOrderStatus(),
                        itemsText.toString(),
                        String.format("$%.2f", total)
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders.");
        }
    }
}