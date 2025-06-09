package grace;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class CustomerDashboard extends JFrame {
    private JPanel contentPane;
    private JComboBox<String> comboBox_BBG, comboBox_TOG, comboBox_NOG, comboBox_TYPE;
    private JTable table;
    private DefaultTableModel tableModel;
    private String customerName;
    private int selectedTransactionId = -1;

    public CustomerDashboard(String customerName, int id) {
        this.customerName = customerName;

        setTitle("Customer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 700);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(10, 10, 960, 280);
        panel.setLayout(null);
        contentPane.add(panel);

        // Labels
        JLabel lblBBG = new JLabel("Buy or Borrow Gallon:");
        lblBBG.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        lblBBG.setBounds(50, 30, 221, 40);
        panel.add(lblBBG);

        JLabel lblTOG = new JLabel("Type of Gallon:");
        lblTOG.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        lblTOG.setBounds(50, 80, 221, 40);
        panel.add(lblTOG);

        JLabel lblNOG = new JLabel("No. of Gallon:");
        lblNOG.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        lblNOG.setBounds(50, 130, 221, 40);
        panel.add(lblNOG);

        JLabel lblTYP = new JLabel("Type of Payment:");
        lblTYP.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        lblTYP.setBounds(50, 180, 221, 40);
        panel.add(lblTYP);

        // Combo Boxes
        comboBox_BBG = new JComboBox<>(new String[] {"Choose Here", "Buy", "Borrow"});
        comboBox_BBG.setBounds(270, 30, 200, 40);
        panel.add(comboBox_BBG);

        comboBox_TOG = new JComboBox<>(new String[] {"Choose Here", "Slim", "Round"});
        comboBox_TOG.setBounds(270, 80, 200, 40);
        panel.add(comboBox_TOG);

        comboBox_NOG = new JComboBox<>(new String[] {"Choose Here", "1", "2", "3", "4", "5"});
        comboBox_NOG.setBounds(270, 130, 200, 40);
        panel.add(comboBox_NOG);

        comboBox_TYPE = new JComboBox<>(new String[] {"Choose Here", "G-Cash", "Cash"});
        comboBox_TYPE.setBounds(270, 180, 200, 40);
        panel.add(comboBox_TYPE);

        // Buttons
        JButton btnSubmit = new JButton("Submit");
        btnSubmit.setFont(new Font("Times New Roman", Font.BOLD, 18));
        btnSubmit.setBounds(328, 230, 132, 40);
        panel.add(btnSubmit);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setFont(new Font("Times New Roman", Font.BOLD, 18));
        btnUpdate.setBounds(139, 230, 132, 40);
        panel.add(btnUpdate);

        // Table and scroll
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 310, 960, 330);
        contentPane.add(scrollPane);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[] {
            "ID", "Mode", "Gallon Type", "No. of Gallons", "Payment Type", "Timestamp"
        });

        table = new JTable(tableModel);
        scrollPane.setViewportView(table);

        // Load existing transactions
        loadTransactions(id);

        // Select row listener
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    selectedTransactionId = (int) tableModel.getValueAt(row, 0);
                    comboBox_BBG.setSelectedItem(tableModel.getValueAt(row, 1).toString());
                    comboBox_TOG.setSelectedItem(tableModel.getValueAt(row, 2).toString());
                    comboBox_NOG.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 3)));
                    comboBox_TYPE.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                }
            }
        });

        // Submit Action
        btnSubmit.addActionListener(e -> {
            String bbg = (String) comboBox_BBG.getSelectedItem();
            String tog = (String) comboBox_TOG.getSelectedItem();
            String nog = (String) comboBox_NOG.getSelectedItem();
            String type = (String) comboBox_TYPE.getSelectedItem();

            if (bbg.equals("Choose Here") || tog.equals("Choose Here") ||
                nog.equals("Choose Here") || type.equals("Choose Here")) {
                JOptionPane.showMessageDialog(this, "Please fill out all fields.");
                return;
            }
            saveTransaction(bbg, tog, nog, type, id);
            loadTransactions(id);
            clearForm();
        });

        // Update Action
        btnUpdate.addActionListener(e -> {
            if (selectedTransactionId == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to update.");
                return;
            }

            String bbg = (String) comboBox_BBG.getSelectedItem();
            String tog = (String) comboBox_TOG.getSelectedItem();
            String nog = (String) comboBox_NOG.getSelectedItem();
            String type = (String) comboBox_TYPE.getSelectedItem();

            if (bbg.equals("Choose Here") || tog.equals("Choose Here") ||
                nog.equals("Choose Here") || type.equals("Choose Here")) {
                JOptionPane.showMessageDialog(this, "Please fill out all fields.");
                return;
            }

            String updateQuery = "UPDATE transactions SET action_type=?, gallon_type=?, number_of_gallon=?, payment_type=? WHERE id=?";
            try {
            	Connection connection = DB.getConnection();
            	PreparedStatement stmt = connection.prepareStatement(updateQuery);
                stmt.setString(1, bbg);
                stmt.setString(2, tog);
                stmt.setInt(3, Integer.parseInt(nog));
                stmt.setString(4, type);
                stmt.setInt(5, selectedTransactionId);

                int affected = stmt.executeUpdate();
                if (affected > 0) {
                    JOptionPane.showMessageDialog(this, "Transaction updated.");
                    loadTransactions(id);
                    clearForm();
                    selectedTransactionId = -1;
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating transaction.");
            }
        });
    }

    // Save new transaction
    private void saveTransaction(String bbg, String tog, String nog, String type, int id) {
    	String query = "INSERT INTO transactions (customer_id, action_type, gallon_type, number_of_gallon, payment_type) VALUES (?, ?, ?, ?, ?)";
        try {
        	Connection connection = DB.getConnection();
        	PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.setString(2, bbg);
            stmt.setString(3, tog);
            stmt.setInt(4, Integer.parseInt(nog));
            stmt.setString(5, type);
            int row = stmt.executeUpdate();
            
            if (row > 0) {
				JOptionPane.showMessageDialog(this, "SENT!");
			}
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving transaction.");
        }
    }

    // Load table data
    private void loadTransactions(int id) {
        tableModel.setRowCount(0);
        String query = "SELECT * FROM transactions WHERE customer_id = ? ORDER BY transaction_date DESC";
        try {
        	Connection connection = DB.getConnection();
        	PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                    rs.getInt("id"),
                    rs.getString("action_type"),
                    rs.getString("gallon_type"),
                    rs.getInt("number_of_gallon"),
                    rs.getString("payment_type"),
                    rs.getTimestamp("transaction_date")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Reset form
    private void clearForm() {
        comboBox_BBG.setSelectedIndex(0);
        comboBox_TOG.setSelectedIndex(0);
        comboBox_NOG.setSelectedIndex(0);
        comboBox_TYPE.setSelectedIndex(0);
    }
}
