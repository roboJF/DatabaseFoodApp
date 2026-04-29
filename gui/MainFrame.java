package gui;

import gui.create.*;
import gui.customer.CustomerPanel;
import gui.restaurant.RestaurantPanel;
import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("Foodie - Your Food Delivery App");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new LoginPanel(this), "Login");

        add(mainPanel);

        showLoginPanel();
    }

    public void showLoginPanel() {
        cardLayout.show(mainPanel, "Login");
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showCreateAccountPanel() {
        showPanel("Create Account", new CreateAccountPanel(this));
    }

    public void showCreateCustomerPanel() {
        showPanel("Create Customer", new CreateCustomerPanel(this));
    }

    public void showCreateRestaurantPanel() {
        showPanel("Create Restaurant", new CreateRestaurantPanel(this));
    }

    public void showCreateDriverPanel() {
        showPanel("Create Driver", new CreateDriverPanel(this));
    }

    public void showCreateAdminPanel() {
        showPanel("Create Admin", new CreateAdminPanel(this));
    }

    public void showCustomerPanel(int customerId) {
        showPanel("Customer", new CustomerPanel(this, customerId));
    }

    public void showRestaurantPanel(int restaurantId) {
        showPanel("Restaurant", new RestaurantPanel(restaurantId, this));
    }

    public void showDriverPanel(int driverId) {
        showPanel("Driver", new DriverPanel(this, driverId));
    }

    public void showAdminPanel(int adminId) {
        showPanel("Admin", new AdminPanel(this));
    }

    private void showPanel(String name, JPanel panel) {
        mainPanel.add(panel, name);
        cardLayout.show(mainPanel, name);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}