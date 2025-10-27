package com.example.gympro.controller;

import com.example.gympro.viewModel.ExpiringMember;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

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
    private TableColumn<ExpiringMember, String> colStatus;
    @FXML
    private TableColumn<ExpiringMember, Void> colActions;

    @FXML
    private ComboBox<String> cbFilter;
    @FXML
    private TextField txtSearch;

    private ObservableList<ExpiringMember> memberList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        loadMembersFromJson();
        tblExpiry.setItems(memberList);

        cbFilter.getItems().addAll("Tất cả", "≤ 3 ngày", "≤ 7 ngày", "≤ 14 ngày");
        cbFilter.setValue("Tất cả");
        addActionButtonsToTable();

        cbFilter.setOnAction(event -> applyFilter());

        txtSearch.textProperty().addListener((obs, oldText, newText) -> applySearch(newText));
    }

    private void setupColumns() {
        colCode.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colPhone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        colPackage.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPackageName()));
        colEndDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiry()));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDaysLeft() <= 0 ? "Hết hạn"
                        : data.getValue().getDaysLeft() <= 7 ? "Sắp hết hạn" : "Còn hạn"));
    }

    private void loadMembersFromJson() {
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/com/example/gympro/json/expiring-members.json"))) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<ExpiringMember>>>() {
            }.getType();
            Map<String, List<ExpiringMember>> jsonData = gson.fromJson(reader, type);

            List<ExpiringMember> list = jsonData.get("members");
            memberList.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không thể tải dữ liệu từ JSON!").showAndWait();
        }
    }

    private void applyFilter() {
        String filter = cbFilter.getValue();
        ObservableList<ExpiringMember> filtered = FXCollections.observableArrayList(memberList);

        switch (filter) {
            case "≤ 3 ngày" -> filtered.removeIf(m -> m.getDaysLeft() > 3);
            case "≤ 7 ngày" -> filtered.removeIf(m -> m.getDaysLeft() > 7);
            case "≤ 14 ngày" -> filtered.removeIf(m -> m.getDaysLeft() > 14);
        }

        tblExpiry.setItems(filtered);
    }

    private void applySearch(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            tblExpiry.setItems(memberList);
            return;
        }

        String lower = keyword.toLowerCase();
        ObservableList<ExpiringMember> filtered = FXCollections.observableArrayList(
                memberList.filtered(m -> m.getName().toLowerCase().contains(lower)
                        || m.getPhone().toLowerCase().contains(lower)
                        || m.getId().toLowerCase().contains(lower)));

        tblExpiry.setItems(filtered);
    }

    private void addActionButtonsToTable() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnExtend = new Button("📝Gia hạn");
            private final Button btnCall = new Button("📞Gọi điện");
            private final Button btnEmail = new Button("📧Email");
            private final Button btnSMS = new Button("📱SMS");
            private final Button btnExport = new Button("📤Xuất ");

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
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

}
