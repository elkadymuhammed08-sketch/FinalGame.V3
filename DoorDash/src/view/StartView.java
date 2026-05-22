package view;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class StartView {
    private final VBox root = new VBox(20);
    private final Label titleLabel = new Label("DoorDash: Scare vs Laugh Touchdown");
    private final ComboBox<String> roleCombo = new ComboBox<>();
    private final TextArea instructionsArea = new TextArea();
    private final Button startBtn = new Button("Start Game");
    private final Label notificationLabel = new Label("Select your side to begin!");
    private final Pane backgroundParticles = new Pane();

    public StartView() {
        root.setAlignment(Pos.CENTER);
        
        StringBuilder bgStyle = new StringBuilder();
        bgStyle.append("-fx-padding: 40; ");
        bgStyle.append("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%); ");
        
        try {
            java.io.File bgFile = new java.io.File("monsters2.png");
            if (bgFile.exists()) {
                String imageUrl = bgFile.toURI().toURL().toExternalForm();
                bgStyle.append("-fx-background-image: url('");
                bgStyle.append(imageUrl);
                bgStyle.append("'); ");
                bgStyle.append("-fx-background-size: cover; ");
                bgStyle.append("-fx-background-position: center;");
            }
        } catch (Exception e) {
            System.out.println("Background not loaded: " + e.getMessage());
        }
        
        root.setStyle(bgStyle.toString());
        root.setPrefSize(1280, 850);
        root.getChildren().add(0, backgroundParticles);
        createParticleBackground();

        titleLabel.setFont(Font.font("System Bold", 32));
        titleLabel.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0.5, 0, 3);");
        animateTitleEntrance();

        roleCombo.getItems().addAll("SCARER", "LAUGHER");
        roleCombo.setPromptText("Choose Your Side");
        roleCombo.setPrefWidth(320);
        roleCombo.setStyle("-fx-font-size: 16px; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: rgba(255,255,255,0.5); -fx-background-color: rgba(255,255,255,0.9);");
        animateComboEntrance();

        instructionsArea.setEditable(false);
        instructionsArea.setWrapText(true);
        instructionsArea.setPrefRowCount(9);
        instructionsArea.setPrefWidth(480);
        instructionsArea.setStyle("-fx-font-size: 13px; -fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #667eea; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.3, 0, 2);");
        instructionsArea.setText(
            "HOW TO PLAY:\n" +
            " Roll dice to move across 100 cells\n" +
            " Land on cells to gain/lose energy, draw cards\n" +
            " Activate PowerUp before rolling (costs 500 energy)\n" +
            " Freeze skips your turn | Confusion swaps roles\n" +
            " First monster to reach cell 99 with 1000+ energy wins!"
        );
        animateInstructionsEntrance();

        startBtn.setPrefWidth(240);
        startBtn.setStyle("-fx-font-size: 18px; -fx-background-color: linear-gradient(to bottom, #f093fb 0%, #f5576c 100%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(245,87,108,0.5), 12, 0.4, 0, 3);");
        setupButtonAnimations();

        notificationLabel.setFont(Font.font("System", 15));
        notificationLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.95); -fx-font-weight: bold;");
        animateNotificationEntrance();

        root.getChildren().addAll(titleLabel, roleCombo, instructionsArea, startBtn, notificationLabel);
    }

    private void createParticleBackground() {
        backgroundParticles.setPrefSize(1280, 850);
        backgroundParticles.setStyle("-fx-background-color: transparent;");
        
        for (int i = 0; i < 25; i++) {
            Circle particle = new Circle(Math.random() * 5 + 2);
            particle.setFill(Color.web("#ffffff", 0.3 + Math.random() * 0.4));
            particle.setTranslateX(Math.random() * 1280);
            particle.setTranslateY(Math.random() * 850);
            
            Timeline particleAnim = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(particle.translateYProperty(), particle.getTranslateY())),
                new KeyFrame(Duration.seconds(8 + Math.random() * 12),
                    new KeyValue(particle.translateYProperty(), -50))
            );
            particleAnim.setCycleCount(Timeline.INDEFINITE);
            particleAnim.play();
            
            backgroundParticles.getChildren().add(particle);
        }
    }

    private void animateTitleEntrance() {
        titleLabel.setTranslateY(-50);
        titleLabel.setOpacity(0);
        
        Timeline titleAnim = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(titleLabel.translateYProperty(), -50),
                new KeyValue(titleLabel.opacityProperty(), 0)),
            new KeyFrame(Duration.millis(800),
                new KeyValue(titleLabel.translateYProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(titleLabel.opacityProperty(), 1))
        );
        titleAnim.play();
    }

    private void animateComboEntrance() {
        roleCombo.setTranslateX(-100);
        roleCombo.setOpacity(0);
        
        Timeline comboAnim = new Timeline(
            new KeyFrame(Duration.millis(400),
                new KeyValue(roleCombo.translateXProperty(), -100),
                new KeyValue(roleCombo.opacityProperty(), 0)),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(roleCombo.translateXProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(roleCombo.opacityProperty(), 1))
        );
        comboAnim.play();
    }

    private void animateInstructionsEntrance() {
        instructionsArea.setTranslateY(30);
        instructionsArea.setOpacity(0);
        
        Timeline instrAnim = new Timeline(
            new KeyFrame(Duration.millis(600),
                new KeyValue(instructionsArea.translateYProperty(), 30),
                new KeyValue(instructionsArea.opacityProperty(), 0)),
            new KeyFrame(Duration.millis(1300),
                new KeyValue(instructionsArea.translateYProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(instructionsArea.opacityProperty(), 1))
        );
        instrAnim.play();
    }

    private void animateNotificationEntrance() {
        notificationLabel.setOpacity(0);
        
        Timeline notifAnim = new Timeline(
            new KeyFrame(Duration.millis(900),
                new KeyValue(notificationLabel.opacityProperty(), 0)),
            new KeyFrame(Duration.millis(1600),
                new KeyValue(notificationLabel.opacityProperty(), 1, Interpolator.EASE_IN))
        );
        notifAnim.play();
    }

    private void setupButtonAnimations() {
        ScaleTransition hoverBounce = new ScaleTransition(Duration.millis(150), startBtn);
        hoverBounce.setFromX(1.0);
        hoverBounce.setFromY(1.0);
        hoverBounce.setToX(1.08);
        hoverBounce.setToY(1.08);
        hoverBounce.setAutoReverse(true);
        hoverBounce.setCycleCount(2);
        
        startBtn.setOnMouseEntered(e -> {
            hoverBounce.playFromStart();
            startBtn.setStyle("-fx-font-size: 18px; -fx-background-color: linear-gradient(to bottom, #f5576c 0%, #f093fb 100%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(245,87,108,0.7), 15, 0.5, 0, 4); -fx-cursor: hand;");
        });
        
        startBtn.setOnMouseExited(e -> {
            startBtn.setStyle("-fx-font-size: 18px; -fx-background-color: linear-gradient(to bottom, #f093fb 0%, #f5576c 100%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(245,87,108,0.5), 12, 0.4, 0, 3);");
        });
        
        startBtn.setOnMouseClicked(e -> {
            TranslateTransition clickPress = new TranslateTransition(Duration.millis(80), startBtn);
            clickPress.setByY(3);
            clickPress.setOnFinished(ev -> {
                TranslateTransition release = new TranslateTransition(Duration.millis(80), startBtn);
                release.setByY(-3);
                release.play();
            });
            clickPress.play();
        });
    }

    public VBox getRoot() { return root; }
    public ComboBox<String> getRoleCombo() { return roleCombo; }
    public Button getStartBtn() { return startBtn; }
    public Label getNotificationLabel() { return notificationLabel; }
}