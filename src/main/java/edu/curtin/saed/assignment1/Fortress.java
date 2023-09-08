package edu.curtin.saed.assignment1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Fortress {
    private JFXArena arena;
    private BlockingQueue<Wall> wallQueue = new ArrayBlockingQueue<>(10);
    private static final int MAX_WALLS = 10;
    private Thread fortThread;

    private int remWalls = MAX_WALLS;

    public Fortress(JFXArena arena) {
        this.arena = arena;
    }

    // Method to process Wall requests from blocking queue and add them
    public void processWallRequests() {
        fortThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (!wallQueue.isEmpty()) {
                        Wall request = wallQueue.poll();
                        addNewWall(request.getX(), request.getY());
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
                logger.appendText("Wall Queued at " + "[" + (int) x + "," + (int) y + "] - " + "(" + remWalls + ")"
                        + " walls remaining\n");
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
