package com.bank.calculators.vwap;

import com.bank.calculators.MutableReturnCalculator;
import com.bank.instrumentref.Instrument;
import com.bank.marketdata.MarketUpdate;
import com.bank.marketdata.mutable.MutableTwoWayPrice;
import com.bank.marketdata.mutable.repository.PreallocatedMutableMarketUpdateRepository;

public class VWAPCalculatorIncrementUsingMutableUpdates implements MutableReturnCalculator {

    private final VWAPCalculatorIncremental impl;

    public VWAPCalculatorIncrementUsingMutableUpdates(Instrument instrument) {
        this.impl = new VWAPCalculatorIncremental(instrument, instr -> new PreallocatedMutableMarketUpdateRepository(instrument));
    }

    @Override
    public MutableTwoWayPrice applyMarketUpdate(MarketUpdate twoWayMarketPrice) {
        return impl.applyMarketUpdate(twoWayMarketPrice);
    }

}
