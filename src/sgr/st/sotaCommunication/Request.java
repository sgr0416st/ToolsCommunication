package sgr.st.sotaCommunication;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import sgr.st.data.CommandData;
import sgr.st.dataType.lib.ParseByteData;

/**
 * <pre>
 * 主にsotaとの通信のために作られた自作プロトコルの実装系です。
 * リクエストは送信側、受信側のどちらも以下の形式のバイナリデータを使って送受信します。
 *
 * リクエスト: [
 *      request_header: {
 *      request_size[2 byte], command[1 byte], data_number[1 byte]
 *      }(size: 4byte),
 *      params: {
 *      params1:(data_flag[1byte], data[n byte]), params2:(data_flag[1byte], data[n byte]), * * *
 *      }(size: request_size-4 byte)
 *  ]
 *  例外:
 *  String_param:(data_flag[1 byte], String_size[2 byte], data[n byte])
 *
 * この仕様により任意の命令と任意の種類、数のパラメータの組み合わせを安全に通信することができるようになります。
 * 読み込み時は最初にヘッダを読み込んでメタ情報を記憶し、そのあとに追加パラメータ情報を読み込みます。
 * また、格納されるデータサイズは自身を含めた枠組み全体のサイズを保持します。
 * （request_sizeはリクエスト全体のサイズ、String_sizeはString_param全体のサイズを保持します）
 * </pre>
 *
 * @author suguru
 */
public class Request {

	protected byte command,addDataNumber;
	protected int requestSize;
	protected List<Object>  paramObjects= null;
	protected List<byte[]> params;

	public Request() {
		// TODO 自動生成されたコンストラクター・スタブ
		paramObjects = new ArrayList<Object>(2);
		params = new ArrayList<byte[]>(2);
		clear();

	}

	/**
	 * 保持しているリクエストの情報を破棄します。
	 * 呼び出し側は新しいリクエストを読み込みまたは書き込みする場合、その直前に必ずこのメソッドを呼ぶ必要があります.
	 */
	public void clear(){
		command = 0;
		requestSize = 4;
		addDataNumber = 0;
		if(!paramObjects.isEmpty())
			paramObjects.clear();
	}

	//更新が必要
	/**
	 * 現在保持しているリクエストのコマンド名を返します。
	 * コマンド名は{@code Command}クラスある定義名に準拠しています。
	 * @return commandName リクエストのコマンド名
	 * @see sgr.st.data.CommandData
	 */
	public String getCommandName(){
		String name = null;
		switch (command) {
		case CommandData.COM_TEST:
			name = "COM_TEST";
			break;
		case CommandData.COM_EXIT:
			name = "COM_EXIT";
			break;
		case CommandData.COM_NOD:
			name = "COM_NOD";
			break;
		case CommandData.COM_REJECT:
			name = "COM_REJECT";
			break;
		case CommandData.COM_TILTS:
			name = "COM_TILTS";
			break;
		case CommandData.COM_CONFUSE:
			name = "COM_CONFUSE";
			break;
		case CommandData.COM_HAPPY:
			name = "COM_HAPPY";
			break;
		case CommandData.COM_SAD:
			name = "COM_SAD";
			break;
		case CommandData.COM_ANGRY:
			name = "COM_ANGRY";
			break;
		case CommandData.COM_ANGLES:
			name = "COM_ANGLES";
			break;
		case CommandData.COM_POSTURES:
			name = "COM_POSTURES";
			break;
		case CommandData.COM_MOTIONS:
			name = "COM_MOTIONS";
			break;
		case CommandData.COM_IMITATE:
			name = "COM_IMITATE";
			break;
		case CommandData.COM_STAND:
			name = "COM_STAND";
			break;
		case CommandData.COM_PROPER_MOTIONS:
			name = "COM_PROPER_MOTIONS";
			break;
		case CommandData.COM_UNPROPER_MOTIONS:
			name = "COM_UNPROPER_MOTIONS";
			break;
		default:
			name = "undefined";
			System.out.println("byte command: " + command);
			break;
		}
		return name;
	}

