CREATE DATABASE IF NOT EXISTS food_delivery;
USE food_delivery;

CREATE TABLE administrator (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50)  UNIQUE NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact_info VARCHAR(100),
    username VARCHAR(50)  UNIQUE NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE food_business (
    food_business_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(255) NOT NULL,
    contact_info VARCHAR(100),
    username VARCHAR(50)  UNIQUE NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE delivery_personnel (
    delivery_personnel_id  INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    contact_info VARCHAR(100),
    vehicle_details VARCHAR(255),
    username VARCHAR(50)  UNIQUE NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE admin_manages_customer (
    admin_id INT NOT NULL,
    customer_id INT NOT NULL,
    PRIMARY KEY (admin_id, customer_id),
    FOREIGN KEY (admin_id) REFERENCES administrator(admin_id),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

CREATE TABLE admin_manages_business (
    admin_id INT NOT NULL,
    food_business_id INT NOT NULL,
    PRIMARY KEY (admin_id, food_business_id),
    FOREIGN KEY (admin_id) REFERENCES administrator(admin_id),
    FOREIGN KEY (food_business_id) REFERENCES food_business(food_business_id)
);

CREATE TABLE admin_manages_delivery (
    admin_id INT NOT NULL,
    delivery_personnel_id INT NOT NULL,
    PRIMARY KEY (admin_id, delivery_personnel_id),
    FOREIGN KEY (admin_id) REFERENCES administrator(admin_id),
    FOREIGN KEY (delivery_personnel_id) REFERENCES delivery_personnel(delivery_personnel_id)
);

CREATE TABLE menu_item (
    menu_item_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    availability BOOLEAN NOT NULL DEFAULT TRUE,
    food_business_id INT NOT NULL,
    FOREIGN KEY (food_business_id) REFERENCES food_business(food_business_id)
);

CREATE TABLE food_order (
    food_order_id INT PRIMARY KEY AUTO_INCREMENT,
    order_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    customer_id INT NOT NULL,
    food_business_id INT NOT NULL,
    delivery_personnel_id INT,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (food_business_id) REFERENCES food_business(food_business_id),
    FOREIGN KEY (delivery_personnel_id) REFERENCES delivery_personnel(delivery_personnel_id),
    CONSTRAINT check_status CHECK (order_status IN 
    ('PENDING', 'PREPARING', 'READY', 'OUT_FOR_DELIVERY', 'DELIVERED'))
);

CREATE TABLE order_item (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    quantity INT NOT NULL,
    food_order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    FOREIGN KEY (food_order_id) REFERENCES food_order(food_order_id),
    FOREIGN KEY (menu_item_id) REFERENCES menu_item(menu_item_id),
    CONSTRAINT check_quantity CHECK (quantity >= 1)
);