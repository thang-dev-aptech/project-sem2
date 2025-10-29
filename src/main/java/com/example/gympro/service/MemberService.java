package com.example.gympro.service;

import com.example.gympro.viewModel.Member; // <-- Sử dụng lớp Member của bạn
import com.example.gympro.utils.DatabaseConnection; // <-- Sử dụng lớp kết nối của bạn
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberService {

    /**
     * Lấy tất cả thành viên từ cơ sở dữ liệu.
     */
    public ObservableList<Member> getAllMembers() {
        ObservableList<Member> memberList = FXCollections.observableArrayList();

        // !!! QUAN TRỌNG !!!
        // Câu SQL này PHẢI lấy TẤT CẢ các cột mà constructor của Member yêu cầu
        // Hãy sửa lại tên cột (ví dụ: join_date, package_type) cho khớp với CSDL
        String sql = "SELECT id, name, email, phone, join_date, status, package_type FROM members";

        // Sử dụng try-with-resources để tự động đóng kết nối
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Gọi constructor của Member với TẤT CẢ các tham số
                Member member = new Member(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("join_date"), // Sửa tên cột nếu cần
                        rs.getString("status"),
                        rs.getString("package_type") // Sửa tên cột nếu cần
                );
                memberList.add(member);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách thành viên: " + e.getMessage());
            e.printStackTrace();
            // Bạn nên ném (throw) lỗi hoặc hiển thị Alert ở đây
        }

        return memberList;
    }
}