package edu.curtin.saed.assignment1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Arena Wars (JavaFX)");
        TextArea logger = new TextArea();
        JFXArena arena = new JFXArena();
        arena.setCitadelLocation();
        Score score = new Score();
        Fortress fort = new Fortress(arena);
        Spawn spawn = new Spawn(arena, logger);
        fort.processWallRequests();
        arena.addListener((x, y) -> {
            // System.out.println("Arena click at (" + x + "," + y + ")");
            fort.requestWall(x, y, logger);
            // score.addDestroyBonus();
        });

        ToolBar toolbar = new ToolBar();
        // Button btn1 = new Button("My Button 1");
        // Button btn2 = new Button("My Button 2");
        Label label = new Label("Score: 0");
        // toolbar.getItems().addAll(btn1, btn2, label);
        toolbar.getItems().addAll(label);

        // btn1.setOnAction((event) ->
        // {
        // System.out.println("Button 1 pressed");
        // });

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(arena, logger);
        arena.setMinWidth(300.0);

        BorderPane contentPane = new BorderPane();
        contentPane.setTop(toolbar);
        contentPane.setCenter(splitPane);

        Scene scene = new Scene(contentPane, 800, 800);
        stage.setScene(scene);
        stage.show();

        spawn.requestRobot();
        spawn.processRobotRequests();

        updateScore(score, stage, label);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                fort.endThread();
                spawn.endThreads();
            }
        });
    }

    // Method to update scores in a new thread
    public void updateScore(Score score, Stage stage, Label label) {
        Thread scoreThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    score.addScore();
                    Platform.runLater(() -> {
                        label.setText("Score: " + score.getScore());
                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        scoreThread.start();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (scoreThread != null && scoreThread.isAlive()) {
                    scoreThread.interrupt();
                }
            }
        });
    }
}
