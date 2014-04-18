package fi.ni.pulssi;

import flanagan.io.*;
import flanagan.math.*;
import flanagan.plot.*;


/*
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */

public class FourierTransformExample{

    public static void main(String[] args){

        int nPoints = 256;  // number of points
        double[] tdata = new double[nPoints];
        double[] ydata = new double[nPoints];

        double amplitude1 = 1.0D;
        double amplitude2 = 2.0D;
        double pointsPerCycle = 200;
        double deltaT = 1.0D/pointsPerCycle;

        // Create wave form
        for(int i=0; i<nPoints; i++){
        	ydata[i]= amplitude1*Math.sin(2.0D*Math.PI*i/pointsPerCycle);
            tdata[i]=i*deltaT;
        }

        // Plot original data
        PlotGraph pg0 = new PlotGraph(tdata, ydata);
        pg0.setGraphTitle("y = sin(2.pi.t) + 2sin(10.pi.t)");
        pg0.setXaxisLegend("time");
        pg0.setXaxisUnitsName("s");
        pg0.setXaxisLegend("y");
        pg0.plot();

        // Obtain Power spectrum
        FourierTransform ft0 = new FourierTransform(ydata);
        ft0.setDeltaT(deltaT);
        double[][] powerSpectrum = ft0.powerSpectrum();

        // Plot power spectrum
        ft0.plotPowerSpectrum();
    }


}