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
	 * 指定されたポート番号から受信用ソケットと受信用パケットを生成します。
	 *
	 * @param myIP 受信に使うIPアドレス
	 * @param port 受信に使うポート番号
	 * @throws SocketException ソケットを開くことができなかった場合、
	 * または指定されたローカルポートにソケットをバインドできなかった場合。
	 * @throws UnknownHostException ローカル・ホスト名をアドレスに解決できなかった場合。
	 */
	public UDPReceiver(String myIP, int port) throws SocketException, UnknownHostException {
		this.buffer = new byte[MAX_BUFFER_SIZE];
		this.packet = new DatagramPacket(buffer,buffer.length);
		this.setSocket(port, InetAddress.getByName(myIP));
	}

	/**
	 * 指定されたポート番号から受信用ソケットと受信用パケットを生成します。
	 *
	 * @param port 受信に使うポート番号
	 * @throws SocketException ソケットを開くことができなかった場合、
	 * または指定されたローカルポートにソケットをバインドできなかった場合。
	 * @throws UnknownHostException ローカル・ホスト名をアドレスに解決できなかった場合。
	 */
	public UDPReceiver(int port) throws SocketException, UnknownHostException {
		this.setSocket(port,  InetAddress.getLocalHost());
		this.buffer = new byte[MAX_BUFFER_SIZE];
		this.packet = new DatagramPacket(buffer,buffer.length);
	}

	/**
	 * 初期化時にこのメソッドによってソケットがセットされます。
	 *
	 * @param port ソケットのポート
	 * @param addr ソケットのアドレス
	 * @throws SocketException
	 */
	protected void setSocket(int port, InetAddress addr) throws SocketException {
		socket = new DatagramSocket(port, addr);
	}

	/**
	 * UDPパケットを受信して、そのバイトストリームを返します。
	 *
	 * @return DatagramPacket 受信したパケット
	 * @throws IOException 入出力エラーが発生した場合。
	 */
	public DatagramPacket receivepacket() throws IOException {
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
		return this.receivepacket().getData();
	}

	/**
	 * このデータグラム・ソケットを閉じます。
	 * このソケットのreceive(java.net.DatagramPacket)で現在ブロックされている
	 * すべてのスレッドがSocketExceptionをスローします。
	 * このソケットに関連するチャネルが存在する場合は、そのチャネルも閉じられます。
	 *
	 */
	public void close() {
		socket.close();
	}

	public boolean isClosed() {
		return socket.isClosed();
	}

}
