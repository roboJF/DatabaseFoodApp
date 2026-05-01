package gui.create;

import gui.MainFrame;
import javax.swing.*;
import java.awt.*;

public class CreateAccountPanel extends JPanel {

    public CreateAccountPanel(MainFrame mainFrame){

        setLayout(new GridBagLayout());

        // form panel
        JPanel formPanel = new JPanel(new GridLayout(6,1,10,10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // title label
        JLabel titleLabel = new JLabel("Choose Account Type", SwingConstants.CENTER);

        // buttons
        JButton customerButton = new JButton("Customer");
        JButton restaurantButton = new JButton("Restaurant");
        JButton driverButton = new JButton("Driver");
        JButton adminButton = new JButton("Admin");
        JButton backButton = new JButton("Back");

        // form layout
        formPanel.add(titleLabel);
        formPanel.add(customerButton);
        formPanel.add(restaurantButton);
        formPanel.add(driverButton);
        formPanel.add(adminButton);
        formPanel.add(backButton);

        // main layout
        add(formPanel);

        // navigation actions
        // customer panel
        customerButton.addActionListener(e -> {
            mainFrame.showCreateCustomerPanel();
        });
        // restaurant panel
        restaurantButton.addActionListener(e -> {
            mainFrame.showCreateRestaurantPanel();
        });
        // delivery driver panel
        driverButton.addActionListener(e -> {
            mainFrame.showCreateDriverPanel();
        });
        // administrator panel
        adminButton.addActionListener(e -> {
            mainFrame.showCreateAdminPanel();
        });
        // go back to login
        backButton.addActionListener(e -> {
            mainFrame.showLoginPanel();
        });
    }
}