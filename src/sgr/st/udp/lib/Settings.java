package sgr.st.udp.lib;

public enum Settings {
	MAX_BUFFER(60000);


	private final int num;

	private Settings(int num) {
        this.num = num;
    }

	public int getSize() {
		return MAX_BUFFER.num;
	}

}
