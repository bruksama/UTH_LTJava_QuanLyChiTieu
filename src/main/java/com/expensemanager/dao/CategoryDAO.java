package main.java.com.expensemanager.dao;

import main.java.com.expensemanager.model.Category;
import java.sql.*;

public class CategoryDAO {
    private final ConnectorDAO dbConnector;

    private static final String INSERT_SQL =
            "INSERT INTO categories (name, type, profileId) +" +
                    "VALUES (?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE categories SET name = ?, type = ? " +
                    "WHERE id = ? AND profileId = ?";

    private static final String DELETE_SQL =
            "DELETE FROM categories WHERE id = ? AND profileId = ?";

    private static final String SELECT_BY_PROFILE_SQL =
            "SELECT * FROM categories WHERE profileId = ? ORDER BY name ASC";

    public CategoryDAO(String dbURL) {
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
}
