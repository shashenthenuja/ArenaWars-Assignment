package edu.curtin.saed.assignment1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/* *******************************************************************
* File:       Fortress.java
* Author:     G.G.T.Shashen
* Created:    24/08/2023
* Modified:   09/09/2022
* Desc:       Wall building implementation
***********************************************************************/
public class Fortress implements Runnable {
    private JFXArena arena;
    private Label label;
    private BlockingQueue<Wall> wallQueue = new ArrayBlockingQueue<>(10);
    private static final int MAX_WALLS = 10;
    private Thread fortThread;

    private int remWalls = MAX_WALLS;
    private int queuedWalls = 0;

    public Fortress(JFXArena arena, Label label) {
        this.arena = arena;
        this.label = label;
    }

    /* Override run method to process Wall requests from the blocking queue and add
    them */
    @Override
    public void run() {
        fortThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (!wallQueue.isEmpty()) {
                        Wall request = wallQueue.poll();
                        addNewWall(request.getX(), request.getY());
                        queuedWalls--;
                        Platform.runLater(() -> {
                            label.setText("Walls Queued : " + String.valueOf(queuedWalls));
                        });
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        fortThread.start();
    }

    // Method to get coordinates and create a new wall request and add to queue
    public void requestWall(double x, double y, TextArea logger) {
        Wall newWall = new Wall(x, y);
        if (remWalls > 0) {
            // check if the grid currently contains a wall and if it is already queued
            if (!wallQueue.contains(newWall) && !arena.containsImage(x, y)) {
                wallQueue.add(newWall);
                remWalls--;
                queuedWalls++;
                Platform.runLater(() -> {
                    logger.appendText("Wall Queued at " + "[" + (int) x + "," + (int) y + "] - " + "(" + remWalls + ")"
                            + " walls remaining\n");
                    label.setText("Walls Queued : " + String.valueOf(queuedWalls));
                });
            }
        }
    }

    // GUI thread calling method to add a new wall to the screen
    public void addNewWall(double x, double y) {
        if (arena != null) {
            Platform.runLater(() -> {
                arena.addNewWall(x, y);
            });
        }
    }

    // Method to increase wall count
    public void increaseWallCount() {
        remWalls++;
    }

    // Method to end the wall thread
    public void endThread() {
        if (fortThread != null && fortThread.isAlive()) {
            fortThread.interrupt();
        }
    }

}
