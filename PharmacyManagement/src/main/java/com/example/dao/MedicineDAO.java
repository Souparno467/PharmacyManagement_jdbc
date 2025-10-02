package com.example.dao;

import com.example.model.Medicine;
import com.example.exceptions.DatabaseOperationException;
import com.example.exceptions.MedicineNotFoundException;
import java.util.ArrayList;

public interface MedicineDAO { // Data Access Object interface for Medicine
    void addMedicine(Medicine medicine) throws DatabaseOperationException;
    Medicine getMedicineById(int id) throws MedicineNotFoundException, DatabaseOperationException;
    ArrayList<Medicine> getAllMedicines() throws DatabaseOperationException;
    void updateMedicine(Medicine medicine) throws MedicineNotFoundException, DatabaseOperationException;
    void deleteMedicine(int id) throws MedicineNotFoundException, DatabaseOperationException;
}