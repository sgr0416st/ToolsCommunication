package sgr.st.dataType.lib;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * @author suguru
 *
 *バイナリ配列と各基本型を相互に変換するクラスです。
 */
public class ParseByteData {

	private ParseByteData() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/**
	 * 要素2のbyte配列からshort型のデータを復元します。
	 * @param data_short 2byteのshort型のバイナリ表現
	 * @return 変換したshort型の値
	 */
	public static short toShort(byte[] data_short) {
		short ans = 0;
		if (data_short.length != 2) {
			System.out.println("Unexpected End of Stream");
			return -1;
		}

		ans = ByteBuffer.wrap(data_short).order(ByteOrder.LITTLE_ENDIAN).getShort();
		return ans;
	}

	/**
	 * 要素4のbyte配列からint型のデータを復元します。
	 * @param data_int 4byteのint型のバイナリ表現
	 * @return 変換したint型の値
	 */
	public static int toInt(byte[] data_int){
		int ans = 0;
		if (data_int.length != 4) {
			System.out.println("Unexpected End of Stream");
		}

		ans = ByteBuffer.wrap(data_int).order(ByteOrder.LITTLE_ENDIAN).getInt();
		return ans;
	}
	/**
	 * 要素8のbyte配列からdouble型のデータを復元します。
	 * @param data_double 8byteのdouble型のバイナリ表現
	 * @return 変換したdouble型の値
	 */
	public static double toDouble(byte[] data_double){
		double ans = 0;
		if (data_double.length != 8) {
			System.out.println("Unexpected End of Stream");
		}

		ans = ByteBuffer.wrap(data_double).order(ByteOrder.LITTLE_ENDIAN).getDouble();
		return ans;
	}
	/**
	 * byte配列からString型のデータをデコードします。ただしエンコードは UTF-8 でされていなければいけません。
	 * @param data_String String型のバイナリ表現(UTF-8エンコード)
	 * @return 変換したString型の値
	 */
	public static String toString(byte[] data_String) {
		String ans = null;
		try {
			ans = new String(data_String, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return ans;
	}

	/**
	 * short型のデータを2byteのバイトデータに変換します。
	 * @param data short型の値
	 * @return data のバイナリ表現
	 */
	public static byte[] toByte(short data) {
		return toByteBuffer(data).array();
	}

	/**
	 * int型のデータを4byteのバイトデータに変換します。
	 * @param data int型の値
	 * @return data のバイナリ表現
	 */
	public static byte[] toByte(int data) {
		return toByteBuffer(data).array();
	}

	/**
	 * double型のデータを8byteのバイトデータに変換します。
	 * @param data double型の値
	 * @return data のバイナリ表現
	 */
	public static byte[] toByte(double data) {
		return toByteBuffer(data).array();
	}

	private static ByteBuffer toByteBuffer(double data) {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putDouble(data);
		buf.flip();
		return buf;
	}

	private static ByteBuffer toByteBuffer(int data) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(data);
		buf.flip();
		return buf;
	}

	private static ByteBuffer toByteBuffer(short data) {
		ByteBuffer buf = ByteBuffer.allocate(2);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putShort(data);
		buf.flip();
		return buf;
	}




}
