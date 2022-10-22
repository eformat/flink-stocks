package org.acme.data;

import java.util.ArrayList;
import java.util.List;

public class Quotes {

    private List<Quote> quotes;

    public List<Quote> getQuotes() {
        return quotes;
    }

    public Quote getQuotes(String symbol) {
        for (Quote q: getQuotes()) {
            if (q.getSymbol().equalsIgnoreCase(symbol))
                return q;
        }
        return new Quote();
    }
    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }

    public void add(Quote quote) {
        if (null == quotes) {
            quotes = new ArrayList<>();
        }
        this.quotes.add(quote);
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (Quote quote : quotes) {
            stringBuffer.append(quote.toString());
        }
        return stringBuffer.toString();
    }
}
