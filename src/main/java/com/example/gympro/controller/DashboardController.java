package com.example.gympro.controller;

import com.example.gympro.viewModel.DashboardStat;
import com.example.gympro.viewModel.ExpiringMember;
import com.example.gympro.viewModel.PieStats;
import com.example.gympro.viewModel.RevenueData;
import com.example.gympro.service.DashboardService;
import com.example.gympro.service.ExpiringMemberService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

public class DashboardController {

    @FXML
    private GridPane statsGrid;

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
    private TableColumn<ExpiringMember, Integer> colExpiry;
    @FXML
    private TableColumn<ExpiringMember, String> colEndDate;
    @FXML
    private TableColumn<ExpiringMember, Void> colActions;

    @FXML
    private Button btnAddMembers;
    @FXML
    private Button btnRegistraction;
    @FXML
    private Button btnPayment;

    @FXML
    private BarChart<String, Number> revenueBarChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private PieChart memberPieChart;

    private final DashboardService dashboardService = new DashboardService();
    private final ExpiringMemberService memberService = new ExpiringMemberService();
    private ObservableList<ExpiringMember> memberList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadDashboardStats();
        setupColumns();
        loadMembers();
        addActionButtonsToTable();
        loadRevenueChart();
        loadMemberPieChart();
    }

    private void loadDashboardStats() {
        List<DashboardStat> stats = dashboardService.getDashboardStats();
        statsGrid.getChildren().clear();

        int col = 0;
        for (DashboardStat stat : stats) {
            VBox card = createStatCard(stat);
            statsGrid.add(card, col++, 0);
        }
    }

    private VBox createStatCard(DashboardStat stat) {
        VBox card = new VBox(10);
        card.getStyleClass().add("dashboard-card");
        card.setStyle("-fx-background-color: " + stat.getBgColor());

        Label icon = new Label(stat.getIcon());
        icon.getStyleClass().add("dashboard-icon");

        Label label = new Label(stat.getLabel());
        label.getStyleClass().add("dashboard-label");

        Label value = new Label(stat.getValue());
        value.getStyleClass().add("dashboard-value");

        card.getChildren().addAll(icon, label, value);
        return card;
    }

    private void setupColumns() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPackage.setCellValueFactory(new PropertyValueFactory<>("packageName"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("expiry"));
        colExpiry.setCellValueFactory(new PropertyValueFactory<>("daysLeft"));
    }

    private void loadMembers() {
        memberList = memberService.getExpiringMembers(3);
        tblExpiry.setItems(memberList);
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

    private void loadRevenueChart() {
        var revenueList = dashboardService.getMonthlyRevenue();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu theo tháng");

        for (RevenueData data : revenueList) {
            String monthLabel = data.getLabel();
            double total = data.getTotalRevenue();
            series.getData().add(new XYChart.Data<>(monthLabel, total));
        }

        revenueBarChart.getData().clear();
        revenueBarChart.getData().add(series);

        revenueBarChart.setTitle("Doanh thu theo tháng");

    }

    private void loadMemberPieChart() {
        PieStats stats = dashboardService.getPieStats();

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Active: " + stats.getActiveMembers(), stats.getActiveMembers()),
                new PieChart.Data("Expiring: " + stats.getExpiringMembers(), stats.getExpiringMembers()),
                new PieChart.Data("Expired: " + stats.getExpiredMembers(), stats.getExpiredMembers()));

        memberPieChart.setData(pieData);
        memberPieChart.setTitle("Member Status");
        memberPieChart.setLegendVisible(false);

        showDataLabels(memberPieChart);
    }

    private void showDataLabels(PieChart chart) {
        chart.getData().forEach(data -> {
            Text label = new Text(data.getName());

            data.getNode().parentProperty().addListener((obs, oldParent, parent) -> {
                if (parent != null) {
                    ((javafx.scene.Group) parent).getChildren().add(label);
                }
            });

            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.layoutXProperty().addListener((o, oldX, newX) -> label.setLayoutX(newX.doubleValue()));
                    newNode.layoutYProperty().addListener((o, oldY, newY) -> label.setLayoutY(newY.doubleValue()));
                }
            });
        });
    }
}