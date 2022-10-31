package com.acme.stocks;

import org.acme.data.Buy;
import org.acme.data.Quote;
import org.acme.data.Quotes;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

public class StockBuyer {

    static Logger logger = LoggerFactory.getLogger(StockBuyer.class);

    private static DataStream<Quote> createSourceFromApplicationProperties(final StreamExecutionEnvironment env, String symbol) {
        return env.addSource(new SSESource(logger))
                .map(new MapFunction<String, Quote>() {
                    private String[] tokens;

                    @Override
                    public Quote map(String value) throws Exception {
                        Jsonb jsonb = JsonbBuilder.create();
                        Quotes quotes = jsonb.fromJson(value, Quotes.class);
                        return quotes.getQuotes(symbol);
                    }
                })
                .name("SSE Source " + symbol)
                .uid("SSE Source " + symbol)
                .setParallelism(1);
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment("localhost", 8081, "target/flink-stocks-0.1.jar");

        DataStream<Quote> nflxPrices = createSourceFromApplicationProperties(env, "NFLX");
        DataStream<Quote> rhtPrices = createSourceFromApplicationProperties(env, "RHT");

        DataStream<Buy> nflx = nflxPrices
                .keyBy(Quote::getSymbol)
                .process(new BuyFunction())
                .name("NFLX quote");

        nflx
                .addSink(new BuySink())
                //.addSink(new PulsarBuySink("NFLX"))
                .name("NFLX buy");

        DataStream<Buy> rht = rhtPrices
                .keyBy(Quote::getSymbol)
                .process(new BuyFunction())
                .name("RHT quote");

        rht
                .addSink(new BuySink())
                //.addSink(new PulsarBuySink("RHT"))
                .name("RHT buy");

        env.execute("Stock Buyer");
    }
}
