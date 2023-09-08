package edu.curtin.saed.assignment1;

import java.util.Random;

import javafx.application.Platform;

public class Movement {
    private Robot robot;
    private JFXArena arena;
    private Thread moveThread;

    public Movement(Robot robot, JFXArena arena) {
        this.robot = robot;
        this.arena = arena;
    }

    public void moveDown() {
        if (robot.getY() < arena.getGridHeight() - 1.0 && !arena.containsImage(robot.getX(), robot.getY() + 1.0)) {
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void moveUp() {
        if (robot.getY() > 0.0 && !arena.containsImage(robot.getX(), robot.getY() - 1.0)) {
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void moveLeft() {
        if (robot.getX() > 0.0 && !arena.containsImage(robot.getX() - 1.0, robot.getY())) {
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void moveRight() {
        if (robot.getX() < arena.getGridWidth() - 1.0 && !arena.containsImage(robot.getX() + 1.0, robot.getY())) {
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void movementLogic() {
        // move the robots in random ways
        Random random = new Random();
        int mov = random.nextInt(4) + 1;
        switch (mov) {
            case 1:
                moveDown();
                break;
            case 2:
                moveUp();
                break;
            case 3:
                moveLeft();
                break;
            case 4:
                moveRight();
                break;
            default:
                break;
        }
    }

    public void move() {
        // movement thread with robot delay
        moveThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(robot.getDelay());
                    movementLogic();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        moveThread.start();
    }

}
