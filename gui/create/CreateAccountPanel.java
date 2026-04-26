package gui.create;
import gui.MainFrame;
import javax.swing.*;
import java.awt.*;
import javax.swing.*;
import java.awt.*;

public class CreateAccountPanel extends JPanel {

    public CreateAccountPanel(MainFrame mainFrame){

        setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridLayout(6,1,10,10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel titleLabel = new JLabel("choose account type", SwingConstants.CENTER);

        JButton customerButton = new JButton("customer");
        JButton restaurantButton = new JButton("restaurant");
        JButton driverButton = new JButton("driver");
        JButton adminButton = new JButton("admin");
        JButton backButton = new JButton("back");

        formPanel.add(titleLabel);
        formPanel.add(customerButton);
        formPanel.add(restaurantButton);
        formPanel.add(driverButton);
        formPanel.add(adminButton);
        formPanel.add(backButton);

        add(formPanel);

        customerButton.addActionListener(e -> {
            mainFrame.showCreateCustomerPanel();
        });

        restaurantButton.addActionListener(e -> {
            mainFrame.showCreateRestaurantPanel();
        });

        driverButton.addActionListener(e -> {
            mainFrame.showCreateDriverPanel();
        });

        adminButton.addActionListener(e -> {
            mainFrame.showCreateAdminPanel();
        });

        backButton.addActionListener(e -> {
            mainFrame.showLoginPanel();
        });
    }
}