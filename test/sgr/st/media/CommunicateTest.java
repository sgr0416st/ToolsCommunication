package sgr.st.media;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.LineUnavailableException;

public class CommunicateTest {
	private static final String VIDEO_NAME_RECEIVE = "dest/test_image_receive";
	private static final String VIDEO_NAME_TRANSMIT = "dest/test_image_taransmit";
	private static final String AUDIO_NAME_RECEIVE = "dest/test_audio_receive";
	private static final String AUDIO_NAME_TRANSMIT = "dest/test_audio_transmit";
	private static final int TIME = 10000;

	public static void main(String[] args) {
		ExecutorService exec = Executors.newFixedThreadPool(4);
		try {
			String IP = InetAddress.getLocalHost().getHostAddress();
			ImageReceiveThread receiveVideoThread = new ImageReceiveThread(VIDEO_NAME_RECEIVE);
			ImageTransmitThread transmitVideoThread = new ImageTransmitThread(IP, VIDEO_NAME_TRANSMIT);
			AudioReceiveThread receiveAudioThread = new AudioReceiveThread(AUDIO_NAME_RECEIVE);
			AudioTransmitThread transmitAudioThread = new AudioTransmitThread(IP, AUDIO_NAME_TRANSMIT);

			exec.submit(receiveVideoThread);
			exec.submit(transmitVideoThread);
			exec.submit(receiveAudioThread);
			exec.submit(transmitAudioThread);

			try {
				Thread.sleep(TIME);
				System.out.println("done sleep");
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			exec.shutdown();
			receiveVideoThread.stopThread();
			transmitVideoThread.stopThread();
			receiveAudioThread.stopThread();
			transmitAudioThread.stopThread();

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
		} catch (LineUnavailableException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
	}


}
