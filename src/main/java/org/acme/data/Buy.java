package org.acme.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Buy {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(Buy.class);

    private Quote quote;

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public Buy() {
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Buy buy = (Buy) o;
        return Objects.equals(quote, buy.quote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quote);
    }

    @Override
    public String toString() {
        return "Buy{" +
                "quote=" + quote +
                '}';
    }
}
