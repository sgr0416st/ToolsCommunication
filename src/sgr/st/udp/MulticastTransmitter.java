package sgr.st.udp;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.SocketException;

public class MulticastTransmitter extends UDPTransmitter {

	public MulticastTransmitter(int destPort, String destIP, int myPort) throws SocketException {
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
