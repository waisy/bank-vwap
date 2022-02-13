package com.bank.marketdata.mutable;

import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;
import com.bank.marketdata.mutable.repository.FlyweightMutableMarketUpdateRepository;

public class MutableMarketUpdateFlyweightImplTest extends MutableMarketUpdateTest {

    @Override
    protected MutableMarketUpdate createUpdate(Market market, Instrument instrument) {
        FlyweightMutableMarketUpdateRepository repo = new FlyweightMutableMarketUpdateRepository(instrument);
        return repo.getUpdate(market);
    }
}
