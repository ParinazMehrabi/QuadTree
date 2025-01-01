package org.example.model.Node;

import java.awt.Rectangle;
import java.util.List;

public abstract class Node {
    protected boolean isLeaf;
    int size = 0;
    public boolean isLeaf() {
        return isLeaf;

    }
    public void setSize(int size)
    {
        this.size = size;
    }
    public int getSize()
    {
       return size;
    }
}