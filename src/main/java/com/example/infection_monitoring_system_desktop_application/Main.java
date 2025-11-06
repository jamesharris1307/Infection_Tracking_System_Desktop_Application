package com.example.infection_monitoring_system_desktop_application;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.shape.Line;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Group root = new Group();
        Scene scene = new Scene(root,600,600, Color.SKYBLUE);
        Stage stage = new Stage();

        Text text = new Text();
        text.setText("Yo Dude wtf");
        text.setX(50);
        text.setY(50);
        text.setFont(Font.font("Verdana",50));
        text.setFill(Color.LIMEGREEN);

        Line line = new Line();
        line.setStartX(200);
        line.setStartY(200);
        line.setEndX(500);
        line.setEndY(200);

        root.getChildren().add(text);
        root.getChildren().add(line);
        stage.setScene(scene);
        stage.show();
    }
}
