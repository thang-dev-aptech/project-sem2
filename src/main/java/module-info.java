module com.example.gympro {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.gympro to javafx.fxml;
    exports com.example.gympro;
}