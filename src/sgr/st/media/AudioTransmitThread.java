package sgr.st.media;

import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import sgr.st.sound.AudioCapture;
import sgr.st.sound.AudioRecorder;
import sgr.st.udp.UDPTransmitter;

public class AudioTransmitThread implements Runnable{
	protected AudioCapture capture;
	protected UDPTransmitter transmitter;
	protected AudioRecorder recorder;
	protected AudioFormat ulawformat;
	protected byte[] data;
	protected String fileName;
	protected boolean isStopped, doRecord;

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
		init(destPort, destIP, myPort, audioBufSize, fileName);
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
		init(destPort, destIP, myPort, audioBufSize, null);
	}

	protected void init(int destPort, String destIP, int myPort, int audioBufSize, String fileName) throws LineUnavailableException, SocketException{
		this.isStopped = false;
		this.setCapture(audioBufSize);
		this.setTransmitter(destPort, destIP, myPort);
		this.setRecorder(fileName);
	}

	protected void setTransmitter(int destPort, String destIP, int myPort) throws SocketException {
		transmitter = new UDPTransmitter(destPort, destIP, myPort);
	}

	protected void setCapture(int audioBufSize) throws LineUnavailableException {
		ulawformat = MediaSettings.getUlawFormat();
		capture = new AudioCapture(audioBufSize, ulawformat);
	}

	protected void setRecorder(String fileName) {
		if(fileName != null) {
			this.fileName = fileName;
			this.doRecord = true;
			recorder = new AudioRecorder(ulawformat);
		}else {
			this.fileName = null;
			this.doRecord = false;
			recorder = null;
		}
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
		capture.close();
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