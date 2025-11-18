package com.example.gympro.repository.plan;

import com.example.gympro.domain.plan.Plan;
import com.example.gympro.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PackageRepository implements PackageRepositoryInterface {

    private static final String BASE_SELECT_SQL =
            "SELECT id, branch_id, code, name, description, price, duration_days, is_active, created_at, updated_at FROM plans";
    private static final String INSERT_SQL =
            "INSERT INTO plans (branch_id, code, name, description, price, duration_days, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE plans SET code = ?, name = ?, description = ?, price = ?, duration_days = ?, is_active = ? WHERE id = ?";
    private static final String DELETE_SQL =
            "DELETE FROM plans WHERE id = ?";

    @Override
    public List<Plan> findAll() {
        return findAll(null, null);
    }

    @Override
    public List<Plan> findAll(String searchTerm, Boolean isActive) {
        List<Plan> plans = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder(BASE_SELECT_SQL).append(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (isActive != null) {
            sqlBuilder.append(" AND is_active = ?");
            params.add(isActive ? 1 : 0);
        }

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sqlBuilder.append(" AND (code LIKE ? OR name LIKE ? OR description LIKE ?)");
            String likeParam = "%" + searchTerm.trim() + "%";
            params.add(likeParam);
            params.add(likeParam);
            params.add(likeParam);
        }

        sqlBuilder.append(" ORDER BY id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else {
                    pstmt.setInt(i + 1, (Integer) param);
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    plans.add(mapResultSetToPlan(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải gói tập: " + e.getMessage());
        }
        return plans;
    }

    @Override
    public Optional<Plan> insert(Plan plan) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, plan.getBranchId());
            pstmt.setString(2, plan.getCode());
            pstmt.setString(3, plan.getName());
            pstmt.setString(4, plan.getDescription());
            pstmt.setBigDecimal(5, plan.getPrice());
            pstmt.setInt(6, plan.getDurationDays());
            pstmt.setBoolean(7, plan.isActive());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long newId = generatedKeys.getLong(1);
                        return Optional.of(plan.toBuilder().id(newId).build());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm gói tập: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Plan plan) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {

            pstmt.setString(1, plan.getCode());
            pstmt.setString(2, plan.getName());
            pstmt.setString(3, plan.getDescription());
            pstmt.setBigDecimal(4, plan.getPrice());
            pstmt.setInt(5, plan.getDurationDays());
            pstmt.setBoolean(6, plan.isActive());
            pstmt.setLong(7, plan.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật gói tập: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(long id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa gói tập: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Plan> findById(long id) {
        throw new UnsupportedOperationException("Chưa implement findById");
    }

    private Plan mapResultSetToPlan(ResultSet rs) throws SQLException {
        Timestamp createdTs = rs.getTimestamp("created_at");
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        return new Plan.Builder()
                .id(rs.getLong("id"))
                .branchId(rs.getLong("branch_id"))
                .code(rs.getString("code"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .price(rs.getBigDecimal("price"))
                .durationDays(rs.getInt("duration_days"))
                .active(rs.getBoolean("is_active"))
                .createdAt(createdTs != null ? createdTs.toLocalDateTime() : null)
                .updatedAt(updatedTs != null ? updatedTs.toLocalDateTime() : null)
                .build();
    }
}

