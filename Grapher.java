package oneil;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Grapher extends JFrame{

	private static final long serialVersionUID = 1L;
	Pane pane;
	long startTime;
	double firstDataTime = 0;
	LinkedBlockingDeque<double[]> samples = new LinkedBlockingDeque<double[]>();
	
	
	public Grapher() {
		pane = new Pane();
		JPanel paneHolder = new JPanel();
		paneHolder.setBorder(new EmptyBorder(5, 5, 5, 5));
		paneHolder.add(pane, BorderLayout.CENTER);
		this.getContentPane().add(paneHolder);
		this.setBackground(Color.white);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);		
		startEmotiv();
	}
	
	class Pane extends JPanel{
		
		private static final long serialVersionUID = 1L;

		public Pane(){
			this.setBackground(Color.white);
			this.setPreferredSize(new Dimension(1850, 1000)); //62 pixels each for 16 channels
		}

		@Override
		protected void paintComponent(Graphics g1) {
			super.paintComponent(g1);
			Graphics2D g = (Graphics2D) g1;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setStroke(new BasicStroke(2));
			
			if (samples.isEmpty())
				return;
			
			//Print seperator lines
//			g.setColor(Color.black);
//			for (int j = 0 ; j < 20; j++) 
//				g.drawLine(0, 62*j, 1900, 62*j);
			
			//Print data lines
			double elapsedTime = getElapsedTimeInSeconds();
			Iterator<double[]> it = samples.iterator();
			double[] sample = it.next();
			while (it.hasNext()) {
				double[] nextSample = it.next();
				
				int x1 = (int)(1850 - ((elapsedTime + firstDataTime - sample[19])*128));
				int x2 = (int)(1850 - ((elapsedTime + firstDataTime - nextSample[19])*128));
				g.setColor(Color.black);
				g.drawLine(x1, (int)(62*1.5-sample[3]/70), x2, (int)(62*1.5-nextSample[3]/70));
				g.setColor(Color.red);
				g.drawLine(x1, (int)(62*2.5-sample[4]/70), x2, (int)(62*2.5-nextSample[4]/70));
				g.setColor(Color.blue);
				g.drawLine(x1, (int)(62*3.5-sample[5]/70), x2, (int)(62*3.5-nextSample[5]/70));
				g.setColor(Color.orange);
				g.drawLine(x1, (int)(62*4.5-sample[6]/70), x2, (int)(62*4.5-nextSample[6]/70));
				g.setColor(Color.cyan);
				g.drawLine(x1, (int)(62*5.5-sample[7]/70), x2, (int)(62*5.5-nextSample[7]/70));
				g.setColor(Color.green);
				g.drawLine(x1, (int)(62*6.5-sample[8]/70), x2, (int)(62*6.5-nextSample[8]/70));
				g.setColor(Color.magenta);
				g.drawLine(x1, (int)(62*7.5-sample[9]/70), x2, (int)(62*7.5-nextSample[9]/70));
				g.setColor(Color.orange);
				g.drawLine(x1, (int)(62*8.5-sample[10]/70), x2, (int)(62*8.5-nextSample[10]/70));
				g.setColor(Color.pink);
				g.drawLine(x1, (int)(62*9.5-sample[11]/70), x2, (int)(62*9.5-nextSample[11]/70));
				g.setColor(Color.black);
				g.drawLine(x1, (int)(62*10.5-sample[12]/70), x2, (int)(62*10.5-nextSample[12]/70));
				g.setColor(Color.red);
				g.drawLine(x1, (int)(62*11.5-sample[13]/70), x2, (int)(62*11.5-nextSample[13]/70));
				g.setColor(Color.blue);
				g.drawLine(x1, (int)(62*12.5-sample[14]/70), x2, (int)(62*12.5-nextSample[14]/70));
				g.setColor(Color.orange);
				g.drawLine(x1, (int)(62*13.5-sample[15]/70), x2, (int)(62*13.5-nextSample[15]/70));
				g.setColor(Color.cyan);
				g.drawLine(x1, (int)(62*14.5-sample[16]/70), x2, (int)(62*14.5-nextSample[16]/70));
				g.setColor(Color.green);
				g.drawLine(x1, (int)(62*15.5-sample[17]/140), x2, (int)(62*15.5-nextSample[17]/140));
				g.setColor(Color.magenta);
				g.drawLine(x1, (int)(62*16-sample[18]/250), x2, (int)(62*16-nextSample[18]/250));
				
				sample = nextSample;
			}
			
		}
	}
	
	private double getElapsedTimeInSeconds() {
		return (double)((System.nanoTime() - startTime)/ 1000000000.0) ;
	}
	
	private void startEmotiv() {
		
		EmotivController ec = new EmotivController();
		startTime = System.nanoTime();
		
		while (true) {
			List<double[]> samp = ec.getAvailableSamples();
			if (samp != null) {
				
				//The headset's timestamp usually starts around 3 seconds
				if (firstDataTime == 0)
					firstDataTime = samp.get(0)[19];
				
				//Add new samples to the end of the LinkedBlockingDeque
				for (int i = 0; i < samp.size(); i++) 
					samples.addLast(samp.get(i));
				
				//remove samples that are older than 13 seconds (off the screen)
				while (!samples.isEmpty()) {
					if (samples.getFirst()[19] + 13 < getElapsedTimeInSeconds()) 
						samples.removeFirst();
					else
						break;
				}
				
				repaint();
			}
		}
	}

	public static void main(String[] args) {
		new Grapher();
	}

}
