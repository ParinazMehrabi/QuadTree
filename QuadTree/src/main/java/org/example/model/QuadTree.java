package org.example.model;

import org.example.model.Node.Leaf;
import org.example.model.Node.Node;
import org.example.model.Node.NonLeaf;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


import java.util.stream.Gatherer.Integrator;

public class QuadTree {
    private static final Class<? extends Node> NonLeaf = null;
        private Node root;
        private List<Color[]> image;
        private int width;
        private int height;
    
        public QuadTree(List<Color[]> image) {
            this.image = image;
            this.width = image.get(0).length;
            this.height = image.size();
            this.root = build(0, 0, width - 1, height - 1);
        }
        public void setRoot(Node rootNode)
        {
            root = rootNode;
        }
        public Node getRoot()
        {
            return root;
        }
    
        private Node build(int x1, int y1, int x2, int y2) {
            if (x1 > x2 || y1 > y2) {
                return null;
            }
    
            if (x1 == x2 && y1 == y2) { // Base case: Single pixel
                return new Leaf(image.get(y1)[x1]);
            }
    
            boolean isUniform = true;
            Color firstColor = image.get(y1)[x1];
            for (int i = x1; i <= x2 && isUniform; i++) {
                for (int j = y1; j <= y2 && isUniform; j++) {
                    if (!image.get(j)[i].equals(firstColor)) { 
                        isUniform = false;
                        break; 
                    }
                }
            }
    
            if (isUniform) {
                return new Leaf(firstColor);
            }
    
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;
    
            Node topLeft = build(x1, y1, midX, midY);
            Node topRight = build(x1, midY + 1, midX, y2);
            Node bottomLeft = build(midX + 1, y1, x2, midY);
            Node bottomRight = build(midX + 1, midY + 1, x2, y2);
    
            return new NonLeaf(topLeft, topRight, bottomLeft, bottomRight);
        }
        public boolean isNonLeaf(Color[][] image, int x1, int y1, int x2, int y2) {
            Color firstColor = image[x1][y1];
            for (int i = x1; i <= x2; i++) {
                for (int j = y1; j <= y2; j++) {
                    if (!image[i][j].equals(firstColor)) {
                        return false;
                    }
                }
            }
            return true;
        }
        public int treeDepth(Node node)
        {
            if(node == null || node.isLeaf())
                return 0;
            int topLeft = treeDepth(((NonLeaf)node).getTopLeft());
            int topRight = treeDepth(((NonLeaf)node).getTopRight());
            int bottomLeft = treeDepth(((NonLeaf)node).getBottomLeft());
            int bottomRight = treeDepth(((NonLeaf)node).getBottomRight());
            return Math.max(Math.max(topLeft,topRight),Math.max(bottomLeft,bottomRight)) + 1;
        }
        public int pixelDepth(int px, int py) {
    return pixelDepthHelper(root, px, py, 0);
}

private int pixelDepthHelper(Node node, int px, int py, int currentDepth) {
    if (node.isLeaf()) {
        return currentDepth;
    }

    NonLeaf nonLeaf = (NonLeaf) node;
    int midX = width / 2;
    int midY = height / 2;

    if (px < midX && py < midY) {
        return pixelDepthHelper(nonLeaf.getTopLeft(), px, py, currentDepth + 1);
    } else if (px < midX && py >= midY) {
        return pixelDepthHelper(nonLeaf.getBottomLeft(), px, py, currentDepth + 1);
    } else if (px >= midX && py < midY) {
        return pixelDepthHelper(nonLeaf.getTopRight(), px, py, currentDepth + 1);
    } else { 
        return pixelDepthHelper(nonLeaf.getBottomRight(), px, py, currentDepth + 1);
    }
}
        public BufferedImage generateImage() {
            BufferedImage compressedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            traverse(root, compressedImage, 0, 0, width, height); 
            return compressedImage;
        }
    
