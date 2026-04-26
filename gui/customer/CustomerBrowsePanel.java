package gui.customer;

import dao.FoodBusinessDAO;
import dao.MenuItemDAO;
import dao.FoodOrderDAO;
import dao.OrderItemDAO;

import model.FoodBusiness;
import model.MenuItem;
import model.FoodOrder;
import model.OrderItem;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerBrowsePanel extends JPanel {

    private int customerId;
    private Runnable onOrderPlaced;

    private JTable restaurantTable;
    private JTable menuTable;
    private JTable cartTable;
    private JLabel totalLabel;

    private DefaultTableModel restaurantModel;
    private DefaultTableModel menuModel;
    private DefaultTableModel cartModel;

    private List<FoodBusiness> restaurantList = new ArrayList<>();
    private List<MenuItem> menuItemList = new ArrayList<>();
    private List<CartItem> cartItems = new ArrayList<>();

    private int selectedBusinessId = -1;

    private JTextField addQuantityField;

    public CustomerBrowsePanel(int customerId, Runnable onOrderPlaced) {
        this.customerId = customerId;
        this.onOrderPlaced = onOrderPlaced;

        setLayout(new BorderLayout(10, 10));

        // table models
        restaurantModel = new DefaultTableModel(new String[] { "Restaurant", "Location", "Phone" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        menuModel = new DefaultTableModel(new String[] { "Item", "Description", "Price" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartModel = new DefaultTableModel(new String[] { "Item", "Price", "Quantity", "Subtotal" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        // tables
        restaurantTable = new JTable(restaurantModel);
        menuTable = new JTable(menuModel);
        cartTable = new JTable(cartModel);

        restaurantTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // restaurant panel
        JPanel restaurantPanel = new JPanel(new BorderLayout(5, 5));
        restaurantPanel.add(new JLabel("Restaurants"), BorderLayout.NORTH);
        restaurantPanel.add(new JScrollPane(restaurantTable), BorderLayout.CENTER);

        // menu panel
        JPanel menuPanel = new JPanel(new BorderLayout(5, 5));
        menuPanel.add(new JLabel("Menu"), BorderLayout.NORTH);
        menuPanel.add(new JScrollPane(menuTable), BorderLayout.CENTER);

        // add to cart panel
        JPanel addToCartPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addQuantityField = new JTextField("1", 4);
        JButton addToCartButton = new JButton("Add to Cart");

        addToCartPanel.add(new JLabel("Quantity:"));
        addToCartPanel.add(addQuantityField);
        addToCartPanel.add(addToCartButton);

        menuPanel.add(addToCartPanel, BorderLayout.SOUTH);

        // top browse panel
        JPanel topBrowsePanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topBrowsePanel.add(restaurantPanel);
        topBrowsePanel.add(menuPanel);

        // cart panel
        JPanel cartPanel = new JPanel(new BorderLayout(5, 5));
        cartPanel.add(new JLabel("Cart"), BorderLayout.NORTH);
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // cart buttons
        JPanel cartButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton removeButton = new JButton("Remove Selected");
        JButton placeOrderButton = new JButton("Place Order");

        cartButtonPanel.add(removeButton);
        cartButtonPanel.add(placeOrderButton);

        // cart total
        totalLabel = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.add(totalLabel, BorderLayout.EAST);

        // cart footer
        JPanel cartFooterPanel = new JPanel(new BorderLayout());
        cartFooterPanel.add(totalPanel, BorderLayout.NORTH);
        cartFooterPanel.add(cartButtonPanel, BorderLayout.SOUTH);

        cartPanel.add(cartFooterPanel, BorderLayout.SOUTH);

        // main layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topBrowsePanel, cartPanel);
        splitPane.setResizeWeight(0.65);

        add(splitPane, BorderLayout.CENTER);

        // restaurant selection
        restaurantTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && restaurantTable.getSelectedRow() != -1) {
                int row = restaurantTable.getSelectedRow();
                FoodBusiness selected = restaurantList.get(row);

                int newBusinessId = selected.getFoodBusinessId();

                // clear cart if they choose a different restaurant
                if (!cartItems.isEmpty() && selectedBusinessId != -1 && newBusinessId != selectedBusinessId) {
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "Switching restaurants will clear your cart. Continue?",
                            "Clear Cart?",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm != JOptionPane.YES_OPTION) {
                        int oldRow = getRestaurantRowById(selectedBusinessId);
                        restaurantTable.setRowSelectionInterval(oldRow, oldRow);
                        return;
                    }

                    cartItems.clear();
                    refreshCartTable();
                }

                selectedBusinessId = newBusinessId;
                loadMenuItems();
            }
        });

        // add to cart button
        addToCartButton.addActionListener(e -> addSelectedItemToCart());

        // cart quantity edit
        cartModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE &&
                    e.getColumn() == 2 &&
                    e.getFirstRow() >= 0 &&
                    e.getFirstRow() < cartItems.size()) {
                updateCartQuantity(e.getFirstRow());
            }
        });

        // remove item button
        removeButton.addActionListener(e -> removeSelectedCartItem());

        // place order button
        placeOrderButton.addActionListener(e -> placeOrder());

        loadRestaurants();
    }

    // load restaurants
    private void loadRestaurants() {
        try {
            restaurantList = new FoodBusinessDAO().getAll();
            restaurantModel.setRowCount(0);

            for (FoodBusiness b : restaurantList) {
                restaurantModel.addRow(new Object[] {
                        b.getName(),
                        b.getLocation(),
                        b.getContactInfo()
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading restaurants.");
        }
    }

    // load menu items
    private void loadMenuItems() {
        try {
            menuItemList = new MenuItemDAO().getAvailableByBusiness(selectedBusinessId);
            menuModel.setRowCount(0);

            for (MenuItem item : menuItemList) {
                menuModel.addRow(new Object[] {
                        item.getName(),
                        item.getDescription(),
                        String.format("$%.2f", item.getPrice())
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading menu items.");
        }
    }

    // add item to cart
    private void addSelectedItemToCart() {
        int row = menuTable.getSelectedRow();

        if (selectedBusinessId == -1) {
            JOptionPane.showMessageDialog(this, "Select a restaurant first.");
            return;
        }

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a menu item first.");
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(addQuantityField.getText().trim());

            if (quantity < 1) {
                JOptionPane.showMessageDialog(this, "Quantity must be at least 1.");
                return;
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a number.");
            return;
        }

        MenuItem selectedItem = menuItemList.get(row);

        for (CartItem cartItem : cartItems) {
            if (cartItem.getMenuItem().getMenuItemId() == selectedItem.getMenuItemId()) {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                refreshCartTable();
                addQuantityField.setText("1");
                return;
            }
        }

        cartItems.add(new CartItem(selectedItem, quantity));
        refreshCartTable();
        addQuantityField.setText("1");
    }

    // update cart quantity
    private void updateCartQuantity(int row) {
        try {
            int quantity = Integer.parseInt(cartModel.getValueAt(row, 2).toString());
            // verify minimum quantity
            if (quantity < 1) {
                JOptionPane.showMessageDialog(this, "Quantity must be at least 1.");
                cartItems.get(row).setQuantity(1);
            } else {
                cartItems.get(row).setQuantity(quantity);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a number.");
            cartItems.get(row).setQuantity(1);
        }

        refreshCartTable();
    }

    // remove item from cart
    private void removeSelectedCartItem() {
        int row = cartTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item to remove.");
            return;
        }

        cartItems.remove(row);
        refreshCartTable();

        if (cartItems.isEmpty()) {
            selectedBusinessId = -1;
            menuModel.setRowCount(0);
            restaurantTable.clearSelection();
        }
    }

    // refresh cart table and total
    private void refreshCartTable() {
        cartModel.setRowCount(0);

        double total = 0;
        // calculate total price
        for (CartItem cartItem : cartItems) {
            MenuItem item = cartItem.getMenuItem();
            int quantity = cartItem.getQuantity();
            double subtotal = item.getPrice() * quantity;

            total += subtotal;

            // format total field
            cartModel.addRow(new Object[] {
                    item.getName(),
                    String.format("$%.2f", item.getPrice()),
                    quantity,
                    String.format("$%.2f", subtotal)
            });
        }

        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    // place order
    private void placeOrder() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.");
            return;
        }
        // add the items to a new pending order
        try {
            FoodOrder order = new FoodOrder("PENDING", customerId, selectedBusinessId);
            int orderId = new FoodOrderDAO().insert(order);

            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem cartItem : cartItems) {
                orderItems.add(new OrderItem(
                        cartItem.getQuantity(),
                        orderId,
                        cartItem.getMenuItem().getMenuItemId()));
            }

            new OrderItemDAO().insertBatch(orderItems);

            JOptionPane.showMessageDialog(this, "Order placed.");
            
            // reset the cart
            cartItems.clear();
            refreshCartTable();
            selectedBusinessId = -1;
            menuModel.setRowCount(0);
            restaurantTable.clearSelection();

            if (onOrderPlaced != null) {
                onOrderPlaced.run();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error placing order.");
        }
    }

    // helper to find restaurant row
    private int getRestaurantRowById(int businessId) {
        for (int i = 0; i < restaurantList.size(); i++) {
            if (restaurantList.get(i).getFoodBusinessId() == businessId) {
                return i;
            }
        }
        return 0;
    }

    private static class CartItem {
        private MenuItem menuItem;
        private int quantity;

        public CartItem(MenuItem menuItem, int quantity) {
            this.menuItem = menuItem;
            this.quantity = quantity;
        }

        public MenuItem getMenuItem() {
            return menuItem;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}