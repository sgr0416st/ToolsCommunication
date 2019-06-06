package sgr.st.media;

import java.net.SocketException;

import javax.sound.sampled.LineUnavailableException;

import sgr.st.udp.MulticastReceiver;

class MulticastAudioReceiveThread extends AudioReceiveThread{

	public MulticastAudioReceiveThread(int myPort, String myIP, int audioBufSize_bf, int audioBufSize_af)
			throws SocketException, LineUnavailableException {
		super(myPort, myIP, audioBufSize_bf, audioBufSize_af);
	}

	@Override
	protected void setReciever(int myPort, String myIP) throws SocketException {
		receiver = new MulticastReceiver(myPort, myIP);
	}
}