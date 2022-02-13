package com.bank.marketdata.mutable;

import com.bank.marketdata.State;
import com.bank.marketdata.TwoWayPrice;

public interface MutableTwoWayPrice extends TwoWayPrice {
    void setBidPrice(double bidPrice);

    void setBidAmount(double bidAmount);

    void setOfferPrice(double offerPrice);

    void setOfferAmount(double offerAmount);

    void setState(State state);

    default void copyFrom(TwoWayPrice src) {
        if (src.getInstrument() != this.getInstrument()) {
            throw new IllegalArgumentException("Copying from a price with a different instrument is not allowed. Instrument for this price is: " + getInstrument() + " but source instrument is: " + src.getInstrument());
        }

        setBidAmount(src.getBidAmount());
        setBidPrice(src.getBidPrice());
        setOfferPrice(src.getOfferPrice());
        setOfferAmount(src.getOfferAmount());
        setState(src.getState());
    }
}
