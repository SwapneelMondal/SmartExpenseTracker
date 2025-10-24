package com.tracker;

public class Expense {
    private int id;
    private String description;
    private String category;
    private double amount;

    public Expense(int id, String description, String category, double amount) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.amount = amount;
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
}
