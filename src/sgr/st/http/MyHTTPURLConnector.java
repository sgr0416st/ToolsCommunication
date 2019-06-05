package sgr.st.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @author s.suguru
 *このクラスは、HTTP通信を簡易的に実装するためのクラスです。
 *
 *一般的に、HTTP通信では以下の手順を踏みます。
 *1.URL に対して openConnection メソッドを呼び出すことによって、接続オブジェクトを生成する
 *2.セットアップパラメータと一般要求プロパティーを操作する
 *3.connect メソッドを使用して、リモートオブジェクトへの実際の接続を確立する
 *4.リモートオブジェクトが使用可能になる。リモートオブジェクトのヘッダーフィールドと内容にアクセスできるようになる
 *
 *　1 はこのクラスのインスタンス作成時にURLを指定する、またはsetURLメソッドにて実行されます。
 *　2 の実行には、setPropertiesメソッドを呼び出してください。
 *　3 の実行には、connectメソッドを呼び出してください。
 *その後 get~メソッド,　write~メソッド 等を用いて、データの通信を行います。
 *最後に　close　メソッドを必ず使用してください。
 *
 *まとめると、
 *インスタンス生成 -> setProperties メソッド -> connect -> write~メソッド , get~メソッド -> close
 *の手順で実行してください。
 * */
public class MyHTTPURLConnector {

	public static final String TAG = "MyHttpURLConnector";

	public static final String TEXT_HTML = "text/html";
	public static final String TEXT_PLAIN = "text/plain";
	public static final String IMAGE_PNG = "image/png";
	public static final String IMAGE_JPG = "image/jpg";
	public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
	public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

	public static final int URLCONNECTION_CLOSED = 0;
	public static final int URLCONNECTION_OPENED = 1;
	public static final int URLCONNECTION_PREPARED_PROPERTIES = 2;
	public static final int URLCONNECTION_CONNECTED = 3;

	public static final int OUTPUTSTREAM__CLOSED = 0;
	public static final int OUTPUTSTREAM__RAW = 1;
	public static final int OUTPUTSTREAM__PRINTWRITER = 2;
	public static final int INPUTSTREAM__CLOSED = 0;
	public static final int INPUTSTREAM__RAW = 1;
	public static final int INPUTSTREAM__BUFFEREDREADER = 2;


	private int state_URLConnection;
	private int state_OutputStream;
	private int state_InputStream;


	protected HttpURLConnection urlconn;

	//画像ファイル読み込みストリーム
	protected DataInputStream dis;
	//送信ストリーム
	protected OutputStream os;

	//レスポンス（body）の読み取り用バッファ
	protected StringBuffer strbff;
	//レスポンス（body）の読み取り用リーダー
	protected BufferedReader reader;

	PrintWriter pw;


	/**
	 * 接続オブジェクトを生成し、URLコネクションをオープンします。
	 * @param url　接続先のURL
	 * @throws IOException　指定したURLが接続不可能な場合
	 */
	public MyHTTPURLConnector(URL url) throws IOException {
		state_URLConnection = URLCONNECTION_CLOSED;
		dis = null;
		os = null;
		pw = null;
		state_OutputStream = OUTPUTSTREAM__CLOSED;
		state_InputStream = INPUTSTREAM__CLOSED;
		setURL(url);
	}

	public MyHTTPURLConnector() throws IOException {
		this(null);
	}

	/**
	 * 通信先のURLを指定します。URLがnullの場合は例外を出さず、何もせずに返ります。
	 * すでにほかのURLを指定している場合、それを変更せずに新しいURLは破棄されます。
	 * @param url　通信先のURL
	 * @throws IOException　openConnectionが例外を投げた時
	 */
	public void setURL(URL url) throws IOException {
		if(url == null) {
			System.out.println("url is null !!");
			return;
		}
		switch (state_URLConnection) {
		case URLCONNECTION_CLOSED:
			urlconn = (HttpURLConnection)url.openConnection();
			state_URLConnection = URLCONNECTION_OPENED;
			break;
		default:
			System.err.println("error: URLはすでに指定されています");
			break;
		}
		return;
	}

