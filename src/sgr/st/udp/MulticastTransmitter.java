package sgr.st.udp;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MulticastTransmitter extends UDPTransmitter {

	public MulticastTransmitter(int destPort, int myPort, String destIP) throws SocketException, UnknownHostException {
		super(destPort, destIP, myPort);
	}

	@Override
	protected boolean setSocket(int myPort, String myIP){
		try {
			this.socket = new MulticastSocket(myPort);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
