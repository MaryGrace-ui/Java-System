package grace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CustomerLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public CustomerLogin() {
        setTitle("Customer Login");
        setSize(412, 324);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        JLabel label = new JLabel("Username:");
        label.setFont(new Font("Tahoma", Font.BOLD, 12));
        label.setBounds(0, 1, 188, 45);
        getContentPane().add(label);
        usernameField = new JTextField();
        usernameField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        usernameField.setBounds(198, 1, 188, 45);
        getContentPane().add(usernameField);

        JLabel label_1 = new JLabel("Password:");
        label_1.setFont(new Font("Tahoma", Font.BOLD, 12));
        label_1.setBounds(0, 56, 188, 45);
        getContentPane().add(label_1);
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        passwordField.setBounds(198, 56, 188, 45);
        getContentPane().add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(146, 156, 248));
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        loginButton.setBounds(0, 111, 188, 45);
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(146, 156, 248));
        registerButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        registerButton.setBounds(198, 111, 188, 45);

        getContentPane().add(loginButton);
        getContentPane().add(registerButton);

        loginButton.addActionListener(e -> loginCustomer());
        registerButton.addActionListener(e -> {
            dispose();
            new CustomerRegistration().setVisible(true);
        });

        setVisible(true);
    }

    private void loginCustomer() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        String sql = "SELECT * FROM customers WHERE username = ? AND password = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String customerName = rs.getString("fullname");
                int customerId = rs.getInt("id");
                JOptionPane.showMessageDialog(this, "Welcome " + customerName + "!");
                dispose();
                new CustomerDashboard(customerId, customerName).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login failed.");
        }
    }

public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
        try {
            new CustomerLogin().setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
}
}

