package sgr.st.media;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.LineUnavailableException;

import sgr.st.properties.PropertiesReader;

public class MulticastCommunicationTest {

	public static void main(String[] args) {
		System.setProperty("java.net.preferIPv4Stack", "true");

		int rcvPort, sndPort, audioBufSize_ulaw, audioBufSize_pcm;
		String rcvIP;
		PropertiesReader reader;

		try {
			// プロパティの読み込み
			reader = new PropertiesReader(
					"/Users/satousuguru/workspace/programing/java/propaties/network.properties"
					);
			rcvIP = reader.getProPerty("IP_MULTICAST");
			rcvPort = Integer.parseInt(reader.getProPerty("PORT_AUDIO_RECEIVE"));
			sndPort = Integer.parseInt(reader.getProPerty("PORT_AUDIO_SEND"));
			audioBufSize_ulaw = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_ULAW"));
			audioBufSize_pcm = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_LINEAR"));

			// スレッドの初期化
			ExecutorService exec = Executors.newFixedThreadPool(2);
			MulticastAudioReceiveThread receiveThread = new MulticastAudioReceiveThread(rcvPort, rcvIP, audioBufSize_ulaw, audioBufSize_pcm);
			MulticastAudioTransmitThread transmitThread = new MulticastAudioTransmitThread(rcvPort, rcvIP, sndPort, audioBufSize_ulaw);

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
		} catch (LineUnavailableException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
	}


}