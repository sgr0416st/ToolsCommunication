package sgr.st.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class MulticastReceiver extends UDPReceiver {

	public MulticastReceiver(int myPort, String myIP) throws SocketException {
		super(myPort, myIP);
	}

	@Override
	protected boolean setSocket(int myPort, String myIP) {
		try {
			MulticastSocket s_temp =  new MulticastSocket(myPort);
			InetAddress group = InetAddress.getByName(myIP);
			s_temp.joinGroup(group);
			this.socket = s_temp;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


}
