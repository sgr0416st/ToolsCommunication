package sgr.st.media.lib;

public enum MediaSettings {
	PORT_IMAGE_RECEIVE(9011),
	PORT_IMAGE_SEND(9012),
	PORT_AUDIO_RECEIVE(9001),
	PORT_AUDIO_SEND(9002),
	FPS_DEFAULT(30),
	IMAGE_WIDTH_OF_COMMUNICATION(600),
	IMAGE_HEIGHT_OF_COMMUNICATION(600),
	VIDEO_EXTENSION("jpeg");

	private int num;
	private String str;

	private MediaSettings(int num) {
		this.num = num;
		this.str = null;
	}

	private MediaSettings(String str) {
		this.num = -1;
		this.str = str;
	}

	public int getNum() {
		return num;
	}

	public String getString() {
		return str;
	}
}
