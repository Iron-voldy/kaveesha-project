package com.ims185.servlet;

import com.ims185.model.Item;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

//@WebServlet("/inventory")
public class InventoryServlet extends HttpServlet {
    private List<Item> loadItemsFromFile() {
        List<Item> items = new ArrayList<>();
        String filePath = getServletContext().getRealPath("/") + "inventory.txt";
        System.out.println("Loading from: " + filePath); // Debug
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 10) {
                    Item item = new Item();
                    item.setId(Integer.parseInt(parts[0]));
                    item.setName(parts[1]);
                    item.setCategory(parts[2]);
                    item.setStock(Integer.parseInt(parts[3]));
                    item.setPrice(Double.parseDouble(parts[4]));
                    item.setItemId(parts[5]);
                    item.setImagePath(parts[6]);
                    item.setExpiryDate(parts[7]);
                    item.setAddedDate(parts[8]);
                    item.setLastUpdatedDate(parts[9]);
                    items.add(item);
                }
            }
        } catch (IOException e) {
            System.out.println("File not found or error: " + e.getMessage()); // Debug
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(""); // Create empty file
            } catch (IOException ex) {
                System.out.println("Failed to create file: " + ex.getMessage());
            }
        }
        return items;
    }

    private void saveItemsToFile(List<Item> items) {
        String filePath = getServletContext().getRealPath("/") + "inventory.txt";
        System.out.println("Saving to: " + filePath); // Debug
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Item item : items) {
                writer.write(String.format("%d,%s,%s,%d,%.2f,%s,%s,%s,%s,%s%n",
                        item.getId(), item.getName(), item.getCategory(), item.getStock(), item.getPrice(),
                        item.getItemId(), item.getImagePath(), item.getExpiryDate(), item.getAddedDate(), item.getLastUpdatedDate()));
            }
            System.out.println("Saved " + items.size() + " items.");
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        List<Item> items = loadItemsFromFile();
        request.setAttribute("items", items);
        request.getRequestDispatcher("/inventory.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Stack<Item> itemStack = (Stack<Item>) session.getAttribute("itemStack");
        if (itemStack == null) {
            itemStack = new Stack<>();
            session.setAttribute("itemStack", itemStack);
        }
        String action = request.getParameter("action");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Item> items = loadItemsFromFile();

        if ("Add".equals(action)) {
            Item item = new Item();
            item.setId(items.size() > 0 ? items.get(items.size() - 1).getId() + 1 : 1);
            item.setName(request.getParameter("name"));
            item.setCategory(request.getParameter("category"));
            item.setStock(Integer.parseInt(request.getParameter("stock")));
            item.setPrice(Double.parseDouble(request.getParameter("price")));
            item.setItemId(request.getParameter("itemId"));
            item.setExpiryDate(request.getParameter("expiryDate"));
            item.setAddedDate(dateFormat.format(new Date()));
            item.setLastUpdatedDate(dateFormat.format(new Date()));
            item.setImagePath("images/" + item.getItemId() + ".jpg"); // Placeholder
            items.add(item);
            itemStack.push(item);
            saveItemsToFile(items);
        } else if ("Update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("itemId"));
            Item item = items.stream().filter(i -> i.getId() == id).findFirst().orElse(null);
            if (item != null) {
                itemStack.remove(item);
                item.setName(request.getParameter("name"));
                item.setCategory(request.getParameter("category"));
                item.setStock(Integer.parseInt(request.getParameter("stock")));
                item.setPrice(Double.parseDouble(request.getParameter("price")));
                item.setItemId(request.getParameter("itemId"));
                item.setExpiryDate(request.getParameter("expiryDate"));
                item.setLastUpdatedDate(dateFormat.format(new Date()));
                item.setImagePath("images/" + item.getItemId() + ".jpg"); // Placeholder
                itemStack.push(item);
                saveItemsToFile(items);
            }
        } else if ("Delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("itemId"));
            Item item = items.stream().filter(i -> i.getId() == id).findFirst().orElse(null);
            if (item != null) {
                itemStack.remove(item);
                items.remove(item);
                saveItemsToFile(items);
            }
        }

        session.setAttribute("itemStack", itemStack);
        request.setAttribute("items", items);
        request.getRequestDispatcher("/inventory.jsp").forward(request, response);
    }
}