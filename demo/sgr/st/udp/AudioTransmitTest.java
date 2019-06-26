package sgr.st.udp;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.LineUnavailableException;

import sgr.st.AudioCapture;
import sgr.st.properties.PropertiesReader;

public class AudioTransmitTest {

	public static void main(String[] args) {
		int destPort, myPort, audioBufSize_linear;
		String destIP;
		PropertiesReader reader;

		UDPTransmitter transmitter = null;
		AudioCapture capture = null;
		byte[] data;

		try {
			// プロパティの読み込み
			reader = new PropertiesReader(
					"/Users/satousuguru/workspace/programing/java/propaties/network.properties"
					);
			destIP = reader.getProPerty("IP_MACAIR");
			destPort = Integer.parseInt(reader.getProPerty("PORT_AUDIO_RECEIVE"));
			myPort = Integer.parseInt(reader.getProPerty("PORT_AUDIO_SEND"));
			audioBufSize_linear = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_LINEAR"));

			transmitter = new UDPTransmitter(destPort, destIP, myPort);

			capture = new AudioCapture(audioBufSize_linear);
			//recorder = new AudioRecorder(ulawFormat);

			int counter = 0;

			long captured ,transmitted , capture_time = 0, transmit_time = 0;
			while(counter < 600) {
				transmitted = System.currentTimeMillis();
				data = capture.read();
				captured = System.currentTimeMillis();
				capture_time +=  captured - transmitted;
				System.out.println("captured: " + capture_time);
				//recorder.write(data);
				transmitter.transmit(data);
				transmitted = System.currentTimeMillis();
				transmit_time += transmitted - captured;
				counter++;
			}

			System.out.println("captured: " + capture_time);
			System.out.println("transmit_time: " + transmit_time);


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
