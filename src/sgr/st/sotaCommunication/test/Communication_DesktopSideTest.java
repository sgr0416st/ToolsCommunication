package sgr.st.sotaCommunication.test;

import java.io.IOException;

import sgr.st.data.CommandData;
import sgr.st.sotaCommunication.lib.RecieveRequest;
import sgr.st.sotaCommunication.lib.RequestCommunicater;
import sgr.st.sotaCommunication.lib.SendRequest;

//クライアント側
public class Communication_DesktopSideTest {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		RequestCommunicater communicater = new RequestCommunicater();
		SendRequest s_request = null;
		RecieveRequest r_request = null;
		String SOTA_IP = "133.34.174.154";
		int port = 7777;

		communicater.connect(SOTA_IP,port);
		try {
			s_request = new SendRequest();

			double dNum = 21.435;
			String sNum = "abc";
			int iNum = 4;
			String sNum2 = "あいう";
			String sNum3 = "えお";
			s_request.addParam(dNum);
			s_request.addParam(sNum);
			s_request.addParam(iNum);
			s_request.addParam(sNum2);
			s_request.addParam(sNum3);
			s_request.setCommand(CommandData.COM_EXIT);
			s_request.show();
			communicater.send(s_request);

			r_request = communicater.receive();
			r_request.show();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		communicater.close();
		System.out.println("CommunicationRequestTest 終了");
	}

}
