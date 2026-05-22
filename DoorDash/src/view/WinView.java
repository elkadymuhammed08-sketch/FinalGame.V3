package view;

import javafx.animation.*;
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
import javafx.util.Duration;
import game.engine.Game;
import game.engine.monsters.Monster;

public class WinView {
    private final VBox root = new VBox(25);
    private final Label title = new Label("🏆 GAME OVER 🏆");
    private final Label winnerLabel = new Label();
    private final Label finalEnergies = new Label();
    private final Button playAgainBtn = new Button("🔄 Return to Start");
    private final Pane confettiLayer = new Pane();

    public WinView(Monster winner, Game engine) {
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30; -fx-background-color: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);");
        root.getChildren().add(0, confettiLayer);
        confettiLayer.setPrefSize(800, 500);
        confettiLayer.setStyle("-fx-background-color: transparent;");
        
        title.setFont(Font.font("System Bold", 42));
        title.setStyle("-fx-text-fill: gold; -fx-effect: dropshadow(gaussian, rgba(255,215,0,0.8), 20, 0.6, 0, 5);");
        animateTitleCelebration();

        winnerLabel.setText("🎉 Winner: " + winner.getName() + " (" + winner.getRole() + ") 🎉");
        winnerLabel.setFont(Font.font("System Bold", 28));
        winnerLabel.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.4, 0, 2);");
        animateWinnerEntrance();
        
        finalEnergies.setText(String.format(
            "💎 Final Energies:\n⚡ %s: %d | 👾 %s: %d",
            engine.getPlayer().getName(), engine.getPlayer().getEnergy(),
            engine.getOpponent().getName(), engine.getOpponent().getEnergy()
        ));
        finalEnergies.setFont(Font.font("System", 20));
        finalEnergies.setStyle("-fx-text-fill: rgba(255,255,255,0.95); -fx-font-weight: bold;");
        animateEnergiesEntrance();
        
        playAgainBtn.setFont(Font.font("System Bold", 20));
        playAgainBtn.setStyle("-fx-padding: 15 50; -fx-background-color: linear-gradient(to bottom, #ffc107 0%, #ff9800 100%); -fx-text-fill: #212529; -fx-font-weight: bold; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(255,152,0,0.6), 15, 0.5, 0, 4);");
        setupPlayAgainAnimations();
        
        root.getChildren().addAll(title, winnerLabel, finalEnergies, playAgainBtn);
        
        startConfetti();
    }

    private void animateTitleCelebration() {
        title.setScaleX(0);
        title.setScaleY(0);
        title.setRotate(-20);
        
        ScaleTransition st = new ScaleTransition(Duration.millis(600), title);
        st.setFromX(0);
        st.setFromY(0);
        st.setToX(1);
        st.setToY(1);
        
        RotateTransition rt = new RotateTransition(Duration.millis(600), title);
        rt.setFromAngle(-20);
        rt.setToAngle(0);
        
        ParallelTransition pt = new ParallelTransition(st, rt);
        pt.setInterpolator(Interpolator.EASE_OUT);
        pt.play();
        
        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(title.scaleXProperty(), 1), new KeyValue(title.scaleYProperty(), 1)),
            new KeyFrame(Duration.millis(500), new KeyValue(title.scaleXProperty(), 1.05), new KeyValue(title.scaleYProperty(), 1.05)),
            new KeyFrame(Duration.seconds(1), new KeyValue(title.scaleXProperty(), 1), new KeyValue(title.scaleYProperty(), 1))
        );
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.play();
    }

    private void animateWinnerEntrance() {
        winnerLabel.setTranslateY(30);
        winnerLabel.setOpacity(0);
        
        Timeline anim = new Timeline(
            new KeyFrame(Duration.millis(400),
                new KeyValue(winnerLabel.translateYProperty(), 30),
                new KeyValue(winnerLabel.opacityProperty(), 0)),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(winnerLabel.translateYProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(winnerLabel.opacityProperty(), 1))
        );
        anim.play();
    }

    private void animateEnergiesEntrance() {
        finalEnergies.setTranslateY(20);
        finalEnergies.setOpacity(0);
        
        Timeline anim = new Timeline(
            new KeyFrame(Duration.millis(600),
                new KeyValue(finalEnergies.translateYProperty(), 20),
                new KeyValue(finalEnergies.opacityProperty(), 0)),
            new KeyFrame(Duration.millis(1200),
                new KeyValue(finalEnergies.translateYProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(finalEnergies.opacityProperty(), 1))
        );
        anim.play();
    }

    private void setupPlayAgainAnimations() {
        ScaleTransition hoverScale = new ScaleTransition(Duration.millis(120), playAgainBtn);
        hoverScale.setFromX(1.0);
        hoverScale.setFromY(1.0);
        hoverScale.setToX(1.1);
        hoverScale.setToY(1.1);
        hoverScale.setAutoReverse(true);
        hoverScale.setCycleCount(2);
        
        playAgainBtn.setOnMouseEntered(e -> {
            hoverScale.playFromStart();
            playAgainBtn.setStyle("-fx-padding: 15 50; -fx-background-color: linear-gradient(to bottom, #ff9800 0%, #ffc107 100%); -fx-text-fill: #212529; -fx-font-weight: bold; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(255,152,0,0.8), 18, 0.6, 0, 5); -fx-cursor: hand;");
        });
        
        playAgainBtn.setOnMouseExited(e -> {
            playAgainBtn.setStyle("-fx-padding: 15 50; -fx-background-color: linear-gradient(to bottom, #ffc107 0%, #ff9800 100%); -fx-text-fill: #212529; -fx-font-weight: bold; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(255,152,0,0.6), 15, 0.5, 0, 4);");
        });
    }

    private void startConfetti() {
        String[] emojis = {"🎉", "✨", "🎊", "⭐", "💫", "🏆", "⚡", "👾"};
        
        for (int i = 0; i < 40; i++) {
            Label confetti = new Label(emojis[(int)(Math.random() * emojis.length)]);
            confetti.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 18 + Math.random() * 10));
            confetti.setTranslateX(Math.random() * 800);
            confetti.setTranslateY(-30);
            confetti.setRotate(Math.random() * 360);
            confetti.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 5, 0.5, 0, 1);");
            
            double duration = 3 + Math.random() * 4;
            double endX = confetti.getTranslateX() + (Math.random() * 200 - 100);
            double endY = 530 + Math.random() * 50;
            double endRotate = confetti.getRotate() + (Math.random() * 720 - 360);
            
            Timeline confettiAnim = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(confetti.translateXProperty(), confetti.getTranslateX()),
                    new KeyValue(confetti.translateYProperty(), confetti.getTranslateY()),
                    new KeyValue(confetti.rotateProperty(), confetti.getRotate()),
                    new KeyValue(confetti.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(duration),
                    new KeyValue(confetti.translateXProperty(), endX, Interpolator.EASE_OUT),
                    new KeyValue(confetti.translateYProperty(), endY, Interpolator.EASE_IN),
                    new KeyValue(confetti.rotateProperty(), endRotate),
                    new KeyValue(confetti.opacityProperty(), 0))
            );
            confettiAnim.setOnFinished(e -> confettiLayer.getChildren().remove(confetti));
            confettiAnim.play();
            
            confettiLayer.getChildren().add(confetti);
        }
        
        Timeline confettiSpawner = new Timeline(
            new KeyFrame(Duration.seconds(0), e -> startConfetti())
        );
        confettiSpawner.setDelay(Duration.seconds(5));
        confettiSpawner.setCycleCount(1);
        confettiSpawner.play();
    }

    public VBox getRoot() { return root; }
    public Button getPlayAgainBtn() { return playAgainBtn; }
}