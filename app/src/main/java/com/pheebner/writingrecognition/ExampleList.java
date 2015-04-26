package com.pheebner.writingrecognition;

/**
 * Created by pjhee_000 on 4/26/2015.
 */
public class ExampleList {

    private Node end = null;
    private Node current = null;

    private static class Node {
        public double[] example;
        public Node next;
    }

    public void addExample(double[] example) {
        if (end == null) {
            end = new Node();
            end.example = example;
            end.next = end;
            current = end;
        } else {
            Node nNode = new Node();
            nNode.example = example;
            nNode.next = end.next;

            end.next = nNode;
            end = nNode;
        }
    }

    public double[] getExample() {
        if (current == null) return null;

        double[] retVal = current.example;

        current = current.next;

        return retVal;
    }
}
