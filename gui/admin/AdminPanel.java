package gui.admin;

import gui.MainFrame;
import dao.AdministratorDAO;
import model.Administrator;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class AdminPanel extends JPanel {

    private MainFrame mainFrame;
    private int adminId;

    private AdministratorDAO adminDAO = new AdministratorDAO();

    public AdminPanel(MainFrame mainFrame, int adminId) {
        this.mainFrame = mainFrame;
        this.adminId = adminId;

        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel welcomeLabel = new JLabel("  Logged in as: " + getAdminUsername());

        JButton logoutButton = new JButton("Logout");

        header.add(title, BorderLayout.WEST);
        header.add(welcomeLabel, BorderLayout.CENTER);
        header.add(logoutButton, BorderLayout.EAST);

        // logout action
        logoutButton.addActionListener(e -> {
            mainFrame.showLoginPanel();
        });

        return header;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Admins", new AdminAdminPanel(adminId));
        tabs.addTab("Customers", new AdminCustomerPanel(adminId));
        tabs.addTab("Food Businesses", new AdminBusinessPanel(adminId));
        tabs.addTab("Delivery Personnel", new AdminDriverPanel(adminId));

        return tabs;
    }

    // get logged in admin username
    private String getAdminUsername() {
        try {
            Administrator admin = adminDAO.getById(adminId);
            return admin != null ? admin.getUsername() : "Unknown";

        } catch (SQLException e) {
            return "Unknown";
        }
    }
}
