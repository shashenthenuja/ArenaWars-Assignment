package edu.curtin.saed.assignment1;

import java.util.Objects;
import java.util.Random;

public class Robot {
    private double x;
    private double y;
    private int delay;
    private int typeId;
    private String id;
    private Random random = new Random();
    private JFXArena arena;
    private Movement movement;

    public Robot(JFXArena arena, double x, double y, String id, int typeId) {
        this.arena = arena;
        this.x = x;
        this.y = y;
        this.id = id;
        this.typeId = typeId;
        this.delay = random.nextInt(2000 - 500 + 1) + 500;
        movement = new Movement(this, arena);
        movement.move();
    }

    // override hashcode to check if robot object is same
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    // override equal method to check if robot object is same
    @Override
    public boolean equals(Object obj) {
        // check if the obj is the same instance
        if (this == obj) {
            return true;
        }
        // check if the object is a different class
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        // type cast the object to robot
        Robot robot = (Robot) obj;
        // return true if the coordinates are equal
        return Double.compare(robot.x, x) == 0 && Double.compare(robot.y, y) == 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getId() {
        return id;
    }

    public int getDelay() {
        return delay;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
