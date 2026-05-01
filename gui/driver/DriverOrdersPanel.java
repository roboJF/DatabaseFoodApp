package gui.driver;

import dao.FoodOrderDAO;
import dao.FoodBusinessDAO;
import dao.CustomerDAO;
import model.FoodOrder;
import model.FoodBusiness;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DriverOrdersPanel extends JPanel {

    private int driverId;

    private JTable availableTable;
    private JTable activeTable;
    private JTable deliveredTable;

    private DefaultTableModel availableModel;
    private DefaultTableModel activeModel;
    private DefaultTableModel deliveredModel;

    public DriverOrdersPanel(int driverId) {
        this.driverId = driverId;

        setLayout(new BorderLayout(10, 10));

        buildPanel();
        loadOrders();
    }

    // build panel
    private void buildPanel() {
        availableModel = buildOrderModel();
        activeModel = buildOrderModel();
        deliveredModel = buildOrderModel();

        availableTable = new JTable(availableModel);
        activeTable = new JTable(activeModel);
        deliveredTable = new JTable(deliveredModel);

        applyStatusRenderer(availableTable);
        applyStatusRenderer(activeTable);
        applyStatusRenderer(deliveredTable);

        JPanel availablePanel = buildAvailablePanel();
        JPanel activePanel = buildActivePanel();
        JPanel deliveredPanel = buildDeliveredPanel();

        JSplitPane lowerSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                activePanel,
                deliveredPanel);
        lowerSplit.setResizeWeight(0.5);

        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                availablePanel,
                lowerSplit);
        mainSplit.setResizeWeight(0.33);

        add(mainSplit, BorderLayout.CENTER);
    }

    // build available orders panel
    private JPanel buildAvailablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        panel.add(new JLabel("Available Orders"), BorderLayout.NORTH);
        panel.add(new JScrollPane(availableTable), BorderLayout.CENTER);

        JButton takeOrderButton = new JButton("Take Order");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(takeOrderButton);
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        takeOrderButton.addActionListener(e -> takeOrder());
        refreshButton.addActionListener(e -> loadOrders());

        return panel;
    }

    // build active orders panel
    private JPanel buildActivePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        panel.add(new JLabel("My Active Orders"), BorderLayout.NORTH);
        panel.add(new JScrollPane(activeTable), BorderLayout.CENTER);

        JButton unassignButton = new JButton("Unassign Order");
        JButton markPickedUpButton = new JButton("Mark Picked Up");
        JButton markDeliveredButton = new JButton("Mark Delivered");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(unassignButton);
        buttonPanel.add(markPickedUpButton);
        buttonPanel.add(markDeliveredButton);
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        unassignButton.addActionListener(e -> unassignOrder());
        markPickedUpButton.addActionListener(e -> markPickedUp());
        markDeliveredButton.addActionListener(e -> markDelivered());
        refreshButton.addActionListener(e -> loadOrders());

        return panel;
    }

    // build delivered orders panel
    private JPanel buildDeliveredPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        panel.add(new JLabel("Delivered Orders"), BorderLayout.NORTH);
        panel.add(new JScrollPane(deliveredTable), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadOrders());

        return panel;
    }

    // build table model
    private DefaultTableModel buildOrderModel() {
        return new DefaultTableModel(
                new String[]{
                        "Order ID",
                        "Restaurant",
                        "Restaurant Location",
                        "Customer",
                        "Delivery Address",
                        "Status"
                },
                0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // display formatted status while keeping raw status in table model
    private void applyStatusRenderer(JTable table) {
        table.getColumnModel().getColumn(5).setCellRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(formatStatus(value == null ? "" : value.toString()));
            label.setFont(tbl.getFont());
            label.setOpaque(true);

            if (isSelected) {
                label.setBackground(tbl.getSelectionBackground());
                label.setForeground(tbl.getSelectionForeground());
            } else {
                label.setBackground(tbl.getBackground());
                label.setForeground(tbl.getForeground());
            }

            return label;
        });
    }

    // format the status string displayed to user
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
                result.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1));
            }

            if (i < words.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    // load orders
    private void loadOrders() {
        try {
            availableModel.setRowCount(0);
            activeModel.setRowCount(0);
            deliveredModel.setRowCount(0);

            FoodOrderDAO orderDAO = new FoodOrderDAO();
            FoodBusinessDAO businessDAO = new FoodBusinessDAO();
            CustomerDAO customerDAO = new CustomerDAO();

            List<FoodOrder> availableOrders = orderDAO.getUnassignedOrders();
            List<FoodOrder> myOrders = orderDAO.getByDeliveryPersonnel(driverId);

            for (FoodOrder order : availableOrders) {
                addOrderRow(availableModel, order, businessDAO, customerDAO);
            }

            for (FoodOrder order : myOrders) {
                if (order.getOrderStatus().equals("DELIVERED")) {
                    addOrderRow(deliveredModel, order, businessDAO, customerDAO);
                } else {
                    addOrderRow(activeModel, order, businessDAO, customerDAO);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders.");
        }
    }

    // add order row
    private void addOrderRow(DefaultTableModel model, FoodOrder order,
            FoodBusinessDAO businessDAO, CustomerDAO customerDAO) throws Exception {

        FoodBusiness business = businessDAO.getById(order.getFoodBusinessId());
        Customer customer = customerDAO.getById(order.getCustomerId());

        model.addRow(new Object[]{
                order.getFoodOrderId(),
                business != null ? business.getName() : "Unknown",
                business != null ? business.getLocation() : "Unknown",
                customer != null ? customer.getFullName() : "Unknown",
                customer != null ? customer.getAddress() : "Unknown",
                order.getOrderStatus()
        });
    }

    // take order
    private void takeOrder() {
        int row = availableTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an available order first.");
            return;
        }

        int orderId = (int) availableModel.getValueAt(row, 0);

        try {
            new FoodOrderDAO().assignDeliveryPersonnel(orderId, driverId);
            loadOrders();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error taking order.");
        }
    }

    // unassign order
    private void unassignOrder() {
        int row = activeTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select one of your active orders first.");
            return;
        }

        int orderId = (int) activeModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to unassign order #" + orderId + "?",
                "Confirm Unassign",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            new FoodOrderDAO().unassignDeliveryPersonnel(orderId);
            loadOrders();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error unassigning order.");
        }
    }

    // mark picked up
    private void markPickedUp() {
        int row = activeTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select one of your active orders first.");
            return;
        }

        int orderId = (int) activeModel.getValueAt(row, 0);
        String status = (String) activeModel.getValueAt(row, 5);

        if (!status.equals("ASSIGNED")) {
            JOptionPane.showMessageDialog(this, "Only assigned orders can be marked as picked up.");
            return;
        }

        try {
            new FoodOrderDAO().updateStatus(orderId, "OUT_FOR_DELIVERY");
            loadOrders();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating order.");
        }
    }

    // mark delivered
    private void markDelivered() {
        int row = activeTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select one of your active orders first.");
            return;
        }

        int orderId = (int) activeModel.getValueAt(row, 0);
        String status = (String) activeModel.getValueAt(row, 5);

        if (!status.equals("OUT_FOR_DELIVERY")) {
            JOptionPane.showMessageDialog(this, "Only out-for-delivery orders can be marked as delivered.");
            return;
        }

        try {
            new FoodOrderDAO().updateStatus(orderId, "DELIVERED");
            loadOrders();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating order.");
        }
    }
}