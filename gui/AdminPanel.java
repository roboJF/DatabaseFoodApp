package gui;

import java.awt.*;
import javax.swing.*;

public class AdminPanel extends JPanel {

    private MainFrame mainFrame;

    public AdminPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Admin Dashboard");

        JButton logout = new JButton("Logout");

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);
        top.add(logout, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Customers", new JLabel("Customer management here"));
        tabs.add("Restaurants", new JLabel("Restaurant management here"));
        tabs.add("Drivers", new JLabel("Driver management here"));
        tabs.add("Orders", new JLabel("Order management here"));

        add(tabs, BorderLayout.CENTER);

        logout.addActionListener(e -> mainFrame.showLoginPanel());
    }
}
