package sgr.st.rtp.lib;

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import sgr.st.data.NetworkData;
import sgr.st.thread.lib.ICallback;
import sgr.st.thread.lib.OperatableThread;

public class RTPReceiveThread extends OperatableThread {
	private DatagramSocket socket;
	private ICallback<ByteArrayInputStream, Void> callback;
	private ByteArrayInputStream byteArrayInputStream;
	private byte[] buffer;
	private DatagramPacket packet;
	private int max_size;

	public RTPReceiveThread(DatagramSocket socket, ICallback<ByteArrayInputStream, Void> callback, int max_size) {
		super();
		this.socket = socket;
		this.callback = callback;
		this.max_size = max_size;
		this.buffer = new byte[this.max_size + NetworkData.RTP_HEADER_SIZE];
		this.packet = new DatagramPacket(buffer,buffer.length);
	}

	@Override
	protected void doRepeatedTask() {
		try {
			this.socket.receive(packet);
			// RTPヘッダーを読み飛ばす
			 byteArrayInputStream = new ByteArrayInputStream(
					packet.getData(),
					NetworkData.RTP_HEADER_SIZE,
					this.max_size
					);
			this.doCallback(byteArrayInputStream);
		}
		catch(SocketException e) {
			String s = e.getMessage();
			if(s.equals("Socket closed")) {
				System.out.println(s);
			}else {
				e.printStackTrace();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected void doCallback(ByteArrayInputStream byteArrayInputStream) {
		this.callback.callback(byteArrayInputStream);
	}

	@Override
	public synchronized void close() {
		super.close();
		socket.close();
	}
}
