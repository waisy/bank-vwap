package com.bank.calculators.vwap;

import com.bank.calculators.Calculator;
import com.bank.instrumentref.Instrument;
import com.bank.marketdata.MarketUpdate;
import com.bank.marketdata.State;
import com.bank.marketdata.TwoWayPrice;
import com.bank.marketdata.mutable.MutableMarketUpdate;
import com.bank.marketdata.mutable.MutableTwoWayPrice;
import com.bank.marketdata.mutable.MutableTwoWayPriceDefaultImpl;
import com.bank.marketdata.mutable.repository.MutableMarketUpdateRepository;

import java.util.Objects;
import java.util.function.Function;

public class VWAPCalculatorIncremental implements Calculator {

    private final MutableMarketUpdateRepository marketUpdateRepository;
    private final MutableTwoWayPrice recycledReturnValue;

    public VWAPCalculatorIncremental(Instrument instrument, Function<Instrument, ? extends MutableMarketUpdateRepository> repositoryFactory) {
        this.recycledReturnValue = new MutableTwoWayPriceDefaultImpl(instrument);
        this.marketUpdateRepository = repositoryFactory.apply(instrument);
    }

    @Override
    public MutableTwoWayPrice applyMarketUpdate(MarketUpdate priceUpdate) {
        Objects.requireNonNull(priceUpdate, "Null price object passed in. If intention is to mark a previous price as invalid, follow the NullObject pattern");
        if (priceUpdate.getTwoWayPrice().getInstrument() != recycledReturnValue.getInstrument()) {
            throw new IllegalArgumentException("Expecting updates with instrument: " + recycledReturnValue.getInstrument() + " but was: " + priceUpdate.getTwoWayPrice().getInstrument());
        }

        calculateNewVwapPrice(priceUpdate);
        marketUpdateRepository.applyMarketUpdate(priceUpdate);

        setNewStateOn(recycledReturnValue);
        return recycledReturnValue;
    }

    private void setNewStateOn(MutableTwoWayPrice dst) {
        dst.setState(marketUpdateRepository.existsMarketWithIndicativePrice() ? State.INDICATIVE : State.FIRM);
    }

    private void calculateNewVwapPrice(MarketUpdate marketUpdate) {

        MutableMarketUpdate oldUpdate = marketUpdateRepository.getUpdate(marketUpdate.getMarket());
        TwoWayPrice oldPrice = oldUpdate.getTwoWayPrice();
        TwoWayPrice newPrice = marketUpdate.getTwoWayPrice();

        double bidPriceVwap;
        double bidAmount;
        double offerPriceVwap;
        double offerAmount;

        if (!Double.isNaN(oldPrice.getBidPrice())) {
            bidPriceVwap = recycledReturnValue.getBidPrice() * recycledReturnValue.getBidAmount();
            bidPriceVwap -= oldPrice.getBidPrice() * oldPrice.getBidAmount();
            bidAmount = recycledReturnValue.getBidAmount() - oldPrice.getBidAmount();
        } else {
            bidPriceVwap = zeroIfNan(recycledReturnValue.getBidPrice());
            bidAmount = zeroIfNan(recycledReturnValue.getBidAmount());
            bidPriceVwap *= bidAmount;
        }


        if (!Double.isNaN(newPrice.getBidPrice()) && !Double.isNaN(newPrice.getBidAmount())) {
            bidPriceVwap += newPrice.getBidPrice() * newPrice.getBidAmount();
            bidAmount += newPrice.getBidAmount();
        }

        if (!Double.isNaN(oldPrice.getOfferPrice())) {
            offerPriceVwap = recycledReturnValue.getOfferPrice() * recycledReturnValue.getOfferAmount();
            offerPriceVwap -= oldPrice.getOfferPrice() * oldPrice.getOfferAmount();
            offerAmount = recycledReturnValue.getOfferAmount() - oldPrice.getOfferAmount();
        } else {
            offerPriceVwap = zeroIfNan(recycledReturnValue.getOfferPrice());
            offerAmount = zeroIfNan(recycledReturnValue.getOfferAmount());
            offerPriceVwap *= offerAmount;
        }

        if (!Double.isNaN(newPrice.getOfferPrice()) && !Double.isNaN(newPrice.getOfferAmount())) {
            offerPriceVwap += newPrice.getOfferPrice() * newPrice.getOfferAmount();
            offerAmount += newPrice.getOfferAmount();
        }

        bidPriceVwap /= bidAmount;
        offerPriceVwap /= offerAmount;


        recycledReturnValue.setBidPrice(bidPriceVwap);
        recycledReturnValue.setBidAmount(bidAmount);
        recycledReturnValue.setOfferPrice(offerPriceVwap);
        recycledReturnValue.setOfferAmount(offerAmount);
    }

    private double zeroIfNan(double x) {
        if (Double.isNaN(x)) {
            return 0;
        } else
            return x;
    }
}
