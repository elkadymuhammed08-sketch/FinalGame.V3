package controller;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.stage.Stage;
import game.engine.Game;
import game.engine.Board;
import game.engine.cells.Cell;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.cells.CardCell;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.ContaminationSock;
import game.engine.monsters.Monster;
import view.GameView;
import view.WinView;
import controller.WinController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameController {
    private final GameView view;
    private final Game engine;
    private final Stage stage;
    private boolean isProcessing = false;
    private boolean isAnimating = false;
    private final Map<Integer, StackPane> monsterIcons = new HashMap<>();
    private final Map<Integer, Timeline> monsterAnimations = new HashMap<>();
    private final Map<Monster, Integer> lastPositions = new HashMap<>();

    public GameController(GameView view, Game engine, Stage stage) {
        this.view = view;
        this.engine = engine;
        this.stage = stage;
        setupActions();
    }

    public void initializeUI() {
        renderBoardBase();
        updateAllUI();
        view.getRollBtn().setDisable(false);
        lastPositions.put(engine.getPlayer(), engine.getPlayer().getPosition());
        lastPositions.put(engine.getOpponent(), engine.getOpponent().getPosition());
        isProcessing = false;
        isAnimating = false;
    }

    private void setupActions() {
        view.getRollBtn().setOnAction(e -> {
            if (isProcessing) {
                showNotification("Please wait for animation to complete!", "error");
                return;
            }
            if (isAnimating) {
                showNotification("Animation in progress, please wait...", "error");
                return;
            }
            
            isProcessing = true;
            view.getRollBtn().setDisable(true);

            final int diceValue = (int)(Math.random() * 6) + 1;

            view.triggerDiceRollAnimation();
            view.setDiceResult(diceValue);
            view.setBonusValue(0);
            view.getNotificationLabel().setText("Rolled: " + diceValue);
            animateNotificationPulse();

            try {
                if (view.getPowerupCheck().isSelected()) {
                    engine.usePowerup();
                    showNotification("PowerUp activated!", "success");
                }

                Monster movingMonster = engine.getCurrent();
                int oldEnergy = movingMonster.getEnergy();
                int oldPlayerPos = engine.getPlayer().getPosition();
                int oldOpponentPos = engine.getOpponent().getPosition();

                engine.playTurn(diceValue);
                
                int newEnergy = movingMonster.getEnergy();
                if (newEnergy != oldEnergy) {
                    view.appendNotification(movingMonster.getName() + " got " + newEnergy + " energy!");
                }

                int newPlayerPos = engine.getPlayer().getPosition();
                int newOpponentPos = engine.getOpponent().getPosition();
                
                int actualPlayerMove = Math.abs(newPlayerPos - oldPlayerPos);
                int actualOpponentMove = Math.abs(newOpponentPos - oldOpponentPos);
                int actualMove = Math.max(actualPlayerMove, actualOpponentMove);
                
                int bonus = actualMove - diceValue;
                view.setBonusValue(bonus);
                
                if (bonus > 0) {
                    String reason = getBonusReason(newPlayerPos > oldPlayerPos ? newPlayerPos : oldOpponentPos);
                    view.showBonus(bonus, reason);
                }

                checkAndAnimateMovement(oldPlayerPos, oldOpponentPos);

                Monster winner = engine.getWinner();
                if (winner != null) {
                    showWinScreen(winner);
                    return;
                }
                
            } catch (Exception ex) {
                showNotification("Action Blocked: " + ex.getMessage(), "error");
                if (view.getPowerupCheck().isSelected()) {
                    view.getPowerupCheck().setSelected(false);
                }
                isProcessing = false;
                Platform.runLater(() -> view.getRollBtn().setDisable(false));
            }
        });
    }

    private String getBonusReason(int finalPos) {
        int row = finalPos / 10;
        int col = (row % 2 == 0) ? finalPos % 10 : 9 - (finalPos % 10);
        Cell cell = engine.getBoard().getBoardCells()[row][col];
        if (cell instanceof ConveyorBelt) return "Conveyor Belt";
        if (cell instanceof DoorCell) return "Door Boost";
        if (cell instanceof MonsterCell) return "Monster Push";
        if (cell instanceof ContaminationSock) return "Sock Slide";
        return "Bonus Effect";
    }

    private void checkAndAnimateMovement(int oldPlayerPos, int oldOpponentPos) {
        Monster player = engine.getPlayer();
        Monster opponent = engine.getOpponent();

        if (player.getPosition() != oldPlayerPos) {
            animateMonsterMovement(player, oldPlayerPos, player.getPosition(), true);
        } else if (opponent.getPosition() != oldOpponentPos) {
            animateMonsterMovement(opponent, oldOpponentPos, opponent.getPosition(), false);
        } else {
            updateAllUI();
            isProcessing = false;
            isAnimating = false;
            Platform.runLater(() -> view.getRollBtn().setDisable(false));
        }
    }

    private void animateMonsterMovement(Monster monster, int oldPos, int newPos, boolean isPlayer) {
        isAnimating = true;
        
        int oldRow = oldPos / 10;
        int oldCol = (oldRow % 2 == 0) ? oldPos % 10 : 9 - (oldPos % 10);
        int newRow = newPos / 10;
        int newCol = (newRow % 2 == 0) ? newPos % 10 : 9 - (newPos % 10);

        StackPane floatingIcon = createMonsterCharacterIcon(isPlayer);
        floatingIcon.setManaged(false);
        view.getBoardGrid().getChildren().add(floatingIcon);

        double startX = oldCol * 63 + 8 + 30 - 15;
        double startY = oldRow * 63 + 8 + 30 - 15;
        floatingIcon.setLayoutX(startX);
        floatingIcon.setLayoutY(startY);

        int steps = Math.abs(newPos - oldPos);
        double stepDuration = 0.28;
        Timeline movementTimeline = new Timeline();

        for (int i = 1; i <= steps; i++) {
            double time = stepDuration * i;
            int currentStepPos = oldPos + (newPos > oldPos ? i : -i);
            int stepRow = currentStepPos / 10;
            int stepCol = (stepRow % 2 == 0) ? currentStepPos % 10 : 9 - (currentStepPos % 10);
            
            double targetX = stepCol * 63 + 8 + 30 - 15;
            double targetY = stepRow * 63 + 8 + 30 - 15;

            movementTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(time),
                new KeyValue(floatingIcon.layoutXProperty(), targetX, Interpolator.EASE_OUT),
                new KeyValue(floatingIcon.layoutYProperty(), targetY, Interpolator.EASE_OUT)
            ));
        }

        ScaleTransition bounce = new ScaleTransition(Duration.millis(220), floatingIcon);
        bounce.setFromX(1.0); bounce.setFromY(1.0); bounce.setToX(1.35); bounce.setToY(1.35);
        bounce.setCycleCount(steps * 2); bounce.setAutoReverse(true); bounce.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition hopAnimation = new ParallelTransition(movementTimeline, bounce);
        
        hopAnimation.setOnFinished(finishEvent -> {
            view.getBoardGrid().getChildren().remove(floatingIcon);
            updateAllUI();
            lastPositions.put(monster, newPos);
            isAnimating = false;
            isProcessing = false;
            Platform.runLater(() -> {
                view.getRollBtn().setDisable(false);
                showNotification(monster.getName() + " moved! " + engine.getCurrent().getName() + "'s turn next.", "success");
            });
        });

        hopAnimation.play();
    }

    private StackPane createMonsterCharacterIcon(boolean isPlayer) {
        Circle glow = new Circle(20);
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, isPlayer ? Color.web("#4facfe") : Color.web("#43e97b")),
            new Stop(1, isPlayer ? Color.web("#00f2fe") : Color.web("#38f9d7")));
        glow.setFill(gradient); glow.setOpacity(0.5);
        
        Circle icon = new Circle(18);
        LinearGradient iconGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, isPlayer ? Color.web("#4facfe") : Color.web("#43e97b")),
            new Stop(1, isPlayer ? Color.web("#00f2fe") : Color.web("#38f9d7")));
        icon.setFill(iconGradient); icon.setStroke(Color.WHITE); icon.setStrokeWidth(3);
        icon.setEffect(new javafx.scene.effect.DropShadow(10, Color.rgb(0,0,0,0.4)));
        
        StackPane container = new StackPane(glow, icon);
        
        if (isPlayer) {
            try {
                java.io.File sulleyFile = new java.io.File("sully_face.png");
                Image sulleyImage = new Image(sulleyFile.toURI().toURL().toExternalForm());
                ImageView sulleyView = new ImageView(sulleyImage);
                sulleyView.setFitWidth(32); sulleyView.setFitHeight(32); sulleyView.setPreserveRatio(true);
                container.getChildren().add(sulleyView);
            } catch (Exception e) {
                Text face = new Text("");
                face.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));
                face.setFill(Color.WHITE); face.setTranslateY(6);
                container.getChildren().add(face);
            }
        } else {
            try {
                java.io.File mikeFile = new java.io.File("mike_face.png");
                Image mikeImage = new Image(mikeFile.toURI().toURL().toExternalForm());
                ImageView mikeView = new ImageView(mikeImage);
                mikeView.setFitWidth(32); mikeView.setFitHeight(32); mikeView.setPreserveRatio(true);
                container.getChildren().add(mikeView);
            } catch (Exception e) {
                Text face = new Text("");
                face.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));
                face.setFill(Color.WHITE); face.setTranslateY(6);
                container.getChildren().add(face);
            }
        }
        
        container.setId("monster-icon");
        StackPane.setAlignment(container, Pos.CENTER);
        return container;
    }

    private String getMonsterEmoji(Monster m) { return ""; }

    private void animateNotificationPulse() {
        Label lbl = view.getNotificationLabel();
        ScaleTransition st = new ScaleTransition(Duration.millis(150), lbl);
        st.setFromX(1.0); st.setFromY(1.0); st.setToX(1.1); st.setToY(1.1);
        st.setCycleCount(2); st.setAutoReverse(true); st.play();
    }

    private void updateAllUI() {
        Monster player = engine.getPlayer();
        Monster opponent = engine.getOpponent();
        Monster current = engine.getCurrent();

        view.getTurnLabel().setText("Turn: " + current.getName() + " (" + current.getRole() + ")");
        updateMonsterPanel(view.getPlayerPanel(), player, true);
        updateMonsterPanel(view.getOpponentPanel(), opponent, false);
        updateBoardState(player, opponent);
    }

    private void updateMonsterPanel(VBox panel, Monster m, boolean isPlayer) {
        int size = panel.getChildren().size();
        if (size > 1) panel.getChildren().subList(1, size).clear();

        String type = getMonsterType(m);
        String status = buildStatusString(m);

        panel.getChildren().addAll(
            createAnimatedInfoRow(" Name", m.getName()),
            createAnimatedInfoRow(" Original Role", m.getOriginalRole().toString()),
            createAnimatedInfoRow("Current Role", m.getRole().toString() + (m.isConfused() ? " [CONFUSED]" : "")),
            createAnimatedInfoRow("Type", type),
            createAnimatedInfoRow("Energy", String.valueOf(m.getEnergy())),
            createAnimatedInfoRow("Position", String.valueOf(m.getPosition())),
            createAnimatedInfoRow("Status Effects", status)
        );
    }

    private VBox createAnimatedInfoRow(String label, String value) {
        VBox row = new VBox(3);
        Label l = new Label(label);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #495057;");
        Label v = new Label(value);
        v.setWrapText(true); v.setStyle("-fx-text-fill: #212529; -fx-font-size: 11px;");
        
        row.setOnMouseEntered(e -> {
            row.setStyle("-fx-background-color: rgba(102, 126, 234, 0.1); -fx-background-radius: 5; -fx-padding: 3;");
            row.setScaleX(1.02); row.setScaleY(1.02);
        });
        row.setOnMouseExited(e -> {
            row.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            row.setScaleX(1.0); row.setScaleY(1.0);
        });
        row.getChildren().addAll(l, v);
        return row;
    }

    private String getMonsterType(Monster m) {
        if (m instanceof game.engine.monsters.Dasher) return "Dasher";
        if (m instanceof game.engine.monsters.Dynamo) return "Dynamo";
        if (m instanceof game.engine.monsters.MultiTasker) return "MultiTasker";
        if (m instanceof game.engine.monsters.Schemer) return "Schemer";
        return "Unknown";
    }

    private String buildStatusString(Monster m) {
        ArrayList<String> effects = new ArrayList<>();
        if (m.isFrozen()) effects.add("Freeze");
        if (m.isConfused()) effects.add("Confused (" + m.getConfusionTurns() + ")");
        if (m.isShielded()) effects.add("Shielded");
        return effects.isEmpty() ? "None" : String.join(" | ", effects);
    }

    private void updateBoardState(Monster player, Monster opponent) {
        Board board = engine.getBoard();
        Cell[][] cells = board.getBoardCells();

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                StackPane pane = (StackPane) view.getBoardGrid().getChildren().get(r * 10 + c);
                int linearIdx = indexFromRowCol(r, c);
                pane.getChildren().removeIf(node -> node instanceof Circle || (node instanceof StackPane && node.getId() != null && node.getId().equals("monster-icon")));

                Cell cell = cells[r][c];
                applyCellStyle(pane, cell, linearIdx);

                if (player.getPosition() == linearIdx) {
                    addStaticMonsterIcon(pane, player, true, linearIdx);
                }
                if (opponent.getPosition() == linearIdx && opponent.getPosition() != player.getPosition()) {
                    addStaticMonsterIcon(pane, opponent, false, linearIdx);
                } else if (opponent.getPosition() == linearIdx && opponent.getPosition() == player.getPosition()) {
                    addStaticMonsterIcon(pane, opponent, false, linearIdx);
                }
            }
        }
    }

    private void addStaticMonsterIcon(StackPane pane, Monster monster, boolean isPlayer, int position) {
        StackPane icon = createMonsterCharacterIcon(isPlayer);
        pane.getChildren().add(icon);
        if (!monsterAnimations.containsKey(position)) startMonsterIdleAnimation(icon, position);
    }

    private void startMonsterIdleAnimation(StackPane monster, int position) {
        if (monsterAnimations.containsKey(position)) monsterAnimations.get(position).stop();
        Timeline idleAnimation = new Timeline(
            new KeyFrame(Duration.seconds(0), new KeyValue(monster.scaleXProperty(), 1.0), new KeyValue(monster.scaleYProperty(), 1.0)),
            new KeyFrame(Duration.seconds(1), new KeyValue(monster.scaleXProperty(), 1.15), new KeyValue(monster.scaleYProperty(), 1.15)),
            new KeyFrame(Duration.seconds(2), new KeyValue(monster.scaleXProperty(), 1.0), new KeyValue(monster.scaleYProperty(), 1.0))
        );
        idleAnimation.setCycleCount(Timeline.INDEFINITE); idleAnimation.play();
        monsterAnimations.put(position, idleAnimation);
    }

    private void applyCellStyle(StackPane pane, Cell cell, int idx) {
        String base = "-fx-border-width: 1; -fx-background-radius: 6; -fx-border-radius: 6;";
        
        while (pane.getChildren().size() > 1) {
            pane.getChildren().remove(1);
        }

        if (cell instanceof DoorCell) {
            DoorCell dc = (DoorCell) cell;
            boolean isScarer = dc.getRole().toString().equals("SCARER");
            String roleColor = isScarer ? "#dc3545" : "#ffc107";
            String activation = dc.isActivated() ? "-fx-opacity: 0.6;" : "";
            
            pane.setStyle(base + " -fx-background-color: " + roleColor + "; " + activation + " -fx-border-color: #333; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.3, 0, 2);");
            
            Text symbol = new Text(isScarer ? "S" : "L");
            symbol.setStyle("-fx-font-weight: bold; -fx-font-size: 22px; -fx-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0.5, 0, 1);");
            pane.getChildren().add(symbol);

        } else if (cell instanceof CardCell) {
            pane.setStyle(base + " -fx-background-color: linear-gradient(135deg, #6f42c1 0%, #5a32a3 100%); -fx-border-color: #3d2273; -fx-effect: dropshadow(gaussian, rgba(111,66,193,0.4), 6, 0.4, 0, 2);");
            
            Text symbol = new Text("C");
            symbol.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-fill: white;");
            pane.getChildren().add(symbol);

        } else if (cell instanceof ConveyorBelt) {
            pane.setStyle(base + " -fx-background-color: linear-gradient(135deg, #28a745 0%, #218838 100%); -fx-border-color: #1e7e34; -fx-effect: dropshadow(gaussian, rgba(40,167,69,0.4), 6, 0.4, 0, 2);");
            
            Text symbol = new Text(">>");
            symbol.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-fill: white;");
            pane.getChildren().add(symbol);

        } else if (cell instanceof ContaminationSock) {
            pane.setStyle(base + " -fx-background-color: linear-gradient(135deg, #fd7e14 0%, #e8590c 100%); -fx-border-color: #d94800; -fx-effect: dropshadow(gaussian, rgba(253,126,20,0.4), 6, 0.4, 0, 2);");
            
            Text symbol = new Text("X");
            symbol.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-fill: white;");
            pane.getChildren().add(symbol);

        } else if (cell instanceof MonsterCell) {
            pane.setStyle(base + " -fx-background-color: linear-gradient(135deg, #17a2b8 0%, #117a8b 100%); -fx-border-color: #0c5460; -fx-effect: dropshadow(gaussian, rgba(23,162,184,0.4), 6, 0.4, 0, 2);");
            
            Text symbol = new Text("M");
            symbol.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-fill: white;");
            pane.getChildren().add(symbol);

        } else {
            pane.setStyle(base + " -fx-background-color: rgba(255,255,255,0.95); -fx-border-color: #adb5bd;");
        }
    }

    private void renderBoardBase() {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                StackPane pane = (StackPane) view.getBoardGrid().getChildren().get(r * 10 + c);
                int linearIdx = indexFromRowCol(r, c);
                Text idxText = (Text) pane.getChildren().get(0);
                idxText.setText(String.valueOf(linearIdx));
                pane.setStyle("-fx-border-color: rgba(108,117,125,0.5); -fx-border-width: 1; -fx-background-color: rgba(248,249,250,0.95); -fx-background-radius: 6; -fx-border-radius: 6;");
            }
        }
    }

    private void showNotification(String msg, String type) {
        Platform.runLater(() -> {
            Label lbl = view.getNotificationLabel();
            lbl.setText(msg);
            if (type.equals("error")) lbl.setStyle("-fx-text-fill: #721c24; -fx-font-weight: bold; -fx-font-size: 14px;");
            else if (type.equals("success")) lbl.setStyle("-fx-text-fill: #155724; -fx-font-weight: bold; -fx-font-size: 14px;");
            else lbl.setStyle("-fx-text-fill: #856404; -fx-font-size: 13px;");
            
            ScaleTransition st = new ScaleTransition(Duration.millis(200), lbl);
            st.setFromX(0.95); st.setFromY(0.95); st.setToX(1.0); st.setToY(1.0); st.play();
        });
    }

    private void showWinScreen(Monster winner) {
        WinView winView = new WinView(winner, engine);
        WinController winCtrl = new WinController(winView, stage);
        stage.setScene(new Scene(winView.getRoot(), 800, 500));
        ScaleTransition st = new ScaleTransition(Duration.millis(500), winView.getRoot());
        st.setFromX(0); st.setFromY(0); st.setToX(1); st.setToY(1); st.setCycleCount(1); st.play();
    }

    private int indexFromRowCol(int row, int col) {
        int index = row * 10;
        if (row % 2 == 1) col = 9 - col;
        return index + col;
    }
}