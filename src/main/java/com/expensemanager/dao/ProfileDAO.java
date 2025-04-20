package main.java.com.expensemanager.dao;


import main.java.com.expensemanager.model.Profile;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
;
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
    private static final String SELECT_BY_USERNAME_SQL = "SELECT * FROM profiles WHERE name = ?";

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
                        profile.setId(generatedKeys.getInt(1));
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
//    public boolean deleteProfile(int profileId) {
//        try (Connection conn = dbConnector.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
//
//            stmt.setInt(1, profileId); // Tham số truyền vào là profileId
//
//            int affectedRows = stmt.executeUpdate();
//            return affectedRows > 0;
//        } catch (SQLException e) {
//            System.err.println(e.getMessage());
//            return false;
//        }
//    }


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

    public Profile getProfileByUsername(String selectedProfile) {
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USERNAME_SQL)) {

            stmt.setString(1, selectedProfile);  // Gán giá trị tên vào câu truy vấn
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Profile profile = new Profile();
                profile.setId(rs.getInt("id"));
                profile.setName(rs.getString("name"));

                profile.setCreateAt(rs.getString("createAt"));
                return profile;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isProfileExistByName(String newProfileName) {
        String query = "SELECT COUNT(*) FROM profiles WHERE name = ?";  // Truy vấn kiểm tra sự tồn tại của profile

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newProfileName);  // Truyền tên hồ sơ vào câu truy vấn
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Kiểm tra số lượng profile với tên đã cho
                int count = rs.getInt(1);
                System.out.println("Đã tìm thấy " + count + " profile với tên '" + newProfileName + "'.");
                return count > 0;  // Nếu số lượng trả về > 0, hồ sơ đã tồn tại
            }
            return false;  // Nếu không tìm thấy hồ sơ với tên này
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if profile exists by name: " + e.getMessage(), e);
        }
    }



    public List<String> getAllProfileNames() {
        List<String> profileNames = new ArrayList<>();
        String query = "SELECT DISTINCT name FROM profiles";  // Truy vấn để lấy tên tất cả các profile

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                profileNames.add(rs.getString("name"));  // Thêm tên profile vào danh sách
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách profile: " + e.getMessage());
        }

        // In ra kết quả để kiểm tra dữ liệu
        System.out.println("Danh sách profile lấy từ cơ sở dữ liệu: " + profileNames);
        return profileNames;
    }

    public boolean deleteProfileByName(String profileName) {
        String query = "DELETE FROM profiles WHERE name = ?";
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, profileName);  // Truyền tên profile vào câu truy vấn
            int affectedRows = stmt.executeUpdate();

            // Kiểm tra số lượng bản ghi bị xóa
            System.out.println("Affected rows: " + affectedRows);

            return affectedRows > 0;  // Trả về true nếu xóa thành công
        } catch (SQLException e) {
            System.err.println("Error deleting profile by name: " + e.getMessage());
            return false;
        }
    }






}


