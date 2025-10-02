package com.example.dao;

import com.example.model.Medicine;
import com.example.util.DBConnection;
import com.example.exceptions.DatabaseOperationException;
import com.example.exceptions.MedicineNotFoundException;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class MedicineDAOImpl implements MedicineDAO {

    private Medicine extractFromResultSet(ResultSet rs) throws SQLException { // method to map ResultSet to Medicine object
        Medicine med = new Medicine();
        med.setId(rs.getInt("medicine_id"));
        med.setName(rs.getString("name"));
        med.setPrice(rs.getDouble("price"));
        med.setStock(rs.getInt("stock"));
        Date sqlDate = rs.getDate("expiry_date");
        if (sqlDate != null) med.setExpiryDate(sqlDate.toLocalDate());
        return med;
    }

    @Override
    public void addMedicine(Medicine med) throws DatabaseOperationException { // add new medicine to DB
        String sql = "INSERT INTO medicines (name, expiry_date, price, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); // try-with-resources for auto-closing
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, med.getName());
            ps.setDate(2, med.getExpiryDate() != null ? Date.valueOf(med.getExpiryDate()) : null);
            ps.setDouble(3, med.getPrice());
            ps.setInt(4, med.getStock());
            ps.executeUpdate();

                try (Statement stmt = conn.createStatement(); // get the generated ID
                 ResultSet rs = stmt.executeQuery("SELECT MAX(medicine_id) FROM medicines")) {
                if (rs.next()) {
                    med.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error adding medicine: " + e.getMessage(), e);
        }
    }

    @Override
    public Medicine getMedicineById(int id) throws MedicineNotFoundException, DatabaseOperationException { // fetch medicine by ID
        String sql = "SELECT * FROM medicines WHERE medicine_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) { // prepared statement to prevent SQL injection
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { // execute query and get result set
                if (rs.next()) return extractFromResultSet(rs);
                else throw new MedicineNotFoundException("Medicine with ID " + id + " not found.");
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching medicine: " + e.getMessage(), e);
        }
    }

    @Override
    public ArrayList<Medicine> getAllMedicines() throws DatabaseOperationException {// fetch all medicines
        ArrayList<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(extractFromResultSet(rs));
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching medicines: " + e.getMessage(), e);
        }

            TreeSet<Medicine> sorted = new TreeSet<>( // sort by expiry date, then by ID
                Comparator.comparing(Medicine::getExpiryDate)
                        .thenComparing(Medicine::getId)
        );
        sorted.addAll(list);

        // Remove expired items
        LocalDate today = LocalDate.now();
        sorted.removeIf(m -> m.getExpiryDate() != null && m.getExpiryDate().isBefore(today)); // remove expired

        // Detect low stock (<=5), but KEEP them in results
        HashMap<String, Integer> lowStockMap = new HashMap<>();
        for (Medicine m : sorted) {
            if (m.getStock() <= 5) {
                lowStockMap.put(m.getName(), m.getStock());
            }
        }

        if (!lowStockMap.isEmpty()) { // Print low stock warnings
            System.out.println("\n Low Stock Medicines:");
            lowStockMap.forEach((k, v) -> System.out.println(k + " -> " + v + " units left"));
        }


        return new ArrayList<>(sorted);
    }

    @Override
    public void updateMedicine(Medicine med) throws MedicineNotFoundException, DatabaseOperationException { // update existing medicine
        String sql = "UPDATE medicines SET name=?, expiry_date=?, price=?, stock=? WHERE medicine_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, med.getName());
            ps.setDate(2, med.getExpiryDate() != null ? Date.valueOf(med.getExpiryDate()) : null);
            ps.setDouble(3, med.getPrice());
            ps.setInt(4, med.getStock());
            ps.setInt(5, med.getId());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new MedicineNotFoundException("Medicine ID " + med.getId() + " not found."); // if no rows affected, ID not found
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating medicine: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteMedicine(int id) throws MedicineNotFoundException, DatabaseOperationException { // delete medicine by ID
        String sql = "DELETE FROM medicines WHERE medicine_id=?"; // prepared statement to prevent SQL injection
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new MedicineNotFoundException("Medicine ID " + id + " not found.");
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error deleting medicine: " + e.getMessage(), e);
        }
    }
}
