package sgr.st.udp;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import sgr.st.AudioCapture;
import sgr.st.media.MediaSettings;
import sgr.st.properties.PropertiesReader;

public class AudioTransmitTest {

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		int destPort, myPort, audioBufSize_ulaw, audioBufSize_linear;
		String destIP;
		PropertiesReader reader;

		UDPTransmitter transmitter = null;
		AudioCapture capture = null;
		@SuppressWarnings("unused")
		AudioFormat ulawFormat, linearFormat;
		byte[] data;

		try {
			// プロパティの読み込み
			reader = new PropertiesReader(
					"/Users/satousuguru/workspace/programing/java/propaties/network.properties"
					);
			destIP = reader.getProPerty("IP_MACAIR");
			destPort = Integer.parseInt(reader.getProPerty("PORT_AUDIO_RECEIVE"));
			myPort = Integer.parseInt(reader.getProPerty("PORT_AUDIO_SEND"));
			audioBufSize_ulaw = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_ULAW"));
			audioBufSize_linear = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_LINEAR"));

			linearFormat = MediaSettings.getLinearFormat();
			ulawFormat = MediaSettings.getUlawFormat();

			transmitter = new UDPTransmitter(destPort, destIP, myPort);

			//capture = new AudioCapture(audioBufSize_ulaw, ulawFormat);
			capture = new AudioCapture(audioBufSize_linear);
			//recorder = new AudioRecorder(ulawFormat);

			int counter = 0;
			while(counter < 600) {
				data = capture.read();
				//recorder.write(data);
				transmitter.transmit(data);
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

		capture.close();
		transmitter.close();

	}

}
