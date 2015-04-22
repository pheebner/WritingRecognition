package com.pheebner.writingrecognition;

/**
 * Philip Heebner
 * Artificial Intelligence
 * Final Project
 * Neuron to be used in a neural network
 */
public class Neuron {

    public int numInputs;
    public double[] weights; //weights on inputs to this neuron
    public double output;    //last output of this neuron
    public double error;     //holds error for back propagation

    public Neuron(int numInputs) {
        this.numInputs = numInputs;

        weights = new double[numInputs];

        output = 0;

        for (int i = 0; i < numInputs; i++) {
            //random number between -1 and 1
            weights[i] = Math.random() * 2.0 - 1.0;
        }
    }
}
