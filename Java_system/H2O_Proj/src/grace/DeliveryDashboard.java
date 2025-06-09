package grace;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.sql.*;

public class DeliveryDashboard extends JFrame {
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel tableModel;

    public DeliveryDashboard() {
        setTitle("Delivery Boy Dashboard");
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
                return column == 4; // Only checkbox column
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

        // Listen for checkbox updates
        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 4) {
                int row = e.getFirstRow();
                updatePaymentStatus(row);
            }
        });
    }

    private void loadDeliveryData() {
        tableModel.setRowCount(0);

        String query = "SELECT t.id, t.customer_id, c.address, t.number_of_gallon, t.payment_status, c.fullname  " +
                       "FROM transactions t " +
                       "JOIN customers c ON t.customer_id = c.id " +
                       "ORDER BY t.transaction_date DESC";

        try {
        	Connection connection = DB.getConnection();
        	Statement stmt = connection.createStatement();
        	ResultSet rs = stmt.executeQuery(query);
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

    private void updatePaymentStatus(int row) {
        int transactionId = (int) tableModel.getValueAt(row, 0);
        boolean isPaid = (Boolean) tableModel.getValueAt(row, 4);
        String newStatus = isPaid ? "Paid" : "Credit";

        String updateQuery = "UPDATE transactions SET payment_status = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/water_station", "root", "");
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, transactionId);

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Payment status updated.");
            } else {
                JOptionPane.showMessageDialog(this, "No matching transaction found.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating payment status.");
        }
    }
}
