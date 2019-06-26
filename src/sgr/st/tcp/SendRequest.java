package sgr.st.tcp;

import sgr.st.dataTranslater.ParseByteData;

/**
 * リクエストのエンコードに必要なメソッドを追加してRequestクラスを拡張したクラスです。
 * 受信用のリクエストには RecieveRequest クラスを使ってください。
 * 与えられたコマンドと追加パラメータ情報から、以下のプロトコルに沿ったリクエストのバイナリデータを生成するのに役立ちます。
 * <pre>
 * リクエストの形式
 * リクエスト: [
 *      request_header: {
 *      request_size[2 byte], command[1 byte], data_number[1 byte]
 *      }(size: 4byte),
 *      params: {
 *      params1:(data_flag[1byte], data[n byte]), params2:(data_flag[1byte], data[n byte]), * * *
 *      }(size: request_size-4 byte)
 *  ]
 *  ただし、String型の場合:
 *  String_param:(data_flag[1 byte], String_size[2 byte], data[n byte])
 *  </pre>
 *
 * ユーザーは詳細な仕様を理解しなくても、コマンドとパラメータを追加するだけでこのプロトコルに従ったリクエストを構築できます。
 * 格納されるデータサイズは自身を含めた枠組み全体のサイズを保持します。
 * （request_sizeはリクエスト全体のサイズ、String_sizeはString_param全体のサイズを保持します）
 *
 * @author suguru
 */
public class SendRequest extends Request {

	private static final int HEDDERSIZE = 4;
	private byte[] requestHeader,requestData;

	/**
	 * リクエストを初期化します。
	 */
	public SendRequest() {
		super();
		requestHeader = new byte[HEDDERSIZE];
		requestData = null;
	}

	/**
	 * Requestオブジェクトから、sendRequestを生成します。
	 * 新しいSendRequestオブジェクトは、元のRequestオブジェクトのデータを引き継ぎます。
	 *
	 * @param request 元となるリクエスト。
	 * @return
	 */
	public static SendRequest create(Request request) {
		SendRequest sendRequest = new SendRequest();
		sendRequest.addDataNumber = request.addDataNumber;
		sendRequest.command = request.command;
		sendRequest.requestSize = request.getRequestSize();
		sendRequest.paramObjects = request.paramObjects;
		sendRequest.params = request.params;

		return sendRequest;
	}

	/**
	 * リクエストのパラメータ情報に、dataを追加します。
	 * @param data 追加するbyte型のパラメータ
	 */
	public void addParam(byte data){
		paramObjects.add(data);
		params.add(toByteParam(data));
		requestSize += 2;
		addDataNumber ++;
	}

	/**
	 * リクエストのパラメータ情報に、dataを追加します。
	 * @param data 追加するshort型のパラメータ
	 */
	public void addParam(short data){
		paramObjects.add(data);
		params.add(toByteParam(data));
		requestSize += 3;
		addDataNumber ++;

	}

	/**
	 * リクエストのパラメータ情報に、dataを追加します。
	 * @param data 追加するint型のパラメータ
	 */
	public void addParam(int data){
		paramObjects.add(data);
		params.add(toByteParam(data));
		requestSize += 5;
		addDataNumber ++;
	}

	/**
	 * リクエストのパラメータ情報に、dataを追加します。
	 * @param data 追加するdouble型のパラメータ
	 */
	public void addParam(double data){
		paramObjects.add(data);
		params.add(toByteParam(data));
		requestSize += 9;
		addDataNumber ++;
	}

	/**
	 * リクエストのパラメータ情報に、dataを追加します。
	 * @param data 追加するString型のパラメータ
	 */
	public void addParam(String data){
		byte[] param = toByteParam(data);
		paramObjects.add(data);
		params.add(param);
		requestSize += param.length;
		addDataNumber ++;
	}

	/**
	 * リクエストに、送信するコマンドの情報を追加します。
	 * @param command 送信するコマンド
	 */
	public void setCommand(byte command){
		this.command = command;
	}

	/**
	 * 保持しているリクエストデータから、リクエストヘッダを作成します。
	 * このメソッドを使用後はリクエストの変更はできません。
	 */
	private void setRequestHeader(){
		setRequestHeader(ParseByteData.toByte((short)requestSize), this.command, (byte)addDataNumber);
	}
	private void setRequestHeader(byte[] requestSizeData, byte command, byte addDataNumber){
		if(requestSizeData.length != 2){
			System.out.println("reqestSize error");
			return;
		}
		requestSize = (int)ParseByteData.toShort(requestSizeData);
		this.command = command;
		this.addDataNumber = addDataNumber;

		requestHeader[0] = requestSizeData[0];
		requestHeader[1] = requestSizeData[1];
		requestHeader[2] = command;
		requestHeader[3] = addDataNumber;
	}

	/**
	 * あらかじめ設定されたリクエストから実際にバイナリデータを構築します。
	 *
	 * @return requestData リクエストのバイナリデータ。
	 */
	public byte[] build(){
		setRequestHeader();

		//requestHedder, paramsリスト から request を作成
		byte[] param;
		int readByte = 0;
		requestData = new byte[requestSize];
		for(int i = 0;i < HEDDERSIZE;i++){
			requestData[i] = requestHeader[i];
			readByte++;
		}
		for(int j = 0;j < addDataNumber;j++){
			param = params.get(j);
			switch (param[0]) {
			case TCPConstants.FLAG_BYTE:
				for(int k = 0; k < 2; k++){
					requestData[readByte] = param[k];
					readByte++;
				}
				break;
			case TCPConstants.FLAG_SHORT:
				for(int k = 0; k < 3; k++){
					requestData[readByte] = param[k];
					readByte++;
				}
				break;
			case TCPConstants.FLAG_INT:
				for(int k = 0; k < 5; k++){
					requestData[readByte] = param[k];
					readByte++;
				}
				break;
			case TCPConstants.FLAG_DOUBLE:
				for(int k = 0; k < 9; k++){
					requestData[readByte] = param[k];
					readByte++;
				}
				break;
			case TCPConstants.FLAG_STRING:
				byte[] stringSizeData = new byte[2];
				for(int s = 0; s < 2; s++){
					stringSizeData[s] = param[s+1];
				}
				int dataSize = (int)ParseByteData.toShort(stringSizeData);
				for(int k = 0; k < dataSize; k++){
					requestData[readByte] = param[k];
					readByte++;
				}
				break;
			default:
				break;
			}
		}
		return requestData;
	}


	public void clear() {
		if(params != null)
		{
			params.clear();
		}
		super.clear();
	}
}
