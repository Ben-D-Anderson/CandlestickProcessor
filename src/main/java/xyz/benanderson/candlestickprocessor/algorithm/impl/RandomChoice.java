package xyz.benanderson.candlestickprocessor.algorithm.impl;

import xyz.benanderson.candlestickprocessor.algorithm.Algorithm;
import xyz.benanderson.candlestickprocessor.data.CandlestickSessionWindow;
import xyz.benanderson.candlestickprocessor.data.Direction;

import java.util.Optional;
import java.util.Random;

public class RandomChoice implements Algorithm {
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
        return Optional.of(new Random().nextBoolean() ? Direction.UP : Direction.DOWN);
    }
}
