package sgr.st.media.test;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import sgr.st.media.lib.ImageReceiveThread;
import sgr.st.media.lib.ImageTransmitThread;

public class CommunicateImageTest {
	private static final String VIDEO_NAME = "test";
	private static final String IP = "133.34.174.93";


	public static void main(String[] args) {
		ExecutorService exec = Executors.newFixedThreadPool(2);
		try {
			ImageReceiveThread receiveThread = new ImageReceiveThread(VIDEO_NAME);
			ImageTransmitThread transmitThread = new ImageTransmitThread(VIDEO_NAME, IP);

			exec.submit(receiveThread);
			exec.submit(transmitThread);


			try {
				Thread.sleep(5000);
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
