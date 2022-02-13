package com.bank.marketdata.mutable;

import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;

public class MutableMarketUpdateObjectMother {

    private final MutableTwoWayPriceObjectMother twoWayPriceObjectMother = new MutableTwoWayPriceObjectMother();

    public MutableMarketUpdateDefaultImpl getUpdateWithSomeValues() {
        MutableMarketUpdateDefaultImpl ret = new MutableMarketUpdateDefaultImpl(Market.MARKET0, Instrument.INSTRUMENT0);
        twoWayPriceObjectMother.setValueSet1On(ret.getTwoWayPrice());
        return ret;
    }
}
