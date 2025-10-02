package com.example.model;
import java.time.LocalDate;
import java.util.Objects;

public class Medicine {    // Fields
    private int id;
    private String name;
    private LocalDate expiryDate;
    private double price;
    private int stock;

    public Medicine() {}  // Default constructor

    public Medicine(String name, LocalDate expiryDate, double price, int stock) { // Parameterized constructor without id
        this.name = name;
        this.expiryDate = expiryDate;
        this.price = price;
        this.stock = stock;
    }

    public Medicine(int id, String name, LocalDate expiryDate, double price, int stock) {  // Parameterized constructor with id
        this.id = id;
        this.name = name;
        this.expiryDate = expiryDate;
        this.price = price;
        this.stock = stock;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", expiryDate=" + expiryDate +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicine medicine)) return false;
        return id == medicine.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

