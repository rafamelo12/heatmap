package com.sportshack.heatmap.beacontracker;

public class Coordinate {
    private float x;
    private float y;
    private float z;

    public Coordinate(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Coordinate(float x, float y) {
        this(x, y, 0);
    }

    public Coordinate() {
        this(0, 0);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public void trilaterate(float d1, float d2, float d3, Coordinate a, Coordinate b, Coordinate c) {
        this.x = (d1*d1 - d2*d2 - b.getX() * b.getX()) /(2 * b.getX());

        this.y =  (d1*d1 - d3*d3 + c.getX()*c.getX() + c.getY()*c.getY())/(2*c.getY()) - (this.x * c.getX())/c.getY();

        this.z = (float)Math.sqrt(d1*d1 - this.x * this.x - this.y * this.y);

    }
}

