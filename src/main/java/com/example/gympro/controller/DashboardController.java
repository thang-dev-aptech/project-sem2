package com.example.gympro.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.example.gympro.viewModel.DashboardStat;
import com.example.gympro.viewModel.Reminder;
import com.example.gympro.viewModel.RevenueData;
import com.example.gympro.utils.DataLoader;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {
    @FXML
    private GridPane statsGrid;
    @FXML
    private Pane revenueChartPane;
    @FXML
    private Pane memberStatusChartPane;
    @FXML
    private VBox urgentRemindersBox;

    @FXML
    private void initialize() {
        createStatCards();
        createRevenueChart();
        createMemberStatusChart();
        createUrgentReminders();
    }

    private void createStatCards() {
        List<DashboardStat> stats = loadDashboardStats();
        int col = 0;
        for (DashboardStat stat : stats) {
            VBox card = createStatCard(stat.getLabel(), stat.getValue(), stat.getIcon(), stat.getBgColor());
            statsGrid.add(card, col++, 0);
        }
    }

    private List<DashboardStat> loadDashboardStats() {
        List<DashboardStat> stats = new ArrayList<>();
        JsonArray statsArray = DataLoader.getJsonArray("json/dashboard-stats.json", "stats");
        
        for (JsonElement element : statsArray) {
            JsonObject obj = element.getAsJsonObject();
            stats.add(new DashboardStat(
                obj.get("label").getAsString(),
                obj.get("value").getAsString(),
                obj.get("icon").getAsString(),
                obj.get("bgColor").getAsString()
            ));
        }
        return stats;
    }

    private VBox createStatCard(String label, String value, String icon, String bgColor) {
        VBox card = new VBox(10);
        card.getStyleClass().add("stat-card");
        card.setPadding(new Insets(20));

        HBox content = new HBox(15);
        content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox textBox = new VBox(5);
        Label labelLbl = new Label(label);
        labelLbl.getStyleClass().add("stat-label");
        Label valueLbl = new Label(value);
        valueLbl.getStyleClass().add("stat-value");
        textBox.getChildren().addAll(labelLbl, valueLbl);

        StackPane iconBox = new StackPane();
        iconBox.getStyleClass().add("stat-icon");
        iconBox.setStyle("-fx-background-color: " + bgColor + ";");
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 24;");
        iconBox.getChildren().add(iconLbl);
        iconBox.setPrefSize(60, 60);

        content.getChildren().addAll(textBox, iconBox);
        HBox.setHgrow(textBox, javafx.scene.layout.Priority.ALWAYS);
        card.getChildren().add(content);

        return card;
    }

    private void createRevenueChart() {
        List<RevenueData> revenueList = loadRevenueData();
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Monthly Revenue");
        barChart.setStyle("-fx-font-size: 12;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");
        
        for (RevenueData data : revenueList) {
            series.getData().add(new XYChart.Data<>(data.getMonth(), data.getRevenue()));
        }

        barChart.getData().add(series);
        revenueChartPane.getChildren().add(barChart);
    }

    private List<RevenueData> loadRevenueData() {
        List<RevenueData> revenueList = new ArrayList<>();
        JsonArray revenueArray = DataLoader.getJsonArray("json/revenue-data.json", "monthlyRevenue");
        
        for (JsonElement element : revenueArray) {
            JsonObject obj = element.getAsJsonObject();
            revenueList.add(new RevenueData(
                obj.get("month").getAsString(),
                obj.get("revenue").getAsInt()
            ));
        }
        return revenueList;
    }

    private void createMemberStatusChart() {
        Label chartLabel = new Label("Member Status Chart\n(Pie chart implementation)");
        chartLabel.setStyle("-fx-font-size: 14; -fx-text-alignment: center;");
        memberStatusChartPane.getChildren().add(chartLabel);
    }

    private void createUrgentReminders() {
        Label title = new Label("Urgent Reminders - Expiring Within 5 Days");
        title.getStyleClass().add("title");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #b91c1c;");
        urgentRemindersBox.getChildren().add(title);

        List<Reminder> reminders = loadReminders();
        for (Reminder reminder : reminders) {
            VBox reminderCard = createReminderCard(reminder);
            urgentRemindersBox.getChildren().add(reminderCard);
        }
    }

    private List<Reminder> loadReminders() {
        List<Reminder> reminders = new ArrayList<>();
        JsonArray remindersArray = DataLoader.getJsonArray("json/urgent-reminders.json", "reminders");
        
        for (JsonElement element : remindersArray) {
            JsonObject obj = element.getAsJsonObject();
            reminders.add(new Reminder(
                obj.get("id").getAsString(),
                obj.get("name").getAsString(),
                obj.get("package").getAsString(),
                obj.get("expiryDate").getAsString(),
                obj.get("daysLeft").getAsString()
            ));
        }
        return reminders;
    }

    private VBox createReminderCard(Reminder reminder) {
        VBox card = new VBox(8);
        card.getStyleClass().add("reminder-card");
        card.setPadding(new Insets(15));

//        HBox header = new HBox(10);
//        header.setAlignment(javafx.geometry.Pos.SPACE_BETWEEN);
//        Label nameLbl = new Label(reminder.getName());
//        nameLbl.setStyle("-fx-font-weight: bold;");
//        Label daysLbl = new Label(reminder.getDaysLeft());
//        daysLbl.getStyleClass().add("badge-caution");
//        header.getChildren().addAll(nameLbl, daysLbl);
        HBox header = new HBox(10);
        Label nameLbl = new Label(reminder.getName());
        nameLbl.setStyle("-fx-font-weight: bold;");
        Label daysLbl = new Label(reminder.getDaysLeft());
        daysLbl.getStyleClass().add("badge-caution");

// ✅ Thêm một khoảng trống để đẩy 2 label ra hai bên (giống space-between)
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

// thêm tất cả vào header
        header.getChildren().addAll(nameLbl, spacer, daysLbl);


        Label idLbl = new Label(reminder.getId());
        idLbl.setStyle("-fx-font-size: 11; -fx-text-fill: #666666;");

        Label pkgLbl = new Label("Package: " + reminder.getPackageType());
        Label expiryLbl = new Label("Expires: " + reminder.getExpiryDate());

        card.getChildren().addAll(header, idLbl, pkgLbl, expiryLbl);
        return card;
    }
}
