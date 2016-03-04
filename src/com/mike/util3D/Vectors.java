package com.mike.util3D;

/**
 * Created by mike on 3/2/2016.
 */
public class Vectors {

//    static public double[] init (double[] v) {
//        if (v == null) {
//            v = new double[3];
//            v[0] = v[1] = v[2] = 0.0;
//        }
//        return v;
//    }
    static public double[] init () {
        double[] a = new double[3];
        a[0] = a[1] = a[2] = 0.0;
        return a;
    }

    static public double[] init (Location loc) {
        double[] v = new double[3];
        v[0] = loc.getX();
        v[1] = loc.getY();
        v[2] = loc.getZ();
        return v;
    }
    static public double[] init (double[] b) {
        double[] a = new double[3];
        if (b == null)
            a[0] = a[1] = a[2] = 0.0;
        else {
            a[0] = b[0];
            a[1] = b[1];
            a[2] = b[2];
        }
        return a;
    }

    // vector addition
    static public double[] add(double[] a, Location location) {
        double[] b = init(location);
        return add(a, b);
    }

    static public double[] add(double[] a, double[] b) {
        a = init (a);

        a[0] += b[0];
        a[1] += b[1];
        a[2] += b[2];
        return a;
    }
    // vector subtract a scalar
    static public double[] subtract(double[] v, double s) {
        v = init (v);

        v[0] -= s;
        v[1] -= s;
        v[2] -= s;
        return v;
    }
    // vector subtraction
    static public double[] subtract(double[] a, Location location) {
        double[] b = init(location);
        return subtract(a, b);
    }
    // vector subtract vectors
    static public double[] subtract(double[] a, double[] b) {
        a = init (a);

        a[0] -= b[0];
        a[1] -= b[1];
        a[2] -= b[2];
        return a;
    }

    // divide vector by scalar
    static public double[] divide(double[] v, double s) {
        v = init (v);
        v[0] /= s;
        v[1] /= s;
        v[2] /= s;
        return v;
    }
    // mult by scalar
    public static double[] mult(double[] a, double s) {
        a = init (a);
        a[0] *= s;
        a[1] *= s;
        a[2] *= s;
        return a;
    }

    // divide vectors
    static public double[] divide(double[] a, double [] b) {
        a = init (a);
        a[0] /= b[0];
        a[1] /= b[1];
        a[2] /= b[2];
        return a;
    }

    public static double abs(double[] v) {
        return Math.sqrt((v[0] * v[0]) + (v[1] * v[1]) + (v[2] * v[2]));
    }

    /**
     * Created by mike on 3/2/2016.
     */
    public static class Location {
        private double mX = 0;
        private double mY = 0;
        private double mZ = 0;
        private double x;

        public Location (double x, double y, double z) {
            mX = x;
            mY = y;
            mZ = z;
        }
        public Location (double[] v) {
            this (v[0], v[1], v[2]);
        }
        public Location(Location loc) {
            mX = loc.mX;
            mY = loc.mY;
            mZ = loc.mZ;
        }

        public double getX() {
            return mX;
        }
        public double getY() {
            return mY;
        }
        public double getZ() {
            return mZ;
        }
    }
}
