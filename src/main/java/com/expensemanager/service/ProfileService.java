package main.java.com.expensemanager.service;

import main.java.com.expensemanager.model.Profile;
import main.java.com.expensemanager.dao.ProfileDAO;
import java.util.List;
public class ProfileService {
    private final ProfileDAO profileDAO;

    public ProfileService(ProfileDAO profileDAO) {
        this.profileDAO = profileDAO;
    }

    // Thêm mới hồ sơ người dùng
    public boolean createProfile(Profile profile) {
        if (profile.getName() == null || profile.getName().isEmpty()) {
            System.out.println("Tên người dùng không được bỏ trống!");
            return false;
        }

        System.out.println("Đã tạo hồ sơ người dùng " + profile.getName() + "!");
        return profileDAO.insertProfile(profile);
    }

    // Cập nhật thông tin hồ sơ người dùng
    public boolean updateProfile(Profile profile) {
        if (profile.getId() <= 0) {
            System.out.println("Có lỗi xảy ra, vui lòng thử lại!");
            System.err.println("Lỗi tham số truyền không hợp lệ!");
            return false;
        }

        if (profile.getName() == null || profile.getName().isEmpty()) {
            System.out.println("Tên người dùng không được bỏ trống!");
            return false;
        }

        System.out.println("Đã cập nhật hồ sơ người dùng " + profile.getName() + "!");
        return profileDAO.updateProfile(profile);
    }

    // Xóa hồ sơ người dùng
    public boolean deleteProfile(int profileId) {
        if (profileId <= 0) {
            System.out.println("Có lỗi xảy ra, vui lòng thử lại!");
            System.err.println("Lỗi tham số truyền không hợp lệ!");
            return false;
        }

        return profileDAO.deleteProfile(profileId);
    }

    // Lấy hồ sơ người dùng theo ID
    public Profile getProfile(int profileId) {
        if (profileId <= 0) {
            System.out.println("Có lỗi xảy ra, vui lòng thử lại!");
            System.err.println("Lỗi tham số truyền không hợp lệ!");
            return null;
        }

        return profileDAO.getProfileById(profileId);
    }

    // Kiểm tra xem hồ sơ người dùng có tồn tại không
    public boolean isProfileExist(int profileId) {
        if (profileId <= 0) {
            System.out.println("Có lỗi xảy ra, vui lòng thử lại!");
            System.err.println("Lỗi tham số truyền không hợp lệ!");
            return false;
        }

        return profileDAO.isProfileExist(profileId);
    }

    public Profile getProfileByUsername(String selectedProfile) {
        if (selectedProfile == null || selectedProfile.isEmpty()) {
            System.out.println("Tên người dùng không hợp lệ!");
            return null;
        }

        // Gọi ProfileDAO để lấy profile theo tên đăng nhập
        return profileDAO.getProfileByUsername(selectedProfile);

    }
}
