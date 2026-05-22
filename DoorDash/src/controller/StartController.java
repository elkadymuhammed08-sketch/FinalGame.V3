package controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import view.GameView;
import view.StartView;
import controller.GameController;
import game.engine.Role;
import game.engine.Game;

import java.io.IOException;

public class StartController {
    private final StartView view;
    private final Stage stage;

    public StartController(StartView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        setupActions();
    }

    private void setupActions() {
        view.getStartBtn().setOnAction(e -> {
            String selected = view.getRoleCombo().getValue();

            if (selected == null || selected.isEmpty()) {
                showNotification("Please select either SCARER or LAUGHER to continue.", "error");
                return;
            }

            try {
                showNotification("Initializing game engine...", "info");
                
                Game engine = new Game(Role.valueOf(selected));
                
                GameView gameView = new GameView();
                GameController gameCtrl = new GameController(gameView, engine, stage);
                
                stage.setScene(new Scene(gameView.getRootContainer(), 1280, 850));
                gameCtrl.initializeUI();
                showNotification("", "clear");
                
            } catch (IOException ex) {
                showNotification("Failed to load game data: " + ex.getMessage(), "error");
            } catch (Exception ex) {
                showNotification("Initialization failed: " + ex.getMessage(), "error");
            }
        });
    }

    private void showNotification(String message, String type) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Label lbl = view.getNotificationLabel();
                lbl.setText(message);
                if (type.equals("error")) {
                    lbl.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold; -fx-font-size: 14px;");
                } else if (type.equals("info")) {
                    lbl.setStyle("-fx-text-fill: #007bff; -fx-font-weight: bold; -fx-font-size: 14px;");
                } else {
                    lbl.setStyle("-fx-text-fill: transparent;");
                }
            }
        });
    }
}