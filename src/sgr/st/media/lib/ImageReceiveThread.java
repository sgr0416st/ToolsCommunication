package sgr.st.media.lib;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import sgr.st.image.lib.ImageConverter;
import sgr.st.image.lib.ImageViewer;
import sgr.st.udp.lib.UDPReceiver;



public class ImageReceiveThread implements Runnable{
	private boolean isStopped;
	private UDPReceiver receiver;
	private ImageViewer viewer;
	// private ImageRecorder recorder;
	private ByteArrayInputStream stream;
	private BufferedImage image;


	public ImageReceiveThread(String video_name) throws SocketException, UnknownHostException {
		isStopped = false;
		receiver = new UDPReceiver(MediaSettings.PORT_IMAGE_RECEIVE.getNum());
		viewer = new ImageViewer(
				MediaSettings.IMAGE_WIDTH_OF_COMMUNICATION.getNum(),
				MediaSettings.IMAGE_HEIGHT_OF_COMMUNICATION.getNum()
				);
		// recorder = new ImageRecorder(video_name, MediaSettings.FPS_DEFAULT.getNum());
	}

	@Override
	public void run() {

		while(!this.isStopped){
			doRepeatedTask();
		}
		viewer.close();
		// recorder.close();
		System.out.println("ImageReceiveThread : finished");

	}

	private void doRepeatedTask(){
		try {
			stream = receiver.receive();
			image = ImageConverter.ByteStreamToBufferedImage(stream);
			viewer.showImage(image);
		} catch (IOException e) {
			if(this.isStopped) {
				System.out.println("ImageReceiveThread : stopped");
				// recorder.save();
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
