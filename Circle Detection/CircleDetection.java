/* WARNING: You may have to modify the constants inside the detectCircles method to achieve the best results
 * WARNING: Only .jpg format is supported (any resolution)
 * WARNING: Does not deal well with concentric circles because of performance issues
 * I'll make an UI with sliders to permit a better UX as soon as I'm comfortable enough with making UIs
 */

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class CircleDetection{
	
	static File inputFile;
	static BufferedImage buffImage;
	static String dirPath = "C:/Users/nerla/Desktop/Coding/Projects/CircleDetection/src/"; //Directory of image
	static String fName = "objects.jpg"; //Image file name
	static int imgHeight;
	static int imgWidth;

	//Imports the image as a BufferedImage object
	public static void importImage() throws IOException {
		inputFile = new File(dirPath+fName);
		buffImage = ImageIO.read(inputFile);
		imgHeight = buffImage.getHeight();
		imgWidth = buffImage.getWidth(); 
	}
	
	//Converts a BufferedImage object to a Mat object
	public static Mat bufferedImageToMat(BufferedImage buffIm) {
		Mat mat = new Mat(imgHeight, imgWidth, CvType.CV_8UC3);
		  byte[] data = ((DataBufferByte) buffIm.getRaster().getDataBuffer()).getData();
		  mat.put(0, 0, data);
		  return mat;
	}
	
	//Converts a Mat object BufferedImage object
	public static BufferedImage matToBufferedImage(Mat matrix) throws IOException { 
	    MatOfByte matOfByte = new MatOfByte(); 
	    Imgcodecs.imencode(".jpg", matrix, matOfByte);
	    return ImageIO.read(new ByteArrayInputStream(matOfByte.toArray()));
	}
	
	//Handles the circle detection
	public static void detectCircles() throws IOException {
		Mat img = bufferedImageToMat(buffImage);
		Mat grayScale = new Mat();
		//Turns the image into greyscale
	    Imgproc.cvtColor(img, grayScale, Imgproc.COLOR_BGR2GRAY); 
	    //Blurs the image to remove noise
	    Imgproc.blur(grayScale, grayScale, new Size(3, 3)); 

	    Mat circles = new Mat();
	    //Minimum distance between the centres of the circles
	    final double MIN_DISTANCE = 10;
	    // higher threshold of Canny Edge detector, lower threshold is twice smaller
	    final int CANNY_UPPER_TRESHOLD = 150;
	    //By reducing the threshold it increases the "sensitivity" of the detector, increasing the probability of false positives
	    final double VOTE_TRESHOLD = 50; 
	    //Minimum radius of the circles to find
	    final int MIN_RADIUS = 5;
	    //Max radius of the circles to find
	    final int MAX_RADIUS = 0;
	    //Implements Canny Edge detection and, following that, Hough Transform to find the circles
	    Imgproc.HoughCircles(grayScale, circles, Imgproc.CV_HOUGH_GRADIENT, 1, MIN_DISTANCE, CANNY_UPPER_TRESHOLD, VOTE_TRESHOLD, MIN_RADIUS, MAX_RADIUS);

	    //Draws circles on top of the original image
	    for (int x = 0; x < circles.cols(); x++) {
	    	//Gets the circle's parameters (centre X, centre Y, radius)
	        double[] circleData = circles.get(0, x);
	        //Creates a Point object with the centre's coordinates
	        Point center = new Point(Math.round(circleData[0]), Math.round(circleData[1]));
	        int radius = (int) Math.round(circleData[2]);
	        //Draws the rectangle that marks the centre of the circle on top of the image
	        Imgproc.rectangle(img, new Point(center.x - 5, center.y - 5), new Point(center.x + 5, center.y + 5), new Scalar(0, 0, 255)); 
	        //Draws the circles on top of the image
	        Imgproc.circle(img, center, radius, new Scalar(0, 255, 0), 3); 
	    }
	    
	    //Converts the Mat object with detected circles into a BufferedImage object
	    buffImage = matToBufferedImage(img);
	    
	    //Outputs the image with circles and centres drawn on top
	    File outputCV = new File(dirPath+fName.substring(0, fName.indexOf('.'))+"_CirclesDetected.jpg");
        ImageIO.write(matToBufferedImage(img), "jpg", outputCV);
	}
	
	public static void displayImage() { //Displays the image with the circles in a popup window
		circleDetectionUIPanel.main(null);
	}
	
	public static void main(String[] args){
	    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	    try {
			importImage(); //imports image
			detectCircles(); //Runs circle detection and draws circles and centres
			displayImage(); //Displays the final image
		} catch (IOException e) {
			e.printStackTrace();
		}
	    

	    
	}
}
