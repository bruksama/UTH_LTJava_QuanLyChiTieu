package main.java.com.expensemanager.dao;


import main.java.com.expensemanager.model.Profile;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileDAO {
    private final ConnectorDAO dbConnector;

    private static final String INSERT_SQL =
            "INSERT INTO profiles (name) VALUES (?)"; // Không cần truyền createdAt vì nó tự động cấp

    private static final String UPDATE_SQL =
            "UPDATE profiles SET name = ? WHERE id = ?"; // Không cập nhật createdAt vì nó tự động cấp

    private static final String DELETE_SQL =
            "DELETE FROM profiles WHERE id = ?";

    private static final String SELECT_BY_ID_SQL =
            "SELECT * FROM profiles WHERE id = ?";

    private static final String SELECT_ALL_SQL =
            "SELECT * FROM profiles ORDER BY name ASC";

    // Khởi tạo ProfileDAO với đối tượng ConnectorDAO
    public ProfileDAO() {
        this.dbConnector = ConnectorDAO.getInstance();
    }

    // Thêm một profile mới vào cơ sở dữ liệu
    public boolean insertProfile(Profile profile) {
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            // Chỉ truyền tên vì createdAt tự động cấp
            stmt.setString(1, profile.getName());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        profile.setId(generatedKeys.getInt(1));  // Lấy userID tự động cấp
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting profile: " + e.getMessage(), e);
        }
    }

    // Cập nhật thông tin của một profile
    public boolean updateProfile(Profile profile) {
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, profile.getName());
            stmt.setInt(2, profile.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating profile: " + e.getMessage(), e);
        }
    }

    // Xóa một profile khỏi cơ sở dữ liệu
    public boolean deleteProfile(int profileId) {
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, profileId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting profile: " + e.getMessage(), e);
        }
    }

    // Lấy một profile theo ID
    public Profile getProfileById(int profileId) {
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Profile profile = new Profile(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("createAt")  // Chuyển chuỗi thành LocalDateTime nếu cần
                );
                return profile;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving profile by ID: " + e.getMessage(), e);
        }
    }

    // Lấy tất cả các profiles
    public List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<>();
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Profile profile = new Profile(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("createAt")  // Chuyển chuỗi thành LocalDateTime nếu cần
                );
                profiles.add(profile);
            }
            return profiles;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all profiles: " + e.getMessage(), e);
        }
    }
    public boolean isProfileExist(int profileId) {
        String query = "SELECT COUNT(*) FROM profiles WHERE id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, profileId); // Gán giá trị profileId vào câu truy vấn
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;  // Nếu có ít nhất 1 dòng trả về thì hồ sơ tồn tại
            }
            return false;  // Không tìm thấy profile
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if profile exists by ID: " + e.getMessage(), e);
        }
    }

}


