package grace;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class CustomerRegistration extends JFrame {
    private JTextField txtFname, tftAddress, tftContact;
    private JPasswordField ttfPwd;

    public CustomerRegistration() {
        setTitle("Register");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 335);
        getContentPane().setLayout(null);

        JLabel lblFname = new JLabel("Fullname:");
        lblFname.setBounds(50, 30, 100, 25);
        getContentPane().add(lblFname);

        txtFname = new JTextField();
        txtFname.setBounds(150, 30, 200, 25);
        getContentPane().add(txtFname);

        JLabel lblAddress = new JLabel("Address:");
        lblAddress.setBounds(50, 70, 100, 25);
        getContentPane().add(lblAddress);

        tftAddress = new JTextField();
        tftAddress.setBounds(150, 70, 200, 25);
        getContentPane().add(tftAddress);

        JLabel lblContact = new JLabel("Contact No:");
        lblContact.setBounds(50, 110, 100, 25);
        getContentPane().add(lblContact);

        tftContact = new JTextField();
        tftContact.setBounds(150, 110, 200, 25);
        getContentPane().add(tftContact);

        JLabel lblPwd = new JLabel("Password:");
        lblPwd.setBounds(50, 150, 100, 25);
        getContentPane().add(lblPwd);

        ttfPwd = new JPasswordField();
        ttfPwd.setBounds(150, 150, 200, 25);
        getContentPane().add(ttfPwd);

        JButton btnRegister = new JButton("Register");
        btnRegister.setBounds(150, 200, 100, 30);
        getContentPane().add(btnRegister);
        
        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		dispose();
        		new CustomerLogin().setVisible(true);
        	}
        });
        btnLogin.setBounds(150, 241, 100, 30);
        getContentPane().add(btnLogin);

        btnRegister.addActionListener(e -> registerUser());
    }

    private void registerUser() {
        String fullname = txtFname.getText();
        String address = tftAddress.getText();
        String contact = tftContact.getText();
        String password = new String(ttfPwd.getPassword());
        
        String query = "INSERT INTO customers(fullname, address, contact_no, password) VALUES (?, ?, ?, ?)";

        try {
            Connection connection = DB.getConnection();
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, fullname);
            pst.setString(2, address);
            pst.setString(3, contact);
            pst.setString(4, password);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registered Successfully!");
                dispose();
                new CustomerLogin().setVisible(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerRegistration().setVisible(true));
    }
}
