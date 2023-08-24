package edu.curtin.saed.assignment1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Fortress {
    private JFXArena arena;
    private BlockingQueue<Wall> wallQueue = new ArrayBlockingQueue<>(10);
    private static final int MAX_WALLS = 10;

    private int remWalls = MAX_WALLS;

    public Fortress(JFXArena arena) {
        this.arena = arena;
    }

    // Method to process Wall requests from blocking queue and add them
    public void processWallRequests() {
        Thread fortThread = new Thread(() -> {
            while (true) {
                try {
                    Wall request = wallQueue.take();
                    addNewWall(request.getX(), request.getY());
                    Thread.sleep(2000);
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
                logger.appendText("Wall Queued at " + "[" + (int) x + "," + (int) y + "]\n");
                wallQueue.add(newWall);
                remWalls--;
            }
        }
    }

    // GUI thread calling method to add a new wall to the screen
    public void addNewWall(double x, double y) {
        if (arena != null) {
            Platform.runLater(() -> {
                arena.setNewWall();
                arena.addNewWall(x, y);
            });
        }
    }

    // Method to increase wall count
    public void increaseWallCount() {
        remWalls++;
    }

}