        public void traverse(Node node, BufferedImage image, int startX, int startY, int subWidth, int subHeight) {
            if (node instanceof Leaf) {
                Leaf leaf = (Leaf) node;
                for (int x = startX; x < startX + subWidth; x++) { 
                    for (int y = startY; y < startY + subHeight; y++) { 
                        if (x >= 0 && x < width && y >= 0 && y < height) { 
                            image.setRGB(x, y, leaf.getColor().getRGB()); 
                        }
                    }
                }
            } else {
                NonLeaf nonLeaf = (NonLeaf) node;
                int halfWidth = subWidth / 2;
                int halfHeight = subHeight / 2;
                traverse(nonLeaf.getTopLeft(), image, startX, startY, halfWidth, halfHeight);
                traverse(nonLeaf.getTopRight(), image, startX, startY + halfHeight, halfWidth, halfHeight);
                traverse(nonLeaf.getBottomLeft(), image, startX + halfWidth, startY, halfWidth, halfHeight);
                traverse(nonLeaf.getBottomRight(), image, startX + halfWidth, startY + halfHeight, halfWidth, halfHeight);
            }
        }
        public QuadTree compress(int newSize) {
            QuadTree newTree = this.clone();
            compressFromDepth(newTree.root, 0, 0, width, height, newSize);
            return newTree;
        }
        
        private void compressFromDepth(Node node, int startX, int startY, int subWidth, int subHeight, int newSize) {
            if (node instanceof Leaf)return;
            NonLeaf nonLeaf = (NonLeaf) node;
            int halfWidth = subWidth / 2;
            int halfHeight = subHeight / 2;
            if (subWidth <= newSize && subHeight <= newSize) {
                Color avgColor = calAvgeColor(image, startX, startY, startX + halfWidth - 1, startY + halfHeight - 1);
                nonLeaf.setTopLeft(new Leaf(avgColor));
                nonLeaf.setTopRight(new Leaf(avgColor));
                nonLeaf.setBottomLeft(new Leaf(avgColor));
                nonLeaf.setBottomRight(new Leaf(avgColor));
            } else {
                compressFromDepth(nonLeaf.getTopLeft(), startX, startY, halfWidth, halfHeight, newSize);
                compressFromDepth(nonLeaf.getTopRight(), startX, startY + halfHeight, halfWidth, halfHeight, newSize);
                compressFromDepth(nonLeaf.getBottomLeft(), startX + halfWidth, startY, halfWidth, halfHeight, newSize);
                compressFromDepth(nonLeaf.getBottomRight(), startX + halfWidth, startY + halfHeight, halfWidth, halfHeight, newSize);
            }
        }
        
        private Color calAvgeColor(List<Color[]> image, int x1, int y1, int x2, int y2) {
            int rSum = 0;
            int gSum = 0;
            int bSum = 0;
            int count = 0;
            for (int i = x1; i <= x2; ++i) {
                for (int j = y1; j <= y2; ++j) {
                    Color color = image.get(j)[i];
                    rSum += color.getRed();
                    gSum += color.getGreen();
                    bSum += color.getBlue();
                    ++count;
                }
            }
            if (count == 0)
                return Color.BLACK; 
            return new Color(rSum / count, gSum / count, bSum / count);
        }
        public QuadTree clone() {
            List<Color[]> copy = new ArrayList<>();
            for (Color[] row : image) {
                Color[] newRow = new Color[row.length];
                System.arraycopy(row, 0, newRow, 0, row.length);
                copy.add(newRow);
            }
            return new QuadTree(copy);
        }
            
        // public QuadTree searchSubspacesWithRange(int x1, int y1, int x2, int y2) {
        //     Node node = root;
        //     QuadTree newTree = this.clone();
        //     newTree.setRoot(node);
        //     List<Node> result = new ArrayList<>();
        //     Rectangle board = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
        //     int count = 1;
            
        //     node.findBoard(board, x1, y1, x2, y2, 0, 0, image.size());
        //     result = node.collectOverlappingNodes(result, x1, y1, x2, y2, 0, 0, image.size());
        
        //     if (result.isEmpty()) {
        //         // Handle case where no nodes overlap the given range
        //         System.out.println("No nodes found in the specified range.");
        //         return newTree;
        //     }
        
