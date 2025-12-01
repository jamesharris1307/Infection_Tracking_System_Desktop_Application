module com.example.infection_monitoring_system_desktop_application {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires javafx.base;
    requires javafx.graphics;
    requires java.logging;

    opens com.example.infection_monitoring_system_desktop_application to javafx.fxml;
    opens com.example.infection_monitoring_system_desktop_application.Controller to javafx.fxml;
    exports com.example.infection_monitoring_system_desktop_application;
}