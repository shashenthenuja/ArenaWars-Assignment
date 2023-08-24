package edu.curtin.saed.assignment1;

import java.util.Objects;

public class Wall {
    private final double x;
    private final double y;

    // override hashcode to check if wall object is same
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    // override equal method to check if wall object is same
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
        // type cast the object to Wall
        Wall wall = (Wall) obj;
        // return true if the coordinates are equal
        return Double.compare(wall.x, x) == 0 && Double.compare(wall.y, y) == 0;
    }

    public Wall(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
