USE food_delivery;

-- =========================
-- SELECT (2)
-- =========================

-- 1. Show all customers
SELECT * FROM customer;

-- 2. Show all menu items
SELECT * FROM menu_item;

-- =========================
-- WHERE (Filtering) (2)
-- =========================

-- 1. Show only the PENDING orders
SELECT *
FROM food_order
WHERE order_status = 'PENDING';

-- 2. Show menu items that cost more than $6.00
SELECT *
FROM menu_item
WHERE price > 6.00;


-- =========================
-- JOIN (2)
-- =========================

-- 1. Orders with customer names
SELECT 
    fo.food_order_id,
    c.first_name,
    c.last_name,
    fo.order_status
FROM food_order fo
JOIN customer c ON fo.customer_id = c.customer_id;

-- 2. Orders with restaurant names
SELECT 
    fo.food_order_id,
    fb.name AS restaurant,
    fo.order_status
FROM food_order fo
JOIN food_business fb ON fo.food_business_id = fb.food_business_id;


-- =========================
-- ORDER BY (2)
-- =========================

-- 1. Menu items from cheapest to most expensive
SELECT *
FROM menu_item
ORDER BY price ASC;

-- 2. Customers sorted by last name
SELECT *
FROM customer
ORDER BY last_name;


-- =========================
-- GROUP BY (2)
-- =========================

-- 1. Count orders per status
SELECT 
    order_status,
    COUNT(*) AS total
FROM food_order
GROUP BY order_status;

-- 2. Count menu items per business
SELECT 
    food_business_id,
    COUNT(*) AS total_items
FROM menu_item
GROUP BY food_business_id;


-- =========================
-- UPDATE (2)
-- =========================

-- 1. Assign delivery person to order 1
UPDATE food_order
SET delivery_personnel_id = 1,
    order_status = 'ASSIGNED'
WHERE food_order_id = 1;

-- Show result
SELECT * FROM food_order WHERE food_order_id = 1;

-- 2. Change price of a menu item
UPDATE menu_item
SET price = 6.49
WHERE menu_item_id = 1;

-- Show result
SELECT * FROM menu_item WHERE menu_item_id = 1;


-- =========================
-- DELETE (2)
-- =========================

-- 1. Delete one order item
DELETE FROM order_item
WHERE order_item_id = 1;

-- Show result
SELECT * FROM order_item;

-- DELETE example 2: insert then delete a temporary menu item (had a few errors so had to be smart)
-- Did not want to use SET SQL_SAFE_UPDATES = 0; because of safety
INSERT INTO menu_item (name, description, price, availability, food_business_id)
VALUES ('Test Item', 'Temporary item for delete example', 1.99, TRUE, 1);

SELECT * FROM menu_item;

DELETE FROM menu_item
WHERE menu_item_id = 10;

SELECT * FROM menu_item;