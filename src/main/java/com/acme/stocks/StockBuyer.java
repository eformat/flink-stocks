package com.acme.stocks;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockBuyer {

    static String url = "http://localhost:8080/quotes/stream";
    static Logger logger = LoggerFactory.getLogger(StockBuyer.class);

    private static DataStream<String> createSourceFromApplicationProperties(final StreamExecutionEnvironment env) {
        return env.addSource(new SSESource(logger)).name("SSE Source").uid("SSE Source").setParallelism(1);
    }

    public static void main(String[] args) throws Exception {
        //StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment("localhost", 8081, "target/flink-stocks-0.1.jar");

        DataStream<String> input = createSourceFromApplicationProperties(env);
        logger.info("SseEventSource Input stream created: " + input.toString());
        input.addSink(new PrintSinkFunction<>());
        //input.print();
        env.execute("Stock Buyer");

    }
}
