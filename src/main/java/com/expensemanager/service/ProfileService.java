package main.java.com.expensemanager.service;


import com.expensemanager.dao.ProfileDAO;
import com.expensemanager.model.Profile;

import java.util.List;

public class ProfileService {

    private ProfileDAO profileDAO;

    public ProfileService() {
        // Khởi tạo ProfileDAO
        this.profileDAO = new ProfileDAO();
    }

    // Lấy tất cả hồ sơ người dùng
    public List<Profile> getAllProfiles() {
        return profileDAO.getAllProfiles();
    }

    // Lấy thông tin hồ sơ người dùng theo ID
    public Profile getProfileById(int profileId) {
        return profileDAO.getProfileById(profileId);
    }

    // Thêm hồ sơ người dùng mới
    public boolean addProfile(Profile profile) {
        if (profile != null && profile.getName() != null && !profile.getName().isEmpty()) {
            return profileDAO.addProfile(profile);
        }
        return false;
    }

    // Cập nhật thông tin hồ sơ người dùng
    public boolean updateProfile(Profile profile) {
        if (profile != null && profile.getId() > 0) {
            return profileDAO.updateProfile(profile);
        }
        return false;
    }

    // Xóa hồ sơ người dùng
    public boolean deleteProfile(int profileId) {
        if (profileId > 0) {
            return profileDAO.deleteProfile(profileId);
        }
        return false;
    }
}
