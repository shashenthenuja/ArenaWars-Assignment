package edu.curtin.saed.assignment1;

import java.util.concurrent.ThreadLocalRandom;

public class Robot {
    private double x;
    private double y;
    private int delay;
    private int typeId;
    private String id;

    public Robot(double x, double y, String id, int typeId) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.typeId = typeId;
        this.delay = ThreadLocalRandom.current().nextInt(2000 - 500 + 1) + 500;
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
