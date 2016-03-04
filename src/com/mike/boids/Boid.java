package com.mike.boids;

import com.mike.util3D.Vectors;

/**
 * Created by mike on 3/2/2016.
 */
public class Boid {

    // 30mph is 13.41 m/s
    private double mVelocityLimit = 13.41;


    private double[] mLocation; // in XYZ meters
    private double[] mVelocity; // in XYZ m/s

    public Boid(double[] loc) {
        mVelocity = new double[3];
        mVelocity[0] = mVelocity[1] = mVelocity[2] = 0.0;

        mLocation = Vectors.init(loc);
    }

    public double[] getLocation() {
        return mLocation;
    }

    public double[] getVelocity() {
        return mVelocity;
    }

    public void move(double[] velocity) {
        mVelocity = Vectors.add(mVelocity, velocity);
    }

    /**
     * change the velocity but with limitations
     *
     * @TODO should make the limit more realistic since
     * we don't limit the rate of change or model the increasing cost
     * to go faster
     *
     * @param delta
     */
    public void changeVelocity(double[] delta) {
        mVelocity = Vectors.add(mVelocity, delta);

        double abs = Vectors.abs(getVelocity());
        if (abs > mVelocityLimit) {
            double[] v = Vectors.mult((Vectors.divide(getVelocity(), abs)), mVelocityLimit);
            mVelocity = v;
        }
    }

    /**
     * given a delta-time (in s) and a velocity (in m/s)
     * compute new location
     *
     * @param deltaT
     */
    public void updatePosition(double deltaT) {
        double[] loc = Vectors.init(mLocation);
        double[] v = Vectors.mult(mVelocity, deltaT);
        mLocation = Vectors.add(loc, v);
    }

}
