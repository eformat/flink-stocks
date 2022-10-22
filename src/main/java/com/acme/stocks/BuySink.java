package com.acme.stocks;

import org.acme.data.Buy;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuySink implements SinkFunction<Buy> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(BuySink.class);

    public BuySink() {
    }

    public void invoke(Buy value, SinkFunction.Context context) {
        logger.info(value.toString());
    }

}
