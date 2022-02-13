package com.bank.marketdata.mutable;

import com.bank.instrumentref.Instrument;
import com.bank.marketdata.State;

import java.util.Objects;

public class MutableTwoWayPriceDefaultImpl implements MutableTwoWayPrice {

    private final Instrument instrument;

    private double bidPrice = Double.NaN;
    private double bidAmount = Double.NaN;
    private double offerPrice = Double.NaN;
    private double offerAmount = Double.NaN;

    private State state = State.INDICATIVE;

    public MutableTwoWayPriceDefaultImpl(Instrument instrument) {
        Objects.requireNonNull(instrument);
        this.instrument = instrument;
    }

    @Override
    public Instrument getInstrument() {
        return instrument;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public double getBidPrice() {
        return bidPrice;
    }

    @Override
    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    @Override
    public double getBidAmount() {
        return bidAmount;
    }

    @Override
    public void setBidAmount(double bidAmount) {
        this.bidAmount = bidAmount;
    }

    @Override
    public double getOfferPrice() {
        return offerPrice;
    }

    @Override
    public void setOfferPrice(double offerPrice) {
        this.offerPrice = offerPrice;
    }

    @Override
    public double getOfferAmount() {
        return offerAmount;
    }

    @Override
    public void setOfferAmount(double offerAmount) {
        this.offerAmount = offerAmount;
    }

}
