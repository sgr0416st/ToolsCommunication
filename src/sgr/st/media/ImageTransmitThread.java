package sgr.st.media;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import sgr.st.image.lib.ImageCaputrue;
import sgr.st.image.lib.ImageConverter;
import sgr.st.image.lib.ImageRecorder;
import sgr.st.udp.UDPTransmitter;

public class ImageTransmitThread implements Runnable{
	private boolean isStopped, doRecord;
	private ImageCaputrue capture;
	private UDPTransmitter transmitter;
	private ImageRecorder recorder;
	private BufferedImage image;
	private byte[] data;

	public ImageTransmitThread(String destIP, String video_name) throws SocketException, UnknownHostException {
		init(destIP);
		this.doRecord = true;
		recorder = new ImageRecorder(video_name, MediaSettings.FPS_DEFAULT.getNum());
	}

	public ImageTransmitThread(String destIP) throws SocketException, UnknownHostException {
		init(destIP);
		this.doRecord = false;
		recorder = null;
	}

	protected void init(String destIP) throws SocketException, UnknownHostException {
		isStopped = false;
		capture = new ImageCaputrue();
		transmitter = new UDPTransmitter(
				destIP,
				MediaSettings.PORT_IMAGE_RECEIVE.getNum(),
				MediaSettings.PORT_IMAGE_SEND.getNum()
				);
	}

	@Override
	public void run() {
		while(!isStopped){
			doRepeatedTask();
		}
		capture.close();
		if(doRecord) {
			recorder.save();
			recorder.close();
			System.out.println("ImageTransmitThread : recorded");
		}
		System.out.println("ImageTransmitThread : finished");
	}

	private void doRepeatedTask(){
		try {
			image = ImageConverter.matToBufferedImage(capture.capture());
			data = ImageConverter.BufferedImageToByte(image, MediaSettings.VIDEO_EXTENSION.getString());
			transmitter.transmit(data);
			if(doRecord) {
				recorder.write(image);
			}
		} catch (IOException e) {
			if(this.isStopped) {
				System.out.println("ImageTransmitThread : stopped");
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
