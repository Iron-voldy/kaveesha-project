<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ims185.model.User" %>
<%@ page import="com.ims185.model.Item" %>
<%@ page import="com.ims185.model.Notification" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.ims185.servlet.InventoryServlet" %>
<%@ page import="java.time.LocalDateTime" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - IMS-185</title>
    <style>
        /* Basic Reset */
        *, *::before, *::after {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Roboto', sans-serif;
            background-color: #f0f0f0;
            color: #333;
            line-height: 1.6;
        }

        /* Layout */
        .container {
            display: flex;
            min-height: 100vh;
        }

        .sidebar {
            width: 250px;
            background-color: #222;
            color: #fff;
            padding: 20px;
        }

        .main-content {
            flex: 1;
            padding: 20px;
        }

        /* Header */
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            margin-bottom: 20px;
        }

        .header-left {
            font-size: 1.5em;
            font-weight: bold;
            color: #e50914;
        }

        .header-right {
            text-align: right;
            color: #000000;
        }

        /* Sidebar Navigation */
        .sidebar h1 {
            font-size: 1.5em;
            margin-bottom: 20px;
            color: #e50914;
        }

        .sidebar ul {
            list-style: none;
            padding: 0;
        }

        .sidebar li {
            margin-bottom: 10px;
        }

        .sidebar a {
            color: #fff;
            text-decoration: none;
            display: block;
            padding: 10px;
            border-radius: 5px;
            transition: background-color 0.3s ease;
        }

        .sidebar a:hover {
            background-color: #444;
        }

        .sidebar a.active {
            background-color: #e50914;
            font-weight: bold;
        }

        /* Sections */
        .section {
            background-color: #fff;
            padding: 20px;
            margin-bottom: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        .section h2 {
            margin-bottom: 15px;
            color: #555;
            border-bottom: 1px solid #eee;
            padding-bottom: 5px;
        }

        /* Profile Picture and User Info */
        .user-info {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
        }

        .profile-pic {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            object-fit: cover;
            margin-right: 20px;
        }

        /* Notification Section */
        .notification-item {
            padding: 10px;
            border-bottom: 1px solid #eee;
        }

        .notification-item:last-child {
            border-bottom: none;
        }

        /* Recent Items & Users Section */
        .item-list, .user-list {
            list-style: none;
            padding: 0;
        }

        .item-list li, .user-list li {
            padding: 10px;
            border-bottom: 1px solid #eee;
        }

        .item-list li:last-child, .user-list li:last-child {
            border-bottom: none;
        }

        /* Tables */
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        th, td {
            padding: 10px;
            text-align: left;
            border: 1px solid #eee;
        }

        th {
            background-color: #f5f5f5;
            color: #333;
        }

        /* CSS Animations */
        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        .fade-in {
            animation: fadeIn 0.5s ease-in-out;
        }

        /* Responsiveness */
        @media (max-width: 768px) {
            .container {
                flex-direction: column;
            }

            .sidebar {
                width: 100%;
            }
        }
    </style>
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const navLinks = document.querySelectorAll(".sidebar a");
            const currentPath = window.location.pathname.split("/").pop();
            navLinks.forEach(link => {
                const linkPath = link.getAttribute("href");
                if (linkPath === currentPath || (linkPath === "#" && currentPath === "dashboard")) {
                    link.classList.add("active");
                }
            });
        });
    </script>
