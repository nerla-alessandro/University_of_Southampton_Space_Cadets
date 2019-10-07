import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Spirograph {
   
    public static void main(String[] args) {
    	initialize(); 
    }

    private static void initialize() {
        JFrame f = new JFrame("Spirograph");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new spiroPanel());
        f.pack();
        f.setVisible(true);
    }
}

@SuppressWarnings("serial")
class spiroPanel extends JPanel {

    public Dimension getPreferredSize() {
        return new Dimension(800,800);
    }

    public void paintComponent(Graphics g) {
        double R = 144;	//Radius of the fixed circle
        double r = 69;	//Radius of the moving circle
        double O = 80; //Offset of the pen point
        int centreX = 400; //Centre of the Window (x-axis)
        int centreY = 400; //Centre of the Window (y-axis)
        super.paintComponent(g);       
        
        int lastX = (int) (R + r - O)+centreX; //Sets starting point (x-axis)
	int lastY = centreY; //Sets starting point (y-axis)
        int nextX;
        int nextY;
        for (int i = 0; i < 10000; i++) {
        	double t = i * Math.PI / 180;	//Transforms Degrees into Radians   	
        	nextX = (int)((R+r)*Math.cos(t)-O*Math.cos(((R+r)/r)*t))+centreX;
        	nextY = (int)((R+r)*Math.sin(t)-O*Math.sin(((R+r)/r)*t))+centreY;
        	g.drawLine(lastX, lastY, nextX, nextY);
        	lastX = nextX;
        	lastY = nextY;
        }
    }  
}
