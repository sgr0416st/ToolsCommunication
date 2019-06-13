package sgr.st.udp;

import java.io.IOException;
import java.net.SocketException;

import sgr.st.thread.RepeatedTaskThread;
import sgr.st.thread.exception.AbortTaskException;

/**
 *  データをリアルタイム送信するためのスレッドクラスです。
 *  1. doBeforeTask メソッドの実行時にsetTransmitter メソッドを実行して、UDP送信機を作成します。
 *	2. doRepeatedTask メソッドの実行時に、まず doRepeatedDataGeneratingTask メソッドによりデータを生成して、それを
 *		udp送信機へパケットを送ります。送信する前にデータ処理する場合は、
 *		doRepeatedDataGeneratingTask をオーバーライドしてください。
 *	3. doAfterTaskでは何もしません。
 *	4. stopThreadでは、終了命令を発行した後にUDP送信機を閉じます。その結果、このオブジェクトは現在のタスクが終了すると、
 *		doRepeatedTask の繰り返しを終了させます。
 *
 *	抽象メソッド doRepeatedDataGeneratingTask を実装するだけでこの抽象クラスを実装することができます。
 *	ただし、それにともなう初期化処理や終了処理等が必要になる場合は、適切なメソッドをオーバーライドすることで実現してください。
 *
 * @author satousuguru
 *
 */
public abstract class DataTransmitThread extends RepeatedTaskThread{
	protected UDPTransmitter transmitter;
	protected byte[] data;

	protected int destPort, myPort;
	protected String destIP;

	/**
	 * 指定した送信先ポート、送信先アドレス、送信元ポートを記憶します。
	 *
	 * @param destPort 送信先ポート番号
	 * @param destIP 送信先IPアドレス
	 * @param myPort 送信元ポート番号
	 */
	public DataTransmitThread(int destPort, String destIP, int myPort){
		super();
		this.destPort = destPort;
		this.destIP = destIP;
		this.myPort = myPort;
	}

	/**
	 * オブジェクト作成時に記憶した送信先ポート, 送信先IP、送信元ポートを用いてUDPデータ送信機を作成します。
	 * このメソッドは、doBeforeTask()メソッドの実行時に呼ばれます。
	 *
	 * @throws SocketException　UDP送信機の生成に失敗した時
	 */
	protected void setTransmitter() throws SocketException {
		transmitter = new UDPTransmitter(destPort, destIP, myPort);
	}

	/**
	 * 送信するデータを生成するための抽象メソッドです。
	 * このメソッドをオーバーライドすることで、使用者は送信データの生成が行えます。
	 *
	 * @return 送信するデータのバイナリ表現
	 */
	protected abstract byte[] doRepeatedDataGeneratingTask();


	@Override
	protected void doBeforeTask() {
		try {
			this.setTransmitter();
		} catch (SocketException e) {
			e.printStackTrace();
			this.stopThread();
		}
	}

	@Override
	protected void doRepeatedTask() throws AbortTaskException {
		try {
			data = doRepeatedDataGeneratingTask();
			transmitter.transmit(data);
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
		transmitter.close();
	}

	@Override
	protected void close() {
	}
}
