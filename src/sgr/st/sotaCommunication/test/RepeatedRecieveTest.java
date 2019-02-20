package sgr.st.sotaCommunication.test;

import java.io.IOException;

import sgr.st.data.CommandData;
import sgr.st.sotaCommunication.lib.Request;
import sgr.st.sotaCommunication.lib.RequestCommunicater;

public class RepeatedRecieveTest {


	public static void main(String[] args) {
		/*
		 * リクエストを”COM_EXIT”を受け取るまで繰り返し受信する
		 */
		boolean repeat = true;
		RequestCommunicater reciver = new RequestCommunicater();
		Request request = null;
		reciver.connect(7777);
		while(repeat){
			try {
				request = reciver.receive();
				//reciver.debug();
				request.show();
				if(request.getCommand() == CommandData.COM_EXIT){
					repeat = false;
					System.out.println("終了します");
				}
				request.clear();

			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				repeat = false;
			}
		}
		reciver.close();
	}



}
