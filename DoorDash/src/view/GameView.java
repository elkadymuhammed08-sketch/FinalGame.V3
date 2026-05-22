package view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameView {
    private final BorderPane root = new BorderPane();
    private final HBox controlBar = new HBox(15);
    private final GridPane boardGrid = new GridPane();
    private final VBox playerPanel = new VBox(8);
    private final VBox opponentPanel = new VBox(8);
    private final HBox statusBar = new HBox(10);
    
    private final CheckBox powerupCheck = new CheckBox("Activate PowerUp (Cost: 500)");
    private final Button rollBtn = new Button("Roll Dice");
    private final Label turnLabel = new Label("Turn: Player (SCARER)");
    private final Label notificationLabel = new Label("Game started. Roll the dice to begin!");
    private final VBox cardDisplay = new VBox(5);
    private final Label cardTitle = new Label("Last Card Drawn:");
    private final Label cardNameLabel = new Label("None");
    private final Label cardEffectLabel = new Label("");
    
    private final StackPane diceContainer = new StackPane();
    private final Text diceFace = new Text();
    private Timeline diceRollTimeline;
    private ScaleTransition rollButtonBounce;
    
    private final HBox diceInfoBox = new HBox(10);
    private final Label diceValueLabel = new Label("-");
    private final Label bonusValueLabel = new Label("+0");
    
    private final VBox bonusContainer = new VBox(2);
    private final Label bonusLabel = new Label();
    private final Label reasonLabel = new Label();
    private ScaleTransition bonusScale;
    private FadeTransition bonusFade;

    public GameView() {
        setupControlBar();
        setupBoard();
        setupSidePanels();
        setupStatusBar();
        assembleLayout();
        createAnimations();
        startBoardAnimations();
    }

    private void createAnimations() {
        rollButtonBounce = new ScaleTransition(Duration.millis(200), rollBtn);
        rollButtonBounce.setFromX(1.0);
        rollButtonBounce.setFromY(1.0);
        rollButtonBounce.setToX(1.15);
        rollButtonBounce.setToY(1.15);
        rollButtonBounce.setCycleCount(2);
        rollButtonBounce.setAutoReverse(true);
        
        String[] diceFaces = {"", "⚀", "", "", "⚃", "⚄", ""};
        diceRollTimeline = new Timeline();
        diceRollTimeline.setCycleCount(1);
        
        for (int i = 0; i < 12; i++) {
            diceRollTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(80 * i),
                new KeyValue(diceFace.textProperty(), diceFaces[i % 6]),
                new KeyValue(diceContainer.rotateProperty(), 15 * i)
            ));
        }
        diceRollTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000),
            new KeyValue(diceContainer.rotateProperty(), 0, Interpolator.EASE_OUT),
            new KeyValue(diceContainer.scaleXProperty(), 1.0),
            new KeyValue(diceContainer.scaleYProperty(), 1.0)
        ));
        
        bonusScale = new ScaleTransition(Duration.millis(350), bonusContainer);
        bonusScale.setFromX(0);
        bonusScale.setFromY(0);
        bonusScale.setToX(1);
        bonusScale.setToY(1);
        bonusScale.setInterpolator(Interpolator.EASE_OUT);
        
        bonusFade = new FadeTransition(Duration.millis(350), bonusContainer);
        bonusFade.setFromValue(0);
        bonusFade.setToValue(1);
    }

    private void startBoardAnimations() {}

    private HBox createLegendBar() {
        HBox legend = new HBox(15);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(8));
        legend.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 5, 0.3, 0, 2);");
        
        legend.getChildren().addAll(
            createLegendItem("S-Scarer", "#dc3545"),
            createLegendItem("L-Laugh", "#ffc107"),
            createLegendItem("M-Monster", "#17a2b8"),
            createLegendItem("C-Card", "#6f42c1"),
            createLegendItem(">>-Conveyor", "#28a745"),
            createLegendItem("X-Sock", "#fd7e14")
        );
        
        return legend;
    }

    private StackPane createLegendItem(String text, String colorCode) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER_LEFT);
        
        StackPane box = new StackPane();
        box.setPrefSize(20, 20);
        box.setStyle("-fx-background-color: " + colorCode + "; -fx-border-color: white; -fx-border-width: 1; -fx-border-radius: 4;");
        
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("System Bold", 11));
        
        item.getChildren().addAll(box, label);
        return new StackPane(item);
    }

    private void setupControlBar() {
        controlBar.setAlignment(Pos.CENTER_LEFT);
        controlBar.setPadding(new Insets(10));
        controlBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea 0%, #764ba2 100%); -fx-border-color: #dee2e6; -fx-border-width: 0 0 3 0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.3, 0, 3);");
        
        rollBtn.setPrefWidth(140);
        rollBtn.setStyle("-fx-font-weight: bold; -fx-background-color: linear-gradient(to bottom, #f093fb 0%, #f5576c 100%); -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(245,87,108,0.4), 8, 0.3, 0, 2);");
        rollBtn.setOnMouseEntered(e -> rollButtonBounce.playFromStart());
        
        turnLabel.setFont(Font.font("System Bold", 15));
        turnLabel.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0.5, 0, 1);");
        
        diceContainer.setPrefSize(65, 65);
        diceContainer.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: #2c3e50; -fx-border-width: 3; -fx-border-radius: 14; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0.5, 0, 4);");
        diceFace.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 38));
        diceFace.setFill(Color.web("#2c3e50"));
        diceContainer.getChildren().add(diceFace);
        
        diceInfoBox.setAlignment(Pos.CENTER);
        diceInfoBox.setSpacing(10);
        
        Label diceLabel = new Label("Dice:");
        diceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        
        diceValueLabel.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 5 15; -fx-font-weight: bold; -fx-font-size: 16px; -fx-min-width: 40px; -fx-alignment: center;");
        
        Label bonusLabelTitle = new Label("Bonus:");
        bonusLabelTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        
        bonusValueLabel.setStyle("-fx-background-color: linear-gradient(to bottom, #28a745, #218838); -fx-background-radius: 8; -fx-padding: 5 15; -fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: white; -fx-min-width: 50px; -fx-alignment: center;");
        
        diceInfoBox.getChildren().addAll(diceLabel, diceValueLabel, bonusLabelTitle, bonusValueLabel);
        
        bonusContainer.setAlignment(Pos.CENTER);
        bonusContainer.setVisible(false);
        bonusContainer.setOpacity(0);
        
        StackPane bonusBlock = new StackPane(bonusLabel);
        bonusBlock.setPrefSize(45, 35);
        bonusBlock.setStyle("-fx-background-color: linear-gradient(to bottom, #28a745, #218838); -fx-background-radius: 10; -fx-border-color: #1e7e34; -fx-border-width: 2; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(40,167,69,0.6), 8, 0.4, 0, 2);");
        bonusLabel.setFont(Font.font("System Bold", 18));
        bonusLabel.setTextFill(Color.WHITE);
        bonusLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0.5, 0, 1);");
        
        reasonLabel.setFont(Font.font("System Italic", 11));
        reasonLabel.setTextFill(Color.web("#ffd700"));
        reasonLabel.setWrapText(true);
        reasonLabel.setPrefWidth(80);
        reasonLabel.setAlignment(Pos.CENTER);
        
        bonusContainer.getChildren().addAll(bonusBlock, reasonLabel);
        
        HBox diceArea = new HBox(12, diceContainer, bonusContainer, diceInfoBox);
        diceArea.setAlignment(Pos.CENTER);
        
        controlBar.getChildren().addAll(powerupCheck, new Separator(), rollBtn, new Separator(), turnLabel, new Separator(), diceArea);
    }

    private void setupBoard() {
        boardGrid.setHgap(3);
        boardGrid.setVgap(3);
        boardGrid.setPadding(new Insets(8));
        
        StringBuilder bgStyle = new StringBuilder();
        bgStyle.append("-fx-background-color: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%); ");
        bgStyle.append("-fx-background-radius: 12; ");
        bgStyle.append("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0.4, 0, 5);");
        
        try {
            java.io.File bgFile = new java.io.File("monsters_inc_background.jpg");
            java.io.File bgFilePng = new java.io.File("monsters_inc_background.png");
            java.io.File target = null;
            if (bgFile.exists()) target = bgFile;
            else if (bgFilePng.exists()) target = bgFilePng;
            
            if (target != null) {
                String url = target.toURI().toURL().toExternalForm();
                bgStyle.append("-fx-background-image: url('").append(url).append("'); ");
                bgStyle.append("-fx-background-size: cover; -fx-background-position: center;");
            }
        } catch (Exception e) {
            System.out.println("Background not loaded: " + e.getMessage());
        }
        
        boardGrid.setStyle(bgStyle.toString());
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                StackPane cellPane = createBaseCell();
                boardGrid.add(cellPane, c, r);
            }
        }
    }

    private StackPane createBaseCell() {
        StackPane pane = new StackPane();
        pane.setPrefSize(60, 60);
        pane.setStyle("-fx-border-color: rgba(255,255,255,0.3); -fx-border-width: 1; -fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 6; -fx-border-radius: 6;");
        Text idxText = new Text();
        idxText.setFont(Font.font("System Bold", 10));
        idxText.setFill(Color.web("#495057"));
        idxText.setTranslateX(-24);
        idxText.setTranslateY(-24);
        pane.getChildren().add(idxText);
        return pane;
    }

    private void setupSidePanels() {
        playerPanel.setPadding(new Insets(15));
        opponentPanel.setPadding(new Insets(15));
        playerPanel.setStyle("-fx-background-color: linear-gradient(to bottom, #e0e7ff 0%, #d1d5ff 100%); -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.2, 0, 3); -fx-border-color: #667eea; -fx-border-width: 2; -fx-border-radius: 10;");
        opponentPanel.setStyle("-fx-background-color: linear-gradient(to bottom, #ffe0e0 0%, #ffd1d1 100%); -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.2, 0, 3); -fx-border-color: #f5576c; -fx-border-width: 2; -fx-border-radius: 10;");
        addPanelTitle(playerPanel, "🎮 Your Monster", "#667eea");
        addPanelTitle(opponentPanel, "👾 Opponent Monster", "#f5576c");
    }

    private void addPanelTitle(VBox panel, String title, String color) {
        Label t = new Label(title);
        t.setFont(Font.font("System Bold", 15));
        t.setStyle("-fx-text-fill: " + color + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0.3, 0, 1);");
        t.setPadding(new Insets(0, 0, 10, 0));
        panel.getChildren().add(t);
    }

    private void setupStatusBar() {
        statusBar.setPadding(new Insets(12));
        statusBar.setStyle("-fx-background-color: linear-gradient(to right, #fff3cd 0%, #ffe8a1 100%); -fx-border-color: #ffc107; -fx-border-width: 3 0 0 0; -fx-effect: dropshadow(gaussian, rgba(255,193,7,0.3), 10, 0.3, 0, -3);");
        notificationLabel.setFont(Font.font("System Bold", 13));
        notificationLabel.setWrapText(true);
        notificationLabel.setPrefWidth(600);
        notificationLabel.setStyle("-fx-text-fill: #856404;");
        cardDisplay.getChildren().addAll(cardTitle, cardNameLabel, cardEffectLabel);
        cardDisplay.setPrefWidth(300);
        cardNameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0d6efd; -fx-font-size: 14px;");
        cardEffectLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 12px;");
        cardTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #495057;");
        statusBar.getChildren().addAll(notificationLabel, new Separator(), cardDisplay);
    }

    private void assembleLayout() {
        VBox centerContainer = new VBox(5);
        centerContainer.setAlignment(Pos.CENTER);
        centerContainer.getChildren().addAll(createLegendBar(), boardGrid);
        
        root.setTop(controlBar);
        root.setCenter(centerContainer);
        root.setLeft(playerPanel);
        root.setRight(opponentPanel);
        root.setBottom(statusBar);
        root.setStyle("-fx-background-color: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);");
    }

    public void triggerDiceRollAnimation() {
        rollButtonBounce.playFromStart();
        diceFace.setText("");
        diceContainer.setScaleX(0.8);
        diceContainer.setScaleY(0.8);
        diceContainer.setRotate(0);
        
        ScaleTransition prep = new ScaleTransition(Duration.millis(150), diceContainer);
        prep.setFromX(0.8); prep.setFromY(0.8); prep.setToX(1.1); prep.setToY(1.1);
        prep.setOnFinished(e -> diceRollTimeline.playFromStart());
        prep.play();
        
        hideBonus();
    }

    public void setDiceResult(int result) {
        if (result < 1 || result > 6) return;
        String[] faces = {"", "⚀", "", "", "⚃", "", ""};
        diceFace.setText(faces[result]);
        diceValueLabel.setText(String.valueOf(result));
        
        ScaleTransition thud = new ScaleTransition(Duration.millis(200), diceContainer);
        thud.setFromX(1.1); thud.setFromY(1.1); thud.setToX(1.0); thud.setToY(1.0);
        thud.play();
    }

    public void setBonusValue(int bonus) {
        bonusValueLabel.setText("+" + bonus);
    }

    public void showBonus(int amount, String reason) {
        bonusLabel.setText("+" + amount);
        reasonLabel.setText(reason);
        bonusContainer.setVisible(true);
        bonusScale.stop();
        bonusFade.stop();
        ParallelTransition pt = new ParallelTransition(bonusScale, bonusFade);
        pt.play();
    }

    public void hideBonus() {
        if (!bonusContainer.isVisible()) return;
        FadeTransition ft = new FadeTransition(Duration.millis(200), bonusContainer);
        ft.setFromValue(1); ft.setToValue(0);
        ft.setOnFinished(e -> {
            bonusContainer.setVisible(false);
            bonusContainer.setScaleX(0);
            bonusContainer.setScaleY(0);
        });
        ft.play();
    }

    public void appendNotification(String msg) {
        notificationLabel.setText(msg);
        notificationLabel.setStyle("-fx-text-fill: #155724; -fx-font-weight: bold; -fx-font-size: 13px;");
        
        ScaleTransition st = new ScaleTransition(Duration.millis(200), notificationLabel);
        st.setFromX(0.95); st.setFromY(0.95); st.setToX(1.0); st.setToY(1.0);
        st.play();
    }

    public VBox getRootContainer() { return new VBox(root); }
    public GridPane getBoardGrid() { return boardGrid; }
    public VBox getPlayerPanel() { return playerPanel; }
    public VBox getOpponentPanel() { return opponentPanel; }
    public CheckBox getPowerupCheck() { return powerupCheck; }
    public Button getRollBtn() { return rollBtn; }
    public Label getTurnLabel() { return turnLabel; }
    public Label getNotificationLabel() { return notificationLabel; }
    public Label getCardNameLabel() { return cardNameLabel; }
    public Label getCardEffectLabel() { return cardEffectLabel; }
}