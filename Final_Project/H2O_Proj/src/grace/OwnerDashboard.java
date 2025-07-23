package grace;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OwnerDashboard extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    public OwnerDashboard() {
        setTitle("Owner Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new Object[]{
            "ID", "Customer Fullname", "Address", "Gallons", "Type", "Date", "Paid"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) return Boolean.class;
                return String.class;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        String sql = "SELECT t.id, c.fullname, c.address, t.number_of_gallon, t.type, t.transaction_date, t.payment_status " +
                     "FROM transactions t JOIN customers c ON t.customer_id = c.id " +
                     "ORDER BY t.transaction_date DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("fullname"),
                    rs.getString("address"),
                    rs.getInt("number_of_gallon"),
                    rs.getString("type"),
                    rs.getString("transaction_date"),
                    rs.getString("payment_status").equalsIgnoreCase("Paid")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Add this main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OwnerDashboard dashboard = new OwnerDashboard();
            dashboard.setVisible(true);
        });
    }
}
