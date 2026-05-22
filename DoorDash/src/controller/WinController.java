package controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import view.StartView;
import view.WinView;
import controller.StartController;

public class WinController {
    private final WinView view;
    private final Stage stage;

    public WinController(WinView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        view.getPlayAgainBtn().setOnAction(e -> returnToStart());
    }

    private void returnToStart() {
        StartView startView = new StartView();
        StartController startCtrl = new StartController(startView, stage);
        stage.setScene(new Scene(startView.getRoot(), 1280, 850));
    }
}