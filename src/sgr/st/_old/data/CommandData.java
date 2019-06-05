package sgr.st._old.data;

//共通でもたないといけない
/**
 * リクエストのコマンド及びデータ型が記述されたクラスです。
 * Request クラスを使う際はこのクラスのコマンドを指定してください。
 *
 * ・FLAG （後続バイナリのデータ型を識別）
 * ・COM （コマンド）
 *
 *  を保管します。
 *  開発者は新たなコマンドを作成する時はこのクラスを使用してください。
 *
 */
public class CommandData {

	public static final byte FLAG_NULL = 0;
	public static final byte FLAG_BYTE = 1;
	public static final byte FLAG_SHORT = 2;
	public static final byte FLAG_INT = 4;
	public static final byte FLAG_DOUBLE = 8;
	public static final byte FLAG_STRING = 101;

	public static final byte COM_OK = 0;
	public static final byte COM_EXIT = 1;
	public static final byte COM_TEST = 2;
	public static final byte COM_START = 3;
	public static final byte COM_MESSAGE = 4;


	public static final byte COM_NOD = 10;
	public static final byte COM_REJECT = 11;
	public static final byte COM_TILTS = 12;

	public static final byte COM_EMOTIONS = 20;
	public static final byte COM_CONFUSE = 21;
	public static final byte COM_HAPPY = 22;
	public static final byte COM_SAD = 23;
	public static final byte COM_ANGRY = 24;

	public static final byte COM_ANGLES = 30;
	public static final byte COM_AJUST_ANGLES = 31;


	public static final byte COM_POSTURES = 40;
	public static final byte COM_MOTIONS = 41;

	public static final byte COM_IMITATE = 50;
	public static final byte COM_STAND = 51;
	public static final byte COM_PROPER_MOTIONS = 52;
	public static final byte COM_UNPROPER_MOTIONS = 53;

	public static final byte COM_COLORS = 60;

	public static final byte COM_UP = 70;
	public static final byte COM_RIGHT = 71;
	public static final byte COM_LEFT = 72;
	public static final byte COM_DOWN = 73;
	public static final byte COM_NEXT = 74;



}
