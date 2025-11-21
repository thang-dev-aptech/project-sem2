package com.example.gympro.repository.reports;

import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.MemberReport;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository cho báo cáo hội viên
 */
public class MemberReportRepository {
    
    /**
     * Lấy báo cáo hội viên theo khoảng thời gian
     */
    public List<MemberReport> getMemberReport(LocalDate fromDate, LocalDate toDate) {
        List<MemberReport> reports = new ArrayList<>();
        String sql = """
            SELECT 
                m.member_code,
                m.full_name AS member_name,
                m.phone,
                m.email,
                m.status,
                p.name AS package_name,
                s.start_date,
                s.end_date,
                m.created_at
            FROM members m
            LEFT JOIN subscriptions s ON m.id = s.member_id AND s.status = 'ACTIVE'
            LEFT JOIN plans p ON s.plan_id = p.id
            WHERE m.created_at BETWEEN ? AND ?
            ORDER BY m.created_at DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(fromDate.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(toDate.atTime(23, 59, 59)));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MemberReport report = new MemberReport(
                        rs.getString("member_code"),
                        rs.getString("member_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("status"),
                        rs.getString("package_name"),
                        rs.getDate("start_date") != null ? rs.getDate("start_date").toLocalDate() : null,
                        rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null,
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime().toLocalDate() : null
                    );
                    reports.add(report);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }
    
    /**
     * Lấy tổng hợp hội viên
     */
    public MemberSummary getMemberSummary(LocalDate fromDate, LocalDate toDate) {
        String sql = """
            SELECT 
                COUNT(*) AS total_members,
                SUM(CASE WHEN m.created_at BETWEEN ? AND ? THEN 1 ELSE 0 END) AS new_members,
                SUM(CASE WHEN m.status = 'ACTIVE' THEN 1 ELSE 0 END) AS active_members,
                SUM(CASE WHEN m.status = 'EXPIRED' THEN 1 ELSE 0 END) AS expired_members
            FROM members m
            WHERE m.created_at BETWEEN ? AND ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            Timestamp from = Timestamp.valueOf(fromDate.atStartOfDay());
            Timestamp to = Timestamp.valueOf(toDate.atTime(23, 59, 59));
            ps.setTimestamp(1, from);
            ps.setTimestamp(2, to);
            ps.setTimestamp(3, from);
            ps.setTimestamp(4, to);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MemberSummary(
                        rs.getInt("total_members"),
                        rs.getInt("new_members"),
                        rs.getInt("active_members"),
                        rs.getInt("expired_members")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new MemberSummary(0, 0, 0, 0);
    }
    
    /**
     * Summary class cho hội viên
     */
    public static class MemberSummary {
        public final int totalMembers;
        public final int newMembers;
        public final int activeMembers;
        public final int expiredMembers;
        
        public MemberSummary(int totalMembers, int newMembers, int activeMembers, int expiredMembers) {
            this.totalMembers = totalMembers;
            this.newMembers = newMembers;
            this.activeMembers = activeMembers;
            this.expiredMembers = expiredMembers;
        }
    }
}

