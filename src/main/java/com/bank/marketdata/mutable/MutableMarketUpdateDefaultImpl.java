package com.bank.marketdata.mutable;

import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;

import java.util.Objects;

public class MutableMarketUpdateDefaultImpl implements MutableMarketUpdate {

    private final Market market;
    private final MutableTwoWayPriceDefaultImpl price;

    public MutableMarketUpdateDefaultImpl(Market market, Instrument instrument) {
        Objects.requireNonNull(market);
        Objects.requireNonNull(instrument);
        this.market = market;
        this.price = new MutableTwoWayPriceDefaultImpl(instrument);
    }

    @Override
    public Market getMarket() {
        return market;
    }

    @Override
    public MutableTwoWayPriceDefaultImpl getTwoWayPrice() {
        return price;
    }

}
