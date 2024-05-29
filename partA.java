package ecte331_project_partA;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class partA {
	public static short [][] grayImage;
	public static int widthtemplate;
	public static int heighttemplate;
	public static int width;
	public static int height;
	public static int widthSource;
	public static int heightSource;
	private static BufferedImage image;
	private static BufferedImage image1;

	public static void main(String[] args) throws IOException {


		String SourceName="TenCardG.jpg";
		String TemplateName="Template.jpg";
		String fileNameInp = null;
		//readColourImage(fileNameInp);


		File inp=new File(SourceName);
		image = ImageIO.read(inp);
		widthSource = image.getWidth();
		heightSource = image.getHeight();
		short [] [] Source_image = readColourImage(SourceName);

		File inp1=new File(TemplateName);
		image1 = ImageIO.read(inp1);
		widthtemplate = image1.getWidth();
		heighttemplate = image1.getHeight();     
		short [] [] temp_image = readColourImage(TemplateName);

		int Tempsize = widthtemplate*heighttemplate;
		double Minimum = 1000000;





		double[][] absDiffMat;
		for (int i=0; i<= heightSource - heighttemplate; i++) {
			for (int j=0; j<= heightSource - heighttemplate; j++) {
				double SumDiff = 0.0;
				for (int y=0; y<heighttemplate; y+++) {
					for (int x=0; x<widthtemplate; x++) {
						SumDiff += Math.abs(Source_image[i+y][j+x] - temp_image[y][x]);
					}
				}
				double meanDiff = SumDiff/Tempsize;
				absDiffMat[i][j]=meanDiff;

				if (meanDiff < Minimum) {
					Minimum = meanDiff;
				}
			}
		}

		int ratio=10;
		double Threshold = ratio * Minimum

				coorrdinates=find(absDiffMat<=Threshold)

				for (int i = 0; i <heighttemplate ; i++) {
					for (int j = 0; j < widthtemplate ; j++) {

						System.out.print(temp_image[i][j]+ " ");


					}
					System.out.println("\n");


				}
	}


	public static short [][] readColourImage(String fileName) {

		try
		{
			// RGB pixel values
			byte[] pixels;

			File inp=new File(fileName);
			image = ImageIO.read(inp);
			width = image.getWidth();
			height = image.getHeight();   




			pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			//System.out.println("Dimension of the image: W x H = " + widthSource + " x "+heightSource+" "+ "| Number of Pixels: "+ pixels.length);
			System.out.println("Dimension of the Template image: W x H = " + width + " x "+height+" "+ "| Number of Pixels: "+ pixels.length);



			//rgb2gray in a 2D array grayImage                 
			int pr;// red
			int pg;//  green
			int pb;// blue     

			grayImage =new short [height][width];
			int coord;
			for (int i=0; i<height;i++)
				for(int j=0;j<width;j++)
				{        		     
					coord= 3*(i*width+j);
					pr= ((short) pixels[coord] & 0xff); // red
					pg= (((short) pixels[coord+1] & 0xff));//  green
					pb= (((short) pixels[coord+2] & 0xff));// blue                

					grayImage[i][j]=(short)Math.round(0.299 *pr + 0.587 * pg  + 0.114 * pb);         

				}  
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		return grayImage;

	}    


}
