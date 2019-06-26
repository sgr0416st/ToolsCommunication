package sgr.st.udp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import sgr.st.AudioPlayer;
import sgr.st.media.MediaSettings;
import sgr.st.properties.PropertiesReader;

public class AudioReceiveTest {

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		int myPort, audioBufSize_ulaw, audioBufSize_linear;
		String myIP;
		PropertiesReader reader;

		UDPReceiver receiver = null;
		AudioPlayer player = null;
		SourceDataLine sourceDataLine = null;

		@SuppressWarnings("unused")
		AudioFormat ulawFormat, linearFormat;
		byte[] data;

		try {
			// プロパティの読み込み
			reader = new PropertiesReader(
					"/Users/satousuguru/workspase/EclipseProjects/properties/network.properties"
					);
			myIP = reader.getProPerty("IP_MACAIR");
			myPort = Integer.parseInt(reader.getProPerty("PORT_AUDIO_RECEIVE"));
			audioBufSize_ulaw = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_ULAW"));
			audioBufSize_linear = Integer.parseInt(reader.getProPerty("SIZE_MAX_DATA_LINEAR"));

			ulawFormat = MediaSettings.getUlawFormat();
			linearFormat = MediaSettings.getLinearFormat();
			receiver = new UDPReceiver(myPort, myIP);
			//player = new AudioPlayer(audioBufSize_ulaw, audioBufSize_linear, ulawFormat, linearFormat);
			player = new AudioPlayer(audioBufSize_linear);
			/**//*
			Info dataInfo = new DataLine.Info(SourceDataLine.class,linearFormat);
			sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataInfo);
			sourceDataLine.open(linearFormat);
			sourceDataLine.start();
			*/
			//recorder = new AudioRecorder(ulawFormat);

			long recieved ,writed , write_time = 0, recieve_time = 0;
			int counter = 0;
			data = receiver.receive();
			writed = System.currentTimeMillis();

			while(counter < 4000) {
				data = receiver.receive();
				recieved = System.currentTimeMillis();
				recieve_time +=  recieved - writed;
				//recorder.write(data);

			 	player.write(new ByteArrayInputStream(data));
				//sourceDataLine.write(data,0, audioBufSize_linear);

				writed = System.currentTimeMillis();
				write_time += writed - recieved;
				counter++;
			}

			System.out.println("write_time: " + write_time);
			System.out.println("recieve_time: " + recieve_time);

		} catch (SocketException | UnknownHostException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}finally {
			player.close();
			//sourceDataLine.close();
			receiver.close();
		}


	}

}