	/**
	 * 接続前の各種リクエストプロパティの設定をします。
	 * HTMLのプロトコルに準拠した記法で引数を指定してください。
	 * 現在指定できるリクエストメソッドは "GET" または "POST" です。
	 * @param RequestMethod HTTPのリクエストメソッド。現在指定できるメソッドは "GET" または "POST" です。
	 * @param contentsType　送信するデータのコンテンツタイプ。データを送信しない場合は無視されるためnullでよい。
	 * @param acceptLangage　優先的に選択される（自然）言語
	 * @throws IOException setRequestMethodが失敗したときまたは出力ストリームの取得に失敗したとき
	 */
	public void setProperties(String RequestMethod, String contentsType, String acceptLangage) throws IOException {
		switch (state_URLConnection) {
		case URLCONNECTION_CLOSED:
			System.err.println("error: URLがオープンされていません");
			break;
		case URLCONNECTION_OPENED:
		case URLCONNECTION_PREPARED_PROPERTIES:
			urlconn.setRequestMethod(RequestMethod);
			if(RequestMethod.equals("POST")) {
				urlconn.setDoOutput(true);
				urlconn.setDoInput(true);
				urlconn.setRequestProperty("Content-Type", contentsType);
			}
			urlconn.setRequestProperty("Accept-Language", acceptLangage);
			//urlconn.setInstanceFollowRedirects(false);
			state_URLConnection = URLCONNECTION_PREPARED_PROPERTIES;
			break;
		case URLCONNECTION_CONNECTED:
		default:
			System.err.println("error: すでに接続完了しているためプロパティは変更できません。");
			break;
		}
	}

	/**
	 * 接続前の各種リクエストプロパティの設定をします。
	 * HTMLのプロトコルに準拠した記法で引数を指定してください。
	 * 現在指定できるリクエストメソッドは "GET" または "POST" です。
	 * @param RequestMethod HTTPのリクエストメソッド。現在指定できるメソッドは "GET" または "POST" です。
	 * @param contentsType　送信するデータのコンテンツタイプ。データを送信しない場合は無視されるためnullでよい。
	 * @throws IOException setRequestMethodが失敗したときまたは出力ストリームの取得に失敗したとき
	 */
	public void setProperties(String RequestMethod, String contentsType) throws IOException {
		setProperties(RequestMethod, contentsType, "ja;q=0.7,en;q=0.3");
	}

	/**
	 * 設定をもとに実際の接続を確立します。
	 * このメソッドを使用する前にユーザは用意されているいずれかのcall~ メソッドを呼び出し、
	 * リクエストの種類を確定させている必要があります。
	 * @throws IOException　設定が失敗したとき
	 */
	public void connect() throws IOException{
		switch (state_URLConnection) {
		case URLCONNECTION_PREPARED_PROPERTIES:
			urlconn.connect();
			state_URLConnection = URLCONNECTION_CONNECTED;
			break;
		default:
			System.err.println("error: 接続できる状態ではありません。");
			break;
		}
	}

	/**
	 * 指定されたバイト配列の data.length バイトを出力ストリームに書き込みます。
	 * @param data データ
	 * @throws IOException
	 */
	public void write(byte[] data) throws IOException {
		switch (state_URLConnection) {
		case URLCONNECTION_CONNECTED:
			switch (state_OutputStream) {
			case OUTPUTSTREAM__CLOSED:
				os = urlconn.getOutputStream();
				state_OutputStream = OUTPUTSTREAM__RAW;
				//break;	ブレークしない
			case OUTPUTSTREAM__RAW:
				os.write(data);
				os.flush();
				break;
			default:
				System.out.println("error: このストリームはラップされています");
				break;
			}
			break;
		default:
			System.out.println("error: 接続が確立されていません。");
			break;
		}
	}

	/**
	 * 指定したファイルを読み込み、それを出力ストリームに書き込みます。
	 * @param ｆile
	 * @return
	 * @throws IOException
	 */
	public int writeFile(File ｆile) throws IOException {
		int readByte=0, totalByte = 0, restByte = 0;

		restByte = (int)ｆile.length();
		byte[] buffer = new byte[restByte];
		//画像ファイル→データ配列変換用ストリーム
		dis = new DataInputStream( new BufferedInputStream( new FileInputStream(ｆile)));
		do{
			readByte = dis.read(buffer, totalByte, restByte);
			totalByte += readByte;
			restByte -= readByte;
		}while(readByte != -1 && readByte != 0);

		write(buffer);
		return totalByte;
	}

