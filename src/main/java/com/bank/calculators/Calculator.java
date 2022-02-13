package com.bank.calculators;

import com.bank.marketdata.MarketUpdate;
import com.bank.marketdata.TwoWayPrice;

public interface Calculator {
    TwoWayPrice applyMarketUpdate(final MarketUpdate twoWayMarketPrice);
}
