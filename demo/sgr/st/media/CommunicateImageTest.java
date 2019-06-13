package sgr.st.media;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import sgr.st.properties.PropertiesReader;

/**
 *
 * ImageReceiveThread, およびImageTransmitThreadのテスト。
 *
 * @author satousuguru
 *
 */
public class CommunicateImageTest {
	private static final int TIME = 10000;

	public static void main(String[] args) {
		int rcvPort, sndPort, showWidth, showHeight;
		String rcvIP;
		PropertiesReader reader;

		try {
			// プロパティの読み込み
			reader = new PropertiesReader(
					"/Users/satousuguru/workspace/programing/java/propaties/network.properties"
					);
			rcvIP = reader.getProPerty("IP_MACPRO");
			rcvPort = Integer.parseInt(reader.getProPerty("PORT_IMAGE_RECEIVE"));
			sndPort = Integer.parseInt(reader.getProPerty("PORT_IMAGE_SEND"));
			showWidth = Integer.parseInt(reader.getProPerty("IMAGE_WIDTH_OF_COMMUNICATION"));
			showHeight = Integer.parseInt(reader.getProPerty("IMAGE_HEIGHT_OF_COMMUNICATION"));

			// スレッドの初期化
			ExecutorService exec = Executors.newFixedThreadPool(4);
			ImageReceiveThread receiveVideoThread = new ImageReceiveThread(rcvPort, showWidth, showHeight);
			ImageTransmitThread transmitVideoThread = new ImageTransmitThread(rcvPort, rcvIP, sndPort);

			//スレッドの実行
			exec.submit(receiveVideoThread);
			exec.submit(transmitVideoThread);

			// 待機
			try {
				Thread.sleep(TIME);
				System.out.println("done sleep");
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			// 終了処理
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
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
	}
}
