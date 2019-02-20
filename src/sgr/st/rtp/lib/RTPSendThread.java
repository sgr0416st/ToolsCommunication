package sgr.st.rtp.lib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Random;

import sgr.st.data.NetworkData;
import sgr.st.thread.lib.ICallback;
import sgr.st.thread.lib.OperatableThread;

public class RTPSendThread extends OperatableThread{
	private short sequenceNum;		// シーケンス番号
	private int timeStamp;			// タイムスタンプ
	private int syncSourceId;		// 同期ソースID
	private byte marker;			// マーカービット

	private DatagramSocket socket;
	private String destIP;
	private String destPort;
	private ICallback<Void, ByteArrayOutputStream> callback;
	private int max_size;
	private ByteArrayOutputStream byteStream;
	private byte[] rtpPacket;
	private InetSocketAddress address;
	private DatagramPacket packet;

	// コンストラクタ
	public RTPSendThread(DatagramSocket socket,String destIP,String destPort,
			ICallback<Void, ByteArrayOutputStream> callback, int max_size) {
		this.socket = socket;
		this.destIP = destIP;
		this.destPort = destPort;
		this.callback = callback;
		this.max_size = max_size;

		// Init RTP Headerstop でたらめ
		Random r = new Random();
		this.sequenceNum = 0;
		this.timeStamp = 0;
		this.syncSourceId = r.nextInt();
		this.marker = -128;

		address = new InetSocketAddress(this.destIP,Integer.parseInt(this.destPort));
		packet = null;
	}

	@Override
	public synchronized void start() {
		// TODO 自動生成されたメソッド・スタブ
		super.start();
	}

	@Override
	protected void doRepeatedTask() {
		rtpPacket = new byte[this.max_size + NetworkData.RTP_HEADER_SIZE];
		try {
			// RTPヘッダーを付ける
			byteStream = this.callback.callback(null);
			rtpPacket = this.addRtpHeader(byteStream);
			packet = new DatagramPacket(rtpPacket,rtpPacket.length,address);
			// 相手へ送信
			this.socket.send(packet);
		}catch(Exception ee) {
			ee.printStackTrace();
		}
	}


	/**
	 * データストリームにRTPヘッダーを追加し、できあがったRTPパケットを返します
	 * @param stream
	 * @return RTPヘッダを持つパケット
	 * @throws IOException
	 */
	private byte[] addRtpHeader(ByteArrayOutputStream stream) throws IOException {
		byte[] rtpHeader = new byte[12];	// RTPヘッダ
		byte version	= -128;				// バージョン番号10000000
		byte padding	= 0;				// パディング
		byte extention	= 0;				// 拡張ビット
		byte contribute	= 0;				// コントリビュートカウント
		byte payload	= 0;				// ペイロードタイプ

		// RTPヘッダーの生成
		rtpHeader[0]  = (byte)(version | padding | extention | contribute);
		rtpHeader[1]  = (byte)(marker | payload);
		rtpHeader[2]  = (byte)(this.sequenceNum >> 8);
		rtpHeader[3]  = (byte)(this.sequenceNum >> 0);
		rtpHeader[4]  = (byte)(this.timeStamp >> 24);
		rtpHeader[5]  = (byte)(this.timeStamp >> 16);
		rtpHeader[6]  = (byte)(this.timeStamp >>  8);
		rtpHeader[7]  = (byte)(this.timeStamp >>  0);
		rtpHeader[8]  = (byte)(this.syncSourceId >> 24);
		rtpHeader[9]  = (byte)(this.syncSourceId >> 16);
		rtpHeader[10] = (byte)(this.syncSourceId >>  8);
		rtpHeader[11] = (byte)(this.syncSourceId >>  0);

		// シーケンス番号、タイムスタンプ、マーカービット移行
		this.sequenceNum ++;
		this.timeStamp += this.max_size;
		if(this.marker == -128)
			this.marker = 0;

		// RTPヘッダー＋生データ = RTPパケット
		ByteArrayOutputStream out = new ByteArrayOutputStream(this.max_size + NetworkData.RTP_HEADER_SIZE);
		out.write(rtpHeader, 0, NetworkData.RTP_HEADER_SIZE);
		stream.writeTo(out);

		return out.toByteArray();
	}


}
