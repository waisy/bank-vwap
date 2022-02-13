package com.bank.marketdata.mutable;

import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;
import com.bank.marketdata.State;
import com.bank.marketdata.TwoWayPrice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

abstract class MutableMarketUpdateTest {

    private final MutableTwoWayPriceObjectMother mutableTwoWayPriceObjectMother = new MutableTwoWayPriceObjectMother();
    private MutableMarketUpdate update;

    @BeforeEach
    public void setup() {
        update = createUpdate(Market.MARKET0, Instrument.INSTRUMENT0);
    }

    protected abstract MutableMarketUpdate createUpdate(Market market, Instrument instrument);

    @Test
    public void nullMarketOrInstrumentShouldThrownNPE() {
        assertThatThrownBy(() -> createUpdate(null, Instrument.INSTRUMENT0)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> createUpdate(Market.MARKET0, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void getMarketReturnsMarket() {
        assertThat(update.getMarket()).isEqualTo(Market.MARKET0);
    }

    @Test
    public void initialTwoWayPriceIsDefaultMutableTwoWayPrice() {
        assertThat(update.getTwoWayPrice())
                .extracting(TwoWayPrice::getInstrument,
                        TwoWayPrice::getBidPrice, TwoWayPrice::getBidAmount,
                        TwoWayPrice::getOfferPrice, TwoWayPrice::getOfferAmount,
                        TwoWayPrice::getState).containsExactly(Instrument.INSTRUMENT0,
                        Double.NaN, Double.NaN,
                        Double.NaN, Double.NaN,
                        State.INDICATIVE);
    }

    @Test
    public void copyFromRejectsSrcWithDifferentMarket() {
        MutableMarketUpdate dst = createUpdate(Market.MARKET3, Instrument.INSTRUMENT0);
        assertThatThrownBy(() -> dst.copyFrom(update)).isInstanceOf(IllegalArgumentException.class).hasMessage("Source object market: %s does not match market on this object: %s", update.getMarket(), dst.getMarket());

    }

    @Test
    public void copyFromResultsInDeepEquals() {
        MutableMarketUpdate src = createUpdate(update.getMarket(), update.getTwoWayPrice().getInstrument());
        MutableTwoWayPrice srcPrice = src.getTwoWayPrice();
        mutableTwoWayPriceObjectMother.setValueSet1On(srcPrice);

        update.copyFrom(src);

        // reflection based, deep equals checker
        assertThat(update)
                .usingRecursiveComparison()
                .isEqualTo(src);
    }
}