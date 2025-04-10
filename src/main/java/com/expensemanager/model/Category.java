package main.java.com.expensemanager.model;

public class Category {
    public static final String TYPE_INCOME = "Thu";
    public static final String TYPE_EXPENSE = "Chi";

    private int id;
    private String name;
    private String type;
    private int profileId;

    public Category(String name, String type, int profileId) {
        this.name = name;
        setType(type);
        this.profileId = profileId;
    }

    public Category(int id, String name, String type, int profileId) {
        this(name, type, profileId);
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (!type.equals(TYPE_INCOME) && !type.equals(TYPE_EXPENSE)) {
            throw new IllegalArgumentException("Loại danh mục phải là Thu hoặc Chi");
        }
        this.type = type;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIncome() {
        return TYPE_INCOME.equals(type);
    }

    public boolean isExpense() {
        return TYPE_EXPENSE.equals(type);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name=" + name +
                ", type=" + type +
                ", profileId=" + profileId +
                "}";
    }

    public int getId() {
        return id;
    }
}
