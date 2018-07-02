package oneil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Grapher extends JFrame{

	private static final long serialVersionUID = 1L;
	Pane pane;
	List<double[]> samples = new ArrayList<double[]>();
	
	public Grapher() {
		// TODO Auto-generated constructor stub
		pane = new Pane();
		JPanel paneHolder = new JPanel();
		paneHolder.setBorder(new EmptyBorder(5, 5, 5, 5));
		paneHolder.add(pane, BorderLayout.CENTER);
		this.getContentPane().add(paneHolder);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setBackground(Color.white);
		
		startEmotiv();
	}
	
	class Pane extends JPanel{
		
		private static final long serialVersionUID = 1L;

		public Pane(){
			this.setBackground(Color.white);
			this.setPreferredSize(new Dimension(1850, 1000));
			//62 pixels each for 16 channel
		}

		@Override
		protected void paintComponent(Graphics g1) {
			super.paintComponent(g1);
			Graphics2D g = (Graphics2D) g1;
			
			
			g.setColor(Color.black);
			for (int j = 0 ; j < 20; j++) {
				g.drawLine(0, 62*j, 1900, 62*j);
			}
			
			
			//g.drawRect((int)(Math.random()*500), (int)(Math.random()*500), 50, 50);
			for (int i = 0; i < samples.size()-1; i++) {
				int x1 = (int)((samples.get(i)[19]-3)*128);
				int x2 = (int)((samples.get(i+1)[19]-3)*128);
				g.setColor(Color.black);
				g.drawLine(x1, (int)(62*1.5-samples.get(i)[3]/70), x2, (int)(62*1.5-samples.get(i+1)[3]/70));
				g.setColor(Color.red);
				g.drawLine(x1, (int)(62*2.5-samples.get(i)[4]/70), x2, (int)(62*2.5-samples.get(i+1)[4]/70));
				g.setColor(Color.blue);
				g.drawLine(x1, (int)(62*3.5-samples.get(i)[5]/70), x2, (int)(62*3.5-samples.get(i+1)[5]/70));
				g.setColor(Color.orange);
				g.drawLine(x1, (int)(62*4.5-samples.get(i)[6]/70), x2, (int)(62*4.5-samples.get(i+1)[6]/70));
				g.setColor(Color.cyan);
				g.drawLine(x1, (int)(62*5.5-samples.get(i)[7]/70), x2, (int)(62*5.5-samples.get(i+1)[7]/70));
				g.setColor(Color.green);
				g.drawLine(x1, (int)(62*6.5-samples.get(i)[8]/70), x2, (int)(62*6.5-samples.get(i+1)[8]/70));
				g.setColor(Color.magenta);
				g.drawLine(x1, (int)(62*7.5-samples.get(i)[9]/70), x2, (int)(62*7.5-samples.get(i+1)[9]/70));
				g.setColor(Color.orange);
				g.drawLine(x1, (int)(62*8.5-samples.get(i)[10]/70), x2, (int)(62*8.5-samples.get(i+1)[10]/70));
				g.setColor(Color.pink);
				g.drawLine(x1, (int)(62*9.5-samples.get(i)[11]/70), x2, (int)(62*9.5-samples.get(i+1)[11]/70));
				g.setColor(Color.black);
				g.drawLine(x1, (int)(62*10.5-samples.get(i)[12]/70), x2, (int)(62*10.5-samples.get(i+1)[12]/70));
				g.setColor(Color.red);
				g.drawLine(x1, (int)(62*11.5-samples.get(i)[13]/70), x2, (int)(62*11.5-samples.get(i+1)[13]/70));
				g.setColor(Color.blue);
				g.drawLine(x1, (int)(62*12.5-samples.get(i)[14]/70), x2, (int)(62*12.5-samples.get(i+1)[14]/70));
				g.setColor(Color.orange);
				g.drawLine(x1, (int)(62*13.5-samples.get(i)[15]/70), x2, (int)(62*13.5-samples.get(i+1)[15]/70));
				g.setColor(Color.cyan);
				g.drawLine(x1, (int)(62*14.5-samples.get(i)[16]/70), x2, (int)(62*14.5-samples.get(i+1)[16]/70));
				g.setColor(Color.green);
				g.drawLine(x1, (int)(62*15.5-samples.get(i)[17]/140), x2, (int)(62*15.5-samples.get(i+1)[17]/140));
				g.setColor(Color.magenta);
				g.drawLine(x1, (int)(62*16-samples.get(i)[18]/250), x2, (int)(62*16-samples.get(i+1)[18]/250));
			}
			
		}
	}
	
	private void startEmotiv() {
		
		EmotivController ec = new EmotivController();
		
		while (true) {
			List<double[]> samples = ec.getAvailableSamples();
			if (samples != null) {
				for (int i = 0; i < samples.size(); i++) {
					this.samples.add(samples.get(i));
				}
			}
			
			//remove samples that are off the screen
			
			repaint();
		}
	}

	public static void main(String[] args) {
		new Grapher();
	}

}
