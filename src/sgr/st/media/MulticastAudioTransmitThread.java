package sgr.st.media;

import java.net.SocketException;

import javax.sound.sampled.LineUnavailableException;

import sgr.st.udp.MulticastTransmitter;

public class MulticastAudioTransmitThread extends AudioTransmitThread{

	public MulticastAudioTransmitThread(int destPort, String destIP, int myPort, int audioBufSize)
			throws SocketException, LineUnavailableException {
		super(destPort, destIP, myPort, audioBufSize);
	}

	public MulticastAudioTransmitThread(int destPort, String destIP, int myPort, int audioBufSize, String fileName)
			throws SocketException, LineUnavailableException {
		super(destPort, destIP, myPort, audioBufSize, fileName);
	}


	@Override
	protected void setTransmitter(int destPort, String destIP, int myPort) throws SocketException {
		transmitter = new MulticastTransmitter(destPort, destIP, myPort);
	}


}
