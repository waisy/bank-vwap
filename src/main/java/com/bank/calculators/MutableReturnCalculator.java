package com.bank.calculators;

import com.bank.marketdata.MarketUpdate;
import com.bank.marketdata.mutable.MutableTwoWayPrice;

public interface MutableReturnCalculator extends Calculator {
    @Override
    MutableTwoWayPrice applyMarketUpdate(final MarketUpdate twoWayMarketPrice);
}