	/**
	 * コマンドの値を返します。
	 * @return command このインスタンスが持つリクエストのcommand
	 */
	public byte getCommand(){
		return command;
	}
	/**
	 * パラメータの総数を返します。
	 * @return addDataNumber このインスタンスが持つリクエストの追加パラメータ数
	 */
	public byte getParamsNumber(){
		return addDataNumber;
	}
	/**
	 * このリクエストのサイズを返します。
	 * @return requestSize このインスタンスが持つリクエストのbyteサイズ
	 */
	public int getRequestSize(){
		return requestSize;
	}

	/**
	 * パラメータ情報を保持するリストから引数で指定されたインデックスの要素をObject型で返します。呼び出し側で適切なキャストが必要です。
	 * @param index パラメータ情報を保持するリストのインデックス
	 * @return param パラメータ情報を保持するリストの{@code index}番目の要素のObject型
	 */
	public Object getParam(int index){
		return paramObjects.get(index);
	}
	/**
	 * リクエストが保持する全てのパラメータ情報を持つリストを返します。インデックスは送信時に追加された順番に一致します。
	 * また、リストの要素はObject型のため呼び出し側で適切なキャストが必要です。
	 * @return paramObjects 追加パラメータのリスト
	 */
	public List<Object> getParamsList(){
		return paramObjects;
	}

	/**
	 * リクエスト情報を標準出力に表示します。具体的には、
	 * <pre>
	 * ・リクエストサイズ
	 * ・コマンド名
	 * ・追加パラメータ数
	 * ・各追加パラメータの値
	 *  </pre>
	 * がこの順で表示されます
	 */
	public void show(){
		int d;
		System.out.println("-------request-------");
		System.out.println(
				"requestSize: " + requestSize + '\n' +
				"command: " + getCommandName() + '\n' +
				"addDataNumber: " + addDataNumber
				);
		if(paramObjects.size() != addDataNumber){
			System.out.println("param size error:");
			System.out.println("paramObjects.size: " + paramObjects.size());
			return;
		}
		for(d = 0;d < addDataNumber; d++){
/*			param = params.get(d);
			System.out.println("param"+ d + ": " + convertParam(param));*/
			System.out.println("param list"+ d + ": " + paramObjects.get(d));
		}
		System.out.println("-------  end  --------");
	}


	/**
	 * このリクエストを強制的にCOM_EXITに書き換えます。
	 */
	public Request setExitRequest(){
		clear();
		command = CommandData.COM_EXIT;
		return this;
	}

	/**
	 * このリクエストを強制的にCOM_OKに書き換えます。
	 */
	public Request setOKRequest(){
		clear();
		command = CommandData.COM_OK;
		return this;
	}

