package com.pheebner.writingrecognition;

/**
 * Philip Heebner
 * Artificial Intelligence
 * Final Project
 * Layer of a neural network
 * Represents a layer in a neural network.  Can be hidden or output layer.
 */
public class Layer {

    public int numNeurons;
    public Neuron[] neurons;

    //Create a layer with as many neurons as numNeurons.
    //Have each neuron use as many inputs as inputsPerNeuron
    public Layer(int numNeurons, int inputsPerNeuron) {

        this.numNeurons = numNeurons;

        neurons = new Neuron[numNeurons];
        for (int i = 0; i < numNeurons; i++) {
            neurons[i] = new Neuron(inputsPerNeuron);
        }
    }
}