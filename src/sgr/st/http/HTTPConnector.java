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

public class HTTPConnector {


	public static final String TAG = "HTTPConnector";

	public static final String TEXT_HTML = "text/html";
	public static final String TEXT_PLAIN = "text/plain";
	public static final String IMAGE_PNG = "image/png";
	public static final String IMAGE_JPG = "image/jpg";
	public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
	public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";


	protected HttpURLConnection urlconn;

	protected boolean isPostReady = false;

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
	 * @param url 接続先のURL
	 * @throws IOException 指定したURLが接続不可能な場合
	 */
	public HTTPConnector(URL url, Map<String, String> headers) throws IOException {
		dis = null;
		os = null;
		pw = null;
		setURL(url);
		if(headers != null) {
			setProperties(headers);
		}
	}

	/**
	 * 接続オブジェクトを生成し、URLコネクションをオープンします。
	 * @param url 接続先のURL
	 * @throws IOException 指定したURLが接続不可能な場合
	 */
	public HTTPConnector(String strURL, Map<String, String> headers) throws IOException {
		this(new URL(strURL), headers);
	}

	/**
	 * 接続オブジェクトを生成し、URLコネクションをオープンします。
	 * @param url 接続先のURL
	 * @throws IOException 指定したURLが接続不可能な場合
	 */
	public HTTPConnector(URL url) throws IOException {
		this(url, null);
	}

	/**
	 * 通信先のURLを指定します。URLがnullの場合は例外を出さず、何もせずに返ります。
	 * すでにほかのURLを指定している場合、それを変更せずに新しいURLは破棄されます。
	 * @param url 通信先のURL
	 * @throws IOException openConnectionが例外を投げた時
	 */
	private void setURL(URL url) throws IOException {
		urlconn = (HttpURLConnection)url.openConnection();
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
	public void setProperties(Map<String, String> headers) throws IOException {
		for(Map.Entry<String, String> entry : headers.entrySet()) {
			urlconn.setRequestProperty(entry.getKey(), entry.getValue());
		}
	}

	public String get() throws IOException {
		urlconn.setRequestMethod("GET");
		urlconn.setDoInput(true);
		urlconn.connect();
		return urlconn.getResponseMessage();
		}

	public String post(byte[] data) throws IOException {
		if(!isPostReady) {
			urlconn.setRequestMethod("POST");
			urlconn.setDoOutput(true);
			urlconn.setDoInput(true);
			isPostReady = true;
		}
		os = urlconn.getOutputStream();
		os.write(data);
		os.flush();
		urlconn.connect();
		return urlconn.getResponseMessage();
	}

	/**
	 * 指定したファイルを読み込み、それを出力ストリームに書き込みます。
	 * @param ｆile
	 * @throws IOException
	 */
	public String post(File ｆile) throws IOException {
		int readByte=0, totalByte = 0, restByte = 0;
		restByte = (int)ｆile.length();
		byte[] buffer = new byte[restByte];
		//画像ファイル→データ配列変換用ストリーム
		dis = new DataInputStream( new BufferedInputStream(new FileInputStream(ｆile)));
		do{
			readByte = dis.read(buffer, totalByte, restByte);
			totalByte += readByte;
			restByte -= readByte;
		}while(readByte != -1 && restByte != 0);
		return post(buffer);
	}

	/**
	 * 指定したテキストを出力ストリームに書き込みます。
	 * @param text
	 * @throws IOException
	 */
	public String post(String text) throws IOException {
		os = urlconn.getOutputStream();
		pw = new PrintWriter(os);
		pw.write(text);
		pw.flush();
		urlconn.connect();
		return urlconn.getResponseMessage();
	}

	/**
	 * connect メソッドを使用した後のレスポンスコードを返します。
	 * connect メソッドを用いる前に使用した場合は-1が返ります。
	 * @return　通信した結果のレスポンスコード
	 * @throws IOException
	 */
	public int getResponseCode() throws IOException{
		return urlconn.getResponseCode();
	}

	/**
	 * connect メソッドを使用した後のレスポンスコードを返します。
	 * connect メソッドを用いる前に使用した場合はnullが返ります。
	 * @return　通信した結果のレスポンスコード
	 * @throws IOException
	 */
	public String getResponseMessage() throws IOException{
		return urlconn.getResponseMessage();
	}

	public InputStream getInputStream() throws IOException{
		InputStream in = urlconn.getInputStream();
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
		reader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
		while((line = reader.readLine()) != null) {
			strbff.append(line);
		}
		reader.close();
		return strbff.toString();
	}

	/**
	 * すべてのストリームをクローズして、リソースをすべて開放します。
	 * ユーザは最後に必ずこの処理を実行してください。
	 */
	public void close(){
		if(dis != null){
			try {
				dis.close();
				if(reader != null){
					reader.close();
				}
				if(pw != null){
					pw.close();
				}
				if(os != null){
					os.close();
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		urlconn.disconnect();
	}


}
