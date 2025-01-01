package org.example.model.Node;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Leaf extends Node {
    private Color color;
    public Leaf(Color color) {
        this.isLeaf = true;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
        


        

}


