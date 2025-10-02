package com.example;

import com.example.dao.MedicineDAO;
import com.example.dao.MedicineDAOImpl;
import com.example.model.Medicine;
import com.example.exceptions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class PharmacyApp {
    private static MedicineDAO medDAO = new MedicineDAOImpl(); // Using the DAO implementation
    private static Scanner sc = new Scanner(System.in);

     static void main(String[] args) { //
        int choice;
        do {
            showMenu();
            choice = sc.nextInt(); sc.nextLine();
            try {
                switch (choice) { //
                    case 1 -> addMedicine();
                    case 2 -> viewAll();
                    case 3 -> viewById();
                    case 4 -> updateMedicine();
                    case 5 -> deleteMedicine();
                    case 0 -> System.out.println("Exiting... Goodbye!");
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        } while (choice != 0);
        sc.close();
    }

    private static void showMenu() { // Display menu options
        IO.println("\n--- Pharmacy Store ---");
        IO.println("1. Add Medicine");
        IO.println("2. View All Medicines");
        IO.println("3. View Medicine By ID");
        IO.println("4. Update Medicine");
        IO.println("5. Delete Medicine");
        IO.println("0. Exit");
        IO.print("Enter choice: ");
    }

    private static void addMedicine() throws DatabaseOperationException { // Add a new medicine
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Expiry Date (YYYY-MM-DD): ");
        String date = sc.nextLine();
        LocalDate expiry = date.isEmpty() ? null : LocalDate.parse(date);
        System.out.print("Price: ");
        double price = sc.nextDouble();
        System.out.print("Stock: ");
        int stock = sc.nextInt(); sc.nextLine();

        Medicine med = new Medicine(name, expiry, price, stock);
        medDAO.addMedicine(med);
        System.out.println("Medicine added! ID = " + med.getId());
    }

    private static void viewAll() throws DatabaseOperationException { // View all medicines
        ArrayList<Medicine> meds = medDAO.getAllMedicines();
        if (meds.isEmpty()) System.out.println("No medicines found.");
        else meds.forEach(System.out::println);
    }

    private static void viewById() throws MedicineNotFoundException, DatabaseOperationException { // View medicine by ID
        System.out.print("Enter ID: ");
        int id = sc.nextInt(); sc.nextLine();
        Medicine med = medDAO.getMedicineById(id);
        System.out.println(med);
    }

    private static void updateMedicine() throws MedicineNotFoundException, DatabaseOperationException { // Update existing medicine
        System.out.print("Enter ID to update: ");
        int id = sc.nextInt(); sc.nextLine();
        Medicine med = medDAO.getMedicineById(id);

        System.out.print("New Name (" + med.getName() + "): ");
        String newName = sc.nextLine();
        if (!newName.isEmpty()) med.setName(newName);

        System.out.print("New Expiry Date (" + med.getExpiryDate() + "): ");
        String date = sc.nextLine();
        if (!date.isEmpty()) med.setExpiryDate(LocalDate.parse(date));

        System.out.print("New Price (" + med.getPrice() + "): ");
        String priceStr = sc.nextLine();
        if (!priceStr.isEmpty()) med.setPrice(Double.parseDouble(priceStr));

        System.out.print("New Stock (" + med.getStock() + "): ");
        String stockStr = sc.nextLine();
        if (!stockStr.isEmpty()) med.setStock(Integer.parseInt(stockStr));

        medDAO.updateMedicine(med);
        System.out.println("Updated successfully!");
    }

    private static void deleteMedicine() throws MedicineNotFoundException, DatabaseOperationException { // Delete a medicine
        System.out.print("Enter ID to delete: ");
        int id = sc.nextInt(); sc.nextLine();
        medDAO.deleteMedicine(id);
        System.out.println("Deleted successfully!");
    }
}
