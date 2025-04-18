package main.java.com.expensemanager.service;

import main.java.com.expensemanager.dao.CategoryDAO;
import main.java.com.expensemanager.dao.ProfileDAO;
import main.java.com.expensemanager.model.Category;

public class CategoryService {
    private final CategoryDAO categoryDAO;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public boolean insertCategory(Category category) {
        if (category.getType() == null || category.getProfileId() <= 0) {
            System.out.println("Có lỗi xảy ra, vui lòng thử lại!");
            System.err.println("Lỗi tham số truyền không đầy đủ");
            return false;
        }

        if (!isProfileExist(category.getProfileId())) {
            System.out.println("Hồ sơ người dùng không tồn tại, hãy tạo mới và đăng nhập để thực hiện tác vụ này!");
            return false;
        }

        if (category.getName() == null || category.getName().isEmpty()) {
            System.out.println("Tên danh mục không được bỏ trống!");
            return false;
        }

        if (categoryDAO.insertCategory(category)) {
            System.out.println("Đã tạo danh mục " + category.getName() + "!");
            return true;
        } else {
            System.out.println("Tạo danh mục thất bại!");
            return false;
        }
    }

    private boolean isProfileExist(int profileId) {
        ProfileDAO profileDAO = new ProfileDAO();

        return profileDAO.isProfileExist(profileId);
    }

    public boolean deleteCategory(int id, int profileId) {
        if (profileId <= 0 || id <= 0) {
            System.out.println("Có lỗi xảy ra, vui lòng thử lại!");
            System.err.println("Lỗi tham số truyền không đầy đủ!");
            return false;
        }

        if (categoryDAO.deleteCategory(id, profileId)) {
            System.out.println("Đã xóa danh mục thành công!");
            return true;
        } else {
            System.out.println("Xóa danh mục thất bại!");
            return false;
        }
    }
}
