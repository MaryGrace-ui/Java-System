package grace;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.sql.*;

public class OwnerDashboard extends JFrame {
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel tableModel;

    public OwnerDashboard() {
        setTitle("Owner Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 500);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // Table Model
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Owner cannot edit
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Boolean.class;
                return String.class;
            }
        };

        tableModel.setColumnIdentifiers(new Object[] {
            "ID", "Customer Fullname", "Address", "No. of Gallons", "Paid"
        });

        table = new JTable(tableModel);
        table.setRowHeight(25);

        // Hide ID column from view
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        loadDeliveryData();
    }

    private void loadDeliveryData() {
        tableModel.setRowCount(0);

        String query = "SELECT t.id, t.customer_id, c.address, t.number_of_gallon, t.payment_status, c.fullname " +
                       "FROM transactions t " +
                       "JOIN customers c ON t.customer_id = c.id " +
                       "ORDER BY t.transaction_date DESC";

        try (Connection connection = DB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullname = rs.getString("fullname");
                String address = rs.getString("address");
                int numGallons = rs.getInt("number_of_gallon");
                String paymentStatus = rs.getString("payment_status");
                boolean isPaid = "Paid".equalsIgnoreCase(paymentStatus);

                tableModel.addRow(new Object[] { id, fullname, address, numGallons, isPaid });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading delivery data.");
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                OwnerDashboard frame = new OwnerDashboard();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
