package com.bank.marketdata.mutable;

import com.bank.instrumentref.Instrument;
import com.bank.marketdata.mutable.repository.FlyweightMutableMarketUpdateRepository;
import com.bank.marketdata.mutable.repository.MutableMarketUpdateRepository;

class FlyweightMutableMarketUpdateRepositoryTest extends MutableMarketUpdateRepositoryTest {

    @Override
    protected MutableMarketUpdateRepository constructRepo(Instrument instrument) {
        return new FlyweightMutableMarketUpdateRepository(instrument);
    }
}