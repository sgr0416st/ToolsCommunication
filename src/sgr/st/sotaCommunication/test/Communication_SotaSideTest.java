package sgr.st.sotaCommunication.test;

import java.io.IOException;

import sgr.st.sotaCommunication.lib.Request;
import sgr.st.sotaCommunication.lib.RequestCommunicater;

//サーバ側
public class Communication_SotaSideTest {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		RequestCommunicater communicater = new RequestCommunicater();
		Request request = null;
		communicater.connect(7777);
		try {
			request = communicater.receive();
			request.show();
			//communicater.send(request);
			//request.show();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		communicater.close();
		System.out.println("CommunicationRequestTest 終了");
	}

}
