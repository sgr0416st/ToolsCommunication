package sgr.st.media;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketException;

import javax.imageio.ImageIO;

import sgr.st.ImageRecorder;
import sgr.st.ImageViewer;
import sgr.st.udp.UDPReceiver;


public class ImageReceiveThread implements Runnable{
	private boolean isStopped, doRecord;
	private UDPReceiver receiver;
	private ImageViewer viewer;
	private ImageRecorder recorder;
	private byte[] data;
	private BufferedImage image;


	public ImageReceiveThread(int myport, int width, int height, int fps, String filePath) throws SocketException {
		init(myport, width, height);
		this.doRecord = true;
		recorder = new ImageRecorder(filePath, width, height);
		recorder.start();
	}

	public ImageReceiveThread(int myport, int width, int height) throws SocketException {
		init(myport, width, height);
		this.doRecord = false;
		recorder = null;
	}

	protected void init(int myport, int width, int height) throws SocketException {
		this.isStopped = false;
		this.receiver = new UDPReceiver(myport);
		this.viewer = new ImageViewer(width, height);
	}

	@Override
	public void run() {
		while(!this.isStopped){
			doRepeatedTask();
		}
		if(doRecord) {
			recorder.stop();
			recorder.rewriteTruthTime();
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
