package com.bank.marketdata.mutable;

import com.bank.instrumentref.Instrument;
import com.bank.marketdata.mutable.repository.MutableMarketUpdateRepository;
import com.bank.marketdata.mutable.repository.PreallocatedMutableMarketUpdateRepository;

class PreallocatedMutableMarketUpdateRepositoryTest extends MutableMarketUpdateRepositoryTest {

    @Override
    public MutableMarketUpdateRepository constructRepo(Instrument instrument) {
        return new PreallocatedMutableMarketUpdateRepository(instrument);
    }
}