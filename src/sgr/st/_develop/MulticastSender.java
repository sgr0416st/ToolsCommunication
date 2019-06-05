package sgr.st._develop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSender {

	  public static final int ECHO_PORT = 10007;
	  public static final int PACKET_SIZE = 1024;
	  public static final String MCAST_ADDRESS = "224.0.1.1";

	  public static void main(String args[]) {
	    MulticastSocket socket = null;

	    try {
	      InetAddress mcastAddress = InetAddress.getByName(MCAST_ADDRESS);
	      BufferedReader keyIn =
	        new BufferedReader(new InputStreamReader(System.in));
	      socket = new MulticastSocket();

	      String message;
	      while ( (message = keyIn.readLine()).length() > 0 ) {
	        byte[] bytes = message.getBytes();
	        DatagramPacket packet =
	          new DatagramPacket(bytes, bytes.length, mcastAddress, ECHO_PORT);
	        socket.send(packet);
	      }
	    } catch (IOException e) {
	      e.printStackTrace();
	    } finally {
	      if (socket != null) {
	        socket.close();
	      }
	    }
	  }
}
