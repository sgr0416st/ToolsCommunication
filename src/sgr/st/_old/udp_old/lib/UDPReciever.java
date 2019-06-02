package sgr.st._old.udp_old.lib;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class UDPReciever extends MyUDP{

	/**
	 * 与えられた情報から、待ち受けポートと受信用パケットを生成します。
	 * @param myport　待ち受けポート番号
	 * @param size　パケットサイズ
	 */
	public UDPReciever(int myport,int size) {
		// TODO 自動生成されたコンストラクター・スタブ
		super(myport,size);
	}
	/**
	 * 与えられた情報から、待ち受けポートと受信用パケットを生成します。
	 * 通信で受け取れる最大のサイズでバッファを生成します。
	 * @param myport　待ち受けポート番号
	 */
	public UDPReciever(int myport) {
		// TODO 自動生成されたコンストラクター・スタブ
		this(myport,MyUDP.MAX_UDP_COMMUNICATION);
	}

	/**
	 * １つのデータグラムをudpによって受信します。
	 * @return　受信したパケット
	 * @throws IOException
	 */
	public byte[] recive() throws IOException {
		ss.receive(packet);
		return packet.getData();
	}

	/**
	 * 画像データグラムを受信します。
	 * 受信したデータグラムはBufferedImageに格納されます。
	 * @return　受信画像
	 * @throws IOException
	 */
	public BufferedImage reciveImage() throws IOException {
		BufferedImage image = null;
		image = ImageIO.read( new ByteArrayInputStream( recive() ) );
		return image;
	}
}
