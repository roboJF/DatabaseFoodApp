package gui.restaurant;

import gui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class RestaurantPanel extends JPanel {

    private int restaurantId;
    private MainFrame mainFrame;

    public RestaurantPanel(int restaurantId, MainFrame mainFrame) {
        this.restaurantId = restaurantId;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // title label
        JLabel titleLabel = new JLabel("Restaurant Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // logout button
        JButton logoutButton = new JButton("Logout");

        // top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // tabbed pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Orders", new RestaurantOrdersPanel(restaurantId));
        tabs.addTab("Menu", new RestaurantMenuPanel(restaurantId));
        tabs.addTab("My Info", new RestaurantInfoPanel(restaurantId));

        // main layout
        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        // logout action
        logoutButton.addActionListener(e -> {
            mainFrame.showLoginPanel();
        });
    }
}