package sgr.st.media.lib;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

import sgr.st.image.lib.ImageRecorder;
import sgr.st.image.lib.ImageViewer;
import sgr.st.udp.lib.UDPReceiver;


public class ImageReceiveThread implements Runnable{
	private boolean isStopped, doRecord;
	private UDPReceiver receiver;
	private ImageViewer viewer;
	private ImageRecorder recorder;
	private byte[] data;
	private BufferedImage image;


	public ImageReceiveThread(String video_name) throws SocketException, UnknownHostException {
		init();
		this.doRecord = true;
		recorder = new ImageRecorder(video_name, MediaSettings.FPS_DEFAULT.getNum());
	}

	public ImageReceiveThread() throws SocketException, UnknownHostException {
		init();
		this.doRecord = false;
		recorder = null;
	}

	protected void init() throws SocketException, UnknownHostException {
		this.isStopped = false;
		this.receiver = new UDPReceiver(MediaSettings.PORT_IMAGE_RECEIVE.getNum());
		this.viewer = new ImageViewer(
				MediaSettings.IMAGE_WIDTH_OF_COMMUNICATION.getNum(),
				MediaSettings.IMAGE_HEIGHT_OF_COMMUNICATION.getNum()
				);
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
			data = receiver.receive();
			image = ImageIO.read( new ByteArrayInputStream( data ));
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