        //     Node first = result.get(0);
        //     while (count != result.size() - 1) {
        //         traverse(first, null, 0, 0, first.getSize(), first.getSize());
        //         first = result.get(count++);
        //     }
        //     return newTree;
        // }
        
    
        public BufferedImage searchSubspacesWithRange(int x1, int y1, int x2, int y2) {
            int subWidth = x2 - x1 + 1;
            int subHeight = y2 - y1 + 1;
            BufferedImage resultImage = new BufferedImage(subWidth, subHeight, BufferedImage.TYPE_INT_RGB);
            Graphics g = resultImage.getGraphics();
            g.setColor(Color.WHITE); 
            g.fillRect(0, 0, subWidth, subHeight); 
        
            fillSubspace(root, resultImage, x1, y1, x2, y2, 0, 0, width, height, subWidth, subHeight);
        
            return resultImage;
        }
        
        private void fillSubspace(Node node, BufferedImage resultImage, int x1, int y1, int x2, int y2, 
                                  int startX, int startY, int width, int height, int subWidth, int subHeight) {
            if (node == null) {
                return;
            }
            if (node.isLeaf()) {
                Leaf leaf = (Leaf) node;
                Color color = leaf.getColor();
    
                if (isIntersecting(startX, startY, width, height, x1, y1, x2, y2)) {
                    int overlapX1 = Math.max(x1, startX);
                    int overlapY1 = Math.max(y1, startY);
                    int overlapX2 = Math.min(x2, startX + width - 1);
                    int overlapY2 = Math.min(y2, startY + height - 1);
        
                    for (int i = overlapX1; i <= overlapX2; i++) {
                        for (int j = overlapY1; j <= overlapY2; j++) {
                            int resultX = i - x1;
                            int resultY = j - y1;
                            resultImage.setRGB(resultX, resultY, color.getRGB());
                        }
                    }
                }
                return;
            }

            NonLeaf nonLeaf = (NonLeaf) node;
            int halfWidth = width / 2;
            int halfHeight = height / 2;

            fillSubspace(nonLeaf.getTopLeft(), resultImage, x1, y1, x2, y2, startX, startY, halfWidth, halfHeight, subWidth, subHeight);
            fillSubspace(nonLeaf.getTopRight(), resultImage, x1, y1, x2, y2, startX + halfWidth, startY, halfWidth, halfHeight, subWidth, subHeight);
            fillSubspace(nonLeaf.getBottomLeft(), resultImage, x1, y1, x2, y2, startX, startY + halfHeight, halfWidth, halfHeight, subWidth, subHeight);
            fillSubspace(nonLeaf.getBottomRight(), resultImage, x1, y1, x2, y2, startX + halfWidth, startY + halfHeight, halfWidth, halfHeight, subWidth, subHeight);
        }
        

        private boolean isIntersecting(int startX, int startY, int width, int height, int x1, int y1, int x2, int y2) {
            return !(startX + width - 1 < x1 || startX > x2 || startY + height - 1 < y1 || startY > y2);
        }
        public QuadTree mask(int x1, int y1, int x2, int y2) {
            QuadTree newTree = this.clone(); 
            maskSubspaces(newTree.root, x1, y1, x2, y2, 0, 0, newTree.width, newTree.height);
            return newTree;
        }

        private void maskSubspaces(Node node, int x1, int y1, int x2, int y2, 
                                   int startX, int startY, int width, int height) {
            if (node == null) {
                return;
            }

            if (node.isLeaf()) {
                Leaf leaf = (Leaf) node;
                Color color = leaf.getColor();
                if (isIntersecting(startX, startY, width, height, x1, y1, x2, y2)) {
                    leaf.setColor(Color.WHITE); 
                }
                return;
            }
            NonLeaf nonLeaf = (NonLeaf) node;
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            maskSubspaces(nonLeaf.getTopLeft(), x1, y1, x2, y2, startX, startY, halfWidth, halfHeight);
            maskSubspaces(nonLeaf.getTopRight(), x1, y1, x2, y2, startX + halfWidth, startY, halfWidth, halfHeight);
            maskSubspaces(nonLeaf.getBottomLeft(), x1, y1, x2, y2, startX, startY + halfHeight, halfWidth, halfHeight);
            maskSubspaces(nonLeaf.getBottomRight(), x1, y1, x2, y2, startX + halfWidth, startY + halfHeight, halfWidth, halfHeight);
        }

        
    
}