package gui.admin;

import gui.MainFrame;
import dao.AdministratorDAO;
import model.Administrator;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class AdminPanel extends JPanel {

    private MainFrame mainFrame;    // main app window
    private int adminId;    // id of the admin

    private AdministratorDAO adminDAO = new AdministratorDAO(); // admin's data access object

    // panel constructor
    public AdminPanel(MainFrame mainFrame, int adminId) {
        this.mainFrame = mainFrame;
        this.adminId = adminId;

        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
    }

    // top ui pane
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        // add some spacing
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // title
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        
        // show current admin's userid
        JLabel welcomeLabel = new JLabel("  Logged in as: " + getAdminUsername());

        JButton logoutButton = new JButton("Logout");

        header.add(title, BorderLayout.WEST);
        header.add(welcomeLabel, BorderLayout.CENTER);
        header.add(logoutButton, BorderLayout.EAST);

        // logout back to main login screen
        logoutButton.addActionListener(e -> {
            mainFrame.showLoginPanel();
        });

        return header;
    }

    // builds set of tabs for admin subpanels
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Admins", new AdminAdminPanel(adminId));
        tabs.addTab("Customers", new AdminCustomerPanel(adminId));
        tabs.addTab("Food Businesses", new AdminBusinessPanel(adminId));
        tabs.addTab("Delivery Personnel", new AdminDriverPanel(adminId));
        tabs.addTab("Statistics", new AdminStatsPanel());

        return tabs;
    }

    // get current logged in admin username
    private String getAdminUsername() {
        try {
            Administrator admin = adminDAO.getById(adminId);
            return admin != null ? admin.getUsername() : "Unknown";

        } catch (SQLException e) {
            return "Unknown";
        }
    }
}
