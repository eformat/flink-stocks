package org.acme.data;

import java.util.Objects;

public class Quote {

    private String exchange;
    private String name;
    private long period;
    private String symbol;
    private int stocks;
    private double price;
    private double bid;
    private double ask;
    private int share;
    private double value;
    private int volume;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getStocks() {
        return stocks;
    }

    public void setStocks(int stocks) {
        this.stocks = stocks;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public int getShare() {
        return share;
    }

    public void setShare(int share) {
        this.share = share;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quote quote = (Quote) o;
        return period == quote.period && stocks == quote.stocks && Double.compare(quote.price, price) == 0 && Double.compare(quote.bid, bid) == 0 && Double.compare(quote.ask, ask) == 0 && share == quote.share && Double.compare(quote.value, value) == 0 && volume == quote.volume && Objects.equals(exchange, quote.exchange) && Objects.equals(name, quote.name) && Objects.equals(symbol, quote.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exchange, name, period, symbol, stocks, price, bid, ask, share, value, volume);
    }

    @Override
    public String toString() {
        return "Quote{" +
                "exchange='" + exchange + '\'' +
                ", name='" + name + '\'' +
                ", period=" + period +
                ", symbol='" + symbol + '\'' +
                ", stocks=" + stocks +
                ", price=" + price +
                ", bid=" + bid +
                ", ask=" + ask +
                ", share=" + share +
                ", value=" + value +
                ", volume=" + volume +
                '}';
    }
}
