# Candlestick Processor

This project provides an environment in which you can easily implement and test financial prediction algorithms.

Candlestick data is fetched from different `CandlestickFetcher` implementations and automatically tested against the
provided price movement prediction algorithms. Each algorithm is then given rating and profit scores.

## Building & Running The Project

### Prerequisites

In order to build this project, you are required to have and use the following tools:
- Git
- JDK 16
- Apache Maven

### Steps

First, clone the git repository by executing the following command:
```bash
git clone https://github.com/Ben-D-Anderson/CandlestickProcessor
```
Next, navigate into the cloned repository and build the project as follows:
```bash
cd CandlestickProcessor && mvn clean install
```
Finally, run the project by executing the following command:
```bash
mvn exec:java
```

## Sample Algorithms

A set of sample algorithms have already been implemented in the project:
- [`AlwaysUp.java`](src/main/java/xyz/benanderson/candlestickprocessor/algorithm/impl/AlwaysUp.java) - always predicts that the price will increase
- [`AlwaysDown.java`](src/main/java/xyz/benanderson/candlestickprocessor/algorithm/impl/AlwaysDown.java) - always predicts that the price will decrease
- [`ClosedAtHigh.java`](src/main/java/xyz/benanderson/candlestickprocessor/algorithm/impl/ClosedAtHigh.java) - predicts that the price will increase when the closing price of the candlestick was equal to the highest price of the candlestick
- [`ClosedAtHighWithHandle.java`](src/main/java/xyz/benanderson/candlestickprocessor/algorithm/impl/ClosedAtHighWithHandle.java) - predicts that the price will increase when the closing price of the candlestick was equal to the highest price of the candlestick, and the candlestick had a common 'hammer' shape (the difference between open and low is more than double the difference between open and close)
- [`ClosedHigher.java`](src/main/java/xyz/benanderson/candlestickprocessor/algorithm/impl/ClosedHigher.java) - predicts that the price will increase if the closing price of the candlestick was greater than the opening price
- [`OpenClose.java`](src/main/java/xyz/benanderson/candlestickprocessor/algorithm/impl/OpenClose.java) - predicts that the price movement will continue in its current direction (down if open > close, otherwise up)
- [`RandomChoice.java`](src/main/java/xyz/benanderson/candlestickprocessor/algorithm/impl/RandomChoice.java) - predicts that the price will randomly decrease or increase
- [`DoubleIncrease.java`](src/main/java/xyz/benanderson/candlestickprocessor/algorithm/impl/DoubleIncrease.java) - predicts that the price will increase if the closing price of the last two candlesticks both increased
- [`TripleIncrease.java`](src/main/java/xyz/benanderson/candlestickprocessor/algorithm/impl/TripleIncrease.java) - predicts that the price will increase if the closing price of the last three candlesticks all increased
- [`QuadIncrease.java`](src/main/java/xyz/benanderson/candlestickprocessor/algorithm/impl/QuadIncrease.java) - predicts that the price will increase if the closing price of the last three candlesticks all increased


## Defining Custom Algorithms

To define your prediction algorithm, create a class which implements the `Algorithm` interface and contains the required methods:
- `int getMinimumWindowSize()` - the minimum number of candlesticks that the algorithm needs in order to make a prediction.
- `int getMaximumWindowSize()` - the maximum number of candlesticks that will affect the algorithm prediction.
- `Optional<Direction> predict(CandlestickSessionWindow)` - the logic which predicts a price movement direction
  depending on the provided candlestick window. The result is wrapped in an `Optional` and can be empty to represent no prediction.

Please check current algorithm implementations in order to understand how to implement your own.

There is a default implementation to calculate whether a price movement prediction was accurate or not, by checking it
against the following candlestick. However, an algorithm can optionally override this implementation and provide its
own custom logic to determine whether its prediction was correct. This can be achieved by overriding the following
method defined in the `Algorithm` interface:
```
PredictionEvaluation evaluatePrediction(Direction, CandlestickSessionWindow, CandlestickSessionWindow)
```