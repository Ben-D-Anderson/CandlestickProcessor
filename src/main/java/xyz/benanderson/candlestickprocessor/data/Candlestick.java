package xyz.benanderson.candlestickprocessor.data;

import java.time.Instant;

public record Candlestick(Instant openTime, Instant closeTime, double open, double close, double low, double high,
                          double volume) {

}
