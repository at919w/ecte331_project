package projectA;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;           

public class MThread {

    public static short[][] grayScaleMatrix; // Stores the grayscale image data
    public static int imageWidth; // Stores the image width
    public static int imageHeight; // Stores the image height
    private static BufferedImage srcImage; // Stores the source image
    private static BufferedImage tmplImage; // Stores the template image
    private static boolean[][] visitedPixels; // Tracks visited regions in the image
    private static double minDifference = Double.MAX_VALUE; // Minimum difference found

    private static final int THREAD_COUNT = 4; // Number of threads for parallel processing

    public static void main(String[] args) throws IOException {
        
        long startTime = System.currentTimeMillis(); // Start time for execution
        
        String srcImagePath = "TenCardG.jpg";    // Path to the source image
        String tmplImagePath = "Template.jpg";  // Path to the template image

        // Read the source image
        File srcFile = new File(srcImagePath);
        srcImage = ImageIO.read(srcFile);
        short[][] srcGrayImage = GrayScaleconv(srcImagePath);
        
        // Read the template image
        File tmplFile = new File(tmplImagePath);
        tmplImage = ImageIO.read(tmplFile);
        short[][] tmplGrayImage = GrayScaleconv(tmplImagePath);

        // Perform template matching
        Rectangle[] matchedRegions = TempMatch(tmplGrayImage, srcGrayImage);

        // Draw rectangles around matched regions
        for (Rectangle region : matchedRegions) {
            drawRectangle(srcImage, region);
        }

        // Save the result image
        String resultImagePath = "Multi Thread Image.jpg";
        ImageIO.write(srcImage, "jpg", new File(resultImagePath));
        
        System.out.println("Image saved as -> Multi Thread Image.jpg");

        // Display the result image in a GUI window
        displayImage(resultImagePath, "Result");
        
        // Verify if the result file exists
        File resultFile = new File(resultImagePath);
        if (resultFile.exists()) {
            System.out.println("- Multi Thread Image.jpg has been created -");
        } else {
            System.out.println("- Failed -");
        }

        // End time for execution
        long endTime = System.currentTimeMillis();
        
        // Calculate and print the execution time
        long executionTime = endTime - startTime;
        System.out.println("Execution time: " + executionTime + " ms");
    }

