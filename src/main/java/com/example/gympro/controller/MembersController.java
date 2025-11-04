// File: src/main/java/com/gympro.controller/MembersController.java
package com.example.gympro.controller;

import com.example.gympro.viewModel.Member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MembersController {

    @FXML
    private TableView<Member> membersTable;

    private ObservableList<Member> memberData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // 1. Ánh xạ các cột
        initializeColumns();

        // 2. Tải dữ liệu mẫu (thay cho JSON)
        loadSampleMembers();

        // 3. Liên kết dữ liệu với TableView
        membersTable.setItems(memberData);

        // 4. Thêm Double-click để sửa
        membersTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                handleEditMember();
            }
        });
    }

    // Ánh xạ các cột TableView với các thuộc tính Property của Member Model
    private void initializeColumns() {
        List<TableColumn<Member, ?>> columns = (List<TableColumn<Member, ?>>) (List<?>) membersTable.getColumns();

        if (columns.size() >= 6) {
            // Cột 0: Member ID
            ((TableColumn<Member, String>) columns.get(0)).setCellValueFactory(cellData -> cellData.getValue().idProperty());
            // Cột 1: Name
            ((TableColumn<Member, String>) columns.get(1)).setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            // Cột 2: Email
            ((TableColumn<Member, String>) columns.get(2)).setCellValueFactory(cellData -> cellData.getValue().emailProperty());
            // Cột 3: Phone
            ((TableColumn<Member, String>) columns.get(3)).setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
            // Cột 4: Package
            ((TableColumn<Member, String>) columns.get(4)).setCellValueFactory(cellData -> cellData.getValue().packageTypeProperty());
            // Cột 5: Status
            ((TableColumn<Member, String>) columns.get(5)).setCellValueFactory(cellData -> cellData.getValue().statusProperty());
            // Cột 6: Actions - Không cần ánh xạ
        }
    }

    // PHƯƠNG THỨC MỚI: Tải dữ liệu mẫu (thay thế loadMembersFromJson)
    private void loadSampleMembers() {
        memberData.add(new Member("M001", "Alex Johnson", "alex@gympro.com", "0910111213", "2024-01-15", "Active", "Standard"));
        memberData.add(new Member("M002", "Sarah Lee", "sarah@gympro.com", "0914151617", "2024-03-22", "Active", "Premium"));
        memberData.add(new Member("M003", "John Davis", "john@gympro.com", "0918192021", "2025-05-01", "Inactive", "Basic"));

        System.out.println("Loaded " + memberData.size() + " sample members.");
    }

    // ĐÃ LOẠI BỎ saveMembersToJson()

    // ===============================================
    // CÁC HÀM THAO TÁC CRUD
    // ===============================================

    @FXML
    private void handleAddMember() { // (CREATE)
        Member newMember = new Member("", "", "", "", "", "", "");

        showMemberDialog(newMember, "Add New Member").ifPresent(result -> {
            if (result == ButtonType.OK) {
                // Chỉ gán ID và JoinDate khi người dùng nhấn OK
                String newId = "M" + String.format("%03d", memberData.size() + 1);
                newMember.setId(newId);
                newMember.setJoinDate(LocalDate.now().toString()); // Thêm JoinDate

                memberData.add(newMember);
                // KHÔNG CẦN saveMembersToJson() NỮA
                membersTable.scrollTo(newMember);
            }
        });
    }

    @FXML
    private void handleEditMember() { // (UPDATE)
        Member selectedMember = membersTable.getSelectionModel().getSelectedItem();

        if (selectedMember != null) {
            // Tạo bản sao tạm thời để SỬA, tránh sửa đổi trực tiếp nếu người dùng Cancel
            Member tempMember = new Member(
                    selectedMember.getId(), selectedMember.getName(), selectedMember.getEmail(),
                    selectedMember.getPhone(), selectedMember.getJoinDate(), selectedMember.getStatus(), selectedMember.getPackageType()
            );

            showMemberDialog(tempMember, "Edit Member: " + selectedMember.getId()).ifPresent(result -> {
                if (result == ButtonType.OK) {
                    // Áp dụng thay đổi từ tempMember (đã được controller cập nhật) sang selectedMember
                    selectedMember.setName(tempMember.getName());
                    selectedMember.setEmail(tempMember.getEmail());
                    selectedMember.setPhone(tempMember.getPhone());
                    selectedMember.setStatus(tempMember.getStatus());
                    selectedMember.setPackageType(tempMember.getPackageType());

                    membersTable.refresh();
                    // KHÔNG CẦN saveMembersToJson() NỮA
                }
            });
        } else {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một thành viên để sửa.").showAndWait();
        }
    }

    @FXML
    private void handleDeleteMember() { // (DELETE)
        Member selectedMember = membersTable.getSelectionModel().getSelectedItem();

        if (selectedMember != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa thành viên " + selectedMember.getName() + " (ID: " + selectedMember.getId() + ")?", ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText("Xóa Thành Viên");

            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.YES) {
                memberData.remove(selectedMember);
                // KHÔNG CẦN saveMembersToJson() NỮA
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một thành viên để xóa.").showAndWait();
        }
    }

    /**
     * Hiển thị Dialog Form bằng cách tải FXML
     */
    private Optional<ButtonType> showMemberDialog(Member member, String title) {
        try {
            // Tải FXML Dialog và Controller tương ứng
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gympro/fxml/MemberDialog.fxml"));
            Parent root = loader.load();
            MemberDialogController controller = loader.getController();

            // Truyền dữ liệu Member vào Controller của Dialog
            controller.setMember(member);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle(title);
            dialog.setHeaderText(title.contains("Add") ? "Nhập thông tin thành viên mới." : "Chỉnh sửa thông tin thành viên.");
            dialog.getDialogPane().setContent(root);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Lấy nút OK để quản lý trạng thái
            Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

            // Thiết lập trạng thái ban đầu và Listener cho nút OK
            // (Hàm này vẫn hoạt động nếu bạn đã chỉnh sửa nameField thành public trong MemberDialogController)
            okButton.setDisable(!controller.isInputValid());
            controller.nameField.textProperty().addListener((obs, oldV, newV) ->
                    okButton.setDisable(!controller.isInputValid())
            );

            // Xử lý kết quả khi nhấn OK
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK && controller.isInputValid()) {
                    controller.getMember(); // Cập nhật đối tượng Member trong Dialog Controller
                    return ButtonType.OK;
                }
                return null;
            });

            return dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không thể tải Form Dialog FXML. Hãy kiểm tra MemberDialog.fxml và MemberDialogController.java").showAndWait();
            return Optional.empty();
        }
    }
}