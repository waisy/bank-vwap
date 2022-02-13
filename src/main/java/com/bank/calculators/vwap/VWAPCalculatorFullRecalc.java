package com.bank.calculators.vwap;

import com.bank.calculators.Calculator;
import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;
import com.bank.marketdata.MarketUpdate;
import com.bank.marketdata.State;
import com.bank.marketdata.TwoWayPrice;
import com.bank.marketdata.mutable.MutableTwoWayPrice;
import com.bank.marketdata.mutable.MutableTwoWayPriceDefaultImpl;
import com.bank.marketdata.mutable.repository.MutableMarketUpdateRepository;

import java.util.Objects;
import java.util.function.Function;

public class VWAPCalculatorFullRecalc implements Calculator {

    private final MutableMarketUpdateRepository marketUpdateRepository;
    private final MutableTwoWayPrice recycledReturn;

    // Cache Market.values() as it allocates a new array each time.
    private final Market[] markets = Market.values();

    public VWAPCalculatorFullRecalc(Instrument instrument, Function<Instrument, ? extends MutableMarketUpdateRepository> repositoryFactory) {
        this.recycledReturn = new MutableTwoWayPriceDefaultImpl(instrument);
        this.marketUpdateRepository = repositoryFactory.apply(instrument);
    }

    @Override
    public MutableTwoWayPrice applyMarketUpdate(MarketUpdate priceUpdate) {
        Objects.requireNonNull(priceUpdate, "Null price object passed in. If intention is to mark a previous price as invalid, follow the NullObject pattern");
        if (priceUpdate.getTwoWayPrice().getInstrument() != recycledReturn.getInstrument()) {
            throw new IllegalArgumentException("Expecting updates with instrument: " + recycledReturn.getInstrument() + " but was: " + priceUpdate.getTwoWayPrice().getInstrument());
        }


        marketUpdateRepository.applyMarketUpdate(priceUpdate);

        calculateNewVwapPrice(recycledReturn);
        setNewStateOn(recycledReturn);

        return recycledReturn;
    }

    private void setNewStateOn(MutableTwoWayPrice dst) {
        dst.setState(marketUpdateRepository.existsMarketWithIndicativePrice() ? State.INDICATIVE : State.FIRM);
    }

    private void calculateNewVwapPrice(MutableTwoWayPrice dst) {
        double bidVwap = 0;
        double bidTotalAmount = 0;
        double offerVwap = 0;
        double offerTotalAmount = 0;

        for (Market market : markets) {

            MarketUpdate update = marketUpdateRepository.getUpdate(market);
            TwoWayPrice book = update.getTwoWayPrice();

            if (!Double.isNaN(book.getBidPrice()) && !Double.isNaN(book.getBidAmount())) {
                bidVwap += book.getBidPrice() * book.getBidAmount();
                bidTotalAmount += book.getBidAmount();
            }

            if (!Double.isNaN(book.getOfferPrice()) && !Double.isNaN(book.getOfferAmount())) {
                offerVwap += book.getOfferPrice() * book.getOfferAmount();
                offerTotalAmount += book.getOfferAmount();
            }
        }

        bidVwap /= bidTotalAmount;
        offerVwap /= offerTotalAmount;

        dst.setBidPrice(bidVwap);
        dst.setBidAmount(bidTotalAmount);
        dst.setOfferPrice(offerVwap);
        dst.setOfferAmount(offerTotalAmount);
    }
}
