package grace;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CustomerDashboard extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private int customerId;
    private String customerName;

    public CustomerDashboard(int customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;

        setTitle("Customer Dashboard - " + customerName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Gallons", "Type", "Date", "Status"}, 0);
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);

        add(panel, BorderLayout.SOUTH);

        loadTransactions();

        addBtn.addActionListener(e -> createTransaction());
        updateBtn.addActionListener(e -> updateTransaction());
        deleteBtn.addActionListener(e -> deleteTransaction());
    }

    private void loadTransactions() {
        tableModel.setRowCount(0);
        String sql = "SELECT id, number_of_gallon, type, transaction_date, payment_status FROM transactions WHERE customer_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getInt("number_of_gallon"),
                    rs.getString("type"),
                    rs.getString("transaction_date"),
                    rs.getString("payment_status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTransaction() {
        JTextField gallonField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Purified", "Mineral"});

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("No. of Gallons:"));
        panel.add(gallonField);
        panel.add(new JLabel("Type:"));
        panel.add(typeCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "New Transaction", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DB.getConnection()) {
                conn.setAutoCommit(false);
                String sql = "INSERT INTO transactions (customer_id, number_of_gallon, type, transaction_date, payment_status) VALUES (?, ?, ?, NOW(), 'Unpaid')";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, customerId);
                    stmt.setInt(2, Integer.parseInt(gallonField.getText()));
                    stmt.setString(3, (String) typeCombo.getSelectedItem());
                    stmt.executeUpdate();
                    conn.commit();
                    loadTransactions();
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTransaction() {
        int selected = table.getSelectedRow();
        if (selected == -1) return;

        int id = (int) tableModel.getValueAt(selected, 0);
        String currentGallons = tableModel.getValueAt(selected, 1).toString();
        String currentType = tableModel.getValueAt(selected, 2).toString();

        JTextField gallonField = new JTextField(currentGallons);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Purified", "Mineral"});
        typeCombo.setSelectedItem(currentType);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("No. of Gallons:"));
        panel.add(gallonField);
        panel.add(new JLabel("Type:"));
        panel.add(typeCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Transaction", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String sql = "UPDATE transactions SET number_of_gallon = ?, type = ? WHERE id = ?";
            try (Connection conn = DB.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, Integer.parseInt(gallonField.getText()));
                stmt.setString(2, (String) typeCombo.getSelectedItem());
                stmt.setInt(3, id);
                stmt.executeUpdate();
                loadTransactions();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteTransaction() {
        int selected = table.getSelectedRow();
        if (selected == -1) return;

        int id = (int) tableModel.getValueAt(selected, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this transaction?", "Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM transactions WHERE id = ?";
            try (Connection conn = DB.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
                loadTransactions();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
