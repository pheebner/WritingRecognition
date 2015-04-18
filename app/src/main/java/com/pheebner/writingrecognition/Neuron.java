package com.pheebner.writingrecognition;

import android.util.Log;

/**
 * Created by pjhee_000 on 4/12/2015.
 */
public class Neuron {

    public int numInputs;
    public double[] weights;
    public double output;
    public double error;

    public Neuron(int numInputs) {
        this.numInputs = numInputs;

        weights = new double[numInputs];

        output = 0;

        for (int i = 0; i < numInputs; i++) {
            weights[i] = Math.random() * 2.0 - 1.0;
            Log.d("OBS", "weight " + weights[i]);
        }
    }
}
