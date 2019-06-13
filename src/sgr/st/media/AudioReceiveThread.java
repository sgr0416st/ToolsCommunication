package sgr.st.media;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import sgr.st.AudioPlayer;
import sgr.st.AudioRecorder;
import sgr.st.udp.UDPReceiver;

public class AudioReceiveThread implements Runnable{
	protected UDPReceiver receiver;
	protected AudioPlayer player;
	protected AudioRecorder recorder;
	protected AudioFormat linearFormat, ulawFormat;
	protected DatagramPacket packet;
	protected boolean isStopped, doRecord;
	protected String fileName;

	public AudioReceiveThread(int myPort, String myIP, int audioBufSize_bf, int audioBufSize_af, String fileName) throws SocketException, LineUnavailableException {
		init(myPort, myIP, audioBufSize_bf, audioBufSize_af, fileName);
	}

	public AudioReceiveThread(int myPort, int audioBufSize_bf, int audioBufSize_af, String fileName) throws SocketException, LineUnavailableException {
		init(myPort, null, audioBufSize_bf, audioBufSize_af, fileName);
	}

	public AudioReceiveThread(int myPort, String myIP, int audioBufSize_bf, int audioBufSize_af) throws SocketException, LineUnavailableException {
		init(myPort, myIP, audioBufSize_bf, audioBufSize_af, null);
	}

	public AudioReceiveThread(int myPort, int audioBufSize_bf, int audioBufSize_af) throws SocketException, LineUnavailableException {
		init(myPort, null, audioBufSize_bf, audioBufSize_af, null);
	}

	protected void init(int myPort, String myIP, int audioBufSize_bf, int audioBufSize_af, String fileName) throws SocketException, LineUnavailableException {
		isStopped = false;
		this.setReciever(myPort, myIP);
		this.setRecorder(fileName);
		this.setPlayer(audioBufSize_bf, audioBufSize_af);
	}

	protected void setReciever(int myPort, String myIP) throws SocketException {
		receiver = new UDPReceiver(myPort);
	}

	protected void setRecorder(String fileName) {
		if(fileName != null) {
			this.doRecord = true;
			this.fileName = fileName;
			recorder = new AudioRecorder(ulawFormat);
		}else {
			this.doRecord = false;
			this.fileName = null;
			recorder = null;
		}
	}

	protected void setPlayer(int audioBufSize_bf, int audioBufSize_af) throws LineUnavailableException {
		ulawFormat = MediaSettings.getUlawFormat();
		linearFormat = MediaSettings.getLinearFormat();
		player = new AudioPlayer(audioBufSize_bf, audioBufSize_af, ulawFormat, linearFormat);
	}

	@Override
	public void run() {
		while(!this.isStopped){
			doRepeatedTask();
		}
		if(doRecord) {
			recorder.save(fileName);
			recorder.close();
			System.out.println("AudioReceiveThread : recorded");
		}
		player.close();
		System.out.println("AudioReceiveThread : finished");

	}

	private void doRepeatedTask(){
		try {
			packet = receiver.receivePacket();
			int size = packet.getLength();
			byte[] data = packet.getData();
			player.write(new ByteArrayInputStream(data));
			if(doRecord) {
				recorder.write(data, 0, size);
			}
		} catch (IOException e) {
			if(this.isStopped) {
				// 必ず呼ばれる訳ではない
				System.out.println("AudioReceiveThread : stopped");
			}else {
				e.printStackTrace();
			}
		}
	}

	public void stopThread() {
		isStopped  = true;
		receiver.close();
	}

}