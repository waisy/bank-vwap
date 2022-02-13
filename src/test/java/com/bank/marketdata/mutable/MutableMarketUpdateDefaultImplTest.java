package com.bank.marketdata.mutable;

import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;

public class MutableMarketUpdateDefaultImplTest extends MutableMarketUpdateTest {
    @Override
    protected MutableMarketUpdate createUpdate(Market market, Instrument instrument) {
        return new MutableMarketUpdateDefaultImpl(market, instrument);
    }
}
