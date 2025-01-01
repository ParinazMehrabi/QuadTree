package org.example;

import javax.swing.*;

import org.example.model.QuadTree;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        // List<Color[]> image1 = new ArrayList<>();
        // image1.add(new Color[] {Color.RED, Color.WHITE});
        // image1.add(new Color[] {Color.WHITE, Color.RED});


        // QuadTree quadTree = new QuadTree(image1); // You can uncomment this line if you want to use the pre-defined list
        // quadTree.traverseAndPrint(); 

        // BufferedImage image2 = quadTree.generateImage();
        //         JFrame frame2 = new JFrame("Image of CSV");
        //         frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //         ImageIcon icon2 = new ImageIcon(image2);
        //         JLabel label2 = new JLabel(icon2);
        //         frame2.add(label2);
        //         frame2.pack();
        //         frame2.setVisible(true);
        // Test 1: Simple Uniform Image (All pixels are the same color)
        List<Color[]> image1 = new ArrayList<>();
        image1.add(new Color[] {Color.RED, Color.RED});
        image1.add(new Color[] {Color.RED, Color.RED});
        
        QuadTree quadTree1 = new QuadTree(image1);
        
        // Test treeDepth
        int treeDepth1 = quadTree1.treeDepth(quadTree1.getRoot());
        System.out.println("Tree depth for uniform image (all red): " + treeDepth1); 
        // Expected: 1, since all pixels are the same, only one leaf node

        // Test pixelDepth for a known pixel (should return depth 0 for any pixel in a uniform image)
        int pixelDepth1 = quadTree1.pixelDepth(0, 0); 
        System.out.println("Pixel depth at (0, 0): " + pixelDepth1); 
        // Expected: 0, since the entire image is uniform

        // Test 2: Checkerboard Pattern
        List<Color[]> image2 = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Color[] row = new Color[8];
            for (int j = 0; j < 8; j++) {
                row[j] = (i + j) % 2 == 0 ? Color.WHITE : Color.BLACK;
            }
            image2.add(row);
        }
        
        QuadTree quadTree2 = new QuadTree(image2);

        // Test treeDepth
        int treeDepth2 = quadTree2.treeDepth(quadTree2.getRoot());
        System.out.println("Tree depth for checkerboard pattern: " + treeDepth2); 
        // Expected: 3, since the tree will subdivide the image multiple times to accommodate the changes in color.

        // Test pixelDepth for a few pixels
        int pixelDepth2_0_0 = quadTree2.pixelDepth(0, 0); 
        System.out.println("Pixel depth at (0, 0): " + pixelDepth2_0_0); 
        // Expected: 3, as the depth would likely be 3 for a checkerboard pattern.
        
        int pixelDepth2_1_1 = quadTree2.pixelDepth(1, 1); 
        System.out.println("Pixel depth at (1, 1): " + pixelDepth2_1_1); 
        // Expected: 3, same as the above case.

        // Test 3: Another checkerboard pattern, but larger image (16x16)
        List<Color[]> image3 = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            Color[] row = new Color[16];
            for (int j = 0; j < 16; j++) {
                row[j] = (i + j) % 2 == 0 ? Color.WHITE : Color.BLACK;
            }
            image3.add(row);
        }
        
        QuadTree quadTree3 = new QuadTree(image3);

    
        int treeDepth3 = quadTree3.treeDepth(quadTree3.getRoot());
        System.out.println("Tree depth for larger checkerboard pattern (16x16): " + treeDepth3); 
    
        int pixelDepth3_0_0 = quadTree3.pixelDepth(0, 0); 
        System.out.println("Pixel depth at (0, 0): " + pixelDepth3_0_0); 
    

        int pixelDepth3_15_15 = quadTree3.pixelDepth(15, 15); 
        System.out.println("Pixel depth at (15, 15): " + pixelDepth3_15_15); 
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        fileChooser.setDialogTitle("Choose the CSV file");

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedImage image = createImageFromCSV(selectedFile.getAbsolutePath());
                QuadTree quadTree = new QuadTree(bufferedImageToList(image));
                QuadTree new2 = quadTree.compress(8);
              BufferedImage image4 = new2.generateImage();
                JFrame frame2 = new JFrame("Image of CSV");
                frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               ImageIcon icon2 = new ImageIcon(image4);
                JLabel label2 = new JLabel(icon2);
               frame2.add(label2);
                frame2.pack();
                frame2.setVisible(true);
                JFrame frame = new JFrame("Image of CSV");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                ImageIcon icon = new ImageIcon(image);
                JLabel label = new JLabel(icon);
                frame.add(label);
                frame.pack();
                frame.setVisible(true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static List<Color[]> bufferedImageToList(BufferedImage image) {
        List<Color[]> imageData = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            Color[] row = new Color[width];
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                row[x] = new Color(rgb);
            }
            imageData.add(row);
        }

        return imageData;
    }


    private static BufferedImage createImageFromCSV(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine();
            String line = br.readLine();
            String[] allValues = line.split(",");
            int width = (int) Math.sqrt(allValues.length / 3); 
            int height = width;

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            int index = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    try {
                        String rStr = allValues[index].replaceAll("^\"|\"$", "");
                        String gStr = allValues[index + 1].replaceAll("^\"|\"$", "");
                        String bStr = allValues[index + 2].replaceAll("^\"|\"$", "");
                        int r = Integer.parseInt(rStr);
                        int g = Integer.parseInt(gStr);
                        int b = Integer.parseInt(bStr);
                        image.setRGB(x, y, (r << 16) | (g << 8) | b);
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: Skipping val" + index + ": " + e.getMessage());
                    }
                    index += 3;
                }
            }

            return image;
        }
    }
}