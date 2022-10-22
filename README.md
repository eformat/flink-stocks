# flink-stocks

Flink Stocks - Make Buy stock recommendations based on Buying Low and Selling High !

The `BuyFunction` evaluates stock quotes to collect buy recommendations.

The Server Sent Event app generates quotes - https://github.com/eformat/quote-generator

We use Flink Data Stream API to process these events.
