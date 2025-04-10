package main.java.com.expensemanager.model;

public class Profile {
    private int id;
    private String name;
    private String createdAt;

    public Profile(int id, String name, String createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Profile(String name) {
        this.name = name;
    }

    public Profile(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {this.id = id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}

