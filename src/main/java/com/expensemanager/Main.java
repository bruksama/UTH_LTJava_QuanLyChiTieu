package main.java.com.expensemanager;

import main.java.com.expensemanager.dao.CategoryDAO;
import main.java.com.expensemanager.dao.ConnectorDAO;
import main.java.com.expensemanager.dao.ProfileDAO;
import main.java.com.expensemanager.model.Category;
import main.java.com.expensemanager.model.Profile;
//import main.java.com.expensemanager.service.CategoryService;

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

//        if (dbConnector.isConnected()) {
//            System.out.println("Kết nối thành công!");
//        } else {
//            System.out.println("Không thể kết nối!");
//        }
//
        ProfileDAO dbProfile = new ProfileDAO();
        Profile testProfile = dbProfile.getProfileById(4);
        System.out.println("Đang tương tác bằng profile: " + testProfile.getName());
//        if (dbProfile.insertProfile(testProfile)) {
//            System.out.println("Success");
//        } else {
//            System.out.println("Fail");
//        }

        CategoryDAO dbCategory = new CategoryDAO();
//        CategoryService dbCategoryService = new CategoryService(dbCategory);
//        Category testCategory = dbCategory.getCategoryById(3);
//
//        System.out.println("Danh mục " + testCategory.getName());
//
        if(dbCategory.preCreateCategory(testProfile.getId())) {
            System.out.println("Thành công");
        } else {
            System.out.println("Thất bại");
        }

//        Application.launch(Application.class);

    }
}
