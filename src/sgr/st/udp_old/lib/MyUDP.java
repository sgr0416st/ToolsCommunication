package sgr.st.udp_old.lib;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class MyUDP {

	public static final int DEF_PORT = 5000;
	public static final int MAX_UDP_COMMUNICATION = 60000;


	protected DatagramSocket ss;
	protected DatagramPacket packet;
	protected int myPort;
	protected int size;
	protected byte[] data;

	/**
	 * 与えられたポート番号、データサイズをもとにソケット、データグラムを作成します。
	 * データサイズは可変です。
	 * @param myport 接続に使用するポート番号
	 * @param size　送受信するデータサイズ
	 */
	public MyUDP(int myport,int size) {
		// TODO 自動生成されたコンストラクター・スタブ
		ss = null;
		packet = null;
		setSocket(myport);
		setSize(size);
	}


	/**
	 * 与えられたポート番号をもとにソケットを作成します。
	 * オブジェクト生成時に自動で呼び出されます。
	 * @param myPort
	 */
	private void setSocket(int myPort){
		this.myPort = myPort;
		try {
			ss = new DatagramSocket(this.myPort);
		} catch (SocketException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * 送受信するデータサイズを変更します。
	 * @param size
	 */
	public void setSize(int size){
		this.size = size;
		data = new byte[size];
		packet = new DatagramPacket(data,size);
	}

	/**
	 * UDPコネクターを閉じます。
	 * コネクターを破棄する前に必ず使用してください。
	 */
	public void close() {
		if(!ss.isClosed()){
			ss.close();
		}
	}
}