    // Convert an image to grayscale
    public static short[][] GrayScaleconv(String filePath) throws IOException {
        BufferedImage img = ImageIO.read(new File(filePath)); // Read image from file
        imageWidth = img.getWidth(); // Get image width
        imageHeight = img.getHeight(); // Get image height

        byte[] pixelData = ((DataBufferByte) img.getRaster().getDataBuffer()).getData(); // Get pixel data
        grayScaleMatrix = new short[imageHeight][imageWidth]; 
        visitedPixels = new boolean[imageHeight][imageWidth]; 

        int index, red, green, blue;
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                index = 3 * (i * imageWidth + j); 
                red = pixelData[index] & 0xff;   
                green = pixelData[index + 1] & 0xff; 
                blue = pixelData[index + 2] & 0xff; 
                grayScaleMatrix[i][j] = (short) Math.round(0.299 * red + 0.587 * green + 0.114 * blue);
            }
        }
        return grayScaleMatrix; 
    }

    // Check if a region has been visited
    public static boolean isRegionVisited(int row, int col, int tmplHeight, int tmplWidth) {
        for (int i = Math.max(0, row); i < Math.min(row + tmplHeight, imageHeight); i++) {
            for (int j = Math.max(0, col); j < Math.min(col + tmplWidth, imageWidth); j++) {
                if (visitedPixels[i][j]) {
                    return true; // Return true if any part of the region has been visited
                }
            }
        }
        return false; // Return false if no part of the region has been visited
    }

    // Mark a region as visited
    public static void markRegionVisited(int row, int col, int tmplHeight, int tmplWidth) {
        for (int i = Math.max(0, row); i < Math.min(row + tmplHeight, imageHeight); i++) {
            for (int j = Math.max(0, col); j < Math.min(col + tmplWidth, imageWidth); j++) {
                visitedPixels[i][j] = true; // Mark the pixel as visited
            }
        }
    }
    
    // Perform template matching
    public static Rectangle[] TempMatch(final short[][] tmplGrayImage, final short[][] srcGrayImage) throws IOException {
        int srcRows = srcGrayImage.length;      
        final int srcCols = srcGrayImage[0].length;   
        final int tmplRows = tmplGrayImage.length;    
        final int tmplCols = tmplGrayImage[0].length;
        final int tmplSize = tmplRows * tmplCols; // Total pixels in the template
        final double[][] absDiffMatrix = new double[srcRows - tmplRows + 1][srcCols - tmplCols + 1]; 

        Thread[] threads = new Thread[THREAD_COUNT]; // Array for thread objects

        // Processing
        for (int t = 0; t < THREAD_COUNT; t++) {
            final int start = t * (srcRows - tmplRows + 1) / THREAD_COUNT; // Start row for the thread
            final int end = (t == THREAD_COUNT - 1) ? (srcRows - tmplRows + 1) : ((t + 1) * (srcRows - tmplRows + 1) / THREAD_COUNT); // End row for the thread

            threads[t] = new Thread(new Runnable() {
                public void run() {
                    for (int i = start; i < end; i++) {
                        for (int j = 0; j <= srcCols - tmplCols; j++) {
                            double absDiff = 0.0;

                            for (int m = 0; m < tmplRows; m++) {
                                for (int n = 0; n < tmplCols; n++) {
                                    absDiff += Math.abs(srcGrayImage[i + m][j + n] - tmplGrayImage[m][n]);
                                }
                            }
                            absDiff /= tmplSize; // Average absolute difference
                            absDiffMatrix[i][j] = absDiff; 

                            // Update minimum absolute difference
                            synchronized (MThread.class) {
                                if (absDiff < minDifference) {
                                    minDifference = absDiff;
                                }
                            }
                        }
                    }
                }
            });
            threads[t].start(); // Start the thread
        }

        try {
            for (int t = 0; t < THREAD_COUNT; t++) {
                threads[t].join(); // Wait for all threads to complete
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double threshold = 10 * minDifference; // Threshold for finding matches
        
        List<Rectangle> matchedRegions = new ArrayList<>(); 

        // Find regions matching the template
        for (int i = 0; i <= srcRows - tmplRows; i++) {
            for (int j = 0; j <= srcCols - tmplCols; j++) {
                if (absDiffMatrix[i][j] <= threshold && !isRegionVisited(i, j, tmplRows, tmplCols)) {
                    matchedRegions.add(new Rectangle(j, i, tmplCols, tmplRows)); // Add matching region
                    markRegionVisited(i, j, tmplRows, tmplCols); // Mark region as visited
                }
            }
        }

        return matchedRegions.toArray(new Rectangle[0]); // Convert list to array and return
    }
    
    // Display an image in a GUI window
    public static void displayImage(String filePath, String windowTitle) {
        JFrame frame = new JFrame(windowTitle); // Create a JFrame with the specified title
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit application on window close

        ImageIcon icon = new ImageIcon(filePath); // Create an ImageIcon from the image file
        JLabel label = new JLabel(icon); // Create a JLabel to display the image
        frame.add(label); // Add the JLabel to the JFrame

        frame.pack(); // Pack the JFrame
        frame.setVisible(true); // Make the JFrame visible
    }
    
    // Draw a rectangle on an image
    public static void drawRectangle(BufferedImage img, Rectangle rect) {
        Graphics2D g2D = img.createGraphics(); // Create a Graphics2D object
        g2D.setColor(Color.RED); // Set the color for the rectangle
        g2D.drawRect(rect.x, rect.y, rect.width, rect.height); // Draw the rectangle
        g2D.dispose(); // Dispose the Graphics2D object
    }
}
