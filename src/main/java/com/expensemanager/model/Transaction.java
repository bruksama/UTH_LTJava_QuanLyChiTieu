package main.java.com.expensemanager.model;


public class Transaction {
    public static final String TYPE_INCOME = "Thu";
    public static final String TYPE_EXPENSE = "Chi";
    private int id;
    private int profileId;
    private String type; // "income" hoặc "expense"
    private int categoryId;
    private String description;
    private String date;
    private double amount;

    public Transaction(String income, String thuTiềnMặt, int i, String date) {}

    public Transaction(int id, int profileId, String type, int categoryId,
                       String description, String date, double amount) {
        this.id = id;
        this.profileId = profileId;
        this.type = type;
        this.categoryId = categoryId;
        this.description = description;
        this.date = date;
        this.amount = amount;
    }

    public Transaction(int i, String income, double v, String date, String lương) {
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

    public String getDate() { return date; }

    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", profileId=" + profileId +
                ", type='" + type + '\'' +
                ", categoryId=" + categoryId +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", amount=" + amount +
                '}';
    }

    public Object getNote() {
        return null;
    }
}
