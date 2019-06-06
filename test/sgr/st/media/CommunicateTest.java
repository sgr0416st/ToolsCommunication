package sgr.st.media;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.LineUnavailableException;

import sgr.st.properties.PropertiesReader;

public class CommunicateTest {
	private static final String VIDEO_NAME_RECEIVE = "dest/test_image_receive";
	private static final String VIDEO_NAME_TRANSMIT = "dest/test_image_taransmit";
	private static final String AUDIO_NAME_RECEIVE = "dest/test_audio_receive";
	private static final String AUDIO_NAME_TRANSMIT = "dest/test_audio_transmit";
	private static final int TIME = 10000;

	public static void main(String[] args) {
		int rcvImagePort, sndImagePort, rcvAudioPort, sndAudioPort,
		audioBufSize_ulaw, audioBufSize_pcm, showWidth, showHeight, fps;
		String rcvIP;
		PropertiesReader reader;

		try {
			// プロパティの読み込み
			reader = new PropertiesReader(
					"/Users/satousuguru/workspace/programing/java/propaties/network.properties"
					);
			rcvIP = reader.getProPerty("IP_MACPRO");
			rcvImagePort = Integer.parseInt(reader.getProPerty("PORT_IMAGE_RECEIVE"));
			sndImagePort = Integer.parseInt(reader.getProPerty("PORT_IMAGE_SEND"));
			showWidth = Integer.parseInt(reader.getProPerty("IMAGE_WIDTH_OF_COMMUNICATION"));
			showHeight = Integer.parseInt(reader.getProPerty("IMAGE_HEIGHT_OF_COMMUNICATION"));
			rcvAudioPort = Integer.parseInt(reader.getProPerty("PORT_AUDIO_RECEIVE"));
			sndAudioPort = Integer.parseInt(reader.getProPerty("PORT_AUDIO_SEND"));
			audioBufSize_ulaw = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_ULAW"));
			audioBufSize_pcm = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_LINEAR"));
			fps = Integer.parseInt(reader.getProPerty("FPS_DEFAULT"));


			// スレッドの初期化
			ExecutorService exec = Executors.newFixedThreadPool(4);
			ImageReceiveThread receiveVideoThread =
					new ImageReceiveThread(rcvImagePort, showWidth, showHeight, fps, VIDEO_NAME_RECEIVE);
			ImageTransmitThread transmitVideoThread =
					new ImageTransmitThread(rcvImagePort, rcvIP, sndImagePort, fps, VIDEO_NAME_TRANSMIT);
			AudioReceiveThread receiveAudioThread =
					new AudioReceiveThread(rcvAudioPort, audioBufSize_ulaw, audioBufSize_pcm, AUDIO_NAME_RECEIVE);
			AudioTransmitThread transmitAudioThread =
					new AudioTransmitThread(rcvAudioPort, rcvIP, sndAudioPort, audioBufSize_ulaw, AUDIO_NAME_TRANSMIT);


			//スレッドの実行
			exec.submit(receiveVideoThread);
			exec.submit(transmitVideoThread);
			exec.submit(receiveAudioThread);
			exec.submit(transmitAudioThread);


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
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (LineUnavailableException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
	}


}
