package com.pheebner.writingrecognition;

/**
 * Created by pjhee_000 on 4/12/2015.
 */
public class Layer {

    public int numNeurons;
    public Neuron[] neurons;

    public Layer(int numNeurons, int inputsPerNeuron) {

        this.numNeurons = numNeurons;

        neurons = new Neuron[numNeurons];
        for (int i = 0; i < numNeurons; i++) {
            neurons[i] = new Neuron(inputsPerNeuron);
        }
    }
}
