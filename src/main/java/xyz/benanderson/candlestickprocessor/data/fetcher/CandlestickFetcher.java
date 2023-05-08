package xyz.benanderson.candlestickprocessor.data.fetcher;

import xyz.benanderson.candlestickprocessor.data.CandlestickSession;

public interface CandlestickFetcher {

    CandlestickSession fetchCandlestickSession();

    default String getFetcherName() {
        return getClass().getSimpleName();
    }

    default void debug(String message) {
        System.out.printf("[%s] %s\n", getFetcherName(), message);
    }

}
