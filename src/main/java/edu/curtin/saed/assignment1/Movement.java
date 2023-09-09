package edu.curtin.saed.assignment1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/* *******************************************************************
* File:       Movement.java
* Author:     G.G.T.Shashen
* Created:    06/09/2023
* Modified:   09/09/2022
* Desc:       Robot movement logic implementation
***********************************************************************/
public class Movement implements Runnable {
    private Robot robot;
    private JFXArena arena;
    private App app;
    private Score score;
    private Fortress fort;
    private TextArea logger;
    private ExecutorService robotExecutor;

    public Movement(Robot robot, JFXArena arena, TextArea logger, Score score, App app, Fortress fort, ExecutorService robotExecutor) {
        this.robot = robot;
        this.arena = arena;
        this.logger = logger;
        this.score = score;
        this.app = app;
        this.fort = fort;
        this.robotExecutor = robotExecutor;
    }

    // Move the robot down a grid square
    public void moveDown() {
        if (robot != null) {
            // check if the boundaries and the next grid square is valid
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
                    arena.addKey(robot.getId(), robot.getX(), robot.getY(), robot.getTypeId());
                    arena.requestLayout();
                    arena.removeKey(robot.getX(), oldY);
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    // Move the robot up a grid square
    public void moveUp() {
        if (robot != null) {
            // check if the boundaries and the next grid square is valid
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
                    arena.addKey(robot.getId(), robot.getX(), robot.getY(), robot.getTypeId());
                    arena.requestLayout();
                    arena.removeKey(robot.getX(), oldY);
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    // Move the robot left a grid square
    public void moveLeft() {
        if (robot != null) {
            // check if the boundaries and the next grid square is valid
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
                    arena.addKey(robot.getId(), robot.getX(), robot.getY(), robot.getTypeId());
                    arena.requestLayout();
                    arena.removeKey(oldX, robot.getY());
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    // Move the robot right a grid square
    public void moveRight() {
        if (robot != null) {
            // check if the boundaries and the next grid square is valid
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
                    arena.addKey(robot.getId(), robot.getX(), robot.getY(), robot.getTypeId());
                    arena.requestLayout();
                    arena.removeKey(oldX, robot.getY());
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
        // move the robots in random ways, wall destruction and game end
        if (robot != null) {
            int mov = ThreadLocalRandom.current().nextInt(4) + 1;
            switch (mov) {
                case 1:
                    // check if the next path contains wall and destroy them else end the game
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
                        fort.increaseWallCount();
                        score.addDestroyBonus();
                    } else if (arena.containsCitadel(robot.getX(), robot.getY() + 1.0)) {
                        moveDown();
                        logMessage("Game Over!\n");
                        logMessage("Final Score : " + score.getScore() + "\n");
                        arena.gameEnd(robot.getX(), robot.getY());
                        app.endGame();
                    } else {
                        moveDown();
                    }
                    break;
                case 2:
                    // check if the next path contains wall and destroy them else end the game
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
                        fort.increaseWallCount();
                        score.addDestroyBonus();
                    } else if (arena.containsCitadel(robot.getX(), robot.getY() - 1.0)) {
                        moveUp();
                        logMessage("Game Over!\n");
                        logMessage("Final Score : " + score.getScore() + "\n");
                        arena.gameEnd(robot.getX(), robot.getY());
                        app.endGame();
                    } else {
                        moveUp();
                    }
                    break;
                case 3:
                    // check if the next path contains wall and destroy them else end the game
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
                        fort.increaseWallCount();
                        score.addDestroyBonus();
                    } else if (arena.containsCitadel(robot.getX() - 1.0, robot.getY())) {
                        moveLeft();
                        logMessage("Game Over!\n");
                        logMessage("Final Score : " + score.getScore() + "\n");
                        arena.gameEnd(robot.getX(), robot.getY());
                        app.endGame();
                    } else {
                        moveLeft();
                    }
                    break;
                case 4:
                    // check if the next path contains wall and destroy them else end the game
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
                        fort.increaseWallCount();
                        score.addDestroyBonus();
                    } else if (arena.containsCitadel(robot.getX() + 1.0, robot.getY())) {
                        moveRight();
                        logMessage("Game Over!\n");
                        logMessage("Final Score : " + score.getScore() + "\n");
                        arena.gameEnd(robot.getX(), robot.getY());
                        app.endGame();
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
        Thread.currentThread().interrupt();
    }

    // Log messages to the GUI
    public void logMessage(String message) {
        Platform.runLater(() -> {
            logger.appendText(message);
        });
    }

    @Override
    public void run() {
        // movement thread with robot delay
        robotExecutor.submit(() -> {
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
    }

}
