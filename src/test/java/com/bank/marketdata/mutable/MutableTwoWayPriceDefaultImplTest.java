package com.bank.marketdata.mutable;

import com.bank.instrumentref.Instrument;

public class MutableTwoWayPriceDefaultImplTest extends MutableTwoWayPriceTest {
    @Override
    protected MutableTwoWayPrice createPrice(Instrument instrument) {
        return new MutableTwoWayPriceDefaultImpl(instrument);
    }
}
