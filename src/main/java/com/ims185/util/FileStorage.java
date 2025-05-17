package com.ims185.util;

import com.ims185.model.Item;
import com.ims185.model.User;
import com.ims185.model.Customer;
import com.ims185.model.Notification;
import jakarta.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileStorage {
    private static final String USERS_FILE = "C:/IMS-185-Data/users.txt";
    private static final String ITEMS_FILE = "C:/IMS-185-Data/items.txt";
    private static final String CUSTOMERS_FILE = "C:/IMS-185-Data/customers.txt";
    private static final String NOTIFICATIONS_FILE = "C:/IMS-185-Data/notifications.txt";
    private static final String UPLOAD_DIR = "C:/IMS-185-Data/Uploads/";

    static {
        // Ensure directories exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                logError("Failed to create upload directory: " + e.getMessage());
            }
        }
        Path dataPath = Paths.get("C:/IMS-185-Data");
        if (!Files.exists(dataPath)) {
            try {
                Files.createDirectories(dataPath);
            } catch (IOException e) {
                logError("Failed to create data directory: " + e.getMessage());
            }
        }
    }

    // Load users from file
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    User user = new User(
                            parts[0],
                            parts[1],
                            parts[2],
                            parts[3],
                            Boolean.parseBoolean(parts[4]),
                            LocalDateTime.parse(parts[5]),
                            parts[6],
                            parts[7],
                            parts.length > 8 ? parts[8] : "user"
                    );
                    users.add(user);
                }
            }
        } catch (IOException e) {
            logError("Error reading users: " + e.getMessage());
        }
        return users;
    }

    // Write users to file
    public static void writeUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                writer.write(user.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            logError("Error writing users: " + e.getMessage());
        }
    }

    // Check if user exists
    public static boolean userExists(String username) {
        List<User> users = loadUsers();
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }

    // Get all users
    public static List<User> getAllUsers() {
        return loadUsers();
    }

    // Save a single user
    public static void saveUser(User user) {
        List<User> users = loadUsers();
        users.add(user);
        writeUsers(users);
    }

    // Add a user
    public static void addUser(User user) {
        saveUser(user);
    }

    // Update a user
    public static void updateUser(User updatedUser) {
        List<User> users = loadUsers();
        users.replaceAll(user -> user.getId().equals(updatedUser.getId()) ? updatedUser : user);
        writeUsers(users);
    }

    // Delete a user
    public static void deleteUser(String userId) {
        List<User> users = loadUsers();
        users.removeIf(user -> user.getId().equals(userId));
        writeUsers(users);
    }

    // Load items from file
    public static List<Item> loadItems() {
        List<Item> items = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ITEMS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    Item item = new Item(
                            Integer.parseInt(parts[0]),
                            parts[1],
                            parts[2].isEmpty() ? null : parts[2],
                            Integer.parseInt(parts[3]),
                            Double.parseDouble(parts[4]),
                            parts[5],
                            parts[6].isEmpty() ? null : parts[6],
                            parts[7],
                            parts[8],
                            LocalDateTime.now().toString() // or another default value for `lastUpdatedDate`
                    );
                    items.add(item);
                }
            }
        } catch (IOException | NumberFormatException e) {
            logError("Error reading items from " + ITEMS_FILE + ": " + e.getMessage());
        }
        return items;
    }

    // Write items to file
    public static void writeItems(List<Item> items) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ITEMS_FILE))) {
            for (Item item : items) {
                writer.write(item.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            logError("Error writing items: " + e.getMessage());
        }
    }

    // Load customers from file
    public static List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 10) {
                    Customer customer = new Customer(
                            parts[0],
                            parts[1],
                            parts[2],
                            parts[3],
                            Double.parseDouble(parts[4]),
                            Integer.parseInt(parts[5]),
                            parts[6].isEmpty() ? null : LocalDateTime.parse(parts[6]),
                            parts[7].isEmpty() ? null : parts[7],
                            LocalDateTime.parse(parts[8]),
                            parts[9].isEmpty() ? "active" : parts[9]
                    );
                    customers.add(customer);
                }
            }
        } catch (IOException | NumberFormatException e) {
            logError("Error reading customers: " + e.getMessage());
        }
        return customers;
    }

    // Write customers to file
    public static void writeCustomers(List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer customer : customers) {
                writer.write(customer.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            logError("Error writing customers: " + e.getMessage());
        }
    }

    // Add a customer
    public static void addCustomer(Customer customer) {
        List<Customer> customers = loadCustomers();
        customers.add(customer);
        writeCustomers(customers);
    }

    // Update a customer
    public static void updateCustomer(Customer updatedCustomer) {
        List<Customer> customers = loadCustomers();
        customers.replaceAll(customer -> customer.getId().equals(updatedCustomer.getId()) ? updatedCustomer : customer);
        writeCustomers(customers);
    }

    // Delete a customer
    public static void deleteCustomer(String customerId) {
        List<Customer> customers = loadCustomers();
        customers.removeIf(customer -> customer.getId().equals(customerId));
        writeCustomers(customers);
    }

    // Load notifications from file
    //private static final String NOTIFICATIONS_FILE = "C:/IMS-185-Data/notifications.txt";

    public static List<Notification> loadNotifications() {
        List<Notification> notifications = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(NOTIFICATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("type")) continue; // Skip headers or empty lines
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    try {
                        LocalDateTime timestamp = LocalDateTime.parse(parts[1]);
                        Notification notification = new Notification(parts[0], timestamp);
                        notifications.add(notification);
                    } catch (DateTimeParseException e) {
                        logError("Invalid timestamp in notifications.txt: " + parts[1]);
                    }
                }
            }
        } catch (IOException e) {
            logError("Error reading notifications: " + e.getMessage());
        }
        return notifications;
    }

    private static void logToConsole(String message) {
        System.err.println(message); // Replace with proper logging if available
    }

    // Save uploaded image
    public static String saveImage(Part part, String uploadDir) {
        String fileName = UUID.randomUUID().toString() + "_" + part.getSubmittedFileName();
        Path filePath = Paths.get(uploadDir + fileName);
        try (InputStream input = part.getInputStream()) {
            Files.copy(input, filePath);
            return fileName;
        } catch (IOException e) {
            FileStorage.logToConsole("Error saving image: " + e.getMessage());
            return null;
        }
    }

    // Log errors to file
    private static void logError(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/IMS-185-Data/storage_errors.log", true))) {
            writer.write(LocalDateTime.now() + " - " + message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to log error: " + e.getMessage());
        }
    }
}