</head>
<body>
<%
    User user = (User) session.getAttribute("loggedInUser");
    if (user == null) {
        System.out.println("Dashboard: No loggedInUser in session - Redirecting to login");
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    System.out.println("Dashboard: User found in session: " + user.getUsername());
    String profilePicPath = user.getProfilePicPath();
    if (profilePicPath == null || profilePicPath.isEmpty()) {
        profilePicPath = "default.jpg"; // Fallback image if no profile pic
    }
%>
<div class="container">
    <div class="sidebar">
        <h1>Navigation</h1>
        <ul>
            <li><a href="dashboard" class="active">Dashboard</a></li>
            <% if (user.getIsAdmin()) { %>
            <li><a href="<%= request.getContextPath() %>/user_management">Manage Users</a></li>
            <% } %>
            <li><a href="<%= request.getContextPath() %>/inventory">Manage Inventory</a></li>
            <li><a href="<%= request.getContextPath() %>/customer_management">Manage Customers</a></li>
            <li><a href="<%= request.getContextPath() %>/update_profile">Update Profile</a></li>
            <li><a href="<%= request.getContextPath() %>/items">View Items</a></li>
            <li><a href="<%= request.getContextPath() %>/reports">Reports Dashboard</a></li>
            <li><a href="<%= request.getContextPath() %>/analytics">Analytics Overview</a></li>
            <li><a href="<%= request.getContextPath() %>/suppliers">Supplier Management</a></li>
            <li><a href="<%= request.getContextPath() %>/orders">Order Processing</a></li>
            <li><a href="<%= request.getContextPath() %>/returns">Returns Management</a></li>
            <li><a href="<%= request.getContextPath() %>/stockalerts">Stock Alerts</a></li>
            <li><a href="<%= request.getContextPath() %>/activitylog">User Activity Log</a></li>
            <li><a href="<%= request.getContextPath() %>/settings">Settings Configuration</a></li>
            <li><a href="<%= request.getContextPath() %>/audittrail">Audit Trail</a></li>
            <li><a href="<%= request.getContextPath() %>/logout">Logout</a></li>
        </ul>
    </div>
    <div class="main-content">
        <div class="header">
            <div class="header-left">IMS-185</div>
            <div class="header-right">User: <%= user.getUsername() %> (Role: <%= user.getIsAdmin() ? "Admin" : "User" %>)</div>
        </div>

        <section class="section">
            <h2>Welcome to IMS-185 Dashboard</h2>
            <div class="user-info">
                <img src="<%= request.getContextPath() %>/uploads/<%= profilePicPath %>" alt="Profile Picture" class="profile-pic">
                <div>
                    <p>Hello, <%= user.getUsername() %>! Role: <%= user.getIsAdmin() ? "Admin" : "User" %></p>
                    <p>Email: <%= user.getEmail() != null ? user.getEmail() : "N/A" %></p>
                    <p>Contact: <%= user.getPhone() != null ? user.getPhone() : "N/A" %></p>
                </div>
            </div>
        </section>

        <section class="section recent-items-section">
            <h2>Recent Items</h2>
            <%
                List<Item> recentItems = (List<Item>) request.getAttribute("recentItems");
                if (recentItems != null && !recentItems.isEmpty()) {
            %>
            <table>
                <tr>
                    <th>Name</th>
                    <th>Category</th>
                    <th>Stock</th>
                    <th>Price</th>
                </tr>
                <% for (Item item : recentItems) { %>
                <tr>
                    <td><%= item.getName() %></td>
                    <td><%= item.getCategory() != null ? item.getCategory() : "N/A" %></td>
                    <td><%= item.getStock() %></td>
                    <td><%= String.format("%.2f", item.getPrice()) %></td>
                </tr>
                <% } %>
            </table>
            <%
            } else {
            %>
            <p>No recent items available.</p>
            <%
                }
            %>
        </section>

        <section class="section notification-section">
            <h2>Notifications</h2>
            <%
                List<Notification> notifications = (List<Notification>) request.getAttribute("notifications");
                if (notifications == null) {
                    notifications = new ArrayList<>();
                } else {
                    notifications = new ArrayList<>(notifications);
                }

                List<Item> allItems = (List<Item>) request.getServletContext().getAttribute("itemsFromServletContext");
                if (allItems != null) {
                    for (Item item : allItems) {
                        if (item.getStock() < 5) {
                            notifications.add(new Notification("Low stock alert: " + item.getName() + " (Stock: " + item.getStock() + ")", LocalDateTime.now()));
                        }
                    }
                }

                if (!notifications.isEmpty()) {
            %>
            <table>
                <tr>
                    <th>Message</th>
                    <th>Timestamp</th>
                </tr>
                <% for (Notification notification : notifications) { %>
                <tr>
                    <td><%= notification.getMessage() %></td>
                    <td><%= notification.getTimestamp() %></td>
                </tr>
                <% } %>
            </table>
            <%
            } else {
            %>
            <p>No notifications available.</p>
            <%
                }
            %>
        </section>
    </div>
</div>
</body>
</html>