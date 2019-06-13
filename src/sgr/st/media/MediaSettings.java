package sgr.st.media;

import javax.sound.sampled.AudioFormat;

import sgr.st.sound.AudioConstants;



public enum MediaSettings {
	PORT_IMAGE_RECEIVE(9011),
	PORT_IMAGE_SEND(9012),
	PORT_AUDIO_RECEIVE(9001),
	PORT_AUDIO_SEND(9002),
	FPS_DEFAULT(30),
	IMAGE_WIDTH_OF_COMMUNICATION(600),
	IMAGE_HEIGHT_OF_COMMUNICATION(600),
	VIDEO_EXTENSION("jpeg"),
	AUDIO_EXTENSION(".wav"),
	MIXER_NAME_OF_SOTA("CODEC [plughw:2,0]");

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

	public static AudioFormat getUlawFormat() {
		return __format.ULAW_FORMAT;
	}
	public static AudioFormat getLinearFormat() {
		return __format.LINEAR_FORMAT;
	}

	public int getNum() {
		return num;
	}

	public String getString() {
		return str;
	}

	// シングルトン
	public static class __format{
		private static final AudioFormat ULAW_FORMAT = new AudioFormat(
				AudioFormat.Encoding.ULAW,
				AudioConstants.sampleRate,
				AudioConstants.sampleSizeInBits_ulaw,
				AudioConstants.channels,
				AudioConstants.frameSize,
				AudioConstants.frameRate,
				AudioConstants.isBigEndian
				);
		private static final AudioFormat LINEAR_FORMAT = new AudioFormat(
				AudioConstants.sampleRate,
				AudioConstants.sampleSizeInBits_PCM,
				AudioConstants.channels,
				true,
				AudioConstants.isBigEndian
				);
	}


}