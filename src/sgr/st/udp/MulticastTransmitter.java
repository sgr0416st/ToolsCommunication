package sgr.st.udp;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MulticastTransmitter extends UDPTransmitter {

	public MulticastTransmitter(String destIP, int destPort, int myPort) throws SocketException, UnknownHostException {
		super(destIP, destPort, myPort);
	}

	@Override
	protected boolean setSocket(int myPort, String myIP) throws SocketException, UnknownHostException {
		try {
			this.socket = new MulticastSocket(myPort);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}
