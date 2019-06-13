package sgr.st.udp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;

import sgr.st.ImageCapture;
import sgr.st.ImageConstants;
import sgr.st.ImageConverter;
import sgr.st.ImageRecorder;
import sgr.st.ImageViewer;
import sgr.st.properties.PropertiesReader;

/**
 *
 * ImageReceiveThread, およびImageTransmitThreadのテスト。
 *
 * @author satousuguru
 *
 */
public class CommunicateImageDataDemo {
	private static final int TIME = 10000;
	private static final String VIDEO_NAME_RECEIVE = "dest/test_image_receive.avi";
	private static final String VIDEO_NAME_TRANSMIT = "dest/test_image_taransmit.avi";


	public static void main(String[] args) {
		int rcvPort, sndPort;
		String rcvIP;
		PropertiesReader reader;

		try {
			// プロパティの読み込み
			reader = new PropertiesReader(
					"/Users/satousuguru/workspace/programing/java/propaties/network.properties"
					);
			rcvIP = reader.getProPerty("IP_MACPRO");
			rcvPort = Integer.parseInt(reader.getProPerty("PORT_IMAGE_RECEIVE"));
			sndPort = Integer.parseInt(reader.getProPerty("PORT_IMAGE_SEND"));

			// スレッドの初期化
			ExecutorService exec = Executors.newFixedThreadPool(2);
			ImageDataReceiveThread receiveVideoThread = new ImageDataReceiveThread(rcvPort, rcvIP, VIDEO_NAME_RECEIVE);
			ImageDataTransmitThread transmitVideoThread = new ImageDataTransmitThread(rcvPort, rcvIP, sndPort, VIDEO_NAME_TRANSMIT);

			//スレッドの実行
			exec.submit(receiveVideoThread);
			exec.submit(transmitVideoThread);

			// 待機
			try {
				Thread.sleep(TIME);
				System.out.println("done sleep");
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			// 終了処理
			receiveVideoThread.stopThread();
			transmitVideoThread.stopThread();
			exec.shutdown();
			if(exec.awaitTermination(10, TimeUnit.SECONDS)) {
				System.out.println("finish exec");
			}else {
				exec.shutdownNow();
			}

		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
	}
}

class ImageDataTransmitThread extends DataTransmitThread{
	ImageCapture capture;
	ImageRecorder recorder;
	BufferedImage bufferedImage;
	Mat webcamImage;

	String filePath;

	public ImageDataTransmitThread(int destPort, String destIP, int myPort, String filePath) {
		super(destPort, destIP, myPort);
		this.filePath = filePath;
	}

	@Override
	protected void doBeforeTask() {
		super.doBeforeTask();
		capture = new ImageCapture();
		recorder = new ImageRecorder(filePath, ImageConstants.DEFAULT_IMAGE_WIDTH, ImageConstants.DEFAULT_IMAGE_HEIGHT);
	}

	@Override
	protected void doRightBeforeRepeatedTask() {
		super.doRightBeforeRepeatedTask();
		recorder.start();
	}

	@Override
	protected byte[] doRepeatedDataGeneratingTask() {
		try {
			webcamImage  = capture.capture();
			recorder.write(webcamImage);
			bufferedImage = ImageConverter.matToBufferedImage(webcamImage);
			data = ImageConverter.BufferedImageToByte(bufferedImage, "jpeg");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	@Override
	protected void doRightAfterRepeatedTask() {
		super.doRightAfterRepeatedTask();
		if(recorder != null) {
			recorder.stop();
		}
	}

	@Override
	protected void doAfterTask() {
		super.doAfterTask();
		if(recorder != null) {
			recorder.rewriteTruthTime();
		}
		System.out.println("ImageDataTransmitThread : recorded");
	}

	@Override
	protected void close() {
		super.close();
		capture.close();
		if(recorder != null) {
			recorder.close();
		}
		System.out.println("ImageDataTransmitThread : finished");
	}
}

class ImageDataReceiveThread extends DataReceiveThread{

	ImageViewer viewer;
	ImageRecorder recorder;
	BufferedImage bufferedImage;

	String filePath;

	public ImageDataReceiveThread(int myPort, String myIP, String filePath) {
		super(myPort, myIP);
		this.filePath = filePath;
	}

	@Override
	protected void doBeforeTask() {
		super.doBeforeTask();
		viewer = new ImageViewer(ImageConstants.DEFAULT_IMAGE_WIDTH, ImageConstants.DEFAULT_IMAGE_HEIGHT);
		recorder = new ImageRecorder(filePath, ImageConstants.DEFAULT_IMAGE_WIDTH, ImageConstants.DEFAULT_IMAGE_HEIGHT);
	}

	@Override
	protected void doRightBeforeRepeatedTask() {
		super.doRightBeforeRepeatedTask();
		recorder.start();
	}

	@Override
	protected void doRepeatedDataProcessingTask(byte[] data, int size) {
		try {
			bufferedImage = ImageIO.read( new ByteArrayInputStream( data ));
			viewer.showImage(bufferedImage);
			recorder.write(bufferedImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doRightAfterRepeatedTask() {
		super.doRightAfterRepeatedTask();
		if(recorder != null) {
			recorder.stop();
		}
	}

	@Override
	protected void doAfterTask() {
		super.doAfterTask();
		if(recorder != null) {
			recorder.rewriteTruthTime();
		}
		System.out.println("ImageDataReceiveThread : recorded");
	}

	@Override
	protected void close() {
		super.close();
		if(viewer != null) {
			viewer.close();
		}
		if(recorder != null) {
			recorder.close();
		}
		System.out.println("ImageDataReceiveThread : finished");
	}

}

