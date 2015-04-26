package com.pheebner.writingrecognition;

/**
 * Philip Heebner
 * Artificial Intelligence
 * Final Project
 * Neural Network implementation
 * Implementation of a neural network using Layer.java and Neuron.java.
 */
public class Network {

    private int numInputs, numHidden, numOutputs;
    private int numHiddenLayers;
    private double curvature; //used to affect curvature of sigmoid function

    double[] lastInputs; //inputs of the last forward pass performed

    private Layer[] layers; //all hidden layers and the output layer

    public Network(double curvature, int numInputs, int numHidden, int numHiddenLayers, int numOutputs) {
        this.curvature = curvature;
        this.numInputs = numInputs;
        this.numHidden = numHidden;
        this.numOutputs = numOutputs;
        this.numHiddenLayers = numHiddenLayers;

        //create numHidden hidden layers
        layers = new Layer[numHiddenLayers + 1];
        layers[0] = new Layer(numHidden, numInputs);
        int i;
        for (i = 1; i < numHiddenLayers; i++) {
            layers[i] = new Layer(numHidden, numHidden);
        }

        //create one output layer
        layers[i] = new Layer(numOutputs, numHidden);
    }

    //perform a forward pass through the network using inputs
    //return the output of all the output nodes
    public double[] forwardPass(double[] inputs) {
        lastInputs = inputs;
        double[] outputs = null;

        //input length must match number of inputs
        if (inputs.length != numInputs) {
            return null;
        }

        //for all layers
        for (int i = 0; i < layers.length; i++) {
            if (i > 0) {
                inputs = outputs;
            }

            Layer layer = layers[i];

            outputs = new double[layer.numNeurons];

            double netInput;

            //for each neuron in layer
            for (int j = 0; j < layer.numNeurons; j++) {

                Neuron neuron = layer.neurons[j];
                netInput = 0;

                //add all weight-affected inputs to this neuron
                for (int k = 0; k < neuron.numInputs; k++) {
                    netInput += inputs[k] * neuron.weights[k];
                }

                //subtract bias
                netInput += -1;
                //set output
                outputs[j] = neuron.output = sigmoid(netInput);
            }
        }

        return outputs;
    }

    //back-propagate through the network comparing actual output
    //of the last forward pass to the desired output.
    //
    //NOTE: this implementation can probably be refined a bit...
    public void backPropagate(double[] desiredOutputs) {
        if (desiredOutputs.length != numOutputs) {
            return;
        }

        int layerIndex = layers.length - 1; //holds the current layer being examined

        Layer currLayer = layers[layerIndex]; //current layer (initially the output layer)
        Layer prevLayer = layers[layerIndex - 1]; //layer one step backwards (towards input)

        //for each output neuron
        for (int i = 0; i < numOutputs; i++) {
            Neuron neuron = currLayer.neurons[i];
            //error is found through the derivative of the sigmoid function
            neuron.error = neuron.output * (1 - neuron.output) * (desiredOutputs[i] - neuron.output);

            //for each neuron input to this neuron
            for (int j = 0; j < prevLayer.neurons.length; j++) {
                Neuron prevNeuron = prevLayer.neurons[j];

                //change the weight going in so error will be less next time
                neuron.weights[j] = neuron.weights[j] + (neuron.error * prevNeuron.output);
            }
        }

        Layer nextLayer; //layer one step forward (towards output)
        layerIndex--;

        //for every hidden layer except the first
        while (layerIndex != 0) {

            nextLayer = layers[layerIndex + 1];
            currLayer = layers[layerIndex];
            prevLayer = layers[layerIndex - 1];

            //for each neuron in hidden layer
            for (int i = 0; i < numHidden; i++) {
                Neuron neuron = currLayer.neurons[i];

                double errorSummation = 0;
                for (int j = 0; j < nextLayer.neurons.length; j++) {
                    Neuron nextNeuron = nextLayer.neurons[j];
                    //this neuron's error is affected by the error of all
                    //the neurons in the next layer and their respective weights
                    errorSummation += nextNeuron.error * nextNeuron.weights[i];
                }

                neuron.error = neuron.output * (1 - neuron.output) * errorSummation;

                for (int j = 0; j < prevLayer.neurons.length; j++) {
                    Neuron prevNeuron = prevLayer.neurons[j];

                    //change the weights going into this neuron based on the error of this neuron
                    neuron.weights[j] = neuron.weights[j] + (neuron.error * prevNeuron.output);
                }
            }

            layerIndex--;
        }

        nextLayer = layers[layerIndex + 1];
        currLayer = layers[layerIndex];
        //last hidden layer next to the "input layer"

        for (int i = 0; i < numHidden; i++) {
            Neuron neuron = currLayer.neurons[i];

            //same as before
            double errorSummation = 0;
            for (int j = 0; j < nextLayer.neurons.length; j++) {
                Neuron nextNeuron = nextLayer.neurons[j];
                errorSummation += nextNeuron.error * nextNeuron.weights[i];
            }

            neuron.error = neuron.output * (1 - neuron.output) * errorSummation;

            //same as before, except use lastInputs instead of prevNeuron.output
            for (int j = 0; j < lastInputs.length; j++) {
                neuron.weights[j] = neuron.weights[j] + (neuron.error * lastInputs[j]);
            }
        }
    }

    //convenience method.  Performs a forward pass using input, and
    //backpropagates with the desired output.
    public void train(double[] input, double[] output) {
        forwardPass(input);
        backPropagate(output);
    }

    //sigmoid function.  Uses netInput and curvature.
    public double sigmoid(double netInput) {
        return (1 / (1 + Math.exp(-netInput / curvature)));
    }

}