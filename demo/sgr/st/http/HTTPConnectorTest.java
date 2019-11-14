package sgr.st.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class HTTPConnectorTest {



	public static void main(String[] args) {
		//getTest();
		postTest();
		postTest();
		postTest();
		//post3Test();

	}

	public static void getTest() {
		String strURL = "https://httpbin.org/get";
		URL testURL;
		HTTPConnector connector = null;

		Map<String, String> headers = new HashMap<>();
		headers.put("grayscaling", "false");
		headers.put("accept", "application/json");
		headers.put("x-api-key", "test-test-test-test");
		headers.put("Content-Type", "application/octet-stream");


		try {
			testURL = new URL(strURL);
			connector = new HTTPConnector(testURL, headers);
			String res = connector.get();
			System.out.println(res);
			System.out.println(connector.getTextResponse());
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}finally {
			connector.close();
		}
	}

	public static void postTest() {
		String strURL = "https://httpbin.org/post";
		File file = new File("./resource/test.jpg");

		Map<String, String> headers = new HashMap<>();
		headers.put("grayscaling", "false");
		headers.put("accept", "application/json");
		headers.put("x-api-key", "test-test-test-test");
		headers.put("Content-Type", "application/octet-stream");

		HTTPConnector connector = null;

		try {
			connector = new HTTPConnector(strURL, headers);
			String res = connector.post(file);
			System.out.println(res);
			//System.out.println(connector.getTextResponse());
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}finally {
			connector.close();
		}

	}

	//error
	public static void post3Test() {
		String strURL = "https://httpbin.org/post";
		File file = new File("./resource/test.jpg");

		Map<String, String> headers = new HashMap<>();
		headers.put("grayscaling", "false");
		headers.put("accept", "application/json");
		headers.put("x-api-key", "test-test-test-test");
		headers.put("Content-Type", "application/octet-stream");

		HTTPConnector connector = null;

		try {
			connector = new HTTPConnector(strURL, headers);
			String res = connector.post(file);
			System.out.println(res);
			res = connector.post(file);
			System.out.println(res);
			res = connector.post(file);
			System.out.println(res);
			//System.out.println(connector.getTextResponse());
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}finally {
			connector.close();
		}

	}
}
