package gui.driver;

import gui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class DriverPanel extends JPanel {

    private int driverId;
    private MainFrame mainFrame;

    public DriverPanel(MainFrame mainFrame, int driverId) {
        this.mainFrame = mainFrame;
        this.driverId = driverId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // title label
        JLabel titleLabel = new JLabel("Driver Dashboard");

        // logout button
        JButton logoutButton = new JButton("Logout");

        // top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // tabbed pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Orders", new DriverOrdersPanel(driverId));
        tabs.addTab("My Info", new DriverInfoPanel(driverId));

        // main layout
        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        // logout action
        logoutButton.addActionListener(e -> {
            mainFrame.showLoginPanel();
        });
    }
}