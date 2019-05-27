package sgr.st.media.lib;

import java.io.ByteArrayOutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import sgr.st.media.develop.AudioCapture;
import sgr.st.media.develop.AudioRules;
import sgr.st.udp.lib.UDPTransmitter;

public class AudioTransmitThread implements Runnable{

	private boolean isStopped;
	private AudioCapture capture;
	private UDPTransmitter transmitter;
	// private AudioRecorder recorder;
	private ByteArrayOutputStream stream;
	private AudioFormat ulawformat;

	public AudioTransmitThread(String video_name, String destIP) throws SocketException, UnknownHostException, LineUnavailableException {
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
		capture = new AudioCapture(AudioRules.SIZE_MAX_DATA_ULAW, ulawformat);
		//capture = new AudioCapture(AudioRules.SIZE_MAX_DATA_ULAW);


		transmitter = new UDPTransmitter(
				destIP,
				MediaSettings.PORT_AUDIO_RECEIVE.getNum(),
				MediaSettings.PORT_AUDIO_SEND.getNum()
				);


		// recorder = new ImageRecorder(video_name, MediaSettings.FPS_DEFAULT.getNum());

	}

	@Override
	public void run() {
		while(!isStopped){
			doRepeatedTask();
		}
		try {
			capture.close();
		} catch (LineUnavailableException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		// recorder.close();
		System.out.println("AudioTransmitThread : finished");
	}

	private void doRepeatedTask(){
		try {
			stream = capture.read();
			transmitter.transmit(stream);
		} catch (Exception e) {
			if(this.isStopped) {
				System.out.println("AudioTransmitThread : stopped");
				// recorder.save();
			}else {
				e.printStackTrace();
			}
		}
	}

	public void stopThread() {
		this.isStopped = true;
		transmitter.close();
	}



}
