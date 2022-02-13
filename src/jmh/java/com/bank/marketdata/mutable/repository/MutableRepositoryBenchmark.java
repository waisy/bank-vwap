package com.bank.marketdata.mutable.repository;

import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;
import com.bank.marketdata.mutable.MutableMarketUpdate;
import com.bank.marketdata.mutable.MutableMarketUpdateDefaultImpl;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class MutableRepositoryBenchmark {
    public MutableMarketUpdateRepository repository;

    @Param({"FLYWEIGHT", "PREALLOCATED_MUTABLE"})
    private Implementations implementation;

    private MutableMarketUpdate updateToApply;

    @Setup
    public void setup() {
        if (implementation == Implementations.FLYWEIGHT) {
            repository = new FlyweightMutableMarketUpdateRepository(Instrument.INSTRUMENT0);
        } else if (implementation == Implementations.PREALLOCATED_MUTABLE) {
            repository = new PreallocatedMutableMarketUpdateRepository(Instrument.INSTRUMENT0);
        } else {
            throw new IllegalArgumentException("Unsupported impl: " + implementation);
        }

        updateToApply = new MutableMarketUpdateDefaultImpl(Market.MARKET0, Instrument.INSTRUMENT0);
        updateToApply.getTwoWayPrice().setBidPrice(1.0);
        updateToApply.getTwoWayPrice().setBidAmount(2.0);
        updateToApply.getTwoWayPrice().setOfferPrice(3.0);
        updateToApply.getTwoWayPrice().setOfferAmount(4.0);
    }

    @Benchmark
    @Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    public void applyMarketUpdate() {
        repository.applyMarketUpdate(updateToApply);
    }

    public enum Implementations {
        FLYWEIGHT,
        PREALLOCATED_MUTABLE,
    }
}
