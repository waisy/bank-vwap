package com.bank.marketdata.mutable;

import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;
import com.bank.marketdata.MarketUpdate;
import com.bank.marketdata.TwoWayPrice;
import com.bank.marketdata.mutable.repository.MutableMarketUpdateRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.cartesian.CartesianTest;

import static org.assertj.core.api.Assertions.assertThat;

abstract class MutableMarketUpdateRepositoryTest {

    private final MutableMarketUpdateObjectMother mutableMarketUpdateObjectMother = new MutableMarketUpdateObjectMother();
    private final MutableMarketUpdateDefaultImpl DUMMY_UPDATE = mutableMarketUpdateObjectMother.getUpdateWithSomeValues();
    private MutableMarketUpdateRepository repo;

    @BeforeEach
    public void setup() {
        repo = constructRepo(DUMMY_UPDATE.getTwoWayPrice().getInstrument());
    }

    protected abstract MutableMarketUpdateRepository constructRepo(Instrument instrument);


    @CartesianTest
    public void getUpdateReturnsDoesNotReturnNullForAnyMarketInstrumentPair(@CartesianTest.Enum Market market) {
        assertThat(repo.getUpdate(market)).isNotNull();
    }

    @Test
    public void whenMarketUpdateAppliedThenItIsCopiedAndNotStored() {
        repo.applyMarketUpdate(DUMMY_UPDATE);

        MutableMarketUpdate repoCopy = repo.getUpdate(DUMMY_UPDATE.getMarket());
        Assertions.assertNotSame(DUMMY_UPDATE, repoCopy);

        MutableTwoWayPriceDefaultImpl dummyPrx = DUMMY_UPDATE.getTwoWayPrice();

        assertThat(repoCopy).extracting(MarketUpdate::getMarket).isEqualTo((DUMMY_UPDATE.getMarket()));
        assertThat(repoCopy.getTwoWayPrice())
                .extracting(TwoWayPrice::getInstrument, TwoWayPrice::getBidAmount, TwoWayPrice::getOfferAmount, TwoWayPrice::getBidPrice, TwoWayPrice::getOfferPrice, TwoWayPrice::getState)
                .containsExactly(dummyPrx.getInstrument(), dummyPrx.getBidAmount(), dummyPrx.getOfferAmount(), dummyPrx.getBidPrice(), dummyPrx.getOfferPrice(), dummyPrx.getState());

    }
}