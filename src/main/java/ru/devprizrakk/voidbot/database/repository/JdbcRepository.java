package ru.devprizrakk.voidbot.database.repository;

import ru.devprizrakk.voidbot.database.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public abstract class JdbcRepository<T> {

    protected final DatabaseManager databaseManager;

    protected JdbcRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Optional<T> findById(long id) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM " + tableName() + " WHERE id = ?"
             )) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public List<T> findAll() throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM " + tableName()
             );
             ResultSet rs = stmt.executeQuery()) {

            List<T> items = new ArrayList<>();
            while (rs.next()) items.add(map(rs));
            return items;
        }
    }

    public boolean deleteById(long id) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM " + tableName() + " WHERE id = ?"
             )) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    protected long generatedId(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs != null && rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch ( SQLFeatureNotSupportedException ignored ) {
            return 0;
        }
    }

    protected PreparedStatement prepareInsert(Connection conn, String sql) throws SQLException {
        return conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    protected static Timestamp toTs(Date date) {
        return date == null ? null : new Timestamp(date.getTime());
    }

    protected abstract String tableName();

    protected abstract T map(ResultSet rs) throws SQLException;
}
