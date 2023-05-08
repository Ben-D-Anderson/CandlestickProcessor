package xyz.benanderson.candlestickprocessor.algorithm;

import xyz.benanderson.candlestickprocessor.data.*;
import xyz.benanderson.candlestickprocessor.data.fetcher.CandlestickFetcher;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AlgorithmEvaluator {

    public void debugAlgorithmsByRating(Map<Algorithm, Optional<AlgorithmEvaluation>> ratingMap) {
        Comparator<Map.Entry<Algorithm, Optional<AlgorithmEvaluation>>> comparator =
                Comparator.comparingDouble(rating -> rating.getValue().orElse(new AlgorithmEvaluation(0, 0d, 0d)).rating());
        debugAlgorithmsByComparator(ratingMap, comparator.reversed());
    }

    public void debugAlgorithmsByTotalProfitPerUnitCurrency(Map<Algorithm, Optional<AlgorithmEvaluation>> ratingMap) {
        Comparator<Map.Entry<Algorithm, Optional<AlgorithmEvaluation>>> comparator =
                Comparator.comparingDouble(rating ->
                        rating.getValue().orElse(new AlgorithmEvaluation(0, 0d, 0d)).profitPerUnitCurrencyPerPrediction()
                                * rating.getValue().orElse(new AlgorithmEvaluation(0, 0d, 0d)).predictions());
        debugAlgorithmsByComparator(ratingMap, comparator.reversed());
    }

    public void debugAlgorithmsByProfitPerUnitCurrencyPerPrediction(Map<Algorithm, Optional<AlgorithmEvaluation>> ratingMap) {
        Comparator<Map.Entry<Algorithm, Optional<AlgorithmEvaluation>>> comparator =
                Comparator.comparingDouble(rating ->
                        rating.getValue().orElse(new AlgorithmEvaluation(0, 0d, 0d)).profitPerUnitCurrencyPerPrediction());
        debugAlgorithmsByComparator(ratingMap, comparator.reversed());
    }

    private void debugAlgorithmsByComparator(Map<Algorithm, Optional<AlgorithmEvaluation>> ratingMap,
                                             Comparator<Map.Entry<Algorithm, Optional<AlgorithmEvaluation>>> comparator) {
        ratingMap.entrySet()
                .stream()
                .sorted(comparator)
                .forEachOrdered(entry -> entry.getKey().debug(entry.getValue().isPresent()
                        ? entry.getValue().get().toString() : "N/A"));
    }

    public Map<Algorithm, Optional<AlgorithmEvaluation>> evaluate(Set<Algorithm> algorithms, CandlestickFetcher candlestickFetcher) {
        candlestickFetcher.debug("Fetching candlestick data...");
        CandlestickSession session = candlestickFetcher.fetchCandlestickSession();
        return evaluate(algorithms, session);
    }

    public Map<Algorithm, Optional<AlgorithmEvaluation>> evaluate(Set<Algorithm> algorithms, CandlestickSession session) {
        System.out.println("Attempting to find an acceptable common window size for all algorithms...");
        Optional<Integer[]> acceptableWindowSizeRange = findAcceptableWindowSizeRange(algorithms);
        if (acceptableWindowSizeRange.isPresent()) {
            int minimumWindowSize = acceptableWindowSizeRange.get()[0];
            int maximumWindowSize = acceptableWindowSizeRange.get()[1];
            System.out.printf("Found acceptable common window size [%d-%d], carrying out a single window iteration.\n",
                    minimumWindowSize, maximumWindowSize);

            System.out.println("Computing test results for all algorithms...");
            Map<Algorithm, Integer> accuracies = new HashMap<>();
            Map<Algorithm, Integer> predictions = new HashMap<>();
            Map<Algorithm, Double> profitPerUnitCurrency = new HashMap<>();
            forEachCandlestickSessionWindow(minimumWindowSize, maximumWindowSize, session,
                    (sessionWindow, followingSession) -> {
                        for (Algorithm algorithm : algorithms) {
                            Optional<PredictionEvaluation> testResult = testSessionWindow(algorithm, sessionWindow, followingSession);
                            testResult.ifPresent(result -> {
                                predictions.compute(algorithm, (algo, pred) -> (pred == null) ? 1 : pred + 1);
                                profitPerUnitCurrency.compute(algorithm, (algo, prof) -> (prof == null)
                                        ? result.profitPerUnitCurrency() : prof + result.profitPerUnitCurrency());
                                if (result.correctPrediction()) {
                                    accuracies.compute(algorithm, (algo, acc) -> (acc == null) ? 1 : acc + 1);
                                }
                            });
                        }
                    });
            System.out.println("Computing accuracy ratings for all algorithms...");
            Map<Algorithm, Optional<AlgorithmEvaluation>> ratingMap = new HashMap<>();
            algorithms.stream()
                    .filter(algorithm -> !predictions.containsKey(algorithm))
                    .forEach(algorithm -> ratingMap.put(algorithm, Optional.empty()));
            for (Algorithm algorithm : algorithms) {
                if (!predictions.containsKey(algorithm)) {
                    ratingMap.put(algorithm, Optional.empty());
                    continue;
                }
                int algorithmAccuracies = accuracies.get(algorithm);
                int algorithmPredictions = predictions.get(algorithm);
                ratingMap.put(algorithm, Optional.of(
                        new AlgorithmEvaluation(algorithmPredictions, (double) algorithmAccuracies / algorithmPredictions,
                            profitPerUnitCurrency.get(algorithm) / algorithmPredictions)
                ));
            }
            System.out.println("Completed all algorithm accuracy calculations.");
            return ratingMap;
        }
        System.out.println("Failed to find an acceptable common window size, resorting to individual algorithm-window iteration.");
        Map<Algorithm, Optional<AlgorithmEvaluation>> ratingMap = algorithms.stream()
                .collect(Collectors.toMap(Function.identity(),
                        algorithm -> evaluate(algorithm, session)));
        System.out.println("Completed all algorithm accuracy calculations.");
        return ratingMap;
    }

    public Optional<AlgorithmEvaluation> evaluate(Algorithm algorithm, CandlestickFetcher candlestickFetcher) {
        algorithm.debug(String.format("Fetching candlestick data from %s...", candlestickFetcher.getClass().getSimpleName()));
        CandlestickSession session = candlestickFetcher.fetchCandlestickSession();
        return evaluate(algorithm, session);
    }

    public Optional<AlgorithmEvaluation> evaluate(Algorithm algorithm, CandlestickSession session) {
        AtomicInteger accuracies = new AtomicInteger(0);
        AtomicInteger predictions = new AtomicInteger(0);
        AtomicReference<Double> profitPerUnitCurrency = new AtomicReference<>((double) 0);
        algorithm.debug("Computing test results...");
        forEachCandlestickSessionWindow(algorithm.getMinimumWindowSize(), algorithm.getMaximumWindowSize(), session,
                (sessionWindow, followingSession) -> {
                    Optional<PredictionEvaluation> testResult = testSessionWindow(algorithm, sessionWindow, followingSession);
                    testResult.ifPresent(result -> {
                        predictions.getAndIncrement();
                        profitPerUnitCurrency.updateAndGet(v -> v + result.profitPerUnitCurrency());
                        if (result.correctPrediction()) {
                            accuracies.getAndIncrement();
                        }
                    });
                });
        if (predictions.get() == 0) return Optional.empty();
        double successRate = accuracies.doubleValue() / predictions.doubleValue();
        algorithm.debug("Completed algorithm accuracy calculations.");
        return Optional.of(new AlgorithmEvaluation(predictions.get(), successRate, profitPerUnitCurrency.get() / predictions.doubleValue()));
    }

    private void forEachCandlestickSessionWindow(int minimumWindowSize, int maximumWindowSize, CandlestickSession session,
                                                 BiConsumer<CandlestickSessionWindow, CandlestickSessionWindow> windows) {
        for (int a = 0; a < session.size() - minimumWindowSize; a++) {
            for (int b = a + minimumWindowSize; b < session.size() - 1 && (b - a) <= maximumWindowSize; b++) {
                CandlestickSessionWindow sessionWindow = new CandlestickSessionWindow(session, a, b);
                CandlestickSessionWindow followingSession = new CandlestickSessionWindow(session, b + 1, session.size());
                windows.accept(sessionWindow, followingSession);
            }
        }
    }

    private Optional<Integer[]> findAcceptableWindowSizeRange(Collection<Algorithm> algorithms) {
        OptionalInt minimumWindowSize = algorithms.stream().mapToInt(Algorithm::getMinimumWindowSize).max();
        OptionalInt maximumWindowSize = algorithms.stream().mapToInt(Algorithm::getMaximumWindowSize).min();
        if (minimumWindowSize.isPresent() && maximumWindowSize.isPresent()
                && minimumWindowSize.getAsInt() <= maximumWindowSize.getAsInt()) {
            return Optional.of(new Integer[] { minimumWindowSize.getAsInt(), maximumWindowSize.getAsInt() });
        }
        return Optional.empty();
    }

    private Optional<PredictionEvaluation> testSessionWindow(Algorithm algorithm, CandlestickSessionWindow sessionWindow,
                                                             CandlestickSessionWindow followingSession) {
        Optional<Direction> prediction = algorithm.predict(sessionWindow);
        return prediction.map(direction -> algorithm.evaluatePrediction(direction, sessionWindow, followingSession));
    }

}
