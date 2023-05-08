package xyz.benanderson.candlestickprocessor.data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CandlestickSession implements Iterable<Candlestick> {

    private final List<Candlestick> candles;

    public CandlestickSession(Stream<Candlestick> candlesStream) {
        this.candles = candlesStream
                .sorted(Comparator.comparing(Candlestick::openTime))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public CandlestickSession(Collection<Candlestick> candles) {
        this(candles.stream());
    }
	
	public Optional<Candlestick> get(int index) {
		if (index < 0 || index >= candles.size())
			return Optional.empty();
		return Optional.of(candles.get(index));
	}

	public int size() {
		return candles.size();
	}

    @Override
    public Iterator<Candlestick> iterator() {
        return candles.iterator();
    }

}
