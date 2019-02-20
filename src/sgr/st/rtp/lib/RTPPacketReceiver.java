package sgr.st.rtp.lib;

import java.io.ByteArrayInputStream;
import java.net.DatagramSocket;

import sgr.st.thread.lib.ICallback;
import sgr.st.thread.lib.ThreadOperater;

/**
 * ノンブロッキングでRTPパケットを受信するクラス。
 * 受信したパケットはcallback関数を用いて処理する。
 *
 * @author satousuguru
 */
public class RTPPacketReceiver extends ThreadOperater<RTPReceiveThread>{
	/**
	 * 指定したSocketを使用して、
	 * RTPパケットを待ち受ける受信機を生成します
	 *
	 * @param socket RTPパケットを待ち受けるSocket
	 */
	public RTPPacketReceiver(DatagramSocket socket, ICallback<ByteArrayInputStream, Void> callback, int max_size) {
		super(new RTPReceiveThread(socket, callback, max_size));
	}

}
