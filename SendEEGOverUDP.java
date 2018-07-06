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

	@SuppressWarnings("resource")
	public SendEEGOverUDP() throws SocketException, UnknownHostException {
		
		int port = 9092;
		String ip = "localhost";
		
		EmotivController ec = new EmotivController();
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(ip);
		
		while (true) {
			
			List<double[]> samples = ec.getAvailableSamples();
			
			if (samples != null) {
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
					
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					try {
						clientSocket.send(sendPacket);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}
		}
	}
	
	private static byte[] toByteArray(double value) {
	    byte[] bytes = new byte[8];
	    ByteBuffer.wrap(bytes).putDouble(value);
	    return bytes;
	}

	public static void main(String[] args) {
		try {
			new SendEEGOverUDP();
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
