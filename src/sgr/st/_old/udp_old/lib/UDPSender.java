package sgr.st._old.udp_old.lib;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;


public class UDPSender extends MyUDP{

	protected InetAddress sendAddr;
	protected int companionPort;


	/**
	 * 自分のポート番号、データサイズ、送信先IPアドレス、送信先ポート情報をもとに通信路とパケットを確保します。
	 * @param myPort　自分のポート番号
	 * @param size　送信データサイズ
	 * @param sendAddress　送信先IPアドレス
	 * @param companionPort　送信先ポート番号
	 * @throws UnknownHostException
	 */
	public UDPSender(int myPort, int size ,String sendAddress,int companionPort) throws UnknownHostException {
		// TODO 自動生成されたコンストラクター・スタブ
		super(myPort,size);
		this.companionPort = companionPort;
		setSendAddress(sendAddress);
		packet = new DatagramPacket(data, size, sendAddr, companionPort);
	}

	/**
	 * 自分のポート番号、送信先IPアドレス、送信先ポート情報をもとに通信路とパケットを確保します。
	 * データサイズは最大画像サイズ
	 * @param myPort　自分のポート番号
	 * @param sendAddress　送信先IPアドレス
	 * @param companionPort　送信先ポート番号
	 * @throws UnknownHostException
	 */
	public UDPSender(int myPort,String sendAddress,int companionPort) throws UnknownHostException {
		this(myPort, MyUDP.MAX_UDP_COMMUNICATION, sendAddress, companionPort);
	}

	/**
	 * 送信先アドレスを設定します。
	 * @param strAddress
	 * @throws UnknownHostException
	 */
	protected void setSendAddress(String strAddress) throws UnknownHostException{
		sendAddr = InetAddress.getByName(strAddress);
	}

	/**
	 * バイナリデータをudpで送信します。
	 * @param data 送信するデータのバイナリ
	 * @throws IOException ss.send(packet);が例外を投げたとき
	 */
	public void send(byte[] data) throws IOException {
		packet.setData(data);
		ss.send(packet);
	}

	/**
	 * 画像データグラムを送信します。
	 * @param image 送信画像
	 * @param format 拡張子(png, jpeg等)
	 * @throws IOException ImageIO.write( image, format, os );が例外をスローしたとき
	 */
	public void sendImage(BufferedImage image, String format) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedOutputStream os = new BufferedOutputStream(bos);
		image.flush();
		ImageIO.write( image, format, os );
		data = bos.toByteArray();
		send(data);
	}
}
