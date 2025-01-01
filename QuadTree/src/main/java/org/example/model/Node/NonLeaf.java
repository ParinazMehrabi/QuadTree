package org.example.model.Node;

import java.awt.Rectangle;
import java.util.List;

public class NonLeaf extends Node {
    private Node topLeft, topRight, bottomLeft, bottomRight;

    public NonLeaf(Node topLeft, Node topRight, Node bottomLeft, Node bottomRight) {
        this.isLeaf = false;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    public Node getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(Node topLeft) {
        this.topLeft = topLeft;
    }

    public Node getTopRight() {
        return topRight;
    }

    public void setTopRight(Node topRight) {
        this.topRight = topRight;
    }

    public Node getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(Node bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    public Node getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(Node bottomRight) {
        this.bottomRight = bottomRight;
    }
    

}