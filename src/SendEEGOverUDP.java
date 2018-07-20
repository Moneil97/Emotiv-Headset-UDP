import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTextField;

public class SendEEGOverUDP {
	
	private EmotivController3 ec;
	private DatagramSocket clientSocket;
	private Thread t;
	private InetAddress ip;
	private int port;

	public SendEEGOverUDP(String ip, int port) throws UnknownHostException {
		ec = new EmotivController3() {
			@Override
			public void stateUpdated(int wireless, int battery, int[] contactQuality) {
				//System.out.println(wireless + " " + battery + " " + Arrays.toString(contactQuality));
				SendEEGOverUDP.this.stateUpdated(wireless, battery, contactQuality);
			}
		};
		ec.startStateHandler();
		this.ip = InetAddress.getByName(ip);
		this.port = port;
	}
	
	//override to get data
	public void stateUpdated(int wireless, int battery, int[] contactQuality) {}
	
	public void start(final JTextField sampleRateText) throws SocketException {
		
		clientSocket = new DatagramSocket();
		t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				boolean ignoreFirstSamples = true; //Used to clear out buffer of old samples if stopped then continued
				long startTime = System.nanoTime();
				long totalSamples = 0;
				int counter = 0;
				
				while (true) {
					
					List<double[]> samples = ec.getAvailableSamples(true);
					
					if (samples != null) {
						
						if (ignoreFirstSamples) {
							ignoreFirstSamples = false;
							startTime = System.nanoTime();
							continue;
						}
						
						//System.out.println(samples.size() + " samples");
						totalSamples+=samples.size();
						if (counter++ >= 10) {
							if (sampleRateText != null)
								sampleRateText.setText(Double.toString(totalSamples / ((System.nanoTime() - startTime)/1000000000.0)).substring(0, 6) + " Hz");
							counter = 0;
						}
						for (int s = 0; s < samples.size();  s++) {
							
							double[] sample = samples.get(s);
							byte[] sendData = new byte[8*sample.length];
							
							//System.out.println(Arrays.toString(sample));
							
							for (int i = 0; i < sample.length; i++) {
								byte[] d = toByteArray(sample[i]);
								
								for (int j=0; j<8; j++) {
									sendData[i*8 + j] = d[j];
									//System.out.println("setting sendData[" + (i*8 + j) + "] to " + d[j]);
								}
							}
							
							//System.out.println(Arrays.toString(sendData) + "\n");
							
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
							try {
								clientSocket.send(sendPacket);
							} catch (IOException e) {
								e.printStackTrace();
							}
							
						}
					}
					
					if (t.isInterrupted()) {
						clientSocket.close();
						break;
					}
					
				}
			}
		});
		
		t.start();
	}
	
	public void pause() {
		t.interrupt();
	}
	
	public void stop() {
		pause();
		ec.disconnect();
	}
	
	private static byte[] toByteArray(double value) {
	    byte[] bytes = new byte[8];
	    ByteBuffer.wrap(bytes).putDouble(value);
	    return bytes;
	}

	public static void main(String[] args) {
		try {
			new SendEEGOverUDP("localhost", 9092).start(null);
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
	}

	
}
