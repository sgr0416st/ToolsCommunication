package sgr.st.udp.lib;

import java.io.ByteArrayInputStream;
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

	private DatagramSocket socket;
	private ByteArrayInputStream byteArrayInputStream;
	private byte[] buffer;
	private DatagramPacket packet;

	/**
	 * 指定されたポート番号から受信用ソケットと受信用パケットを生成します。
	 *
	 * @param port 受信に使うポート番号
	 * @throws SocketException ソケットを開くことができなかった場合、
	 * または指定されたローカルポートにソケットをバインドできなかった場合。
	 * @throws UnknownHostException ローカル・ホスト名をアドレスに解決できなかった場合。
	 */
	public UDPReceiver(int port) throws SocketException, UnknownHostException {
		socket = new DatagramSocket(port, InetAddress.getLocalHost());
		this.buffer = new byte[Settings.MAX_BUFFER.getSize()];
		this.packet = new DatagramPacket(buffer,buffer.length);
	}

	/**
	 * UDPパケットを受信して、そのバイトストリームを返します。
	 *
	 * @return byteArrayInputStream 受信したパケットのバイトストリーム。
	 * @throws IOException 入出力エラーが発生した場合。
	 */
	public ByteArrayInputStream receive() throws IOException {
		this.socket.receive(packet);
		byteArrayInputStream = new ByteArrayInputStream(packet.getData());
		return byteArrayInputStream;
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
