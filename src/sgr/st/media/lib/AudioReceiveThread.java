package sgr.st.media.lib;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import sgr.st.media.develop.AudioPlayer;
import sgr.st.media.develop.AudioRules;
import sgr.st.udp.lib.UDPReceiver;

public class AudioReceiveThread implements Runnable{
	private boolean isStopped;
	private UDPReceiver receiver;
	private AudioPlayer player;
	// private AudeioRecorder recorder;
	private AudioFormat ulawformat;
	private ByteArrayInputStream stream;


	public AudioReceiveThread(String video_name) throws SocketException, UnknownHostException, LineUnavailableException {
		isStopped = false;
		//ulawフォーマット
		ulawformat = new AudioFormat(
				AudioFormat.Encoding.ULAW,
				AudioRules.sampleRate,
				AudioRules.sampleSizeInBits_ulaw,
				AudioRules.channels,
				AudioRules.frameSize,
				AudioRules.frameRate,
				AudioRules.isBigEndian
				);

		receiver = new UDPReceiver(MediaSettings.PORT_AUDIO_RECEIVE.getNum());
		player = new AudioPlayer(AudioRules.SIZE_MAX_DATA_ULAW, ulawformat);
		//player = new AudioPlayer(AudioRules.SIZE_MAX_DATA_ULAW);


		// recorder = new ImageRecorder(video_name, MediaSettings.FPS_DEFAULT.getNum());
	}

	@Override
	public void run() {
		while(!this.isStopped){
			doRepeatedTask();
		}
		// recorder.save();
		player.close();
		// recorder.close();
		System.out.println("AudioReceiveThread : finished");

	}

	private void doRepeatedTask(){
		try {
			stream = receiver.receive();
			player.write(stream);
		} catch (IOException e) {
			if(this.isStopped) {
				// 必ず呼ばれる訳ではない
				System.out.println("AudioReceiveThread : stopped");
			}else {
				e.printStackTrace();
			}
		}
	}

	public void stopThread() {
		isStopped  = true;
		receiver.close();
	}

}
