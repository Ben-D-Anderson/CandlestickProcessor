package xyz.benanderson.candlestickprocessor;

import xyz.benanderson.candlestickprocessor.algorithm.Algorithm;
import xyz.benanderson.candlestickprocessor.algorithm.AlgorithmEvaluator;
import xyz.benanderson.candlestickprocessor.algorithm.impl.*;
import xyz.benanderson.candlestickprocessor.data.AlgorithmEvaluation;
import xyz.benanderson.candlestickprocessor.data.fetcher.CandlestickFetcher;
import xyz.benanderson.candlestickprocessor.data.fetcher.CryptoCompareFetcher;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Set<Algorithm> algorithms = new HashSet<>();
        algorithms.add(new RandomChoice());
        algorithms.add(new AlwaysUp());
        algorithms.add(new AlwaysDown());
        algorithms.add(new OpenClose());
        algorithms.add(new ClosedHigher());
        algorithms.add(new ClosedAtHigh());
        algorithms.add(new ClosedAtHighWithHandle());
        algorithms.add(new DoubleIncrease());
        algorithms.add(new TripleIncrease());
        algorithms.add(new QuadIncrease());

        CandlestickFetcher fetcher = new CryptoCompareFetcher("BTC");

        AlgorithmEvaluator algorithmEvaluator = new AlgorithmEvaluator();
        Map<Algorithm, Optional<AlgorithmEvaluation>> evaluations = algorithmEvaluator.evaluate(algorithms, fetcher);

        System.out.println("\nALGORITHMS BY RATING");
        algorithmEvaluator.debugAlgorithmsByRating(evaluations);
        System.out.println("\nALGORITHMS BY PROFIT PER UNIT CURRENCY PER PREDICTION");
        algorithmEvaluator.debugAlgorithmsByProfitPerUnitCurrencyPerPrediction(evaluations);
        System.out.println("\nALGORITHMS BY PROFIT PER UNIT CURRENCY");
        algorithmEvaluator.debugAlgorithmsByTotalProfitPerUnitCurrency(evaluations);
    }

}
