package com.bank.marketdata;

import com.bank.instrumentref.Market;

/**
 * Assumes a single instrument
 */
public interface MarketUpdateRepository {
    MarketUpdate getUpdate(Market market);

    boolean existsMarketWithIndicativePrice();
}
