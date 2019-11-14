package sgr.st.udp;

import java.io.IOException;

import sgr.st.properties.PropertiesReader;

public class UDPReceiverTest {
	public static void main(String[] args) {
		UDPReceiver receiver;
		PropertiesReader reader;
		String myIP;
		int myPort;


		try {
			reader = new PropertiesReader(
					"/Users/satousuguru/workspase/EclipseProjects/properties/network.properties"
					);
			myIP = reader.getProPerty("IP_MACAIR");
			myPort = Integer.parseInt(reader.getProPerty("PORT_AUDIO_RECEIVE"));
			receiver = new UDPReceiver(myPort, myIP);
			
			while(true) {
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
