package edu.curtin.saed.assignment1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/* *******************************************************************
* File:       Spawn.java
* Author:     G.G.T.Shashen
* Created:    26/08/2023
* Modified:   09/09/2022
* Desc:       Robot spawn logic implementation
***********************************************************************/
public class Spawn {
    private JFXArena arena;
    private BlockingQueue<Robot> robotQueue = new LinkedBlockingQueue<>();
    private ExecutorService robotExecutor = Executors.newCachedThreadPool();
    private List<Movement> movementThreads = new ArrayList<>();
    private TextArea logger;
    private int robotCount = 1;

    public Spawn(JFXArena arena, TextArea logger) {
        this.arena = arena;
        this.logger = logger;
    }

    /*
     * Method to process the robot requests in the blockingqueue using the
     * threadpool
     */
    public void processRobotRequests(Score score, App app, Fortress fort) {
        robotExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (!robotQueue.isEmpty()) {
                        Robot request = robotQueue.take();
                        addNewRobot(request.getX(), request.getY(), request.getId(), request.getDelay(),
                                request.getTypeId());
                        Platform.runLater(() -> {
                            logger.appendText(request.getId() + " Spawned at " + "[" + (int) request.getX() + ","
                                    + (int) request.getY() + "]\n");
                        });
                        // add the movement to a list so the threads can be interrupted later
                        movementThreads.add(new Movement(request, arena, logger, score, app, fort));
                        movementThreads.get(movementThreads.size() - 1).run();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    /*
     * Method to create new Robot requests and add them to the blockingqueue using
     * the threadpool
     */
    public void requestRobot() {
        robotExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // get the available spawn coordinates and spawn a random robot
                    double[] coordinates = arena.getSpawnCoordinates();
                    if (coordinates != null) {
                        int robotType = ThreadLocalRandom.current().nextInt(3) + 1;
                        Robot newRobot = new Robot(coordinates[0], coordinates[1], "Robot " + robotCount,
                                robotType);
                        robotQueue.add(newRobot);
                        robotCount++;
                        Thread.sleep(1500);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

    }

    // GUI thread calling method to add a new robot to the screen
    public void addNewRobot(double x, double y, String id, int delay, int typeId) {
        if (arena != null) {
            Platform.runLater(() -> {
                arena.addNewRobot(x, y, id, delay, typeId);
            });
        }
    }

    // Method to end the spawning thread
    public void endThreads() {
        // end all the movement threads
        for (Movement mt : movementThreads) {
            mt.endThread();
        }
        robotExecutor.shutdownNow();
    }
}
