package projectA; // Declares the package name for the class

import java.awt.image.BufferedImage; // Import the BufferedImage class
import java.awt.image.DataBufferByte; // Import the DataBufferByte class
import java.io.File; // Import the File class
import java.io.IOException; // Import the IOException class

import javax.imageio.ImageIO; // Import the ImageIO class to read and write images

public class TaskA { // Public class named TaskA

    public static short[][] grayscaleImage; // Declare a public static 2D array to store the grayscale image
    public static int imgWidth; // Declare a public static variable to store the image width
    public static int imgHeight; // Declare a public static variable to store the image height
    private static BufferedImage bufferedImage; // Declare a private static variable to store the BufferedImage object
    
    public static void main(String[] args) { // Main where the program execution begins
        String inputFileName = "TenCardG.jpg"; // Specify the input image file name, which is from the Project file
        String outputFileName = "Template.jpg"; // Specify the output image file name, which is also from the Project file
        
        // Process input image
        File inputFile = new File(inputFileName); // Create a File object for the input image (TenCardG.jpg)
        try { //try and catch error block
            bufferedImage = ImageIO.read(inputFile); // Read the input image into a BufferedImage object
        } catch (IOException e) { // Catch block to handle any IO exceptions
            e.printStackTrace();
        }
        int inputImageWidth = bufferedImage.getWidth(); // Width of the input image
        int inputImageHeight = bufferedImage.getHeight(); // Height of the input image
        
        // Display dimensions and size of the input image
        System.out.println("Dimensions of the image (" + inputFileName + "): WxH= " + inputImageWidth + " x " + inputImageHeight);
        System.out.println("Input Image Size: " + inputImageWidth * inputImageHeight);
        
        // Process output image
        File outputFile = new File(outputFileName); // Create a File object for the output image (Template.jpg)
        try { //try and catch error block
            bufferedImage = ImageIO.read(outputFile); // Read the output image into a BufferedImage object
        } catch (IOException e) { // Catch block to handle any IO exceptions
            e.printStackTrace(); //
        }
        int outputImageWidth = bufferedImage.getWidth(); // Get the width of the output image
        int outputImageHeight = bufferedImage.getHeight(); // Get the height of the output image
        
        // Print the dimensions and size of the output image
        System.out.println("Dimensions of the image (" + outputFileName + "): WxH= " + outputImageWidth + " x " + outputImageHeight); 
        System.out.println("Output Image Size: " + outputImageWidth * outputImageHeight);
        
        // Convert the input and output images to grayscale
        GrayScaleconv(inputFileName);
        GrayScaleconv(outputFileName);
    }
    
    public static short[][] GrayScaleconv(String fileName) { // Method to convert an image to grayscale
        try { //try and catch error block
            // Read the image file
            File file = new File(fileName); // Create a File object for the specified file name
            BufferedImage image = ImageIO.read(file); // Read the image into a BufferedImage object
            imgWidth = image.getWidth(); // Get the width of the image
            imgHeight = image.getHeight(); // Get the height of the image

            // Get pixel data
            byte[] pixelData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData(); // Get the pixel data as a byte array
            System.out.println("Dimensions of the image (" + fileName + "): WxH= " + imgWidth + " x " + imgHeight + " | Number of pixels: " + pixelData.length); //Display the info

            // Convert to grayscale and store in a 2D array
            int red, green, blue; // Variables to hold the red, green, and blue components of a pixel
            grayscaleImage = new short[imgHeight][imgWidth]; // Initialize the 2D array for grayscale image
            int index; // Variable to hold the pixel index
            for (int i = 0; i < imgHeight; i++) { // Loop each row of the image
                for (int j = 0; j < imgWidth; j++) { // Loop each column of the image
                    index = 3 * (i * imgWidth + j); // Calculate the index of the pixel in the byte array
                    red = ((short) pixelData[index] & 0xff); // Extract the red component and convert to unsigned
                    green = ((short) pixelData[index + 1] & 0xff); // Extract the green component and convert to unsigned
                    blue = ((short) pixelData[index + 2] & 0xff); // Extract the blue component and convert to unsigned
                    
                    // Calculate the grayscale value
                    grayscaleImage[i][j] = (short) Math.round(0.299 * red + 0.587 * green + 0.114 * blue);
                }
            }
        } catch (IOException e) { // Catch block to handle any IO exceptions
            e.printStackTrace();
        }
        return grayscaleImage; // Return the 2D array containing the grayscale image
    }
}
