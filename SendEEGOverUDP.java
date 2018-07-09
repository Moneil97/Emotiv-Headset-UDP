package oneil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class SendEEGOverUDP {
	
	private EmotivController ec;
	private DatagramSocket clientSocket;
	private Thread t;
	private InetAddress ip;
	private int port;

	public SendEEGOverUDP(String ip, int port) throws UnknownHostException {
		ec = new EmotivController();
		this.ip = InetAddress.getByName(ip);
		this.port = port;
	}
	
	public void start() throws SocketException {
		
		clientSocket = new DatagramSocket();
		t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				boolean ignoreFirstSamples = true; //Used to clear out buffer of old samples if stopped then continued
				
				while (true) {
					
					List<double[]> samples = ec.getAvailableSamples();
					
					if (samples != null) {
						
						if (ignoreFirstSamples) {
							ignoreFirstSamples = false;
							continue;
						}
						
						System.out.println(samples.size() + " samples");
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
	
	public void stop() {
		t.interrupt();
	}
	
	private static byte[] toByteArray(double value) {
	    byte[] bytes = new byte[8];
	    ByteBuffer.wrap(bytes).putDouble(value);
	    return bytes;
	}

	public static void main(String[] args) {
		try {
			new SendEEGOverUDP("localhost", 9092).start();
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
