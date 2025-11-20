module com.example.gympro {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    
    // Automatic modules (libraries without module-info.java)
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;
    requires java.net.http;
    // Note: MySQL connector loaded via Class.forName(), no module declaration needed
    
    // Logging (optional - only if directly used in code)
    // requires org.slf4j;
    // requires ch.qos.logback.classic;

    opens com.example.gympro.viewModel to com.google.gson;
    opens com.example.gympro.controller to javafx.fxml;
    opens com.example.gympro to javafx.fxml;

    exports com.example.gympro;
    exports com.example.gympro.controller;
    exports com.example.gympro.viewModel;
    exports com.example.gympro.utils;
}