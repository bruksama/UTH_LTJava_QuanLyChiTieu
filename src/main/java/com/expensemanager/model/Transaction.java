package main.java.com.expensemanager.model;

import java.time.LocalDate;
import java.util.UUID;

public class Transaction {
    private String transactionId;
    private String profileId;
    private double amount;
    private String type;           // thu or chi
    private Category category;
    private LocalDate date;
    private String description;

    public Transaction(String profileId, double amount, String type, Category category, LocalDate date, String description) {
        this.transactionId = UUID.randomUUID().toString();
        this.profileId = profileId;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    // Getters và setters
    public String getTransactionId() {
        return transactionId;
    }

    public String getProfileId() {
        return profileId;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
