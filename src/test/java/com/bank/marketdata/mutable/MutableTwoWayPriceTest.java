package com.bank.marketdata.mutable;

import com.bank.instrumentref.Instrument;
import com.bank.marketdata.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

abstract class MutableTwoWayPriceTest {

    private final MutableTwoWayPriceObjectMother mutableTwoWayPriceObjectMother = new MutableTwoWayPriceObjectMother();
    private MutableTwoWayPrice price;

    @BeforeEach
    public void setup() {
        price = createPrice(Instrument.INSTRUMENT0);
    }

    protected abstract MutableTwoWayPrice createPrice(Instrument instrument);

    @Test
    public void instrumentMustBeNonNull() {
        assertThatThrownBy(() -> createPrice(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void bidAndOfferPriceAndAmountAreNaNByDefault() {
        assertThat(price.getBidPrice()).isNaN();
        assertThat(price.getBidAmount()).isNaN();
        assertThat(price.getOfferPrice()).isNaN();
        assertThat(price.getOfferAmount()).isNaN();
    }

    @Test
    public void stateIsIndiativeByDefault() {
        assertThat(price.getState())
                .as("Default state should be INDICATIVE as it is not tradeable")
                .isEqualTo(State.INDICATIVE);
    }

    @Test
    public void bidOfferPriceAmntAndStateSettersSetTheValues() {
        price.setBidPrice(1.1);
        price.setOfferPrice(2.1);
        price.setBidAmount(3.1);
        price.setOfferAmount(4.1);
        price.setState(State.INDICATIVE);

        assertThat(price.getBidPrice()).isEqualTo(1.1);
        assertThat(price.getOfferPrice()).isEqualTo(2.1);
        assertThat(price.getBidAmount()).isEqualTo(3.1);
        assertThat(price.getOfferAmount()).isEqualTo(4.1);
        assertThat(price.getState()).isEqualTo(State.INDICATIVE);
    }

    @Test
    public void copyFromThrowsIfCopyingPriceOfDifferentInstrument() {
        MutableTwoWayPrice dst = createPrice(Instrument.INSTRUMENT17);
        assertThatThrownBy(() -> dst.copyFrom(price))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Copying from a price with a different instrument is not allowed. Instrument for this price is: %s but source instrument is: %s", Instrument.INSTRUMENT17, price.getInstrument());
    }

    @Test
    public void copyFromResultsInDeepEquals() {
        mutableTwoWayPriceObjectMother.setValueSet1On(price);

        MutableTwoWayPrice dst = createPrice(price.getInstrument());
        dst.copyFrom(price);

        // reflection based, deep equals checker
        assertThat(dst)
                .usingRecursiveComparison()
                .isEqualTo(price);
    }
}