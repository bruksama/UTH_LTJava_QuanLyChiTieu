package main.java.com.expensemanager.dao;

import main.java.com.expensemanager.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private final ConnectorDAO dbConnector;

    private static final String INSERT_SQL =
            "INSERT INTO categories (name, type, profileId) " +
                    "VALUES (?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE categories SET name = ?, type = ? " +
                    "WHERE id = ? AND profileId = ?";

    private static final String DELETE_SQL =
            "DELETE FROM categories WHERE id = ? AND profileId = ?";

    private static final String SELECT_BY_PROFILE_SQL =
            "SELECT * FROM categories WHERE profileId = ? ORDER BY name";

    private static final String SELECT_BY_ID =
            "SELECT * FROM categories WHERE id = ?";

    public CategoryDAO() {
        this.dbConnector = ConnectorDAO.getInstance();
    }

    public boolean insertCategory(Category category) {
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getType());
            stmt.setInt(3, category.getProfileId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        category.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteCategory(int id, int profileId) {
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id);
            stmt.setInt(2, profileId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean updateCategory(String name, String type) {
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, name);
            stmt.setString(2, type);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public Category getCategoryById(int id) {
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    return extractCategory(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public List<Category> getCategoriesByProfile(int profileId) {
        List<Category> categories = new ArrayList<>();

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_PROFILE_SQL)) {
            stmt.setInt(1, profileId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(extractCategory(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return categories;
    }

    private Category extractCategory(ResultSet rs) throws SQLException {
        return new Category(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getInt("profileId")
        );
    }

    public boolean preCreateCategory(int profileId) {
        try (Connection conn = dbConnector.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
                createDefaultIncomeCategories(stmt, profileId);
                createDefaultExpenseCategories(stmt, profileId);
                conn.commit();
                return true;
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    private void createDefaultIncomeCategories(PreparedStatement stmt, int profileId) throws SQLException {
        String[] categories = {"Lương", "Thưởng", "Đầu tư", "Bán hàng"};
        for (String category : categories) {
            stmt.setString(1, category);
            stmt.setString(2, Category.TYPE_INCOME);
            stmt.setInt(3, profileId);
        }
    }

    private void createDefaultExpenseCategories(PreparedStatement stmt, int profileId) throws SQLException {
        String[] categories = {"Ăn uống", "Di chuyển", "Thuê nhà", "Mua sắm", "Giải trí", "Sức khỏe", "Giáo dục"};
        for (String category : categories) {
            stmt.setString(1, category);
            stmt.setString(2, Category.TYPE_EXPENSE);
            stmt.setInt(3, profileId);
        }
    }
}
