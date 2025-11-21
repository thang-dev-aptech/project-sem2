package com.example.gympro.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.ExpiringMember;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ExpiringMemberRepository {
    public ObservableList<ExpiringMember> getExpiringMembers(int maxDayLeft) {
        ObservableList<ExpiringMember> list = FXCollections.observableArrayList();
        
        // Sử dụng maxDayLeft trực tiếp (default 7 nếu không chỉ định)
        int maxDays = maxDayLeft > 0 ? maxDayLeft : 7;
        
        // Lấy members sắp hết hạn (trong vòng maxDayLeft ngày) hoặc đã hết hạn (trong vòng 30 ngày qua)
        String sql = """
                      SELECT
                      m.member_code,
                      m.full_name,
                      m.phone,
                      p.name AS packageName,
                      s.end_date,
                      DATEDIFF(s.end_date, CURDATE()) AS days_left
                  FROM members m
                  INNER JOIN subscriptions s ON m.id = s.member_id
                  INNER JOIN plans p ON s.plan_id = p.id
                  INNER JOIN (
                      SELECT member_id, MAX(end_date) AS max_end_date
                      FROM subscriptions
                      GROUP BY member_id
                  ) latest ON s.member_id = latest.member_id 
                      AND s.end_date = latest.max_end_date
                  WHERE DATEDIFF(s.end_date, CURDATE()) BETWEEN -30 AND ?
                    AND m.is_deleted = 0
                  ORDER BY s.end_date ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maxDays);
            System.out.println("🔍 Query expiring members với maxDayLeft = " + maxDayLeft);
            ResultSet rs = ps.executeQuery();

            int count = 0;
            while (rs.next()) {
                count++;
                String id = rs.getString("member_code");
                String name = rs.getString("full_name");
                String phone = rs.getString("phone");
                String packageName = rs.getString("packageName");
                LocalDate endDate = rs.getDate("end_date").toLocalDate();
                int daysLeft = (int) ChronoUnit.DAYS.between(LocalDate.now(), endDate);

                System.out.println("✅ Found: " + id + " - " + name + " - Days left: " + daysLeft);
                list.add(new ExpiringMember(id, name, packageName, endDate.toString(), daysLeft, phone));
            }
            System.out.println("📊 Total expiring members found: " + count);
        } catch (SQLException e) {
            System.err.println("❌ SQL Error in getExpiringMembers: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

}
