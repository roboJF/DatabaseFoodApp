package gui;

import javax.swing.*;
import java.awt.*;

import gui.customer.CustomerPanel;
import gui.create.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("Food Delivery App");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new LoginPanel(this), "login");

        add(mainPanel);

        showLoginPanel();
    }

    public void showLoginPanel() {
        cardLayout.show(mainPanel, "login");
    }

    public void showCreateAccountPanel() {
        showPanel("createAccount", new CreateAccountPanel(this));
    }

    public void showCreateCustomerPanel() {
        showPanel("createCustomer", new CreateCustomerPanel(this));
    }

    public void showCreateRestaurantPanel() {
        showPanel("createRestaurant", new CreateRestaurantPanel(this));
    }

    public void showCreateDriverPanel() {
        showPanel("createDriver", new CreateDriverPanel(this));
    }

    public void showCreateAdminPanel() {
        showPanel("createAdmin", new CreateAdminPanel(this));
    }

    public void showCustomerPanel(int customerId) {
        showPanel("customer", new CustomerPanel(this, customerId));
    }

    public void showRestaurantPanel(int restaurantId) {
        JOptionPane.showMessageDialog(this, "restaurant panel coming next. ID: " + restaurantId);
    }

    public void showDriverPanel(int driverId) {
        JOptionPane.showMessageDialog(this, "driver panel coming next. ID: " + driverId);
    }

    public void showAdminPanel(int adminId) {
        JOptionPane.showMessageDialog(this, "admin panel coming next. ID: " + adminId);
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