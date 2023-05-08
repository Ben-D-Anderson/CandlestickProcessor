package xyz.benanderson.candlestickprocessor.algorithm.impl;

import xyz.benanderson.candlestickprocessor.algorithm.Algorithm;
import xyz.benanderson.candlestickprocessor.data.Candlestick;
import xyz.benanderson.candlestickprocessor.data.CandlestickSessionWindow;
import xyz.benanderson.candlestickprocessor.data.Direction;

import java.util.Optional;

public class OpenClose implements Algorithm {

    @Override
    public int getMinimumWindowSize() {
        return 1;
    }

    @Override
    public int getMaximumWindowSize() {
        return 1;
    }

    @Override
    public Optional<Direction> predict(CandlestickSessionWindow sessionWindow) {
        Optional<Candlestick> lastCandlestick = sessionWindow.get(sessionWindow.size() - 1);
        if (lastCandlestick.isEmpty()) {
            debug("predict#WARNING: last candlestick empty for session window at index " + (sessionWindow.size() - 1));
            return Optional.empty();
        }
        return Optional.of(lastCandlestick.get().open() > lastCandlestick.get().close() ? Direction.DOWN : Direction.UP);
    }

}
