package xyz.benanderson.candlestickprocessor.data.fetcher;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import xyz.benanderson.candlestickprocessor.data.Candlestick;
import xyz.benanderson.candlestickprocessor.data.CandlestickSession;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

public class CryptoCompareFetcher implements CandlestickFetcher {

    private final String ticker;

    public CryptoCompareFetcher(String ticker) {
        this.ticker = ticker.toUpperCase();
    }

    @Override
    public String getFetcherName() {
        return "CryptoCompareFetcher-" + ticker;
    }

    @Override
    public CandlestickSession fetchCandlestickSession() {
        List<Candlestick> candlesticks = new LinkedList<>();
        int candlestickCount = 2000;

        String apiUrl = "https://min-api.cryptocompare.com/data/v2/histominute?fsym=" + ticker + "&tsym=USD&limit=" + candlestickCount;
        try {
            // Make a GET request to the API endpoint
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // Read the response and convert it to a JSON object
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(in, JsonObject.class);

            // Extract the OHLCV data from the JSON object
            JsonArray data = jsonResponse.getAsJsonObject("Data").getAsJsonArray("Data");
            for (JsonElement element : data) {
                JsonObject item = element.getAsJsonObject();
                Instant time = Instant.ofEpochSecond(item.get("time").getAsLong());
                double open = item.get("open").getAsDouble();
                double close = item.get("close").getAsDouble();
                double low = item.get("low").getAsDouble();
                double high = item.get("high").getAsDouble();
                double volume = item.get("volumefrom").getAsDouble(); //volume of crypto
                candlesticks.add(new Candlestick(
                        time.minus(60, ChronoUnit.SECONDS),
                        time,
                        open,
                        close,
                        low,
                        high,
                        volume
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CandlestickSession(candlesticks);
    }

}
