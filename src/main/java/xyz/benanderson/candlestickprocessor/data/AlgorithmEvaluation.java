package xyz.benanderson.candlestickprocessor.data;

public record AlgorithmEvaluation(int predictions, double rating, double profitPerUnitCurrencyPerPrediction) {

    @Override
    public String toString() {
        return "Predictions=" + predictions +
                ", Rating=" + rating +
                ", Profit/Currency/Prediction=" + profitPerUnitCurrencyPerPrediction +
                ", Profit/Currency=" + profitPerUnitCurrencyPerPrediction * predictions +
                '}';
    }
}