	/**
	 * 与えられたパラメータ情報の１つ分のバイナリデータから、パラメータを復元します。
	 * @param param パラメータ１つ分のバイナリデータ
	 * @return Object型にキャストされたパラメータの値
	 */
	protected static Object convertParam(byte[] param){
		Object ans = null;
		int i,s,size;
		byte flag = param[0];
		byte[] data,stringSizeData;
		switch (flag) {
		case CommandData.FLAG_BYTE:
			ans = (int)param[1];
			break;
		case CommandData.FLAG_SHORT:
			data = new byte[2];
			for(i = 0; i < data.length; i++)
				data[i] = param[i + 1];
			ans = ParseByteData.toShort(data);
			break;
		case CommandData.FLAG_INT:
			data = new byte[4];
			for(i = 0; i < data.length; i++)
				data[i] = param[i + 1];
			ans = ParseByteData.toInt(data);
			break;
		case CommandData.FLAG_DOUBLE:
			data = new byte[8];
			for(i = 0; i < data.length; i++)
				data[i] = param[i + 1];
			ans = ParseByteData.toDouble(data);
			break;
		case CommandData.FLAG_STRING:
			stringSizeData = new byte[2];
			for(s = 0; s < 2; s++){
				stringSizeData[s] = param[s+1];
			}
			size = (int)ParseByteData.toShort(stringSizeData) - 3;
			data = new byte[size];
			for(i = 0; i < size; i++){
				data[i] = param[i+3];
			}
			try {
				ans = new String(data,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO 自動生成された catch ブロック
				ans = null;
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		return ans;
	}

	/**
	 * 与えられたパラメータにメタデータを付加して、バイナリデータに変換します。
	 * 具体的には、パラメータの型、サイズ情報をメタデータとするヘッダを生成し、バイナリ化したデータの先頭につけたデータを出力します。
	 * したがって、出力は
	 * params: {
	 *     data_flag[1byte],size_data[2byte], data[n byte]
	 *     }(size: n+3 byte)
	 * となります。
	 * @param data パラメータに追加するString型のデータ
	 * @return dataのパラメータ表現(ヘッダ＋dataのバイナリ表現)
	 */
	protected static byte[] toByteParam(String data) {
		byte[] data_String = null;
		try {
			data_String = data.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		byte[] param = new byte[data_String.length + 3];
		byte[] stringSizeData = ParseByteData.toByte((short)(data_String.length + 3));

		param[0] = CommandData.FLAG_STRING;
		param[1] = stringSizeData[0];
		param[2] = stringSizeData[1];
		for(int i = 0; i < data_String.length; i++){
			param[i+3] = data_String[i];
		}
		return param;
	}

	/**
	 * 与えられたパラメータにメタデータを付加して、バイナリデータに変換します。
	 * 具体的には、パラメータの型情報をメタデータとするヘッダを生成し、バイナリ化したデータの先頭につけたデータを出力します。
	 * <pre>
	 * したがって、出力は
	 * params: {
	 *     data_flag[1byte], data[8 byte]
	 *     }(size: 9 byte)
	 * となります。
	 * </pre>
	 * @param data パラメータに追加するdouble型のデータ
	 * @return dataのパラメータ表現(ヘッダ＋dataのバイナリ表現)
	 */
	protected static byte[] toByteParam(double data) {
		byte[] param = new byte[9];
		byte[] data_double = ParseByteData.toByte(data);
		param[0] = CommandData.FLAG_DOUBLE;
		for(int i = 0; i < data_double.length; i++){
			param[i+1] = data_double[i];
		}
		return param;
	}

	/**
	 * 与えられたパラメータにメタデータを付加して、バイナリデータに変換します。
	 * 具体的には、パラメータの型情報をメタデータとするヘッダを生成し、バイナリ化したデータの先頭につけたデータを出力します。
	 * <pre>
	 * したがって、出力は
	 * params: {
	 *     data_flag[1byte], data[4 byte]
	 *     }(size: 9 byte)
	 * となります。
	 * </pre>
	 * @param data パラメータに追加するint型のデータ
	 * @return dataのパラメータ表現(ヘッダ＋dataのバイナリ表現)
	 */
	protected static byte[] toByteParam(int data) {
		byte[] param = new byte[5];
		byte[] data_int = ParseByteData.toByte(data);
		param[0] = CommandData.FLAG_INT;
		for(int i = 0; i < data_int.length; i++){
			param[i+1] = data_int[i];
		}
		return param;
	}

	/**
	 * 与えられたパラメータにメタデータを付加して、バイナリデータに変換します。
	 * 具体的には、パラメータの型情報をメタデータとするヘッダを生成し、バイナリ化したデータの先頭につけ,そのデータを出力します。
	 * <pre>
	 * したがって、出力は
	 * params: {
	 *     data_flag[1byte], data[2 byte]
	 *     }(size: 3 byte)
	 * となります。
	 * </pre>
	 * @param data パラメータに追加するshort型のデータ
	 * @return dataのパラメータ表現(ヘッダ＋dataのバイナリ表現)
	 */
	protected static byte[] toByteParam(short data){
		byte[] param = new byte[3];
		byte[] data_short = ParseByteData.toByte(data);
		param[0] = CommandData.FLAG_SHORT;
		for(int i = 0; i < 2; i++){
			param[i+1] = data_short[i];
		}
		return param;
	}

	/**
	 * 与えられたパラメータにメタデータを付加して、バイナリデータに変換します。
	 * 具体的には、パラメータの型情報をメタデータとするヘッダを生成し、バイナリ化したデータの先頭につけたデータを出力します。
	 * <pre>
	 * したがって、出力は
	 * params: {
	 *     data_flag[1byte], data[1 byte]
	 *     }(size: 2 byte)
	 * となります。
	 * </pre>
	 * @param data パラメータに追加するbyte型のデータ
	 * @return dataのパラメータ表現(ヘッダ＋dataのバイナリ表現)
	 */
	protected static byte[] toByteParam(byte data){
		byte[] param = new byte[2];
		param[0] = CommandData.FLAG_SHORT;
			param[1] = data;
		return param;
	}
}
