package sgr.st._old.rtp.lib;

import java.io.ByteArrayOutputStream;
import java.net.DatagramSocket;

import sgr.st.thread.lib.ICallback;
import sgr.st.thread.lib.ThreadOperater;


public class RTPpacketSender extends ThreadOperater<RTPSendThread>{
	/**
	 * データをRTPパケットに加工し、
	 * 指定したソケットを使用して、指定した宛先IPアドレス、指定した宛先UDPポート番号へ送信するRTPパケット送信機を生成します
	 * RTPパケットは指定されたコールバック関数を通して受信します。
	 *
	 * @param socket RTPパケットを送信するソケット
	 * @param destIP 送信する相手のIPアドレス
	 * @param destPort 送信する相手のポート番号
	 * @param callback RTPパケットを受け取るコールバック関数。この関数はRTPパケットを受信したタイミングで、
	 * そのバイトストリームを引数として返されます。
	 *
	 */
	public RTPpacketSender(DatagramSocket socket,String destIP,String destPort,
			ICallback<Void, ByteArrayOutputStream> callback, int max_size)
	{
		super(new RTPSendThread(socket, destIP, destPort, callback, max_size));
	}

}
