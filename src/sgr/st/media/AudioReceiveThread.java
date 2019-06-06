package sgr.st.media;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import sgr.st._old.MediaSettings;
import sgr.st.sound.lib.AudioPlayer;
import sgr.st.sound.lib.AudioRecorder;
import sgr.st.udp.UDPReceiver;

public class AudioReceiveThread implements Runnable{
	private boolean isStopped, doRecord;
	private UDPReceiver receiver;
	private AudioPlayer player;
	private AudioRecorder recorder;
	private AudioFormat linearFormat, ulawFormat;
	private DatagramPacket packet;
	private String fileName;

	public AudioReceiveThread(int myPort, int audioBufSize_bf, int audioBufSize_af, String fileName) throws SocketException, UnknownHostException, LineUnavailableException {
		init(myPort, audioBufSize_bf, audioBufSize_af);
		this.doRecord = true;
		this.fileName = fileName;
		recorder = new AudioRecorder(ulawFormat);
	}

	public AudioReceiveThread(int myPort, int audioBufSize_bf, int audioBufSize_af) throws SocketException, UnknownHostException, LineUnavailableException {
		init(myPort, audioBufSize_bf, audioBufSize_af);
		this.doRecord = false;
		this.fileName = null;
		recorder = null;
	}

	protected void init(int myPort, int audioBufSize_bf, int audioBufSize_af) throws SocketException, UnknownHostException, LineUnavailableException {
		isStopped = false;
		ulawFormat = MediaSettings.getUlawFormat();
		linearFormat = MediaSettings.getLinearFormat();
		receiver = new UDPReceiver(myPort);
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
