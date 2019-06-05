package sgr.st.media;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.LineUnavailableException;

/**
 *
 * ImageReceiveThread, およびImageTransmitThreadのテスト。
 *
 * @author satousuguru
 *
 */
public class CommunicateAudioTest {
	private static final String AUDIO_NAME = "test";

	public static void main(String[] args) {
		ExecutorService exec = Executors.newFixedThreadPool(2);
		try {
			String IP = InetAddress.getLocalHost().getHostAddress();
			AudioReceiveThread receiveThread = new AudioReceiveThread(AUDIO_NAME);
			AudioTransmitThread transmitThread = new AudioTransmitThread(IP, AUDIO_NAME);

			exec.submit(receiveThread);
			exec.submit(transmitThread);

			try {
				Thread.sleep(15000);
				System.out.println("done sleep");
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

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
		} catch (LineUnavailableException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
	}
}
