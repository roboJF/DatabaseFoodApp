USE food_delivery;

INSERT INTO customer (first_name, last_name, address, contact_info, username, email, password) VALUES
('Bob', 'Biggs', '1 Main St', '4103216728', 'bobbiggs41', 'bbiggs@email.com', 'password'),
('Sue', 'Plaintiff', '2 Oak St', '4106032736', 'suep', 'suetheplaintiff@email.com', 'password'),
('Tom', 'Twotones', '3 Pine St', '4434729201', 'tommy1', 'tommythegunn13@email.com', 'password');

INSERT INTO food_business (name, location, contact_info, username, email, password) VALUES
('Tonys Pizza', '10 First St', '4432214593', 'tonyspizza', 'tonyspizza@email.com', 'password'),
('Bigtown Burgers', '20 Second St', '4106391937', 'bigtownburger3', 'bigtownburger@email.com', 'password'),
('Joe Taco', '30 Third St', '4104553746', 'joetaco1', 'joetaco@email.com', 'password');

INSERT INTO delivery_personnel (first_name, last_name, contact_info, vehicle_details, username, email, password) VALUES
('Dan', 'Danner', '4100529187', 'Car', 'danthemann8', 'dand67@email.com', 'password'),
('Amy', 'Maybe', '4434510192', 'Bike', 'amydrives21', 'amym63@email.com', 'password'),
('Joe', 'Schmoe', '4101234729', 'Scooter', 'joedelivers1', 'joes31@email.com', 'password');

INSERT INTO administrator (username, email, password) VALUES
('admin1', 'admin1@email.com', 'password'),
('admin2', 'admin2@email.com', 'password'),
('admin3', 'admin3@email.com', 'password');

INSERT INTO menu_item (name, description, price, availability, food_business_id) VALUES
('Cheese Pizza', 'Processed cheese and tomato sauce', 5.99, TRUE, 1),
('Pepperoni Pizza', 'Human grade sausage, cheese, tomato sauce', 6.99, TRUE, 1),
('Gabagool Pizza', 'Gabagool with cheese', 7.99, TRUE, 1),
('Classic Burger', 'Dead cow, lettuce, tomato', 8.99, TRUE, 2),
('Cheese Burger', 'Dead cow with american cheese', 9.99, TRUE, 2),
('BLT Burger', 'Dead cow with dead pig, lettuce, tomato', 10.99, TRUE, 2),
('Crunchy Taco', 'Dead cow, lettuce, and shredded cheese', 4.99, TRUE, 3),
('Soft Taco', 'Shredded dead pig, cheese, lettuce, tomato', 3.99, TRUE, 3),
('Fajita Quesadilla', 'Cheese, onions, roasted peppers', 5.99, TRUE, 3);

INSERT INTO food_order (order_status, customer_id, food_business_id, delivery_personnel_id) VALUES
('PENDING', 1, 1, NULL),
('PENDING', 2, 2, NULL),
('PENDING', 3, 3, NULL),
('READY', 1, 2, NULL),
('READY', 2, 3, NULL),
('READY', 3, 1, NULL),
('ASSIGNED', 1, 1, 1),
('ASSIGNED', 2, 2, 2),
('DELIVERED', 3, 3, 3);

INSERT INTO order_item (quantity, food_order_id, menu_item_id) VALUES
(1,1,1),
(2,2,4),
(1,3,7),
(3,4,5),
(2,5,8),
(1,6,2),
(2,7,3),
(1,8,6),
(4,9,9);

/*
SELECT * FROM customer;
SELECT * FROM food_business;
SELECT * FROM administrator;
SELECT * FROM delivery_personnel;
SELECT * FROM menu_item;
SELECT * FROM food_order;
SELECT * FROM order_item;
*/