package com.acme.stocks;

import org.acme.data.Buy;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class PulsarBuySink implements SinkFunction<Buy> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(PulsarBuySink.class);

    private String name;
    private PulsarClient pulsarClient;
    private Producer<Buy> orderProducer;

    private HashMap<String, Producer<Buy>> producers = new HashMap<>();
    private AtomicInteger count = new AtomicInteger();

    public PulsarBuySink(String name) {
        this.name = name;
    }

    public void invoke(Buy value, Context context) {
        logger.info(value.toString());

        if (! producers.containsKey(name)) {
            try {
                pulsarClient = PulsarClient.builder().serviceUrl("pulsar://localhost:6650").build();
                orderProducer
                        = createProducer(pulsarClient, name, "orders", Buy.class);
                producers.put(name, orderProducer);

            } catch (PulsarClientException e) {
                throw new RuntimeException(e);
            }
        } else {
            Producer<Buy> producer = producers.get(name);
            produceMessage(producer, name + String.valueOf(count.getAndIncrement()), value, new AtomicInteger(1));
        }
    }

    private static <T> Producer<T> createProducer(PulsarClient pulsarClient,
                                                  String producerName,
                                                  String topicName,
                                                  Class<T> classz) throws PulsarClientException {
        logger.info("Creating {} Producer ...", classz.getSimpleName());
        return pulsarClient.newProducer(JSONSchema.of(classz))
                .producerName(producerName)
                .topic(topicName)
                .blockIfQueueFull(true)
                .create();
    }

    private static <T> void produceMessage(Producer<T> producer, String key, T value, AtomicInteger counter) {
        producer.newMessage()
                .key(key)
                .value(value)
                .eventTime(System.currentTimeMillis())
                .sendAsync()
                .whenComplete(callback(counter));
    }

    private static BiConsumer<MessageId, Throwable> callback(AtomicInteger counter) {
        return (id, exception) -> {
            if (exception != null) {
                logger.error("❌ Failed message: {}", exception.getMessage());
            } else {
                logger.info("✅ Acked message {} - Total {}", id, counter.getAndIncrement());
            }
        };
    }
}
