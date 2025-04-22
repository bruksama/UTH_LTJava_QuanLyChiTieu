package main.java.com.expensemanager.service;

import main.java.com.expensemanager.dao.CategoryDAO;
import main.java.com.expensemanager.model.Profile;
import main.java.com.expensemanager.dao.ProfileDAO;
import java.util.List;


public class ProfileService {
    private final ProfileDAO profileDAO;
    private final CategoryDAO categoryDAO ;

    public ProfileService(ProfileDAO profileDAO,CategoryDAO categoryDAO) {
        this.profileDAO = profileDAO;
        this.categoryDAO = categoryDAO;
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
            return  false;
        }

        if (profile.getName() == null || profile.getName().isEmpty()) {
            System.out.println("Tên người dùng không được bỏ trống!");
            return false;
        }

        System.out.println("Đã cập nhật hồ sơ người dùng " + profile.getName() + "!");
        return profileDAO.updateProfile(profile);
    }


// Cập nhật phương thức deleteProfile
public boolean deleteProfile(int profileId) {
    try {
        // Xóa các categories liên quan đến profile
        categoryDAO.deleteByProfileId(profileId);

        // Xóa profile
        return profileDAO.deleteProfile(profileId);
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
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

    public boolean deleteAllProfiles() {
        try {
            List<Profile> profiles = profileDAO.getAllProfiles();
            for (Profile profile : profiles) {
                // Xóa tất cả các category liên quan đến profile
                categoryDAO.deleteByProfileId(profile.getId());
                // Xóa profile
                profileDAO.deleteProfile(profile.getId());
            }
            return true;  // Nếu tất cả các profile và categories đều được xóa thành công
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // Nếu có lỗi xảy ra, trả về false
        }
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

    public boolean isProfileExistByName(String profileName) {
        if (profileName == null || profileName.isEmpty()) {
            System.out.println("Tên người dùng không hợp lệ!");
            return false;
        }

        return profileDAO.isProfileExistByName(profileName);  // Gọi phương thức trong ProfileDAO
    }

    public List<String> getAllProfileNames() {
        return profileDAO.getAllProfileNames();
    }




}
