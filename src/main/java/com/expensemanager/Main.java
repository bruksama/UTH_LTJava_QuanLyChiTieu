package main.java.com.expensemanager;

import main.java.com.expensemanager.dao.ConnectorDAO;

public class Main {
    public static void main(String[] args) {
        ConnectorDAO dbConnector = ConnectorDAO.getInstance();

        if (dbConnector.initDatabase()) {
            System.out.println("Khởi tạo thành công!");
        } else {
            System.out.println("Không thể khởi tạo!");
        }
    }
}
