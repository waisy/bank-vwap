package com.bank.marketdata.mutable.repository;

import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;
import com.bank.marketdata.State;
import com.bank.marketdata.mutable.MutableMarketUpdate;
import com.bank.marketdata.mutable.MutableTwoWayPrice;

import java.util.EnumMap;
import java.util.Objects;

/**
 * This class, while more tedious to write, should be fast for aggregation as it uses continuous memory (similar to column based databases).
 * It also avoids allocation for return values by reusing flyweights.
 */
public class FlyweightMutableMarketUpdateRepository extends BasePrellocatedMutableMarketUpdateRepository {

    private double[] bidPrices;
    private double[] bidAmounts;
    private double[] offerPrices;
    private double[] offerAmounts;
    private State[] states;

    private EnumMap<Market, MutableMarketUpdateFlyweight> updateFlyweights;

    public FlyweightMutableMarketUpdateRepository(Instrument instrument) {
        super(instrument);
    }

    @Override
    protected void preallocateAllMarketInstrumentPairs() {
        updateFlyweights = new EnumMap<>(Market.class);
        int rows = Market.values().length;
        bidPrices = new double[rows];
        bidAmounts = new double[rows];
        offerPrices = new double[rows];
        offerAmounts = new double[rows];
        states = new State[rows];

        for (Market market : Market.values()) {
            updateFlyweights.put(market, new MutableMarketUpdateFlyweight(market));
            bidPrices[market.ordinal()] = Double.NaN;
            bidAmounts[market.ordinal()] = Double.NaN;
            offerPrices[market.ordinal()] = Double.NaN;
            offerAmounts[market.ordinal()] = Double.NaN;
            states[market.ordinal()] = State.INDICATIVE;
        }
    }


    @Override
    public MutableMarketUpdate getUpdate(Market market) {
        Objects.requireNonNull(market);
        return updateFlyweights.get(market);
    }

    private class MutableMarketUpdateFlyweight implements MutableMarketUpdate {

        private final Market market;
        private final MutableTwoWayPriceFlyweight priceFlyweight;

        public MutableMarketUpdateFlyweight(Market market) {
            Objects.requireNonNull(market);
            this.market = market;
            this.priceFlyweight = new MutableTwoWayPriceFlyweight(market);
        }

        @Override
        public Market getMarket() {
            return market;
        }

        @Override
        public MutableTwoWayPrice getTwoWayPrice() {
            return priceFlyweight;
        }
    }

    private class MutableTwoWayPriceFlyweight implements MutableTwoWayPrice {

        private final Market market;

        public MutableTwoWayPriceFlyweight(Market market) {
            this.market = market;
        }

        @Override
        public Instrument getInstrument() {
            return instrument;
        }

        @Override
        public State getState() {
            return states[market.ordinal()];
        }

        @Override
        public void setState(State state) {
            states[market.ordinal()] = state;
        }

        @Override
        public double getBidPrice() {
            return bidPrices[market.ordinal()];
        }

        @Override
        public void setBidPrice(double bidPrice) {
            bidPrices[market.ordinal()] = bidPrice;
        }

        @Override
        public double getOfferAmount() {
            return offerAmounts[market.ordinal()];
        }

        @Override
        public void setOfferAmount(double offerAmount) {
            offerAmounts[market.ordinal()] = offerAmount;
        }

        @Override
        public double getOfferPrice() {
            return offerPrices[market.ordinal()];
        }

        @Override
        public void setOfferPrice(double offerPrice) {
            offerPrices[market.ordinal()] = offerPrice;
        }

        @Override
        public double getBidAmount() {
            return bidAmounts[market.ordinal()];
        }

        @Override
        public void setBidAmount(double bidAmount) {
            bidAmounts[market.ordinal()] = bidAmount;
        }
    }
}
