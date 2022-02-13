package com.bank.marketdata.mutable.repository;

import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;
import com.bank.marketdata.MarketUpdate;
import com.bank.marketdata.State;
import com.bank.marketdata.mutable.MutableMarketUpdate;

import java.util.EnumSet;
import java.util.Objects;

public abstract class BasePrellocatedMutableMarketUpdateRepository implements MutableMarketUpdateRepository {

    protected final Instrument instrument;
    private final EnumSet<Market> marketsWithIndicativePrice = EnumSet.noneOf(Market.class);

    public BasePrellocatedMutableMarketUpdateRepository(Instrument instrument) {
        Objects.requireNonNull(instrument);
        this.instrument = instrument;
        preallocateAllMarketInstrumentPairs();
    }

    protected abstract void preallocateAllMarketInstrumentPairs();

    @Override
    public abstract MutableMarketUpdate getUpdate(Market market);

    @Override
    public void applyMarketUpdate(MarketUpdate update) {
        updateIndicativePriceSet(update);
        copyNewUpdate(update);
    }

    private void copyNewUpdate(MarketUpdate update) {
        getUpdate(update.getMarket()).copyFrom(update);
    }

    private void updateIndicativePriceSet(MarketUpdate update) {
        if (update.getTwoWayPrice().getState() == State.INDICATIVE) {
            marketsWithIndicativePrice.add(update.getMarket());
        } else {
            marketsWithIndicativePrice.remove(update.getMarket());
        }
    }

    public boolean existsMarketWithIndicativePrice() {
        return !marketsWithIndicativePrice.isEmpty();
    }

}
