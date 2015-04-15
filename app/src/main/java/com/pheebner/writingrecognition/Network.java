package com.pheebner.writingrecognition;

public class Network {

    private int numInputs, numHidden, numOutputs;

    private Layer[] layers;

    public Network() {
        layers = new Layer[2];
        layers[0] = new Layer(6, 5);
        layers[1] = new Layer(4, 6);
    }

    public double[] forwardPass(double[] inputs) {
        double[] outputs = null;

        for (int i = 0; i < 2; i++) {
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