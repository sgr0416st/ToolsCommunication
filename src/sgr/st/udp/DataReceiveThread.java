package sgr.st.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import sgr.st.thread.RepeatedTaskThread;
import sgr.st.thread.exception.AbortTaskException;

/**
 *  データをリアルタイム受信するためのスレッドです。
 *  1. doBeforeTask メソッドの実行時にsetReciever メソッドを実行して、UDP受信機を作成します。
 *	2. doRepeatedTask メソッドの実行時にudp受信機からパケットを受け取り、そのデータを
 *		doRepeatedDataProcessingTask メソッドに渡します。受信したデータを利用する場合は、
 *		doRepeatedDataProcessingTask をオーバーライドしてください。
 *	3. doAfterTaskでは何もしません。
 *	4. stopThreadでは、終了命令を発行した後にUDP受信機を閉じます。その結果、
 *		doRepeatedTask メソッド内で UDP受信機が受信待ちだった場合は、 IOException がスローされ、これにより
 *		アンブロッキングすることで doRepeatedTask の繰り返しを終了させます。
 *
 *	抽象メソッド doRepeatedDataProcessingTask を実装するだけでこの抽象クラスを実装することができます。
 *	ただし、それにともなう初期化処理や終了処理等が必要になる場合は、適切なメソッドをオーバーライドすることで実現してください。
 *
 * @author satousuguru
 *
 */
public abstract class DataReceiveThread extends RepeatedTaskThread{
	protected UDPReceiver receiver;
	protected DatagramPacket packet;

	protected int myPort;
	protected String myIP;

	/**
	 * 指定したポート番号とIPアドレスを記憶します。
	 *
	 * @param myPort 受信に使うポート番号
	 * @param myIP 受信するIPアドレス
	 */
	public DataReceiveThread(int myPort, String myIP) {
		super();
		this.myPort = myPort;
		this.myIP = myIP;
	}

	/**
	 * 指定したポート番号を記憶します。
	 *
	 * @param myPort 受信に使うポート番号
	 */
	public DataReceiveThread(int myPort) {
		this(myPort, null);
	}

	/**
	 * オブジェクト作成時に記憶したポート, IPを用いてUDPデータ受信機を作成します。
	 * このオブジェクトの生成時にIPが指定されていなかった場合、システムはローカルホストの使用を試みます。
	 * このメソッドは、doBeforeTask()メソッドの実行時に呼ばれます。
	 *
	 * @throws SocketException 受信機の作成に失敗した時
	 */
	protected void setReceiver() throws SocketException {
		receiver = new UDPReceiver(myPort, myIP);
	}

	/**
	 * 受信したデータとデータサイズを利用して、データ処理をするための抽象メソッドです。
	 * このメソッドをオーバーライドすることで、使用者は受信データの処理が行えます。
	 *
	 * @param data 受信データのバイナリ表現
	 * @param size 受信データのサイズ
	 */
	protected abstract void doRepeatedDataProcessingTask(byte[] data, int size);


	@Override
	protected void doBeforeTask() {
		try {
			this.setReceiver();
		} catch (SocketException e) {
			e.printStackTrace();
			this.stopThread();
		}
	}

	@Override
	protected final void doRepeatedTask() throws AbortTaskException{
		try {
			packet = receiver.receivePacket();
			doRepeatedDataProcessingTask(packet.getData(), packet.getLength());
		} catch (IOException e) {
			if(!this.isStopped()) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doAfterTask() {
	}

	@Override
	public void stopThread() {
		super.stopThread();
		if(receiver != null) {
			receiver.close();
		}
	}

	@Override
	protected void close() {
	}

}
