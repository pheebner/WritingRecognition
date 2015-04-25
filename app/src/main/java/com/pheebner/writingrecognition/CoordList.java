package com.pheebner.writingrecognition;

/**
* Created by pjhee_000 on 4/25/2015.
*/
class CoordList {

    private Node startNode, currentNode;

    private int arrayPos, numNodes;
    public float smallestX, smallestY, largestX, largestY;

    public CoordList() {
        init();
    }

    public void init() {
        startNode = new Node();
        currentNode = startNode;

        arrayPos = 0;
        numNodes = 1;

        smallestX = Integer.MAX_VALUE;
        smallestY = Integer.MAX_VALUE;
        largestX = 0;
        largestY = 0;
    }

    private static class Node {
        public float[] xCoords = new float[1000];
        public float[] yCoords = new float[1000];
        public Node next = null;
    }

    public void addCoords(float x, float y) {

        smallestX = x < smallestX ? x : smallestX;
        smallestY = y < smallestY ? y : smallestY;

        largestX = x > largestX ? x : largestX;
        largestY = y > largestY ? y : largestY;

        if (arrayPos == 1000) {
            Node newNode = new Node();
            currentNode.next = newNode;
            currentNode = newNode;
            numNodes++;

            arrayPos = 0;
        }

        currentNode.xCoords[arrayPos] = x;
        currentNode.yCoords[arrayPos] = y;

        arrayPos++;
    }

    public int getNumCoordPairs() {
        return (numNodes - 1) * 1000 + arrayPos;
    }

    public boolean standardize(int[] xCoords, int[] yCoords) {
        int width = (int)(largestX - smallestX + 1);
        int height = (int)(largestY - smallestY + 1);
        if (width < 50 || height < 50) return false;

        int greaterDimension = width > height ? width : height;

        int numCoordPairs = getNumCoordPairs();
        if (numCoordPairs < 20) return false;

        int samplingWidth = numCoordPairs / 20;
        int pos;
        Node currentNode = startNode;
        for (int i = 0; i < 20; i++) {
            pos = i * samplingWidth;
            if (pos > 999) {
                pos = pos % 1000;
                currentNode = currentNode.next;
            }

            xCoords[i] = (int)((currentNode.xCoords[pos] - smallestX) * 50 / greaterDimension);
            yCoords[i] = (int)((currentNode.yCoords[pos] - smallestY) * 50 / greaterDimension);
        }

        return true;
    }
}
