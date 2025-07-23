package grace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CustomerRegistration extends JFrame {
    private JTextField fullnameField, usernameField, addressField;
    private JPasswordField passwordField;

    public CustomerRegistration() {
        setTitle("Customer Registration");
        setSize(562, 501);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        JLabel label = new JLabel("Full Name:");
        label.setBounds(127, 1, 107, 71);
        label.setFont(new Font("Tahoma", Font.BOLD, 12));
        getContentPane().add(label);
        fullnameField = new JTextField();
        fullnameField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        fullnameField.setBounds(244, 10, 270, 63);
        fullnameField.setBackground(new Color(211, 209, 216));
        getContentPane().add(fullnameField);

        JLabel label_1 = new JLabel("Username:");
        label_1.setBounds(127, 82, 107, 71);
        label_1.setFont(new Font("Tahoma", Font.BOLD, 12));
        getContentPane().add(label_1);
        usernameField = new JTextField();
        usernameField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        usernameField.setBounds(244, 91, 270, 63);
        usernameField.setBackground(new Color(211, 209, 216));
        getContentPane().add(usernameField);

        JLabel label_2 = new JLabel("Password:");
        label_2.setBounds(127, 163, 107, 76);
        label_2.setFont(new Font("Tahoma", Font.BOLD, 12));
        getContentPane().add(label_2);
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        passwordField.setBounds(244, 167, 270, 71);
        passwordField.setBackground(new Color(211, 209, 216));
        getContentPane().add(passwordField);

        JLabel label_3 = new JLabel("Address:");
        label_3.setBounds(127, 244, 107, 71);
        label_3.setFont(new Font("Tahoma", Font.BOLD, 12));
        getContentPane().add(label_3);
        addressField = new JTextField();
        addressField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        addressField.setBounds(244, 259, 270, 57);
        addressField.setBackground(new Color(211, 209, 216));
        getContentPane().add(addressField);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(60, 325, 234, 71);
        registerButton.setFont(new Font("Tahoma", Font.BOLD, 13));
        registerButton.setBackground(new Color(146, 156, 248));
        getContentPane().add(registerButton);

        JButton loginButton = new JButton("Go to Login");
        loginButton.setBounds(304, 326, 234, 70);
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 13));
        loginButton.setBackground(new Color(146, 156, 248));
        getContentPane().add(loginButton);

        registerButton.addActionListener(e -> registerCustomer());
        loginButton.addActionListener(e -> {
            dispose();
            new CustomerLogin().setVisible(true);
        });

        setVisible(true);
    }

    private void registerCustomer() {
        String fullname = fullnameField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String address = addressField.getText();

        if (fullname.isEmpty() || username.isEmpty() || password.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        String sql = "INSERT INTO customers (fullname, username, password, address) VALUES (?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fullname);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, address);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful!");
            clearFields();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration failed.");
        }
    }

    private void clearFields() {
        fullnameField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        addressField.setText("");
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new CustomerRegistration().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
