package com.example.gympro.controller;

import com.example.gympro.viewModel.Member;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.time.LocalDate;

public class MemberDialogController {

    @FXML public TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> packageCombo;
    @FXML private ComboBox<String> statusCombo;

    private Member member; // Đối tượng Member để chứa dữ liệu tạm thời

    @FXML
    public void initialize() {
        // Khởi tạo các ComboBox với dữ liệu cố định
        packageCombo.getItems().addAll("Basic", "Standard", "Premium");
        statusCombo.getItems().addAll("Active", "Inactive", "Suspended");
    }

    /**
     * Phương thức được gọi từ MembersController để truyền dữ liệu Member vào form
     * và thiết lập các giá trị ban đầu (cho chức năng Sửa).
     */
    public void setMember(Member member) {
        this.member = member;

        // Gán dữ liệu của Member vào các trường trên Form
        if (member.getName() != null && !member.getName().isEmpty()) {
            nameField.setText(member.getName());
            emailField.setText(member.getEmail());
            phoneField.setText(member.getPhone());
            packageCombo.setValue(member.getPackageType());
            statusCombo.setValue(member.getStatus());
        } else {
            // Thiết lập giá trị mặc định cho Thêm mới
            packageCombo.setValue("Basic");
            statusCombo.setValue("Active");
        }
    }

    /**
     * Lấy dữ liệu từ Form và cập nhật vào đối tượng Member
     */
    public Member getMember() {
        // Cập nhật các giá trị mới từ form vào đối tượng Member
        member.setName(nameField.getText());
        member.setEmail(emailField.getText());
        member.setPhone(phoneField.getText());
        member.setPackageType(packageCombo.getValue());
        member.setStatus(statusCombo.getValue());

        // Nếu là thêm mới, set JoinDate
        if (member.getJoinDate() == null || member.getJoinDate().isEmpty()) {
            member.setJoinDate(LocalDate.now().toString());
        }

        return member;
    }

    /**
     * Kiểm tra tính hợp lệ của dữ liệu trước khi đóng Dialog
     */
    public boolean isInputValid() {
        return nameField.getText() != null && !nameField.getText().trim().isEmpty() &&
                emailField.getText() != null && !emailField.getText().trim().isEmpty();
        // Có thể thêm logic kiểm tra định dạng email/số điện thoại phức tạp hơn
    }
}