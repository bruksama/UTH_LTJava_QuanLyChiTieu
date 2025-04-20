package main.java.com.expensemanager.dao;

import java.sql.*;

public class ConnectorDAO {
    private static ConnectorDAO instance;
    private static final String DEFAULT_DB_URL = "jdbc:sqlite:database.db";
    private final String dbURL;

    private static final String CREATE_PROFILES_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS profiles (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL , " +
                    "createAt DATETIME DEFAULT CURRENT_TIMESTAMP)";

    private static final String CREATE_TRANSACTIONS_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "profileId INTEGER NOT NULL, " +
                    "type TEXT, " +
                    "categoryId INTEGER NOT NULL, " +
                    "description TEXT, " +
                    "date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "amount REAL NOT NULL, " +
                    "FOREIGN KEY (profileId) REFERENCES profiles(id), " +
                    "FOREIGN KEY (categoryId) REFERENCES categories(id))";

    private static final String CREATE_CATEGORIES_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS categories (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "type TEXT NOT NULL, " +
                    "profileId INTEGER NOT NULL, " +
                    "FOREIGN KEY (profileId) REFERENCES profiles(id))";

    private static final String CREATE_REPORTS_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS reports (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "profileId INTEGER NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "description TEXT, " +
                    "totalTransaction INTEGER NOT NULL, " +
                    "totalAmount REAL NOT NULL, " +
                    "startDate DATETIME NOT NULL, " +
                    "endDate DATETIME NOT NULL, " +
                    "generatedDate DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (profileId) REFERENCES profiles(id))";

    private ConnectorDAO(String dbURL) {
        this.dbURL = dbURL;
    }

    public static synchronized ConnectorDAO getInstance() {
        return getInstance(DEFAULT_DB_URL);
    }

    public static synchronized ConnectorDAO getInstance(String dbURL) {
        if (instance == null) {
            instance = new ConnectorDAO(dbURL);
        }
        return instance;
    }

    public boolean initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON");

            stmt.execute(CREATE_PROFILES_TABLE_SQL);
            stmt.execute(CREATE_CATEGORIES_TABLE_SQL);
            stmt.execute(CREATE_TRANSACTIONS_TABLE_SQL);
            stmt.execute(CREATE_REPORTS_TABLE_SQL);

            System.out.println("Khởi tạo thành công!");
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi khi khởi tạo dữ liệu: " + e.getMessage());
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(dbURL);
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public boolean isConnected() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static synchronized void resetInstance() {
        instance = null;
    }
}
