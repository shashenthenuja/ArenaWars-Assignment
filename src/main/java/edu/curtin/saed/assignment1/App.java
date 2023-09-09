package edu.curtin.saed.assignment1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/* *******************************************************************
* File:       App.java
* Author:     G.G.T.Shashen
* Created:    23/08/2023
* Modified:   09/09/2022
* Desc:       JavaFX Grid Game - Arena Wars
***********************************************************************/
public class App extends Application {
    private Thread scoreThread;
    private Fortress fort;
    private Spawn spawn;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Arena Wars (JavaFX)");
        TextArea logger = new TextArea();
        JFXArena arena = new JFXArena();
        Label label = new Label("Score : 0");
        Label label2 = new Label("Walls Queued : 0");

        // create a space element between the labels
        Pane space = new Pane();
        HBox.setHgrow(space, javafx.scene.layout.Priority.ALWAYS);
        ToolBar toolbar = new ToolBar(label, space, label2);
        // set the citadel location according to the grid size
        arena.setCitadelLocation();

        Score score = new Score();
        fort = new Fortress(arena, label2);
        spawn = new Spawn(arena, logger);
        // run wall thread
        fort.run();

        arena.addListener((x, y) -> {
            // request walls on mouse click events
            fort.requestWall(x, y, logger);
        });

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(arena, logger);
        arena.setMinWidth(300.0);

        BorderPane contentPane = new BorderPane();
        contentPane.setTop(toolbar);
        contentPane.setCenter(splitPane);

        Scene scene = new Scene(contentPane, 800, 800);
        stage.setScene(scene);
        stage.show();

        // run request and process robot threads
        spawn.requestRobot();
        spawn.processRobotRequests(score, this, fort);

        // update the score in a thread
        updateScore(score, stage, label);

        // end the game on gui close button event
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                endGame();
            }
        });

    }

    // Method to update scores in a new thread
    public void updateScore(Score score, Stage stage, Label label) {
        scoreThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    score.addScore();
                    Platform.runLater(() -> {
                        label.setText("Score: " + score.getScore());
                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        scoreThread.start();
    }

    // Method to interrupt all the threads except GUI to end the game
    public void endGame() {
        fort.endThread();
        spawn.endThreads();
        scoreThread.interrupt();
    }
}
