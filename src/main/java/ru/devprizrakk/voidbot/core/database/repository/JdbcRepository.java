package ru.devprizrakk.voidbot.core.database.repository;

import ru.devprizrakk.voidbot.core.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class JdbcRepository<T> {
    protected final DatabaseManager databaseManager;

    protected JdbcRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Optional<T> findById(long id) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName() + " WHERE id = ?")) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    public List<T> findAll() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName());
             ResultSet resultSet = statement.executeQuery()) {
            List<T> items = new ArrayList<>();
            while (resultSet.next()) {
                items.add(map(resultSet));
            }
            return items;
        }
    }

    public boolean deleteById(long id) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM " + tableName() + " WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    protected long generatedId(PreparedStatement statement) throws SQLException {
        ResultSet keys = null;
        try {
            keys = statement.getGeneratedKeys();
            if (keys != null && keys.next()) {
                return keys.getLong(1);
            }
            return 0;
        } catch (SQLFeatureNotSupportedException ignored) {
            // Например, SQLite JDBC не реализует getGeneratedKeys()
            return 0;
        } finally {
            if (keys != null) {
                try { keys.close(); } catch (SQLException ignored) {}
            }
        }
    }

    protected PreparedStatement prepareInsert(Connection connection, String sql) throws SQLException {
        return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    protected abstract String tableName();

    protected abstract T map(ResultSet resultSet) throws SQLException;
}
