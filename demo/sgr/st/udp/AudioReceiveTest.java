package sgr.st.udp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import sgr.st.AudioPlayer;
import sgr.st.media.MediaSettings;
import sgr.st.properties.PropertiesReader;

public class AudioReceiveTest {

	public static void main(String[] args) {
		int myPort, audioBufSize_ulaw, audioBufSize_linear;
		String myIP;
		PropertiesReader reader;

		UDPReceiver receiver = null;
		AudioPlayer player = null;
		AudioFormat ulawFormat, linearFormat;
		byte[] data;

		try {
			// プロパティの読み込み
			reader = new PropertiesReader(
					"/Users/satousuguru/workspace/programing/java/propaties/network.properties"
					);
			myIP = reader.getProPerty("IP_MACAIR");
			myPort = Integer.parseInt(reader.getProPerty("PORT_AUDIO_RECEIVE"));
			audioBufSize_ulaw = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_ULAW"));
			audioBufSize_linear = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_LINEAR"));

			ulawFormat = MediaSettings.getUlawFormat();
			linearFormat = MediaSettings.getUlawFormat();
			receiver = new UDPReceiver(myPort, myIP);
			player = new AudioPlayer(audioBufSize_ulaw, audioBufSize_linear, ulawFormat, linearFormat);
			//recorder = new AudioRecorder(ulawFormat);

			int counter = 0;
			while(counter < 600) {
				data = receiver.receive();
				//recorder.write(data);
			 	player.write(new ByteArrayInputStream(data));
				counter++;
			}

		} catch (SocketException | UnknownHostException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		player.close();
		receiver.close();

	}

}