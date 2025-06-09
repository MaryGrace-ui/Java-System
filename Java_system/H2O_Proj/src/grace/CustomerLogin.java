package grace;

import javax.swing.*;
import java.sql.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CustomerLogin extends JFrame {
    private JTextField txtFname;
    private JPasswordField txtPwd;

    public CustomerLogin() {
        setTitle("Login");
        setSize(350, 226);
        getContentPane().setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel lblFname = new JLabel("Fullname:");
        lblFname.setBounds(50, 30, 100, 25);
        getContentPane().add(lblFname);

        txtFname = new JTextField();
        txtFname.setBounds(150, 30, 130, 25);
        getContentPane().add(txtFname);

        JLabel lblPwd = new JLabel("Password:");
        lblPwd.setBounds(50, 70, 100, 25);
        getContentPane().add(lblPwd);

        txtPwd = new JPasswordField();
        txtPwd.setBounds(150, 70, 130, 25);
        getContentPane().add(txtPwd);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(120, 110, 100, 30);
        getContentPane().add(btnLogin);
        
        JButton btnReg = new JButton("Register");
        btnReg.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		dispose();
        		new CustomerRegistration().setVisible(true);
        	}
        });
        btnReg.setBounds(120, 146, 100, 30);
        getContentPane().add(btnReg);

        btnLogin.addActionListener(e -> loginUser());
    }

    private void loginUser() {
        String fullname = txtFname.getText();
        String password = new String(txtPwd.getPassword());
        String query = "SELECT * FROM customers WHERE fullname=? AND password=?";
        try {
        	Connection connection = DB.getConnection();
        	PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, fullname);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
            	JOptionPane.showMessageDialog(this, "Login Successful!");
            	Boolean isDelivery = rs.getString("fullname").equals("delivery") ? true : false;
            	
            	if (isDelivery) {
					new DeliveryDashboard().setVisible(true);
				} else {
					new CustomerDashboard(fullname, rs.getInt("id")).setVisible(true);
				}
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
