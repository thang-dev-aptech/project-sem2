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
        String sql = """
                SELECT
                m.id,
                m.full_name,
                m.phone,
                p.name AS packageName,
                s.end_date,
                DATEDIFF(s.end_date, CURDATE()) AS days_left
                FROM members m
                JOIN subscriptions s ON m.id = s.member_id
                JOIN plans p ON s.plan_id = p.id
                WHERE DATEDIFF(s.end_date, CURDATE()) BETWEEN 0 AND ?
                  AND s.status = 'ACTIVE'
                  AND m.is_deleted = 0
                ORDER BY s.end_date ASC;

                                """;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, maxDayLeft);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("full_name");
                String phone = rs.getString("phone");
                String packageName = rs.getString("packageName");
                LocalDate endDate = rs.getDate("end_date").toLocalDate();
                int daysLeft = (int) ChronoUnit.DAYS.between(LocalDate.now(), endDate);

                list.add(new ExpiringMember(id, name, packageName, endDate.toString(), daysLeft, phone));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
