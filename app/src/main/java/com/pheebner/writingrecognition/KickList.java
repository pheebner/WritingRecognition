package com.pheebner.writingrecognition;

/**
 * Created by pjhee_000 on 4/25/2015.
 */
public class KickList {

    private int index = 0;

    private int pos = 0;

    private double[][] dataArray = new double[3][40];

    public void put(double[] data) {
        if (index == 2) {
            dataArray[0] = dataArray[1];
            dataArray[1] = dataArray[2];
            dataArray[2] = data;

        } else {
            dataArray[index] = data;
            index++;
        }
    }

    public double[] getNext() {
        double[] retVal = dataArray[pos];

        pos = (pos + 1) % (index + 1);

        return retVal;
    }
}
