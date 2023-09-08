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
    private Thread scoreThread;
    private Score score;
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
        Pane space = new Pane();
        HBox.setHgrow(space, javafx.scene.layout.Priority.ALWAYS);
        ToolBar toolbar = new ToolBar(label, space, label2);
        arena.setCitadelLocation();

        score = new Score();
        fort = new Fortress(arena, label2);
        spawn = new Spawn(arena, logger);
        fort.run();

        arena.addListener((x, y) -> {
            fort.requestWall(x, y, logger);
        });

        // Button btn1 = new Button("My Button 1");
        // Button btn2 = new Button("My Button 2");

        // toolbar.getItems().addAll(btn1, btn2, label);

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
        spawn.processRobotRequests(score, this);

        updateScore(score, stage, label);

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
