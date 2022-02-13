package com.bank.marketdata.mutable;

import com.bank.marketdata.MarketUpdate;

public interface MutableMarketUpdate extends MarketUpdate {

    @Override
    MutableTwoWayPrice getTwoWayPrice();

    default void copyFrom(MarketUpdate src) {
        if (src.getMarket() != this.getMarket()) {
            throw new IllegalArgumentException("Source object market: " + src.getMarket() + " does not match market on this object: " + getMarket());
        }
        getTwoWayPrice().copyFrom(src.getTwoWayPrice());
    }
}
