module com.example.gympro {
    // requires javafx.controls;
    // requires javafx.fxml;
    //
    // requires org.controlsfx.controls;
    // requires org.kordamp.bootstrapfx.core;
    //
    // opens com.example.gympro to javafx.fxml;
    // exports com.example.gympro;

    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;

    opens com.example.gympro.viewModel to com.google.gson;
    opens com.example.gympro.controller to javafx.fxml;
    opens com.example.gympro to javafx.fxml;

    exports com.example.gympro;
    exports com.example.gympro.controller;
    exports com.example.gympro.viewModel;
}