package com.trafficAnalysis;

import org.redfx.strange.Complex;
import org.redfx.strange.gate.SingleQubitGate;

public class Rotation extends SingleQubitGate {

    Complex[][] matrix;
    private final double thetav;

    public Rotation(double theta, Axes axis, int idx) {
        super(idx);
        this.thetav = theta;
        switch(axis){
            case XAxis: matrix =  new Complex[][]{{new Complex(Math.cos(theta/2),0), new Complex(0,-Math.sin(theta/2))}, {new Complex(0,-Math.sin(theta/2)), new Complex(Math.cos(theta/2),0)}}; break;
            case YAxis: matrix =  new Complex[][]{{new Complex(Math.cos(theta/2),0),new Complex(-Math.sin(theta/2),0)}, {new Complex(Math.sin(theta/2),0),new Complex(Math.cos(theta/2))}}; break;
            case ZAxis: matrix =  new Complex[][]{{new Complex(0,Math.pow(Math.E,-theta/2)),Complex.ZERO}, {Complex.ZERO,new Complex(0,Math.pow(Math.E,theta/2))}}; break;
        }
    }

    @Override
    public Complex[][] getMatrix() {
        return matrix;
    }

    @Override
    public void setInverse(boolean v) {
        super.setInverse(v);
        matrix = Complex.conjugateTranspose(matrix);
    }

    @Override public String getCaption() {
        return "RotationX " + thetav;
    }

    public enum Axes{
        XAxis,
        YAxis,
        ZAxis
    }

}
