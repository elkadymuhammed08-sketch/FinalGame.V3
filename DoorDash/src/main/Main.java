package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.StartView;
import controller.StartController;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SCARER VS LAUGHER");
        primaryStage.setResizable(false);
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(850);

        StartView startView = new StartView();
        StartController startCtrl = new StartController(startView, primaryStage);

        Scene startScene = new Scene(startView.getRoot(), 1280, 850);
        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}