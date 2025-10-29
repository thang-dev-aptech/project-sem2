package com.example.gympro.controller;

import com.example.gympro.service.MemberService;
import com.example.gympro.viewModel.Member;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RegistrationController {
    @FXML public ComboBox memberComboBox;
    @FXML public ComboBox packageComboBox;
    @FXML public DatePicker startDatePicker;
    @FXML public TextField endDateField;
    @FXML public Button createButton;
    @FXML public Label summaryMemberLabel;
    @FXML public Label summaryPackageLabel;
    @FXML public Label summaryDurationLabel;
    @FXML public Label summaryStartDateLabel;
    @FXML public Label summaryEndDateLabel;
    @FXML public Label summaryAmountLabel;

    // Service để lấy dữ liệu
    private final MemberService memberService = new MemberService();

    // Định dạng ngày
    private final DateTimeFormatter summaryDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Phương thức này được tự động gọi sau khi FXML được tải.
     */
    @FXML
    private void initialize() {

        // === MÃ MỚI: Dạy ComboBox cách hiển thị Member ===
        memberComboBox.setConverter(new StringConverter<Member>() {
            @Override
            public String toString(Member member) {
                // Nếu member là null, hiển thị rỗng, ngược lại hiển thị tên
                // Bạn có thể đổi member.getName() thành bất cứ thứ gì bạn muốn
                return (member == null) ? "" : member.getName();
            }

            @Override
            public Member fromString(String string) {
                // Không cần thiết cho ComboBox chỉ để chọn
                return null;
            }
        });
        // ===================================================

        // 1. Tải dữ liệu thành viên
        loadMemberData();

        // 2. Thiết lập ngày bắt đầu
        startDatePicker.setValue(LocalDate.now());
        updateSummaryStartDate(LocalDate.now());
        startDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                updateSummaryStartDate(newDate);
            } else {
                summaryStartDateLabel.setText("N/A");
            }
        });

        // 3. Thêm trình lắng nghe để cập nhật Tóm tắt khi chọn thành viên
        memberComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldMember, newMember) -> {
            if (newMember != null) {
                // Chúng ta lấy tên từ đối tượng newMember
                summaryMemberLabel.setText(newMember.getClass().getName());
            } else {
                summaryMemberLabel.setText("None");
            }
        });
    }

    /**
     * Phương thức mới để tải dữ liệu thành viên vào ComboBox.
     */
    private void loadMemberData() {
        try {
            ObservableList<Member> members = memberService.getAllMembers();
            memberComboBox.setItems(members);
        } catch (Exception e) {
            System.err.println("Không thể tải danh sách thành viên: " + e.getMessage());
            e.printStackTrace();
            // (Bạn nên thêm một Alert ở đây để thông báo lỗi cho người dùng)
        }
    }

    /**
     * Được gọi khi nhấn nút "Create Registration".
     */
    @FXML
    void handleCreateRegistration(ActionEvent event) {
        // === Lấy đối tượng Member đã chọn ===
        Member selectedMember = (Member) memberComboBox.getSelectionModel().getSelectedItem();
        String selectedPackage = packageComboBox.getValue().toString();
        LocalDate startDate = startDatePicker.getValue();

        // Kiểm tra lỗi
        if (selectedMember == null) {
            System.out.println("Vui lòng chọn một thành viên!");
            // (Hiển thị cảnh báo)
            return;
        }
        if (selectedPackage == null) {
            System.out.println("Vui lòng chọn một gói tập!");
            // (Hiển thị cảnh báo)
            return;
        }

        // Bây giờ bạn có thể truy cập bất kỳ thông tin nào từ thành viên đã chọn
        String memberId = selectedMember.getId();
        String memberName = selectedMember.getName();

        System.out.println("Đã chọn Thành viên ID: " + memberId);
        System.out.println("Tên thành viên: " + memberName);
        System.out.println("Gói: " + selectedPackage);
        System.out.println("Ngày bắt đầu: " + startDate);

        // ... (Tiếp tục logic lưu đăng ký của bạn với memberId) ...
    }

    /**
     * Cập nhật nhãn ngày bắt đầu trong bản tóm tắt.
     */
    private void updateSummaryStartDate(LocalDate date) {
        if (date != null) {
            summaryStartDateLabel.setText(date.format(summaryDateFormatter));
        }
    }
}
