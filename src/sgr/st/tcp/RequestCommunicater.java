package sgr.st.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RequestCommunicater {

	private ServerSocket ss;
	private Socket socket;
	private OutputStream os;
	private DataOutputStream dos;
	private InputStream is;
	private DataInputStream dis;


	/**
	 * 初期化処理です。
	 */
	public RequestCommunicater(){
		ss = null;
		socket = null;
		is = null;
		dis = null;
		os = null;
		dos = null;
	}
	/**
	 * 指定したポート番号でサーバとして接続を待機します。
	 * 接続が開始されるまでブロックします。
	 * @param port 待ち受けするポート番号（１０００以上推奨）
	 * @return success 接続要求が受理されたら{@code <code>true</code>},それ以外は{@code <code>false</code>}
	 */
	public boolean connect(int port) {
		boolean success = true;

		try {
			ss = new ServerSocket(port);
			System.out.println("accept");
			socket = ss.accept(); //クライアントからの通信開始要求が来るまで待機

			// 以下、クライアントからの要求発生後
			is = socket.getInputStream(); //クライアントから数値を受信
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			success = false;
		}

		return success;
	}
	/**
	 * 与えられた接続先ホストのIPアドレスとポート番号で、クライアントとして接続を試みます。
	 * @param send_ip 接続先ホストのIPアドレス
	 * @param port 通信するポート番号
	 * @return success 接続要求が受理されたら{@code <code>true</code>},それ以外は{@code <code>false</code>}
	 */
	public boolean connect(String send_ip,int port) {
		boolean success = true;
		try {
			socket = new Socket(send_ip,port);
			socket.setKeepAlive(true);
			is = socket.getInputStream(); //クライアントから数値を受信
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			System.out.println("conection error");
		}

		return success;
	}


	/**
	 * connect に成功して、接続要求を受理した相手から実際にリクエストを受信します。
	 * @return request ストリームから取得したリクエストのバイナリデータを解析して、Requestクラスの形式に復元したデータ。
	 * @throws IOException ストリームからリクエストヘッダの読み取りに失敗した時。パラメータの抽出に失敗した時。
	 */
	public RecieveRequest receive() throws IOException{
		//最初に初期化
		RecieveRequest recieveRequest = new RecieveRequest();
		byte[] requestHeader,paramsData;
		requestHeader = new byte[4];
		paramsData = null;
		int readByte = 0;
		int readNow= 0;
		int size = 0;

		//リクエストのヘッダ部分からメタデータを読み取る（データサイズ、命令、追加パラメータの数）
		while(readByte < requestHeader.length){
			readNow = dis.read(requestHeader, readByte, requestHeader.length - readByte);
			if(readNow == -1){
				System.out.println("RecieveRequest error: EOF without command [COM_EXIT].");
				recieveRequest.setExitRequest();
				return null;
			}
			readByte += readNow;
		}

		if(readByte != requestHeader.length){	//読み取り失敗時
			throw new IOException("fail to read requestHeader");
		}

		//requestHeader からメタデータを抽出
		recieveRequest.extractMetaDataFrom(requestHeader);

		size = recieveRequest.getRequestSize();
		paramsData = new byte[size - 4];
		readByte = 0;
		//追加パラメータが記述されたデータの読み込み
		while(readByte < size-4){
			readByte += dis.read(paramsData, readByte, paramsData.length - readByte);
		}
		if(readByte != paramsData.length){	//読み取り失敗時
			System.out.println("readByte: " + readByte + "paramsData.length:" + paramsData.length);
			throw new IOException("fail to read params");
		}

		recieveRequest.extractParamsFrom(paramsData);
		return recieveRequest;
	}

	/**
	 *  connect に成功して、接続要求を受理した相手から実際にリクエストを受信します。
	 * @param request 最低でもリクエストサイズとコマンドが決められた形式で定義されたリクエスト
	 * @param flag 追加パラメータのデータ型．データ型の違う二種類以上の追加パラメータは扱えません．
	 * 追加パラメータがない場合は０を代入してください．
	 * @throws IOException
	 */
	public void send(Request request, byte flag) throws IOException{
		byte[] requestData = new SendRequest(request, flag).build();
		//requestの送信
		dos.write(requestData);
		dos.flush();
	}

	/**
	 *  connect に成功して、接続要求を受理した相手から実際にリクエストを受信します。
	 * @param request 最低でもリクエストサイズとコマンドが決められた形式で定義されたリクエスト
	 * @throws IOException
	 */
	public void send(Request request) throws IOException{
		byte[] requestData = new SendRequest(request).build();
		//requestの送信
		dos.write(requestData);
		dos.flush();
	}

	/**
	 * この入力ストリームのメソッドの次の呼出し側によって、
	 * ブロックせずにこの入力ストリームから読み込むことができる(またはスキップできる)推定バイト数を返します。
	 * 次の呼出し側は、同じスレッドの場合も別のスレッドの場合もあります。
	 * このような多数のバイトを1回で読み込んだりスキップしたりすることでブロックすることはありませんが、
	 * 読み込むまたはスキップするバイト数が少なくなることがあります。
	 * @return　ブロックせずにこの入力ストリームから読み込むことができる(またはスキップできる)推定バイト数。
	 */
	public int readable() {
		int result = -1;
		try {
			result = dis.available();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 終了処理です。このインスタンスが不要になった時、必ず呼び出してください。
	 */
	public void close() {
		try {
			if(dis != null)
				dis.close();
			if(dos != null)
				dos.close();
			if(ss != null)
				ss.close();
			if(socket != null)
				socket.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}



}
