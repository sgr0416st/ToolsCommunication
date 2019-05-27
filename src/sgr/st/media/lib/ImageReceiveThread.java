package sgr.st.media.lib;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import sgr.st.image.lib.ImageConverter;
import sgr.st.image.lib.ImageRecorder;
import sgr.st.image.lib.ImageViewer;
import sgr.st.udp.lib.UDPReceiver;



public class ImageReceiveThread implements Runnable{
	private boolean isStopped, doRecord;
	private UDPReceiver receiver;
	private ImageViewer viewer;
	private ImageRecorder recorder;
	private ByteArrayInputStream stream;
	private BufferedImage image;


	public ImageReceiveThread(String video_name, boolean doRecord) throws SocketException, UnknownHostException {
		this.isStopped = false;
		this.doRecord = doRecord;
		this.receiver = new UDPReceiver(MediaSettings.PORT_IMAGE_RECEIVE.getNum());
		this.viewer = new ImageViewer(
				MediaSettings.IMAGE_WIDTH_OF_COMMUNICATION.getNum(),
				MediaSettings.IMAGE_HEIGHT_OF_COMMUNICATION.getNum()
				);
		if(this.doRecord) {
			recorder = new ImageRecorder(video_name, MediaSettings.FPS_DEFAULT.getNum());
		}else {
			recorder = null;
		}
	}

	public ImageReceiveThread(String video_name) throws SocketException, UnknownHostException {
		this(video_name, false);
	}

	@Override
	public void run() {
		while(!this.isStopped){
			doRepeatedTask();
		}
		if(doRecord) {
			recorder.save();
			recorder.close();
			System.out.println("ImageReceiveThread : recorded");
		}
		viewer.close();
		System.out.println("ImageReceiveThread : finished");

	}

	private void doRepeatedTask(){
		try {
			stream = receiver.receive();
			image = ImageConverter.ByteStreamToBufferedImage(stream);
			viewer.showImage(image);
			if(doRecord) {
				recorder.write(image);
			}
		} catch (IOException e) {
			if(this.isStopped) {
				// 必ず呼ばれる訳ではない
				System.out.println("ImageReceiveThread : stopped");
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
