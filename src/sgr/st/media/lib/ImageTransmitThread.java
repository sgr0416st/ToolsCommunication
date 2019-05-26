package sgr.st.media.lib;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import sgr.st.image.develop.ImageCaputrue;
import sgr.st.image.lib.ImageConverter;
import sgr.st.udp.lib.UDPTransmitter;

public class ImageTransmitThread implements Runnable{
	private boolean isStopped;
	private ImageCaputrue capture;
	private UDPTransmitter transmitter;
	// private ImageRecorder recorder;
	private BufferedImage image;
	private ByteArrayOutputStream stream;

	public ImageTransmitThread(String video_name, String destIP) throws SocketException, UnknownHostException {
		isStopped = false;
		capture = new ImageCaputrue();
		transmitter = new UDPTransmitter(destIP, MediaSettings.PORT_IMAGE_RECEIVE.getNum());
		// recorder = new ImageRecorder(video_name, MediaSettings.FPS_DEFAULT.getNum());

	}

	@Override
	public void run() {
		while(!isStopped){
			doRepeatedTask();
		}
		capture.close();
		transmitter.close();
		// recorder.close();
		System.out.println("ImageTransmitThread : finished");
	}

	private void doRepeatedTask(){
		try {
			image = ImageConverter.matToBufferedImage(capture.capture());
			stream = ImageConverter.BufferedImageToByteStream(
					image,
					MediaSettings.VIDEO_EXTENSION.getString()
					);
			transmitter.transmit(stream);
		} catch (IOException e) {
			if(this.isStopped) {
				System.out.println("ImageTransmitThread : stopped");
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
