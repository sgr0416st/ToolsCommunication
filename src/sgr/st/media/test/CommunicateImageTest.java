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
	private static final String VIDEO_NAME = "test";

	public static void main(String[] args) {
		ExecutorService exec = Executors.newFixedThreadPool(2);
		try {
			String IP = InetAddress.getLocalHost().getHostAddress();
			ImageReceiveThread receiveThread = new ImageReceiveThread(VIDEO_NAME, true);
			ImageTransmitThread transmitThread = new ImageTransmitThread(VIDEO_NAME, IP);

			exec.submit(receiveThread);
			exec.submit(transmitThread);

			try {
				Thread.sleep(10000);
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
		}
	}
}
