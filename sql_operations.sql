-- select queries
SELECT * FROM food_business WHERE food_business_id = 1;

SELECT * FROM customer;

-- join queries
SELECT mi.*, oi.quantity FROM menu_item mi 
JOIN order_item oi ON mi.menu_item_id = oi.menu_item_id 
WHERE oi.food_order_id = 1;

SELECT mi.name, oi.quantity 
FROM order_item oi 
JOIN menu_item mi ON oi.menu_item_id = mi.menu_item_id 
WHERE oi.food_order_id = 2; 

-- update queries
UPDATE customer  
SET first_name = 'someone', last_name = 'cool', address = '159 cool st', contact_info = '1236547809', username = 'customer3', email = 'cool@cool.com', password = 'password'  
WHERE customer_id = 3; 

UPDATE delivery_personnel
SET first_name = 'someone', last_name = 'cooler', contact_info = '1236547809', vehicle_details = 'legs' , username = 'driver3', email = 'cool@cooler.com', password = 'password'
WHERE delivery_personnel_id = 3;

-- delete queries
DELETE oi FROM order_item oi
JOIN food_order fo ON oi.food_order_id = fo.food_order_id
WHERE fo.customer_id = 1;
DELETE FROM food_order WHERE customer_id = 1;
DELETE FROM admin_manages_customer WHERE customer_id = 1;
DELETE FROM customer WHERE customer_id = 1;

UPDATE food_order
SET 
	delivery_personnel_id = NULL,
	order_status = CASE 
		WHEN order_status = 'DELIVERED' THEN order_status
		ELSE 'READY'
END
WHERE delivery_personnel_id = 1;
DELETE FROM admin_manages_delivery WHERE delivery_personnel_id = 1;
DELETE FROM delivery_personnel WHERE delivery_personnel_id = 1;

-- group by queries
SELECT CONCAT(dp.first_name, ' ', dp.last_name) AS driver_name, 
COUNT(fo.food_order_id) AS total_deliveries 
FROM delivery_personnel dp 
LEFT JOIN food_order fo ON dp.delivery_personnel_id = fo.delivery_personnel_id 
GROUP BY dp.delivery_personnel_id, dp.first_name, dp.last_name; 

SELECT fb.name AS business_name,
COUNT(mi.menu_item_id) AS total_items
FROM food_business fb
LEFT JOIN menu_item mi ON fb.food_business_id = mi.food_business_id
GROUP BY fb.food_business_id, fb.name;

-- order by query
SELECT fb.name AS business_name,
SUM(mi.price * oi.quantity) AS total_revenue
FROM food_business fb
JOIN food_order fo ON fb.food_business_id = fo.food_business_id
JOIN order_item oi ON fo.food_order_id = oi.food_order_id
JOIN menu_item mi ON oi.menu_item_id = mi.menu_item_id
WHERE fo.order_status = 'DELIVERED'
GROUP BY fb.food_business_id, fb.name
ORDER BY total_revenue DESC;

-- where queries
SELECT * FROM customer WHERE username = 'suep'; 

SELECT * FROM food_order WHERE customer_id = 1; 
