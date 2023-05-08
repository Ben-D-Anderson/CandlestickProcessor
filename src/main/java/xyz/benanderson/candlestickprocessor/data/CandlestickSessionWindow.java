package xyz.benanderson.candlestickprocessor.data;

import java.util.Optional;

public class CandlestickSessionWindow {

	private final int startIndex, endIndex;
	private final CandlestickSession session;

	/**
	 * @param session - the session to create the window in
	 * @param startIndex - the index of the session in which the window begins (inclusive)
	 * @param endIndex - the index of the session in which the window ends (inclusive)
	 **/
	public CandlestickSessionWindow(CandlestickSession session, int startIndex, int endIndex) {
		this.session = session;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public Optional<Candlestick> get(int index) {
		if (index < 0)
			return Optional.empty();
		int shiftedIndex = startIndex + index;
		if (shiftedIndex > endIndex)
			return Optional.empty();
		return session.get(shiftedIndex);
	}

	public int size() {
		return endIndex - startIndex + 1;
	}

}
