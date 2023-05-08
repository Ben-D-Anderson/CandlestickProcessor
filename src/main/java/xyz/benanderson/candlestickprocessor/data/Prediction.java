package xyz.benanderson.candlestickprocessor.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Prediction {

    private final CandlestickSessionWindow sessionWindow;
    private final Direction direction;

}
