import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



@SuppressWarnings("serial")
class circleDetectionUIPanel extends JPanel {


    
	
	public static void main(String[] args) {
		initialize(); 
	}

	private static void initialize() {
		JFrame f = new JFrame("Circle Detection");
		JLabel imageIcon = new JLabel(new ImageIcon(CircleDetection.buffImage)); 
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    f.add(imageIcon);
	    f.pack();
	    f.setVisible(true);
	}
}