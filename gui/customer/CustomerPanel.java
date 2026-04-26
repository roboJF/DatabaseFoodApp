package gui.customer;
import gui.MainFrame;
import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import java.awt.*;

public class CustomerPanel extends JPanel {

    private int customerId;
    private MainFrame mainFrame;

    private CustomerOrdersPanel ordersPanel;

    public CustomerPanel(MainFrame mainFrame, int customerId) {
        this.mainFrame = mainFrame;
        this.customerId = customerId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Customer Dashboard");
        loadCustomerName(titleLabel);

        JButton logoutButton = new JButton("Logout");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        ordersPanel = new CustomerOrdersPanel(customerId);

        CustomerBrowsePanel browsePanel = new CustomerBrowsePanel(customerId, () -> {
            ordersPanel.loadOrders();
        });

        CustomerInfoPanel infoPanel = new CustomerInfoPanel(customerId);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Browse Restaurants", browsePanel);
        tabs.addTab("My Orders", ordersPanel);
        tabs.addTab("My Info", infoPanel);

        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        logoutButton.addActionListener(e -> {
            mainFrame.showLoginPanel();
        });
    }

    private void loadCustomerName(JLabel titleLabel) {
        try {
            Customer customer = new CustomerDAO().getById(customerId);

            if (customer != null) {
                titleLabel.setText("Customer Dashboard - " + customer.getFullName());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            titleLabel.setText("Customer Dashboard");
        }
    }
}