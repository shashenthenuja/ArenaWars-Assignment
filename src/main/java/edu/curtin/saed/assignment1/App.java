package edu.curtin.saed.assignment1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Arena Wars (JavaFX)");
        TextArea logger = new TextArea();
        JFXArena arena = new JFXArena();
        Fortress fort = new Fortress(arena);
        fort.processWallRequests();
        arena.addListener((x, y) -> {
            System.out.println("Arena click at (" + x + "," + y + ")");
            fort.requestWall(x, y, logger);
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

        updateScore(label);
    }

    // Method to update scores in a new thread
    public void updateScore(Label label) {
        Score score = new Score();
        Thread scoreThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    score.addScore();
                    Platform.runLater(() -> {
                        label.setText("Score: " + score.getScore());
                    });

                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        scoreThread.start();
    }
}
