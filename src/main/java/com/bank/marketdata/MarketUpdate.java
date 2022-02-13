package com.bank.marketdata;

import com.bank.instrumentref.Market;

public interface MarketUpdate {
    Market getMarket();

    TwoWayPrice getTwoWayPrice();
}
