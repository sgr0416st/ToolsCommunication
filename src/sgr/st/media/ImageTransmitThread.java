package sgr.st.media;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import sgr.st.ImageCaputrue;
import sgr.st.ImageConverter;
import sgr.st.ImageRecorder;
import sgr.st._old.MediaSettings;
import sgr.st.udp.UDPTransmitter;

public class ImageTransmitThread implements Runnable{
	private boolean isStopped, doRecord;
	private ImageCaputrue capture;
	private UDPTransmitter transmitter;
	private ImageRecorder recorder;
	private BufferedImage image;
	private byte[] data;

	public ImageTransmitThread(int destport, String destIP, int myport, int fps, String video_name) throws SocketException, UnknownHostException {
		init(destport, destIP, myport);
		this.doRecord = true;
		recorder = new ImageRecorder(video_name, fps);
	}

	public ImageTransmitThread(int destport, String destIP, int myport) throws SocketException, UnknownHostException {
		init(destport, destIP, myport);
		this.doRecord = false;
		recorder = null;
	}

	protected void init(int destport, String destIP, int myport) throws SocketException, UnknownHostException {
		isStopped = false;
		capture = new ImageCaputrue();
		transmitter = new UDPTransmitter(destport, destIP, myport);
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
