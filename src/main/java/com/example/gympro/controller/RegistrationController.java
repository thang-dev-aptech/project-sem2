package com.example.gympro.controller;

import com.example.gympro.service.RegistrationService;
import com.example.gympro.service.RegistrationServiceInterface;
import com.example.gympro.viewModel.ExpiringMember;
import com.example.gympro.viewModel.Member;
import com.example.gympro.viewModel.Plan;
import javafx.collections.FXCollections; // <-- Quan trọng
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List; // <-- Quan trọng

public class RegistrationController {

    // --- FXML Injections ---
    @FXML
    private ComboBox<Member> memberComboBox;
    @FXML
    private ComboBox<Plan> packageComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private TextField endDateField;
    @FXML
    private Button createButton;
    @FXML
    private Label summaryMemberLabel;
    @FXML
    private Label summaryPackageLabel;
    @FXML
    private Label summaryDurationLabel;
    @FXML
    private Label summaryStartDateLabel;
    @FXML
    private Label summaryEndDateLabel;
    @FXML
    private Label summaryAmountLabel;

    // --- Service ---
    private final RegistrationServiceInterface registrationService = new RegistrationService();

    // --- Helpers ---
    private final DateTimeFormatter summaryDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DecimalFormat moneyFormatter = new DecimalFormat("₫#,###");
    private final long CURRENT_USER_ID = 1; // TODO: Lấy ID user thật

    @FXML
    private void initialize() {
        setupMemberComboBox();
        setupPackageComboBox();
        loadMemberData();
        loadPackageData();
        startDatePicker.setValue(LocalDate.now());

        // Listeners
        memberComboBox.valueProperty().addListener((obs, o, n) -> updateSummary());
        packageComboBox.valueProperty().addListener((obs, o, n) -> updateSummary());
        startDatePicker.valueProperty().addListener((obs, o, n) -> updateSummary());

        updateSummary();
    }

    private void loadMemberData() {
        try {
            // 1. Lấy List từ Service
            List<Member> members = registrationService.getMembersForRegistration();
            // 2. Chuyển sang ObservableList cho JavaFX
            memberComboBox.setItems(FXCollections.observableArrayList(members));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi tải hội viên", e.getMessage());
        }
    }

    private void loadPackageData() {
        try {
            // 1. Lấy List từ Service
            List<Plan> plans = registrationService.getPlansForRegistration();
            // 2. Chuyển sang ObservableList cho JavaFX
            packageComboBox.setItems(FXCollections.observableArrayList(plans));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi tải gói tập", e.getMessage());
        }
    }

    @FXML
    void handleCreateRegistration(ActionEvent event) {
        Member selectedMember = memberComboBox.getValue();
        Plan selectedPlan = packageComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = null;

        try {
            endDate = LocalDate.parse(endDateField.getText(), summaryDateFormatter);
        } catch (Exception e) {
            // Bỏ qua, validation ở dưới sẽ bắt
        }

        if (selectedMember == null || selectedPlan == null || startDate == null || endDate == null) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng chọn đầy đủ hội viên, gói tập và ngày.");
            return;
        }

        // Gọi Service (Bộ não)
        boolean success = registrationService.createRegistrationAndInvoice(
                selectedMember, selectedPlan, startDate, endDate, CURRENT_USER_ID);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công",
                    "Đã tạo Hóa đơn (chờ thanh toán) cho: " + selectedMember.getFullName());
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi CSDL", "Không thể lưu đăng ký.");
        }
    }

    // --- CÁC HÀM TIỆN ÍCH (HELPER METHODS) ---

    private void setupMemberComboBox() {
        memberComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Member member) {
                return (member == null) ? "Chọn hội viên..." : member.getFullName() + " (ID: " + member.getId() + ")";
            }

            @Override
            public Member fromString(String string) {
                return null;
            }
        });
        memberComboBox.setPromptText("Chọn hội viên...");
    }

    private void setupPackageComboBox() {
        packageComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Plan plan) {
                if (plan == null)
                    return "Chọn gói tập...";
                return plan.getName() + " - " + plan.getDurationDays() + " ngày ("
                        + moneyFormatter.format(plan.getPrice()) + ")";
            }

            @Override
            public Plan fromString(String string) {
                return null;
            }
        });
        packageComboBox.setPromptText("Chọn gói tập...");
    }

    private void updateSummary() {
        Member member = memberComboBox.getValue();
        Plan plan = packageComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();

        summaryMemberLabel.setText(member != null ? member.getFullName() : "None");

        if (plan != null) {
            summaryPackageLabel.setText(plan.getName());
            summaryDurationLabel.setText(plan.getDurationDays() + " ngày");
            summaryAmountLabel.setText(moneyFormatter.format(plan.getPrice()));
        } else {
            summaryPackageLabel.setText("None");
            summaryDurationLabel.setText("N/A");
            summaryAmountLabel.setText("đ 0");
        }

        if (startDate != null) {
            summaryStartDateLabel.setText(startDate.format(summaryDateFormatter));
            if (plan != null) {
                LocalDate endDate = startDate.plusDays(plan.getDurationDays() - 1);
                endDateField.setText(endDate.format(summaryDateFormatter));
                summaryEndDateLabel.setText(endDate.format(summaryDateFormatter));
            } else {
                endDateField.clear();
                summaryEndDateLabel.setText("N/A");
            }
        } else {
            summaryStartDateLabel.setText("N/A");
            endDateField.clear();
            summaryEndDateLabel.setText("N/A");
        }
    }

    private void clearForm() {
        memberComboBox.setValue(null);
        packageComboBox.setValue(null);
        startDatePicker.setValue(LocalDate.now());
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void fillFormForRenewal(ExpiringMember expMember) {
        if (expMember == null)
            return;

        String memberCode = expMember.getId();
        Member foundMember = null;

        for (Member member : memberComboBox.getItems()) {
            if (member.getMemberCode().equals(memberCode)) {
                foundMember = member;
                break;
            }
        }

        if (foundMember != null) {
            memberComboBox.setValue(foundMember);
        } else {
            for (Member member : memberComboBox.getItems()) {
                if (member.getPhone().equals(expMember.getPhone())) {
                    foundMember = member;
                    break;
                }
            }
            if (foundMember != null) {
                memberComboBox.setValue(foundMember);
            } else {
                showAlert(Alert.AlertType.WARNING, "Không tìm thấy hội viên",
                        "Không tìm thấy hội viên với mã: " + memberCode);
            }
        }

        String packageName = expMember.getPackageName();
        if (packageName != null) {
            for (Plan plan : packageComboBox.getItems()) {
                if (plan.getName().equals(packageName)) {
                    packageComboBox.setValue(plan);
                    break;
                }
            }
        }

        LocalDate currentEndDate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            currentEndDate = LocalDate.parse(expMember.getExpiry(), formatter);
        } catch (DateTimeParseException ex) {
            ex.printStackTrace();
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate = (currentEndDate != null && currentEndDate.isAfter(today))
                ? currentEndDate.plusDays(1)
                : today;
        startDatePicker.setValue(startDate);

        Plan selectedPlan = packageComboBox.getValue();
        if (selectedPlan != null) {
            LocalDate endDate = startDate.plusDays(selectedPlan.getDurationDays() - 1);
            endDateField.setText(endDate.format(formatter));
        }

        updateSummary();
    }

}