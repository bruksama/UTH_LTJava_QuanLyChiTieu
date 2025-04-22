package main.java.com.expensemanager;

import main.java.com.expensemanager.dao.ConnectorDAO;

public class Main {
    public static void main(String[] args) {
//        String appDataPath = System.getenv("LOCALAPPDATA");
//        String dbPath = appDataPath + "\\QuanLyChiTieu\\database.db";
//        String dbURL = "jdbc:sqlite:" + dbPath;
//
//        new File(appDataPath + "\\QuanLyChiTieu").mkdirs();
//
//        ConnectorDAO dbConnector = ConnectorDAO.getInstance(dbURL);
        ConnectorDAO dbConnector = ConnectorDAO.getInstance();
        dbConnector.initDatabase();

        if (dbConnector.isConnected()) {
            System.out.println("Kết nối thành công!");
        } else {
            System.err.println("Không thể kết nối!");
        }

        Application.launch(Application.class);
    }
}