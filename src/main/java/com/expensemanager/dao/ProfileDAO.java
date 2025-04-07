package main.java.com.expensemanager.dao;


import model.Profile;
import java.sql.*;

public class ProfileDAO {
   private static Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:your_database.db"; 
        return DriverManager.getConnection(url);
    }

    // Tạo hồ sơ mới
    public void createProfile(String profileName) {
        String query = "INSERT INTO profile (name, createdAt) VALUES (?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, profileName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật tên hồ sơ
    public void updateProfileName(int profileId, String newProfileName) {
        String query = "UPDATE profile SET name = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newProfileName);
            stmt.setInt(2, profileId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tạo hồ sơ mới
    public Profile getProfile(int profileId) {
        String query = "SELECT * FROM profile WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Profile(rs.getInt("id"), rs.getString("name"), rs.getString("createdAt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Xóa hồ sơ
    public void deleteProfile(int profileId) {
        String query = "DELETE FROM profile WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, profileId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

