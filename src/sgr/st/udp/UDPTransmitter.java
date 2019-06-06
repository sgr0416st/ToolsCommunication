package sgr.st.udp;

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

	protected DatagramSocket socket;
	private InetSocketAddress address;

	/**
	 * 指定された送信先IPアドレス、送信先ポート番号から送信用ソケットを生成します。
	 *
	 * @param destPort 送信先ポート番号
	 * @param destIP 送信先IPアドレス
	 * @param myPort 送信元ポート番号
	 * @param myIP 送信元IPアドレス
	 * @throws SocketException ソケットを開くことができなかった場合、
	 * または指定されたローカル・ポートにソケットをバインドできなかった場合、
	 * またはローカル・ホスト名をアドレスに解決できなかった場合。
	 */
	public UDPTransmitter(int destPort, String destIP,  int myPort, String myIP) throws SocketException{
		if(!this.setSocket(myPort, myIP)) {
			throw new SocketException();
		}
		address = new InetSocketAddress(destIP, destPort);
	}

	/**
	 * 指定された送信先IPアドレス、送信先ポート番号から送信用ソケットを生成します。
	 *
	 * @param destPort 送信先ポート番号
	 * @param destIP 送信先IPアドレス
	 * @param myPort 送信元ポート番号
	 * @throws SocketException ソケットを開くことができなかった場合、
	 * または指定されたローカル・ポートにソケットをバインドできなかった場合、
	 * またはローカル・ホスト名をアドレスに解決できなかった場合。
	 */
	public UDPTransmitter(int destPort, String destIP, int myPort) throws SocketException {
		this(destPort, destIP, myPort, null);
	}

	/**
	 * 与えられたポート、IPを用いてソケットを初期化するメソッド
	 *
	 * @param myPort 指定したポートでソケットを開く
	 * @param myIP nullであればIPを指定せず呼び出す
	 * @return ソケットを開くことができなかった場合、
	 * または指定されたローカル・ポートにソケットをバインドできなかった場合、
	 * またはローカル・ホスト名をアドレスに解決できなかった場合。	 */
	protected boolean setSocket(int myPort, String myIP){
		try {
			if(myIP == null) {
				socket = new DatagramSocket(myPort);
			}else {
				socket = new DatagramSocket(myPort, InetAddress.getByName(myIP));
			}
		} catch (SocketException se) {
			se.printStackTrace();
			return false;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
		return true;
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
