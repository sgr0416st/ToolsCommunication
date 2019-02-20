package sgr.st.sotaCommunication.lib;

import sgr.st.data.CommandData;
import sgr.st.dataType.lib.ParseByteData;

/**
 * バイナリデータ化したリクエストのデコードに必要なメソッドを追加して、Requestクラスを拡張したです。
 * 送信用のリクエストには SendRequest クラスを使ってください。
 * リクエストのバイナリデータから、そのリクエストのコマンドとパラメータ情報を復元するのに役立ちます。
 *  <pre>
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
 * 格納されるデータサイズは自身を含めた枠組み全体のサイズを保持します。
 * （request_sizeはリクエスト全体のサイズ、String_sizeはString_param全体のサイズを保持します）
 *
 * ユーザーは詳細な仕様を理解しなくても、このクラスのメソッドを使ってリクエストからコマンドと追加パラメータを取得できます。
 *
 *
 * @author suguru
 */
public class RecieveRequest extends Request {

	public RecieveRequest() {
		// TODO 自動生成されたコンストラクター・スタブ
		super();
	}

	/**
	 * 引数で与えられたリクエストヘッダから、メタデータを抽出して記憶します。
	 * 具体的には、
	 * <pre>
	 * ・リクエストサイズ
	 * ・コマンド名
	 * ・追加パラメータ数
	 * </pre>
	 * を記憶、保持します。基本的にはリクエスト受信側で使用してください。
	 *
	 * @param requestHeader 送信されたリクエストヘッダのバイナリデータ。与えられる引数は必ず４バイトでなければならない。
	 * @return 抽出に成功した場合は{@code <code>true</code>}, そうでない場合は{@code <code>false</code>}
	 */
	public boolean extractMetaDataFrom(byte[] requestHeader){
		boolean success = true;
		if (requestHeader.length != 4) {
			return false;
		}
		byte[] requestSizeData = new byte[2];
		//リクエストのサイズを取得
		requestSizeData[0] = requestHeader[0];
		requestSizeData[1] = requestHeader[1];
		requestSize = (int)ParseByteData.toShort(requestSizeData);
		//コマンドを取得
		command = requestHeader[2];
		//渡されたパラメータの数を取得
		addDataNumber = requestHeader[3];

		return success;
	}

	/**
	 * 引数に与えられた追加パラメータのバイナリデータから追加パラメータを全て抽出して、リストとして内部に保持します。
	 * この関数を使う時は、あらかじめメタデータ情報がこのリクエストに保持されている必要があります。
	 * メタデータ情報は extractMetaDataFrom メソッドにリクエストデータのバイナリデータを引数として与えることで記憶されます。
	 *
	 * @param paramsData 追加パラメータ群情報のバイナリデータ。長さが リクエストサイズ - 4 byteでなければならない。
	 * @return 抽出に成功した場合は{@code <code>true</code>}, そうでない場合は{@code <code>false</code>}
	 */
	public boolean extractParamsFrom(byte[] paramsData){
		boolean success = true;
		byte data_flag = -1;
		byte[] stringSizeData;
		int readByte = 0;
		int readCounter = 0;
		int d,s;

		if (paramsData.length != (requestSize - 4)) {
			return false;
		}

		for(d = 0; d < addDataNumber; d++){
			data_flag = paramsData[readByte];
			readByte++;

			//パラメータをリストに保管
			switch (data_flag) {
			case CommandData.FLAG_BYTE:
				readCounter = 1;
				byte[] data_byte = new byte[2];
				data_byte[0] = data_flag;
				for(int i = 0; i < readCounter; i++){
					data_byte[i+1] = paramsData[readByte+i];
				}
				readByte += readCounter;
				paramObjects.add(convertParam(data_byte));
				params.add(data_byte);

				break;
			case CommandData.FLAG_SHORT:
				readCounter = 2;
				byte[] data_short = new byte[3];
				data_short[0] = data_flag;
				for(int i = 0; i < readCounter; i++){
					data_short[i+1] = paramsData[readByte+i];
				}
				readByte += readCounter;
				paramObjects.add(convertParam(data_short));
				params.add(data_short);


				break;
			case CommandData.FLAG_INT:
				readCounter = 4;
				byte[] data_int = new byte[5];
				data_int[0] = data_flag;
				for(int i = 0; i < readCounter; i++){
					data_int[i+1] = paramsData[readByte+i];
				}
				readByte += readCounter;
				paramObjects.add(convertParam(data_int));
				params.add(data_int);

				break;
			case CommandData.FLAG_DOUBLE:
				readCounter = 8;
				byte[] data_double = new byte[9];
				data_double[0] = data_flag;
				for(int i = 0; i < readCounter; i++){
					data_double[i+1] = paramsData[readByte+i];
				}
				readByte += readCounter;
				paramObjects.add(convertParam(data_double));
				params.add(data_double);


				break;
			case CommandData.FLAG_STRING:
				stringSizeData = new byte[2];
				for(s = 0; s < 2; s++){
					stringSizeData[s] = paramsData[readByte];
					readByte++;
				}
				readCounter = (int)ParseByteData.toShort(stringSizeData) - 3;
				byte[] data_String = new byte[readCounter + 3];
				data_String[0] = data_flag;
				data_String[1] = stringSizeData[0];
				data_String[2] = stringSizeData[1];
				for(int i = 0; i < readCounter; i++){
					data_String[i+3] = paramsData[readByte+i];
				}
				readByte += readCounter;
				paramObjects.add(convertParam(data_String));
				params.add(data_String);
				break;

			default:
				break;
			}
			readCounter = 0;
		}
		if(readByte != paramsData.length){	//読み取り失敗時
			System.out.println("fail to get paramsData");
			System.out.println("paramsData.length: " + paramsData.length + "readByte: " + readByte);
			success = false;
		}

		return success;
	}

}
