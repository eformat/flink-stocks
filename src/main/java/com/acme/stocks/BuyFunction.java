package com.acme.stocks;

import org.acme.data.Buy;
import org.acme.data.Quote;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuyFunction
        extends KeyedProcessFunction<String, Quote, Buy> {

    Logger logger = LoggerFactory.getLogger(StockBuyer.class);

    private transient ValueState<Boolean> shouldBuy;

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<Boolean> flagDescriptor = new ValueStateDescriptor<>(
                "flag",
                Types.BOOLEAN);
        shouldBuy = getRuntimeContext().getState(flagDescriptor);
    }

    @Override
    public void processElement(Quote quote, Context context, Collector<Buy> collector) throws Exception {
        // Get the current state for the current key
        Boolean lastBuyRecommendation = shouldBuy.value();

        if (quote.getBid() > quote.getAsk()) {
            // Set the flag to true
            shouldBuy.update(true);

            // Buy Low, Sell High !
            // If you want to buy a share, you have to pay the ask price.
            // If you want to sell shares, you'll receive the bid price.

            // only buy if two buy recommendations in a row
            //if (lastBuyRecommendation != null && lastBuyRecommendation) {
                Buy buy = new Buy();
                buy.setQuote(quote);
                collector.collect(buy);
            //}
        } else {
            shouldBuy.update(false);
        }
    }

}
