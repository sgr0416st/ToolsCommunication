package sgr.st.media;

import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import sgr.st.sound.lib.AudioCapture;
import sgr.st.sound.lib.AudioRecorder;
import sgr.st.sound.lib.AudioRules;
import sgr.st.udp.UDPTransmitter;

public class AudioTransmitThread implements Runnable{
	private boolean isStopped, doRecord;
	private AudioCapture capture;
	private UDPTransmitter transmitter;
	private AudioRecorder recorder;
	private byte[] data;
	String fileName;
	private AudioFormat ulawformat;

	public AudioTransmitThread(String destIP, String fileName) throws SocketException, UnknownHostException, LineUnavailableException {
		init(destIP);
		this.fileName = fileName;
		this.doRecord = true;
		recorder = new AudioRecorder(ulawformat);
	}

	public AudioTransmitThread(String destIP) throws SocketException, UnknownHostException, LineUnavailableException {
		init(destIP);
		this.fileName = null;
		this.doRecord = false;
		recorder = null;
	}

	protected void init(String destIP) throws LineUnavailableException, SocketException, UnknownHostException{
		this.isStopped = false;
		ulawformat = MediaSettings.getUlawFormat();
		capture = new AudioCapture(AudioRules.SIZE_MAX_DATA_ULAW, ulawformat);
		transmitter = new UDPTransmitter(
				destIP,
				MediaSettings.PORT_AUDIO_RECEIVE.getNum(),
				MediaSettings.PORT_AUDIO_SEND.getNum()
				);
	}

	@Override
	public void run() {
		while(!isStopped){
			doRepeatedTask();
		}
		if(doRecord) {
			recorder.save(fileName);
			recorder.close();
			System.out.println("AudioReceiveThread : recorded");
		}
		try {
			capture.close();
		} catch (LineUnavailableException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		System.out.println("AudioTransmitThread : finished");
	}

	private void doRepeatedTask(){
		try {
			data = capture.read();
			transmitter.transmit(data);
			if(doRecord) {
				recorder.write(data);
			}
		} catch (Exception e) {
			if(this.isStopped) {
				// 必ず呼ばれる訳ではない
				System.out.println("AudioTransmitThread : stopped");
			}else {
				e.printStackTrace();
			}
		}
	}

	public void stopThread() {
		this.isStopped = true;
		transmitter.close();
	}



}
