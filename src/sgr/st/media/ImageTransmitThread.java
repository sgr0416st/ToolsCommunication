package sgr.st.media;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.SocketException;

import sgr.st.ImageCapture;
import sgr.st.ImageConstants;
import sgr.st.ImageConverter;
import sgr.st.ImageRecorder;
import sgr.st.udp.UDPTransmitter;

public class ImageTransmitThread implements Runnable{
	private boolean isStopped, doRecord;
	private ImageCapture capture;
	private UDPTransmitter transmitter;
	private ImageRecorder recorder;
	private BufferedImage image;
	private byte[] data;

	public ImageTransmitThread(int destport, String destIP, int myport, int fps, String filePath) throws SocketException {
		init(destport, destIP, myport);
		this.doRecord = true;
		recorder = new ImageRecorder(filePath, ImageConstants.DEFAULT_IMAGE_WIDTH, ImageConstants.DEFAULT_IMAGE_HEIGHT);
		recorder.start();
	}

	public ImageTransmitThread(int destport, String destIP, int myport) throws SocketException {
		init(destport, destIP, myport);
		this.doRecord = false;
		recorder = null;
	}

	protected void init(int destport, String destIP, int myport) throws SocketException {
		isStopped = false;
		capture = new ImageCapture();
		transmitter = new UDPTransmitter(destport, destIP, myport);
	}

	@Override
	public void run() {
		while(!isStopped){
			doRepeatedTask();
		}
		capture.close();
		if(doRecord) {
			recorder.stop();
			recorder.rewriteTruthTime();
			recorder.close();
			System.out.println("ImageTransmitThread : recorded");
		}
		System.out.println("ImageTransmitThread : finished");
	}

	private void doRepeatedTask(){
		try {
			image = ImageConverter.matToBufferedImage(capture.capture());
			data = ImageConverter.BufferedImageToByte(image, "jpeg");
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
