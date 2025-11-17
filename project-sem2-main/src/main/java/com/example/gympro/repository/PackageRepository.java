package com.example.gympro.repository;

import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.Package;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PackageRepository implements PackageRepositoryInterface {

    // Giả định DatabaseConnection.getInstance() là Singleton
    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    private static final String BASE_SELECT_SQL =
            "SELECT id, branch_id, code, name, description, price, duration_days, is_active, created_at, updated_at FROM plans ";
    private static final String INSERT_SQL =
            "INSERT INTO plans (branch_id, code, name, description, price, duration_days, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE plans SET code = ?, name = ?, description = ?, price = ?, duration_days = ?, is_active = ? WHERE id = ?";
    private static final String DELETE_SQL =
            "DELETE FROM plans WHERE id = ?";

    @Override
    public List<Package> findAll() {
        return findAll(null, null); // Mặc định không tìm kiếm, không lọc trạng thái
    }

    @Override
    public List<Package> findAll(String searchTerm, Boolean isActive) {
        List<Package> packages = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder(BASE_SELECT_SQL);
        List<Object> params = new ArrayList<>();

        sqlBuilder.append(" WHERE 1=1");

        // 1. Lọc theo trạng thái
        if (isActive != null) {
            sqlBuilder.append(" AND is_active = ?");
            params.add(isActive ? 1 : 0);
        }

        // 2. Tìm kiếm theo từ khóa
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sqlBuilder.append(" AND (code LIKE ? OR name LIKE ? OR description LIKE ?)");
            String likeParam = "%" + searchTerm.trim() + "%";
            params.add(likeParam);
            params.add(likeParam);
            params.add(likeParam);
        }

        sqlBuilder.append(" ORDER BY id DESC"); // Sắp xếp theo ID

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            // Gán các tham số
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof Boolean) {
                    pstmt.setInt(i + 1, (Boolean) param ? 1 : 0);
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Package pkg = new Package();
                    pkg.setId(rs.getLong("id"));
                    pkg.setCode(rs.getString("code"));
                    pkg.setName(rs.getString("name"));
                    pkg.setDescription(rs.getString("description"));
                    pkg.setPrice(rs.getBigDecimal("price"));
                    pkg.setDurationDays(rs.getInt("duration_days"));
                    pkg.setIsActive(rs.getBoolean("is_active"));

                    Timestamp createdTs = rs.getTimestamp("created_at");
                    Timestamp updatedTs = rs.getTimestamp("updated_at");
                    if (createdTs != null) { pkg.setCreatedAt(createdTs.toLocalDateTime()); }
                    if (updatedTs != null) { pkg.setUpdatedAt(updatedTs.toLocalDateTime()); }

                    packages.add(pkg);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải gói tập (có tìm kiếm): " + e.getMessage());
        }
        return packages;
    }

    @Override
    public Optional<Package> insert(Package pkg) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, pkg.getBranchId());
            pstmt.setString(2, pkg.getCode());
            pstmt.setString(3, pkg.getName());
            pstmt.setString(4, pkg.getDescription());
            pstmt.setBigDecimal(5, pkg.getPrice());
            pstmt.setInt(6, pkg.getDurationDays());
            pstmt.setBoolean(7, pkg.isActive());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pkg.setId(generatedKeys.getLong(1));
                        return Optional.of(pkg);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm gói tập: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Package pkg) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {

            pstmt.setString(1, pkg.getCode());
            pstmt.setString(2, pkg.getName());
            pstmt.setString(3, pkg.getDescription());
            pstmt.setBigDecimal(4, pkg.getPrice());
            pstmt.setInt(5, pkg.getDurationDays());
            pstmt.setBoolean(6, pkg.isActive());
            pstmt.setLong(7, pkg.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật gói tập: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(long id) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa gói tập: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Package> findById(long id) { return Optional.empty(); } // Chưa implement
}