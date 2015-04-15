package com.pheebner.writingrecognition;

/**
 * Created by pjhee_000 on 4/12/2015.
 */
public class Neuron {

    public int numInputs;
    public double[] weights;

    public Neuron(int numInputs) {
        this.numInputs = numInputs;

        weights = new double[numInputs + 1];

        for (int i = 0; i < numInputs + 1; i++) {
            weights[i] = Math.random() * 2.0 - 1.0;
        }
    }
}
