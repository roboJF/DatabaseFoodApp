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

        // title label
        JLabel titleLabel = new JLabel("Customer Dashboard");
        loadCustomerName(titleLabel);

        // logout button
        JButton logoutButton = new JButton("Logout");

        // top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // orders panel
        ordersPanel = new CustomerOrdersPanel(customerId);

        // browse panel
        CustomerBrowsePanel browsePanel = new CustomerBrowsePanel(customerId, () -> {
            ordersPanel.loadOrders();
        });

        // info panel
        CustomerInfoPanel infoPanel = new CustomerInfoPanel(customerId);

        // tabbed pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Browse Restaurants", browsePanel);
        tabs.addTab("My Orders", ordersPanel);
        tabs.addTab("My Info", infoPanel);

        // main layout
        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        // logout action
        logoutButton.addActionListener(e -> {
            mainFrame.showLoginPanel();
        });
    }

    // load customer name
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