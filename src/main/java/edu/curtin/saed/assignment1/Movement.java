package edu.curtin.saed.assignment1;

import java.util.concurrent.ThreadLocalRandom;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Movement implements Runnable {
    private Robot robot;
    private JFXArena arena;
    private Score score;
    private Thread moveThread;
    private TextArea logger;

    public Movement(Robot robot, JFXArena arena, TextArea logger, Score score) {
        this.robot = robot;
        this.arena = arena;
        this.logger = logger;
        this.score = score;
    }

    public void moveDown() {
        if (robot != null) {
            if (robot.getY() < arena.getGridHeight() - 1.0 && !arena.containsRobot(robot.getX(), robot.getY() + 1.0)) {
                // Occupy current square and next square
                arena.occupySquare(robot.getX(), robot.getY());
                arena.occupySquare(robot.getX(), robot.getY() + 1.0);
                // move each 0.1 increment until a full square
                for (int i = 0; i < 10; i++) {
                    double oldY = robot.getY();
                    double robotY = robot.getY() + 0.1;
                    double newY = Math.round(robotY * 10.0) / 10.0;
                    robot.setY(newY);
                    Platform.runLater(() -> {
                        arena.addKey(robot.getId(), robot.getX(), robot.getY(), robot.getTypeId());
                        arena.requestLayout();
                        arena.removeKey(robot.getX(), oldY);
                    });
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    public void moveUp() {
        if (robot != null) {
            if (robot.getY() > 0.0 && !arena.containsRobot(robot.getX(), robot.getY() - 1.0)) {
                // Occupy current square and next square
                arena.occupySquare(robot.getX(), robot.getY());
                arena.occupySquare(robot.getX(), robot.getY() - 1.0);
                // move each 0.1 increment until a full square
                for (int i = 0; i < 10; i++) {
                    double oldY = robot.getY();
                    double robotY = robot.getY() - 0.1;
                    double newY = Math.round(robotY * 10.0) / 10.0;
                    robot.setY(newY);
                    Platform.runLater(() -> {
                        arena.addKey(robot.getId(), robot.getX(), robot.getY(), robot.getTypeId());
                        arena.requestLayout();
                        arena.removeKey(robot.getX(), oldY);
                    });
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    public void moveLeft() {
        if (robot != null) {
            if (robot.getX() > 0.0 && !arena.containsRobot(robot.getX() - 1.0, robot.getY())) {
                // Occupy current square and next square
                arena.occupySquare(robot.getX(), robot.getY());
                arena.occupySquare(robot.getX() - 1.0, robot.getY());
                // move each 0.1 increment until a full square
                for (int i = 0; i < 10; i++) {
                    double oldX = robot.getX();
                    double robotX = robot.getX() - 0.1;
                    double newX = Math.round(robotX * 10.0) / 10.0;
                    robot.setX(newX);
                    Platform.runLater(() -> {
                        arena.addKey(robot.getId(), robot.getX(), robot.getY(), robot.getTypeId());
                        arena.requestLayout();
                        arena.removeKey(oldX, robot.getY());
                    });
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    public void moveRight() {
        if (robot != null) {
            if (robot.getX() < arena.getGridWidth() - 1.0 && !arena.containsRobot(robot.getX() + 1.0, robot.getY())) {
                // Occupy current square and next square
                arena.occupySquare(robot.getX(), robot.getY());
                arena.occupySquare(robot.getX() + 1.0, robot.getY());
                // move each 0.1 increment until a full square
                for (int i = 0; i < 10; i++) {
                    double oldX = robot.getX();
                    double robotX = robot.getX() + 0.1;
                    double newX = Math.round(robotX * 10.0) / 10.0;
                    robot.setX(newX);
                    Platform.runLater(() -> {
                        arena.addKey(robot.getId(), robot.getX(), robot.getY(), robot.getTypeId());
                        arena.requestLayout();
                        arena.removeKey(oldX, robot.getY());
                    });
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    public void movementLogic() {
        // move the robots in random ways and wall destruction
        if (robot != null) {
            int mov = ThreadLocalRandom.current().nextInt(4) + 1;
            switch (mov) {
                case 1:
                    // check if the next path contains wall and destroy them
                    if (arena.containsWall(robot.getX(), robot.getY() + 1.0)) {
                        moveDown();
                        arena.destroyWall(robot.getX(), robot.getY());
                        logMessage(robot.getId() + " Impacted Wall at [" + (int) robot.getX() + "," + (int) robot.getY()
                                + "]\n");
                        destroyRobot();
                        score.addDestroyBonus();
                    } else if (arena.containsDestroWall(robot.getX(), robot.getY() + 1.0)) {
                        moveDown();
                        arena.removeKey(robot.getX(), robot.getY());
                        logMessage(robot.getId() + " Destroyed Wall at [" + (int) robot.getX() + ","
                                + (int) robot.getY() + "]\n");
                        destroyRobot();
                        score.addDestroyBonus();
                    } else {
                        moveDown();
                    }
                    break;
                case 2:
                    // check if the next path contains wall and destroy them
                    if (arena.containsWall(robot.getX(), robot.getY() - 1.0)) {
                        moveUp();
                        arena.destroyWall(robot.getX(), robot.getY());
                        logMessage(robot.getId() + " Impacted Wall at [" + (int) robot.getX() + "," + (int) robot.getY()
                                + "]\n");
                        destroyRobot();
                        score.addDestroyBonus();
                    } else if (arena.containsDestroWall(robot.getX(), robot.getY() - 1.0)) {
                        moveUp();
                        arena.removeKey(robot.getX(), robot.getY());
                        logMessage(robot.getId() + " Destroyed Wall at [" + (int) robot.getX() + ","
                                + (int) robot.getY() + "]\n");
                        destroyRobot();
                        score.addDestroyBonus();
                    } else {
                        moveUp();
                    }
                    break;
                case 3:
                    // check if the next path contains wall and destroy them
                    if (arena.containsWall(robot.getX() - 1.0, robot.getY())) {
                        moveLeft();
                        arena.destroyWall(robot.getX(), robot.getY());
                        logMessage(robot.getId() + " Impacted Wall at [" + (int) robot.getX() + "," + (int) robot.getY()
                                + "]\n");
                        destroyRobot();
                        score.addDestroyBonus();
                    } else if (arena.containsDestroWall(robot.getX() - 1.0, robot.getY())) {
                        moveLeft();
                        arena.removeKey(robot.getX(), robot.getY());
                        logMessage(robot.getId() + " Destroyed Wall at [" + (int) robot.getX() + ","
                                + (int) robot.getY() + "]\n");
                        destroyRobot();
                        score.addDestroyBonus();
                    } else {
                        moveLeft();
                    }
                    break;
                case 4:
                    // check if the next path contains wall and destroy them
                    if (arena.containsWall(robot.getX() + 1.0, robot.getY())) {
                        moveRight();
                        arena.destroyWall(robot.getX(), robot.getY());
                        logMessage(robot.getId() + " Impacted Wall at [" + (int) robot.getX() + "," + (int) robot.getY()
                                + "]\n");
                        destroyRobot();
                        score.addDestroyBonus();
                    } else if (arena.containsDestroWall(robot.getX() + 1.0, robot.getY())) {
                        moveRight();
                        arena.removeKey(robot.getX(), robot.getY());
                        logMessage(robot.getId() + " Destroyed Wall at [" + (int) robot.getX() + ","
                                + (int) robot.getY() + "]\n");
                        destroyRobot();
                        score.addDestroyBonus();
                    } else {
                        moveRight();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    // Method to destroy the robot
    public void destroyRobot() {
        robot = null;
    }

    // Log messages to the GUI
    public void logMessage(String message) {
        Platform.runLater(() -> {
            logger.appendText(message);
        });
    }

    // Method to end the wall thread
    public void endThread() {
        moveThread.interrupt();
    }

    @Override
    public void run() {
        // movement thread with robot delay
        moveThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (robot != null) {
                        Thread.sleep(robot.getDelay());
                        movementLogic();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        moveThread.start();
    }

}
