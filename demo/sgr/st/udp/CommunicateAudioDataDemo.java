package sgr.st.udp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import sgr.st.AudioCapture;
import sgr.st.AudioConstants;
import sgr.st.AudioPlayer;
import sgr.st.AudioRecorder;
import sgr.st.properties.PropertiesReader;

/**
 *
 * ImageReceiveThread, およびImageTransmitThreadのテスト。
 *
 * @author satousuguru
 *
 */
public class CommunicateAudioDataDemo {

	private static final String AUDIO_NAME_RECEIVE = "dest/test_audio_receive.wav";
	private static final String AUDIO_NAME_TRANSMIT = "dest/test_audio_transmit.wav";

	public static void main(String[] args) {
		int destPort, myPort, audioBufSize_ulaw, audioBufSize_pcm;
		String myIP, destIP;
		PropertiesReader reader;

		try {
			// プロパティの読み込み
			reader = new PropertiesReader(
					"/Users/satousuguru/workspase/EclipseProjects/properties/network.properties"
					);

			destIP = reader.getProPerty("IP_MACPRO");
			myIP = reader.getProPerty("IP_MACAIR");

			destPort = Integer.parseInt(reader.getProPerty("PORT_AUDIO_RECEIVE"));
			myPort = Integer.parseInt(reader.getProPerty("PORT_AUDIO_SEND"));
			audioBufSize_ulaw = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_ULAW"));
			audioBufSize_pcm = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_LINEAR"));

			// スレッドの初期化
			ExecutorService exec = Executors.newFixedThreadPool(2);
			AudioDataReceiveThread receiveThread = new AudioDataReceiveThread(destPort, myIP, audioBufSize_ulaw, audioBufSize_pcm, AUDIO_NAME_RECEIVE);
			AudioDataTransmitThread transmitThread = new AudioDataTransmitThread(destPort, destIP, myPort, audioBufSize_ulaw, AUDIO_NAME_TRANSMIT);

			// スレッドの実行
			exec.submit(receiveThread);
			exec.submit(transmitThread);

			// 待機
			try {
				Thread.sleep(15000);
				System.out.println("done sleep");
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			// 終了処理
			exec.shutdown();
			receiveThread.stopThread();
			transmitThread.stopThread();
			if(exec.awaitTermination(10, TimeUnit.SECONDS)) {
				System.out.println("finish exec");
			}else {
				exec.shutdownNow();
			}
		} catch (SocketException | UnknownHostException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
	}
}

class AudioDataTransmitThread extends DataTransmitThread{
	AudioCapture capture;
	AudioRecorder recorder;
	AudioFormat ulawFormat;

	int audioBufSize;
	String filePath;

	public AudioDataTransmitThread(int destPort, String destIP, int myPort, int audioBufSize, String filePath) {
		super(destPort, destIP, myPort);
		this.audioBufSize = audioBufSize;
		this.filePath = filePath;
		this.ulawFormat = AudioConstants.ULAW_FORMAT;
	}

	@Override
	protected void doBeforeTask() {
		super.doBeforeTask();
		try {
			capture = new AudioCapture(audioBufSize, ulawFormat);
			recorder = new AudioRecorder(ulawFormat);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			this.stopThread();
		}
	}

	@Override
	protected byte[] doRepeatedDataGeneratingTask() {
		data = capture.read();
		recorder.write(data);
		return data;
	}

	@Override
	protected void doAfterTask() {
		recorder.save(filePath);
		recorder.close();
		System.out.println("AudioReceiveThread : recorded");
	}

	@Override
	protected void close() {
		capture.close();
		System.out.println("AudioReceiveThread : finished");
	}
}

class AudioDataReceiveThread extends DataReceiveThread{

	AudioPlayer player;
	AudioRecorder recorder;
	AudioFormat linearFormat, ulawFormat;

	int audioBufSize_bf, audioBufSize_af;
	String filePath;

	public AudioDataReceiveThread(int myPort, String myIP, int audioBufSize_bf, int audioBufSize_af, String filePath) {
		super(myPort, myIP);
		this.audioBufSize_bf = audioBufSize_bf;
		this.audioBufSize_af = audioBufSize_af;
		this.filePath = filePath;
		this.ulawFormat = AudioConstants.ULAW_FORMAT;
		this.linearFormat = AudioConstants.LINEAR_FORMAT;
	}

	@Override
	protected void doBeforeTask() {
		super.doBeforeTask();
		try {
			player = new AudioPlayer(audioBufSize_bf, audioBufSize_af, ulawFormat, linearFormat);
			recorder = new AudioRecorder(ulawFormat);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			this.stopThread();
		}
	}

	@Override
	protected void doRepeatedDataProcessingTask(byte[] data, int size) {
	 	player.write(new ByteArrayInputStream(data));
		recorder.write(data, 0, size);
	}

	@Override
	protected void doAfterTask() {
		recorder.save(filePath);
		recorder.close();
		System.out.println("AudioReceiveThread : recorded");
	}

	@Override
	protected void close() {
		player.close();
		System.out.println("AudioReceiveThread : finished");
	}

}

