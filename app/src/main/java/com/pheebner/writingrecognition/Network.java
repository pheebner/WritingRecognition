package com.pheebner.writingrecognition;

public class Network {

    private int numInputs, numHidden, numOutputs;
    private int numHiddenLayers;

    double[] lastInputs;

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
        lastInputs = inputs;
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
                outputs[j] = neuron.output = sigmoid(netInput);
            }
        }

        return outputs;
    }

    public void backPropagate(double[] desiredOutputs) {
        if (desiredOutputs.length != numOutputs) {
            return;
        }

        int layerIndex = layers.length - 1;
        Layer currLayer = layers[layerIndex];
        Layer prevLayer = layers[layerIndex - 1];
        for (int i = 0; i < numOutputs; i++) {
            Neuron neuron = currLayer.neurons[i];
            neuron.error = neuron.output * (1 - neuron.output) * (desiredOutputs[i] - neuron.output);

            for (int j = 0; j < prevLayer.neurons.length; j++) {
                Neuron prevNeuron = prevLayer.neurons[j];

                neuron.weights[j] = neuron.weights[j] + (neuron.error * prevNeuron.output);
            }
        }

        Layer nextLayer;
        layerIndex--;

        while (layerIndex != 0) {

            nextLayer = layers[layerIndex + 1];
            currLayer = layers[layerIndex];
            prevLayer = layers[layerIndex - 1];

            for (int i = 0; i < numHidden; i++) {
                Neuron neuron = currLayer.neurons[i];

                double errorSummation = 0;
                for (int j = 0; j < nextLayer.neurons.length; j++) {
                    Neuron nextNeuron = nextLayer.neurons[j];
                    errorSummation += nextNeuron.error * nextNeuron.weights[i];
                }

                neuron.error = neuron.output * (1 - neuron.output) * errorSummation;

                for (int j = 0; j < prevLayer.neurons.length; j++) {
                    Neuron prevNeuron = prevLayer.neurons[j];

                    neuron.weights[j] = neuron.weights[j] + (neuron.error * prevNeuron.output);
                }
            }

            layerIndex--;
        }

        nextLayer = layers[layerIndex + 1];
        currLayer = layers[layerIndex];

        for (int i = 0; i < numHidden; i++) {
            Neuron neuron = currLayer.neurons[i];

            double errorSummation = 0;
            for (int j = 0; j < nextLayer.neurons.length; j++) {
                Neuron nextNeuron = nextLayer.neurons[j];
                errorSummation += nextNeuron.error * nextNeuron.weights[i];
            }

            neuron.error = neuron.output * (1 - neuron.output) * errorSummation;

            for (int j = 0; j < lastInputs.length; j++) {
                neuron.weights[j] = neuron.weights[j] + (neuron.error * lastInputs[j]);
            }
        }
    }

    public void train(double[] inputs, double[] outputs) {
        forwardPass(inputs);
        backPropagate(outputs);
    }

    public double sigmoid(double netInput) {
        return (1 / (1 + Math.exp(-netInput)));
    }

}