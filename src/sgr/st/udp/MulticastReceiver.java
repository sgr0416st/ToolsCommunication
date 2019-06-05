package sgr.st.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MulticastReceiver extends UDPReceiver {

	public MulticastReceiver(String addrstr, int port) throws SocketException, UnknownHostException {
		super(addrstr, port);
	}

	@Override
	protected void setSocket(int port, InetAddress addr) throws SocketException {
		System.setProperty("java.net.preferIPv4Stack", "true");
		try {
			MulticastSocket s_temp =  new MulticastSocket(port);
			s_temp.joinGroup(addr);
			this.socket = s_temp;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}


}
