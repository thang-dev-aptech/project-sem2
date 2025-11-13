package com.example.gympro.controller;

import com.example.gympro.service.ExpiringMemberService;
import com.example.gympro.viewModel.ExpiringMember;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

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
    private TableColumn<ExpiringMember, String> colExpiry;
    @FXML
    private TableColumn<ExpiringMember, String> colStatus;
    @FXML
    private TableColumn<ExpiringMember, Void> colActions;

    @FXML
    private ComboBox<String> cbFilter;
    @FXML

    private TextField txtSearch;

    private ObservableList<ExpiringMember> memberList = FXCollections.observableArrayList();
    private ExpiringMemberService service = new ExpiringMemberService();

    @FXML
    public void initialize() {
        setupColumns();
        loadMembers();
        setupFilter();
        setupSearch();
        addActionButtonsToTable();

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
            tblExpiry.setItems(service.getExpiringMembers(days));
        });

    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((obs, oldText, newText) -> {
            ObservableList<ExpiringMember> filtered = service.search(memberList, newText);
            tblExpiry.setItems(filtered);
        });
    }

    private void addActionButtonsToTable() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnExtend = new Button("📝 Gia hạn");
            private final Button btnCall = new Button("📞 Gọi điện");
            private final Button btnEmail = new Button("📧 Email");
            private final Button btnSMS = new Button("📱 SMS");
            private final Button btnExport = new Button("📤 Xuất");

            private final HBox container = new HBox(5, btnExtend, btnCall, btnEmail, btnSMS, btnExport);

            {
                container.setStyle("-fx-alignment: CENTER; -fx-padding: 5;");
                btnExtend.setStyle("-fx-background-color: #FFD700;");
                btnCall.setStyle("-fx-background-color: #90EE90;");
                btnEmail.setStyle("-fx-background-color: #87CEFA;");
                btnSMS.setStyle("-fx-background-color: #DDA0DD;");
                btnExport.setStyle("-fx-background-color: #FFA07A;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

}
