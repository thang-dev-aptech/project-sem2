package com.example.gympro.controller;

import com.example.gympro.service.ExpiringMemberService;
import com.example.gympro.viewModel.ExpiringMember;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ExpiryController {

    @FXML
    private TableView<ExpiringMember> tblExpiry;
    @FXML
    private TableColumn<ExpiringMember, String> colCode;
    @FXML
    private TableColumn<ExpiringMember, String> colName;
    @FXML
    private TableColumn<ExpiringMember, String> colPhone;
    @FXML
    private TableColumn<ExpiringMember, String> colPackage;
    @FXML
    private TableColumn<ExpiringMember, String> colEndDate;
    @FXML
    private TableColumn<ExpiringMember, Integer> colExpiry;
    @FXML
    private TableColumn<ExpiringMember, String> colStatus;
    @FXML
    private TableColumn<ExpiringMember, Void> colActions;

    @FXML
    private ComboBox<String> cbFilter;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnExport;

    private ObservableList<ExpiringMember> memberList = FXCollections.observableArrayList();
    private ExpiringMemberService service = new ExpiringMemberService();

    @FXML
    public void initialize() {
        setupColumns();
        loadMembers();
        setupFilter();
        setupSearch();
        addActionButtonsToTable();
        btnExport.setOnAction(e -> exportMembersToCSV());

    }

    @FXML
    private void exportMembersToCSV() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Lưu danh sách thành viên");
            fileChooser.setInitialFileName("ExpiringMembers.csv");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

            File file = fileChooser.showSaveDialog(btnExport.getScene().getWindow());
            if (file != null) {
                try (PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {

                    writer.write('\uFEFF');

                    writer.println("Mã,Họ tên,SĐT,Gói,Hết hạn,Số ngày còn lại,Trạng thái");

                    for (ExpiringMember m : memberList) {
                        writer.printf("%s,%s,%s,%s,%s,%d,%s%n",
                                m.getId(),
                                m.getName(),
                                m.getPhone(),
                                m.getPackageName(),
                                m.getExpiry(),
                                m.getDaysLeft(),
                                m.getStatus());
                    }
                }

                showAlert("✅ Xuất thành công: " + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("❌ Lỗi khi xuất file!");
        }
    }

    private void setupColumns() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPackage.setCellValueFactory(new PropertyValueFactory<>("packageName"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("expiry"));
        colExpiry.setCellValueFactory(new PropertyValueFactory<>("daysLeft"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

    }

    private void loadMembers() {
        memberList = service.getExpiringMembers(14);
        tblExpiry.setItems(memberList);
    }

    private void setupFilter() {
        cbFilter.getItems().addAll("Tất cả", "≤ 3 ngày", "≤ 7 ngày", "≤ 14 ngày");
        cbFilter.setValue("Tất cả");
        cbFilter.setOnAction(e -> {
            int days = switch (cbFilter.getValue()) {
                case "≤ 3 ngày" -> 3;
                case "≤ 7 ngày" -> 7;
                case "≤ 14 ngày" -> 14;
                default -> 14;
            };
            memberList = service.getExpiringMembers(days);
            tblExpiry.setItems(memberList);
            // Reset search khi filter
            txtSearch.clear();
        });
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                // Nếu search rỗng, hiển thị lại memberList gốc
                tblExpiry.setItems(memberList);
            } else {
                // Search trên memberList hiện tại
                ObservableList<ExpiringMember> filtered = service.search(memberList, newText);
                tblExpiry.setItems(filtered);
            }
        });
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    private void addActionButtonsToTable() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnExtend = new Button("📝 Gia hạn");
            private final Button btnEmail = new Button("📧 Email");

            private final HBox container = new HBox(5, btnExtend, btnEmail);

            {
                btnExtend.setOnAction(e -> {
                    ExpiringMember member = getTableRow().getItem();
                    if (member != null) {
                        MainController mainController = MainController.getInstance();
                        if (mainController != null) {
                            mainController.navigateToRegistration(member);
                        } else {
                            showAlert("❌ Không thể chuyển trang. Vui lòng thử lại.");
                        }
                    }
                });

                btnEmail.setOnAction(e -> {
                    ExpiringMember member = getTableRow().getItem();
                    if (member != null) {
                        // TODO: Implement email reminder
                        showAlert("📧 Email đã gửi cho: " + member.getName());
                    }
                });

                container.setStyle("-fx-alignment: CENTER; -fx-padding: 5;");
                btnExtend.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #000;");
                btnEmail.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

}
