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
    requires java.sql;
    requires mysql.connector.j;
    requires jbcrypt;

    opens com.example.infection_monitoring_system_desktop_application
            to javafx.fxml, org.testfx.core, javafx.graphics;
    opens com.example.infection_monitoring_system_desktop_application.Controller
            to javafx.fxml, org.testfx.core, javafx.graphics;
    opens com.example.infection_monitoring_system_desktop_application.Model
            to javafx.fxml, org.testfx.core, javafx.graphics;

    exports com.example.infection_monitoring_system_desktop_application;
    exports com.example.infection_monitoring_system_desktop_application.Model;
    exports com.example.infection_monitoring_system_desktop_application.Util;
    opens com.example.infection_monitoring_system_desktop_application.Util to javafx.fxml, javafx.graphics, org.testfx.core;
    exports com.example.infection_monitoring_system_desktop_application.DataAccessObject;
    opens com.example.infection_monitoring_system_desktop_application.DataAccessObject to javafx.fxml, javafx.graphics, org.testfx.core;
}