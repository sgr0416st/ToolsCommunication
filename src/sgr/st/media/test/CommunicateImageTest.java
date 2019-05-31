package sgr.st.media.test;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import sgr.st.media.lib.ImageReceiveThread;
import sgr.st.media.lib.ImageTransmitThread;

/**
 *
 * ImageReceiveThread, およびImageTransmitThreadのテスト。
 *
 * @author satousuguru
 *
 */
public class CommunicateImageTest {
	private static final String VIDEO_NAME_RECEIVE = "test_receive";
	private static final String VIDEO_NAME_TRANSMIT = "test_taransmit";
	private static final int TIME = 10000;

	public static void main(String[] args) {
		ExecutorService exec = Executors.newFixedThreadPool(4);
		try {
			String IP = InetAddress.getLocalHost().getHostAddress();
			ImageReceiveThread receiveVideoThread = new ImageReceiveThread(VIDEO_NAME_RECEIVE, true);
			ImageTransmitThread transmitVideoThread = new ImageTransmitThread(VIDEO_NAME_TRANSMIT, IP, true);

			exec.submit(receiveVideoThread);
			exec.submit(transmitVideoThread);

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
		}
	}
}