	/**
	 * 指定したテキストを出力ストリームに書き込みます。
	 * @param text
	 * @throws IOException
	 */
	public void writeText(String text) throws IOException {
		switch (state_URLConnection) {
		case URLCONNECTION_CONNECTED:
			switch (state_OutputStream) {
			case OUTPUTSTREAM__CLOSED:
				os = urlconn.getOutputStream();
				pw = new PrintWriter(os);
				state_OutputStream = OUTPUTSTREAM__PRINTWRITER;
				//break;	ブレークしない
			case OUTPUTSTREAM__PRINTWRITER:
				pw.write(text);
				pw.flush();
				break;
			default:
				System.out.println("error: このストリームはプリントライターとして使えません");
				break;
			}
			break;
		default:
			System.out.println("error: 接続が確立されていません。");
			break;
		}

	}
	/**
	 * 指定したマップのキーと要素をURL形式に変更して出力ストリームに書き込みます。
	 * @param text
	 * @throws IOException
	 */
	public void writeTextMap(Map<String, String> textMap) throws IOException {
		StringBuffer strbff = new StringBuffer();
		String temp1,temp2 = null;
		for(Map.Entry<String, String> e : textMap.entrySet()) {
			temp1 =e.getKey() + "=" + e.getValue();
			if(temp2 != null){
				strbff.append(temp2);
				strbff.append("&");
			}
			temp2 = temp1;
		}
		strbff.append(temp2);
		writeText(strbff.toString());
	}

	/**
	 * connect メソッドを使用した後のレスポンスコードを返します。
	 * connect メソッドを用いる前に使用した場合は-1が返ります。
	 * @return　通信した結果のレスポンスコード
	 * @throws IOException
	 */
	public int getResponseCode() throws IOException{
		switch (state_URLConnection) {
		case URLCONNECTION_CONNECTED:
			return urlconn.getResponseCode();
		default:
			System.err.println("error: getResponseCode");
			return -1;
		}
	}

	/**
	 * connect メソッドを使用した後のレスポンスコードを返します。
	 * connect メソッドを用いる前に使用した場合はnullが返ります。
	 * @return　通信した結果のレスポンスコード
	 * @throws IOException
	 */
	public String getResponseMessage() throws IOException{
		switch (state_URLConnection) {
		case URLCONNECTION_CONNECTED:
			return urlconn.getResponseMessage();
		default:
			System.err.println("error: getResponseMessage");
			return null;
		}
	}

	public InputStream getInputStream() throws IOException{
		InputStream in = null;
		switch (state_URLConnection) {
		case URLCONNECTION_CONNECTED:
			switch (state_InputStream) {
			case INPUTSTREAM__CLOSED:
				in = urlconn.getInputStream();
				state_InputStream = INPUTSTREAM__RAW;
				break;
			default:
				System.err.println("error: InputStreamは他で使用されています");
				break;
			}
			break;
		default:
			System.err.println("error: URLコネクションが接続されていません");
		}
		return in;
	}

	/**
	 *接続した結果得られたレスポンス全体をテキスト形式で入手します。
	 * @return　レスポンスのテキストデータ
	 * @throws IOException
	 */
	public String getTextResponse() throws IOException{
		strbff = new StringBuffer("");
		String line = null;
		switch (state_URLConnection) {
		case URLCONNECTION_CONNECTED:
			switch (state_InputStream) {
			case INPUTSTREAM__CLOSED:
				reader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
				state_InputStream = INPUTSTREAM__BUFFEREDREADER;
				//break;
			case INPUTSTREAM__BUFFEREDREADER:
				while((line = reader.readLine()) != null) {
					strbff.append(line);
				}
				reader.close();
				state_InputStream = INPUTSTREAM__CLOSED;
				break;
			default:
				System.out.println("このストリームはBufferedReaderではありません");
				break;
			}
			break;
		default:
		}
		return strbff.toString();
	}

	/**
	 * URLConnectorの状態を返します。
	 * @return status このオブジェクトの状態
	 */
	public int getStatus(){
		return state_URLConnection;
	}

	/**
	 * すべてのストリームをクローズして、リソースをすべて開放します。
	 * ユーザは最後に必ずこの処理を実行してください。
	 */
	public void close() throws IOException{
		if(dis != null){
			dis.close();
		}
		if(reader != null){
			reader.close();
		}
		if(pw != null){
			pw.close();
		}
		if(os != null){
			os.close();
		}
		urlconn.disconnect();
		state_URLConnection = URLCONNECTION_CLOSED;
	}

}