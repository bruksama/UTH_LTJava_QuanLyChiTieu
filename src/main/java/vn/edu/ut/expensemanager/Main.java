package main.java.vn.edu.ut.expensemanager;

import main.java.vn.edu.ut.expensemanager.dao.ConnectorDAO;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
//        String appDataPath = System.getenv("LOCALAPPDATA");
//        String dbPath = appDataPath + "\\QuanLyChiTieu\\database.db";
//        String dbURL = "jdbc:sqlite:" + dbPath;
//
//        new File(appDataPath + "\\QuanLyChiTieu").mkdirs();
////
//        ConnectorDAO dbConnector = ConnectorDAO.getInstance(dbURL);
        ConnectorDAO dbConnector = ConnectorDAO.getInstance();
        dbConnector.initDatabase();
//
        if (dbConnector.isConnected()) {
            System.out.println("Kết nối thành công!");
        } else {
            System.err.println("Không thể kết nối!");
        }

        LocalDate now = LocalDate.now();
        String from = now.withDayOfMonth(1).toString();
        String to = now.withDayOfMonth(now.lengthOfMonth()).toString();

        System.out.println(from + " - " + to);

//        Application.main(args);
    }
}