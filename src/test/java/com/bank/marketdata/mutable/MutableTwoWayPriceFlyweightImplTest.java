package com.bank.marketdata.mutable;

import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;
import com.bank.marketdata.mutable.repository.FlyweightMutableMarketUpdateRepository;

public class MutableTwoWayPriceFlyweightImplTest extends MutableTwoWayPriceTest {
    @Override
    protected MutableTwoWayPrice createPrice(Instrument instrument) {
        FlyweightMutableMarketUpdateRepository repo = new FlyweightMutableMarketUpdateRepository(instrument);
        return repo.getUpdate(Market.MARKET0).getTwoWayPrice();
    }
}
