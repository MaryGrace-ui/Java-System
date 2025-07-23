package grace;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DeliveryDashboard extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    public DeliveryDashboard() {
        setTitle("Delivery Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Customer", "Gallons", "Type", "Date", "Status"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton updateBtn = new JButton("Mark as Paid");
        add(updateBtn, BorderLayout.SOUTH);

        loadDeliveries();

        updateBtn.addActionListener(e -> markAsPaid());
    }

    private void loadDeliveries() {
        tableModel.setRowCount(0);
        String sql = "SELECT t.id, c.fullname, t.number_of_gallon, t.type, t.transaction_date, t.payment_status " +
                     "FROM transactions t JOIN customers c ON t.customer_id = c.id " +
                     "ORDER BY t.transaction_date DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("fullname"),
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

    private void markAsPaid() {
        int selected = table.getSelectedRow();
        if (selected == -1) return;

        int id = (int) tableModel.getValueAt(selected, 0);
        String sql = "UPDATE transactions SET payment_status = 'Paid' WHERE id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            loadDeliveries();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
