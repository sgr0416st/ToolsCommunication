package sgr.st.media;

import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import sgr.st._old.MediaSettings;
import sgr.st.sound.lib.AudioCapture;
import sgr.st.sound.lib.AudioRecorder;
import sgr.st.udp.UDPTransmitter;

public class AudioTransmitThread implements Runnable{
	private boolean isStopped, doRecord;
	private AudioCapture capture;
	private UDPTransmitter transmitter;
	private AudioRecorder recorder;
	private byte[] data;
	String fileName;
	private AudioFormat ulawformat;

	/**
	 * レコードあり
	 *
	 * @param destPort
	 * @param destIP
	 * @param myPort
	 * @param audioBufSize
	 * @param fileName
	 * @throws SocketException
	 * @throws LineUnavailableException
	 */
	public AudioTransmitThread(int destPort, String destIP, int myPort, int audioBufSize, String fileName) throws SocketException, LineUnavailableException {
		init(destPort, destIP, myPort, audioBufSize);
		this.fileName = fileName;
		this.doRecord = true;
		recorder = new AudioRecorder(ulawformat);
	}

	/**
	 * レコードなし
	 *
	 * @param destPort
	 * @param destIP
	 * @param myPort
	 * @param audioBufSize
	 * @throws SocketException
	 * @throws LineUnavailableException
	 */
	public AudioTransmitThread(int destPort, String destIP, int myPort, int audioBufSize) throws SocketException, LineUnavailableException {
		init(destPort, destIP, myPort, audioBufSize);
		this.fileName = null;
		this.doRecord = false;
		recorder = null;
	}

	protected void init(int destPort, String destIP, int myPort, int audioBufSize) throws LineUnavailableException, SocketException{
		this.isStopped = false;
		ulawformat = MediaSettings.getUlawFormat();
		capture = new AudioCapture(audioBufSize, ulawformat);
		transmitter = new UDPTransmitter(destPort, destIP, myPort);
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
