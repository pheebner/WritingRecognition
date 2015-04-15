package com.pheebner.writingrecognition;

public class Network {

    private int numInputs, numHidden, numOutputs;
    private int numHiddenLayers;

    private Layer[] layers;

    public Network(int numInputs, int numHidden, int numHiddenLayers, int numOutputs) {
        this.numInputs = numInputs;
        this.numHidden = numHidden;
        this.numOutputs = numOutputs;
        this.numHiddenLayers = numHiddenLayers;

        layers = new Layer[numHiddenLayers + 1];
        layers[0] = new Layer(numHidden, numInputs);
        int i;
        for (i = 1; i < numHiddenLayers; i++) {
            layers[i] = new Layer(numHidden, numHidden);
        }
        layers[i] = new Layer(numOutputs, numHidden);
    }

    public double[] forwardPass(double[] inputs) {
        double[] outputs = null;

        if (inputs.length != numInputs) {
            return null;
        }

        for (int i = 0; i < layers.length; i++) {
            if (i > 0) {
                inputs = outputs;
            }

            Layer layer = layers[i];

            outputs = new double[layer.numNeurons];

            double netInput;

            for (int j = 0; j < layer.numNeurons; j++) {

                Neuron neuron = layer.neurons[j];
                netInput = 0;

                for (int k = 0; k < neuron.numInputs; k++) {
                    netInput += inputs[k] * neuron.weights[k];
                }

                netInput += -1;
                outputs[j] = sigmoid(netInput);
            }
        }

        return outputs;
    }

    public double sigmoid(double netInput) {
        return (1 / (1 + Math.exp(-netInput)));
    }

}