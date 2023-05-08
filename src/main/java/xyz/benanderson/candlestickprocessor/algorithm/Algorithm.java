package xyz.benanderson.candlestickprocessor.algorithm;

import xyz.benanderson.candlestickprocessor.data.Candlestick;
import xyz.benanderson.candlestickprocessor.data.CandlestickSessionWindow;
import xyz.benanderson.candlestickprocessor.data.Direction;
import xyz.benanderson.candlestickprocessor.data.PredictionEvaluation;

import java.util.Optional;

public interface Algorithm {

    default void debug(String message) {
        System.out.printf("[%s] %s\n", getAlgorithmName(), message);
    }

    default String getAlgorithmName() {
        return getClass().getSimpleName();
    }

    static boolean priceIncreased(Candlestick first, Candlestick second) {
        return first.close() <= second.close();
    }

    static double profitPerUnitCurrency(Candlestick first, Candlestick second) {
        return (second.close() - first.close()) / first.close();
    }

	int getMinimumWindowSize();

	int getMaximumWindowSize();

    Optional<Direction> predict(CandlestickSessionWindow sessionWindow);

    default PredictionEvaluation evaluatePrediction(Direction directionPrediction, CandlestickSessionWindow sessionWindow, CandlestickSessionWindow followingSession) {
        Optional<Candlestick> lastCandlestick = sessionWindow.get(sessionWindow.size() - 1);
        if (lastCandlestick.isEmpty()) {
            debug("accurate#WARNING: last candlestick empty for session window at index " + (sessionWindow.size() - 1));
            return new PredictionEvaluation(false, 0);
        }
        Optional<Candlestick> firstCandlestick = followingSession.get(0);
        if (firstCandlestick.isEmpty()) {
            debug("accurate#WARNING: first candlestick empty for following session window");
            return new PredictionEvaluation(false, 0);
        }
        return new PredictionEvaluation(directionPrediction == Direction.UP ?
                (lastCandlestick.get().close() <= firstCandlestick.get().close())
                : (lastCandlestick.get().close() >= firstCandlestick.get().close()),
                profitPerUnitCurrency(lastCandlestick.get(), firstCandlestick.get()));
    }

}
