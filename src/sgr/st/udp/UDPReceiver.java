package sgr.st.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * このクラスはUDPパケットを受信するためのクラスです。
 *
 * @author satousuguru
 *
 */
public class UDPReceiver{

	protected static final int MAX_BUFFER_SIZE = 60000;
	protected DatagramSocket socket;
	private byte[] buffer;
	private DatagramPacket packet;

	/**
	 * 指定されたポート番号とIPアドレスから受信用ソケットと受信用パケットを生成します。
	 *
	 * @param myPort 受信に使うポート番号
	 * @param myIP 受信に使うIPアドレス
	 * @throws SocketException ソケットを開くことができなかった場合、
	 * または指定されたローカルポートにソケットをバインドできなかった場合、
	 * またはローカル・ホスト名をアドレスに解決できなかった場合。
	 */
	public UDPReceiver(int myPort, String myIP) throws SocketException {
		this.buffer = new byte[MAX_BUFFER_SIZE];
		this.packet = new DatagramPacket(buffer,buffer.length);
		if(!this.setSocket(myPort, myIP)) {
			throw new SocketException();
		}
	}

	/**
	 * 指定されたポート番号から受信用ソケットと受信用パケットを生成します。
	 *
	 * @param myPort 受信に使うポート番号
	 * @throws SocketException ソケットを開くことができなかった場合、
	 * または指定されたローカルポートにソケットをバインドできなかった場合、
	 */
	public UDPReceiver(int myPort) throws SocketException {
		this(myPort, null);
	}

	/**
	 * コンストラクタで呼び出される、ソケットを準備するメソッド。
	 *
	 * @param myPort
	 * @param myIP
	 * @return ソケットの生成に成功したらtrue, 失敗したらfalse
	 */
	protected boolean setSocket(int myPort, String myIP){
		try {
			if(myIP == null) {
				socket = new DatagramSocket(myPort);
			}else {
				socket = new DatagramSocket(myPort, InetAddress.getByName(myIP));
			}
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (UnknownHostException e) {
			// TODO 自動生成された catch ブロック
			return false;
		}
		return true;
	}

	/**
	 * UDPパケットを受信して、そのバイトストリームを返します。
	 *
	 * @return DatagramPacket 受信したパケット
	 * @throws IOException 入出力エラーが発生した場合。
	 */
	public DatagramPacket receivePacket() throws IOException {
		this.socket.receive(packet);
		return packet;
	}

	/**
	 * UDPパケットを受信して、そのバイトストリームを返します。
	 *
	 * @return byteArrayInputStream 受信したパケットのバイトストリーム。
	 * @throws IOException 入出力エラーが発生した場合。
	 */
	public byte[] receive() throws IOException {
		return this.receivePacket().getData();
	}

	/**
	 * このデータグラム・ソケットを閉じます。
	 * このソケットのreceive(java.net.DatagramPacket)で現在ブロックされている
	 * すべてのスレッドがSocketExceptionをスローします。
	 * このソケットに関連するチャネルが存在する場合は、そのチャネルも閉じられます。
	 *
	 */
	public void close() {
		if(socket != null) {
			socket.close();
		}
	}

	public boolean isClosed() {
		return socket.isClosed();
	}

}
