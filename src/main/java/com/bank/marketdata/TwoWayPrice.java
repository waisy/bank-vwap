package com.bank.marketdata;


import com.bank.instrumentref.Instrument;

public interface TwoWayPrice {
    Instrument getInstrument();

    State getState();

    double getBidPrice();

    double getOfferAmount();

    double getOfferPrice();

    double getBidAmount();
}
