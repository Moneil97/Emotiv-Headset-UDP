import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

public class Grapher extends JFrame{

	private static final long serialVersionUID = 1L;
	Pane pane;
	long startTime;
	double firstDataTime = 0;
	LinkedBlockingDeque<double[]> samples = new LinkedBlockingDeque<double[]>();
	int i;
	boolean[] bools;
	int height = 950;
	private final int channels = 20;
	private final boolean defaultToggle = false;
	
	Color[] colors = {Color.black, Color.red, Color.blue, Color.orange, Color.cyan, Color.green, Color.magenta, Color.pink};
	private EmotivController3 ec;
	
	public Grapher() {
		pane = new Pane();
		JPanel paneHolder = new JPanel();
		paneHolder.setBorder(new EmptyBorder(5, 5, 5, 5));
		paneHolder.add(pane, BorderLayout.CENTER);
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		this.getContentPane().add(content);
		
		
		JPanel buttonPanel = new JPanel();
		JToggleButton[] buttons = new JToggleButton[channels];
		bools = new boolean[channels];
		
		for (i=0; i < channels; i++) {
			bools[i] = defaultToggle;
			buttons[i] = new JToggleButton("" + i, defaultToggle);
			buttons[i].addActionListener(new ActionListener() {
				
				int num = i;
				
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println(num);
					bools[num] = !bools[num];
				}
			});
			buttonPanel.add(buttons[i]);
		}
		

		content.add(paneHolder,  BorderLayout.CENTER);
		content.add(buttonPanel,  BorderLayout.SOUTH);
		
		this.setBackground(Color.white);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				ec.disconnect();
		    }
		});
		
		startEmotiv();
	}
	
	class Pane extends JPanel{
		
		private static final long serialVersionUID = 1L;

		public Pane(){
			this.setBackground(Color.white);
			this.setPreferredSize(new Dimension(1850, height+5)); //62 pixels each for 16 channels
		}

		@Override
		protected void paintComponent(Graphics g1) {
			super.paintComponent(g1);
			Graphics2D g = (Graphics2D) g1;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setStroke(new BasicStroke(2));
			
			if (samples.isEmpty())
				return;
			
			double largestTime = samples.getLast()[19];
			
			//Print data lines
			Iterator<double[]> it = samples.iterator();
			double[] sample = it.next();
			while (it.hasNext()) {
				double[] nextSample = it.next();
				int x1 = 1850 - (int)((largestTime - sample[19])*128);
				int x2 = 1850 - (int)((largestTime - nextSample[19])*128);
				
				
				for (int j = 0; j < channels; j++) {
					if (bools[j]) {
						g.setColor(colors[j%colors.length]);
						if (j == 17 || j == 18) //Gyroscopes
							g.drawLine(x1, (int)(height+400-sample[j]/10), x2, (int)(height+400-nextSample[j]/10));
						else
							g.drawLine(x1, (int)(height-sample[j]/6), x2, (int)(height-nextSample[j]/6));
					}
				}
				sample = nextSample;
			}
			
		}
	}
	
	private double getElapsedTimeInSeconds() {
		return (double)((System.nanoTime() - startTime)/ 1000000000.0) ;
	}
	
	private void startEmotiv() {
		
		//EmotivController ec = new EmotivController();
		ec = new EmotivController3();
		ec.startStateHandler();
		startTime = System.nanoTime();
		
		while (true) {
			List<double[]> samp = ec.getAvailableSamples(false);
			if (samp != null) {
				
				//The headset's timestamp usually starts around 3 seconds
				if (firstDataTime == 0)
					firstDataTime = samp.get(0)[19];
				
				//Add new samples to the end of the LinkedBlockingDeque
				for (int i = 0; i < samp.size(); i++) 
					samples.addLast(samp.get(i));
				
				//remove samples that are older than 15 seconds (off the screen)
				while (!samples.isEmpty()) {
					if (samples.getFirst()[19] + 15 < getElapsedTimeInSeconds()) 
						samples.removeFirst();
					else
						break;
				}
				
				repaint();
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		System.loadLibrary("edk");
		new Grapher();
	}

}
