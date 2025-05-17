package com.ims185.servlet;

import com.ims185.model.Item;
import com.ims185.model.Notification;
import com.ims185.model.User;
import com.ims185.util.FileStorage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("DashboardServlet: Processing GET request");

        // Check session
        User loggedInUser = (User) request.getSession().getAttribute("loggedInUser");
        if (loggedInUser == null) {
            System.out.println("DashboardServlet: No loggedInUser in session - Redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        System.out.println("DashboardServlet: User authenticated: " + loggedInUser.getUsername());

        // Load items
        List<Item> allItems = FileStorage.loadItems();
        if (allItems == null) {
            allItems = new ArrayList<>();
        }
        System.out.println("DashboardServlet: Loaded " + allItems.size() + " items");

        // Set items in servlet context (for notifications)
        getServletContext().setAttribute("itemsFromServletContext", allItems);

        // Prepare recent items (e.g., last 5 items)
        List<Item> recentItems = new ArrayList<>();
        int count = Math.min(5, allItems.size());
        for (int i = 0; i < count; i++) {
            recentItems.add(allItems.get(i));
        }
        request.setAttribute("recentItems", recentItems);
        System.out.println("DashboardServlet: Set recentItems with " + recentItems.size() + " items");

        // Prepare notifications
        List<Notification> notifications = new ArrayList<>();
        request.setAttribute("notifications", notifications);
        System.out.println("DashboardServlet: Initialized notifications list");

        // Forward to dashboard.jsp
        System.out.println("DashboardServlet: Forwarding to dashboard.jsp");
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}