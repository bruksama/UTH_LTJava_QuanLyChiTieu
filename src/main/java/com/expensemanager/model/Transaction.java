package main.java.com.expensemanager.model;

import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private int profileId;
    private String type; // "income" hoặc "expense"
    private int categoryId;
    private String description;
    private LocalDateTime date;
    private double amount;

    public Transaction() {}

    public Transaction(int id, int profileId, String type, int categoryId,
                       String description, LocalDateTime date, double amount) {
        this.id = id;
        this.profileId = profileId;
        this.type = type;
        this.categoryId = categoryId;
        this.description = description;
        this.date = date;
        this.amount = amount;
    }

    // Getter và Setter
    public int getId() { return id; }

    public int getProfileId() { return profileId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDate() { return date; }

    public double getAmount() { return amount; }

}
