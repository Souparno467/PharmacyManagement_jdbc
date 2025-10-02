package com.example.dao;

import com.example.model.Medicine;
import com.example.util.DBConnection;
import com.example.exceptions.DatabaseOperationException;
import com.example.exceptions.MedicineNotFoundException;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Ensure tests run in order
@DisplayName("PharmacyDAOImpl Integration Tests")// Descriptive name for the test class
class PharmacyDAOImplTest {

    private MedicineDAO medDAO;

    @BeforeEach // Runs before each test
    void setUp() throws SQLException {
        medDAO = new MedicineDAOImpl();

        // Clear table before each test
        try (Connection conn = DBConnection.getConnection(); // Ensure connection is closed
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM medicines");
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should add a new medicine successfully")
    void testAddMedicine() throws DatabaseOperationException, MedicineNotFoundException { // Test adding a medicine
        Medicine med = new Medicine("Paracetamol", LocalDate.now().plusMonths(6), 50.0, 20);
        medDAO.addMedicine(med);

        assertNotEquals(0, med.getId(), "Medicine ID should be generated");

        Medicine retrieved = medDAO.getMedicineById(med.getId());
        assertNotNull(retrieved);
        assertEquals("Paracetamol", retrieved.getName());
    }

    @Test
    @Order(2)
    @DisplayName("Should retrieve all medicines when table is empty")
    void testGetAllEmpty() throws DatabaseOperationException { // Test retrieving from empty table
        ArrayList<Medicine> list = medDAO.getAllMedicines();
        assertNotNull(list);
        assertTrue(list.isEmpty(), "Should be empty initially");
    }

    @Test
    @Order(3)
    @DisplayName("Should retrieve all medicines with data")
    void testGetAllWithData() throws DatabaseOperationException { // Test retrieving from table with data
        medDAO.addMedicine(new Medicine("Aspirin", LocalDate.now().plusMonths(3), 30.0, 10));
        medDAO.addMedicine(new Medicine("Ibuprofen", LocalDate.now().plusMonths(12), 70.0, 15));

        ArrayList<Medicine> list = medDAO.getAllMedicines();
        assertEquals(2, list.size());
    }

    @Test
    @Order(4)
    @DisplayName("Should update an existing medicine successfully")
    void testUpdateMedicine() throws DatabaseOperationException, MedicineNotFoundException { // Test updating a medicine
        Medicine med = new Medicine("Vitamin C", LocalDate.now().plusMonths(8), 25.0, 50);
        medDAO.addMedicine(med);

        med.setName("Vitamin C+");
        med.setPrice(30.0);
        med.setStock(40);
        medDAO.updateMedicine(med);

        Medicine updated = medDAO.getMedicineById(med.getId());
        assertEquals("Vitamin C+", updated.getName());
        assertEquals(30.0, updated.getPrice());
        assertEquals(40, updated.getStock());
    }

    @Test
    @Order(5)
    @DisplayName("Should throw exception when updating non-existent medicine")
    void testUpdateNonExistent() {// Test updating a non-existent medicine
        Medicine fake = new Medicine(9999, "Apple", LocalDate.now(), 10.0, 1);
        assertThrows(MedicineNotFoundException.class, () -> medDAO.updateMedicine(fake));
    }

    @Test
    @Order(6)
    @DisplayName("Should delete a medicine successfully")
    void testDeleteMedicine() throws DatabaseOperationException, MedicineNotFoundException { // Test deleting a medicine
        Medicine med = new Medicine("Antibiotic", LocalDate.now().plusMonths(4), 100.0, 5);
        medDAO.addMedicine(med);

        medDAO.deleteMedicine(med.getId());
        assertThrows(MedicineNotFoundException.class, () -> medDAO.getMedicineById(med.getId()));
    }

    @Test
    @Order(7)
    @DisplayName("Should throw exception when deleting non-existent medicine")
    void testDeleteNonExistent() { // Test deleting a non-existent medicine
        assertThrows(MedicineNotFoundException.class, () -> medDAO.deleteMedicine(9999));
    }

    @Test
    @Order(8)
    @DisplayName("Should automatically remove expired medicines from list")
    void testExpiredMedicineRemoval() throws DatabaseOperationException { // Test that expired medicines are not returned
        Medicine expired = new Medicine("ExpiredMed", LocalDate.now().minusDays(1), 20.0, 10);
        Medicine valid = new Medicine("ValidMed", LocalDate.now().plusMonths(2), 40.0, 15);

        medDAO.addMedicine(expired);
        medDAO.addMedicine(valid);

        ArrayList<Medicine> list = medDAO.getAllMedicines();
        assertEquals(1, list.size());
        assertEquals("ValidMed", list.get(0).getName());
    }

    @Test
    @Order(9)
    @DisplayName("Should detect low-stock medicines")
    void testLowStockDetection() throws DatabaseOperationException { // Test that low-stock medicines are identified
        medDAO.addMedicine(new Medicine("HighStock", LocalDate.now().plusMonths(5), 60.0, 20));
        medDAO.addMedicine(new Medicine("LowStock", LocalDate.now().plusMonths(5), 10.0, 2));

        ArrayList<Medicine> list = medDAO.getAllMedicines();
        boolean foundLowStock = list.stream().anyMatch(m -> m.getName().equals("LowStock"));
        assertTrue(foundLowStock, "Low stock medicine should be present in the result");
    }
}
