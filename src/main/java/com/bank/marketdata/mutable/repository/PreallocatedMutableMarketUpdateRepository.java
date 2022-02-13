package com.bank.marketdata.mutable.repository;

import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;
import com.bank.marketdata.mutable.MutableMarketUpdate;
import com.bank.marketdata.mutable.MutableMarketUpdateDefaultImpl;

import java.util.EnumMap;

public class PreallocatedMutableMarketUpdateRepository extends BasePrellocatedMutableMarketUpdateRepository {

    private EnumMap<Market, MutableMarketUpdate> updates;

    public PreallocatedMutableMarketUpdateRepository(Instrument instrument) {
        super(instrument);
    }

    @Override
    protected void preallocateAllMarketInstrumentPairs() {
        updates = new EnumMap<>(Market.class);
        for (Market market : Market.values()) {
            updates.put(market, new MutableMarketUpdateDefaultImpl(market, instrument));
        }
    }

    @Override
    public MutableMarketUpdate getUpdate(Market market) {
        return updates.get(market);
    }
}
