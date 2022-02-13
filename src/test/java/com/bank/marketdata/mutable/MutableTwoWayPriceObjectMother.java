package com.bank.marketdata.mutable;

import com.bank.marketdata.State;

public class MutableTwoWayPriceObjectMother {

    public void setValueSet1On(MutableTwoWayPrice price) {
        price.setBidPrice(1.1);
        price.setOfferPrice(2.1);
        price.setBidAmount(3.1);
        price.setOfferAmount(4.1);
        price.setState(State.INDICATIVE);
    }
}
