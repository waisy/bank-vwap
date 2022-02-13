package com.bank.marketdata.mutable.repository;

import com.bank.instrumentref.Market;
import com.bank.marketdata.MarketUpdate;
import com.bank.marketdata.MarketUpdateRepository;
import com.bank.marketdata.mutable.MutableMarketUpdate;

public interface MutableMarketUpdateRepository extends MarketUpdateRepository {
    void applyMarketUpdate(MarketUpdate priceUpdate);

    MutableMarketUpdate getUpdate(Market market);
}
