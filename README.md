# flink-stocks

Flink Stocks - Make Buy stock recommendations based on Buying Low and Selling High !

The `BuyFunction` evaluates stock quotes to collect buy recommendations.

The Server Sent Event app generates quotes - https://github.com/eformat/quote-generator

We use Flink Data Stream API to process these events.

## A Better BuyFunction

Using stream state we can implement a function where we only buy of the previous bid > ask as well, in `BuyFunction.java` uncomment

```java
    if (lastBuyRecommendation != null && lastBuyRecommendation) {
        // Buy
    }
```

## Pulsar Sink

Swap over to using the Pulsar Sink connector - in `StockBuyer.java` uncomment the following:
```java
    .addSink(new PulsarBuySink("NFLX"))
    .addSink(new PulsarBuySink("RHT"))
```

Run a local pulsar standalone instance:
```bash
podman run -it -p 6650:6650 -p 8081:8080 --rm --name pulsar docker.io/apachepulsar/pulsar:2.10.2 bin/pulsar standalone
```

Consume Buy orders:
```bash
podman exec -i pulsar bin/pulsar-client consume -s my-subscription -n 0 persistent://public/default/orders
```
