package financialmanagement.model;

public class Category {
    private String name;
    private String type; // "Thu" hoặc "Chi"

    public Category(String name, String type) {
        this.name = name;
        this.type = type;
    }

    // Getters và setters
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}
