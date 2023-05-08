package xyz.benanderson.candlestickprocessor.algorithm.impl;

import xyz.benanderson.candlestickprocessor.algorithm.Algorithm;
import xyz.benanderson.candlestickprocessor.data.CandlestickSessionWindow;
import xyz.benanderson.candlestickprocessor.data.Direction;

import java.util.Optional;

public class DoubleIncrease implements Algorithm {

    @Override
    public int getMinimumWindowSize() {
        return 3;
    }

    @Override
    public int getMaximumWindowSize() {
        return 3;
    }

    @Override
    public Optional<Direction> predict(CandlestickSessionWindow sessionWindow) {
        assert sessionWindow.get(0).isPresent();
        assert sessionWindow.get(1).isPresent();
        assert sessionWindow.get(2).isPresent();
        if (Algorithm.priceIncreased(sessionWindow.get(0).get(), sessionWindow.get(1).get())
                && Algorithm.priceIncreased(sessionWindow.get(1).get(), sessionWindow.get(2).get())) {
            return Optional.of(Direction.UP);
        }
        return Optional.empty();
    }

}
