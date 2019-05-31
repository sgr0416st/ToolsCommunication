package sgr.st.udp.lib;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *　このクラスはUDPパケットを送信するためのクラスです。
 *
 * @author satousuguru
 *
 */
public class UDPTransmitter {

	private DatagramSocket socket;
	private InetSocketAddress address;

	/**
	 * 指定された送信先IPアドレス、送信先ポート番号から送信用ソケットを生成します。
	 *
	 * @param destIP 送信先IPアドレス
	 * @param destPort 送信先ポート番号
	 * @param myPort 送信先ポート番号
	 * @throws SocketException ソケットを開くことができなかった場合、
	 * または指定されたローカル・ポートにソケットをバインドできなかった場合。
	 * @throws UnknownHostException ローカル・ホスト名をアドレスに解決できなかった場合。
	 */
	public UDPTransmitter(String destIP, int destPort, int myPort) throws SocketException, UnknownHostException {
		socket = new DatagramSocket(myPort, InetAddress.getLocalHost());
		address = new InetSocketAddress(destIP, destPort);
	}

	/**
	 * 与えられたバイトストリームのデータからパケットを生成し、それを送信します。
	 * @param byteArrayOutputStream 送信するデータのバイトストリーム
	 * @throws IOException 入出力エラーが発生した場合。
	 */
	public void transmit(byte[] data) throws IOException {
		DatagramPacket packet = new DatagramPacket(data, data.length, address);
		this.socket.send(packet);
	}

	/**
	 * このデータグラム・ソケットを閉じます。
	 * このソケットのreceive(java.net.DatagramPacket)で現在ブロックされている
	 * すべてのスレッドがSocketExceptionをスローします。
	 * このソケットに関連するチャネルが存在する場合は、そのチャネルも閉じられます。
	 */
	public void close() {
		socket.close();
	}

}
