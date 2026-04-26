package gui;

import javax.swing.*;
import java.awt.*;

import gui.customer.CustomerPanel;
import gui.create.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    public MainFrame() {
        setTitle("Foodie - Your Food Delivery App");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // main panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // login panel
        mainPanel.add(new LoginPanel(this), "Login");

        // frame layout
        add(mainPanel);

        showLoginPanel();
    }

    // show login panel
    public void showLoginPanel() {
        cardLayout.show(mainPanel, "Login");
    }

    // show create account panel
    public void showCreateAccountPanel() {
        showPanel("Create Account", new CreateAccountPanel(this));
    }

    // show create customer panel
    public void showCreateCustomerPanel() {
        showPanel("Create Customer", new CreateCustomerPanel(this));
    }

    // show create restaurant panel
    public void showCreateRestaurantPanel() {
        showPanel("Create Restaurant", new CreateRestaurantPanel(this));
    }

    // show create driver panel
    public void showCreateDriverPanel() {
        showPanel("Create Driver", new CreateDriverPanel(this));
    }

    // show create admin panel
    public void showCreateAdminPanel() {
        showPanel("Create Admin", new CreateAdminPanel(this));
    }

    // show customer panel
    public void showCustomerPanel(int customerId) {
        showPanel("Customer", new CustomerPanel(this, customerId));
    }

    // show restaurant panel
    public void showRestaurantPanel(int restaurantId) {
        JOptionPane.showMessageDialog(this, "Restaurant panel coming next. ID: " + restaurantId);
    }

    // show driver panel
    public void showDriverPanel(int driverId) {
        JOptionPane.showMessageDialog(this, "Driver panel coming next. ID: " + driverId);
    }

    // show admin panel
    public void showAdminPanel(int adminId) {
        JOptionPane.showMessageDialog(this, "Admin panel coming next. ID: " + adminId);
    }

    // helper to switch panels
    private void showPanel(String name, JPanel panel) {
        mainPanel.remove(panel);
        mainPanel.add(panel, name);
        cardLayout.show(mainPanel, name);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // app entry point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}