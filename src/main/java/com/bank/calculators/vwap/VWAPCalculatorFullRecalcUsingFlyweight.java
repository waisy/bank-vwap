package com.bank.calculators.vwap;

import com.bank.calculators.MutableReturnCalculator;
import com.bank.instrumentref.Instrument;
import com.bank.marketdata.MarketUpdate;
import com.bank.marketdata.mutable.MutableTwoWayPrice;
import com.bank.marketdata.mutable.repository.FlyweightMutableMarketUpdateRepository;

public class VWAPCalculatorFullRecalcUsingFlyweight implements MutableReturnCalculator {

    private final VWAPCalculatorFullRecalc impl;

    public VWAPCalculatorFullRecalcUsingFlyweight(Instrument instrument) {
        this.impl = new VWAPCalculatorFullRecalc(instrument, instr -> new FlyweightMutableMarketUpdateRepository(instrument));
    }

    @Override
    public MutableTwoWayPrice applyMarketUpdate(MarketUpdate twoWayMarketPrice) {
        return impl.applyMarketUpdate(twoWayMarketPrice);
    }
}
