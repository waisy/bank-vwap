package com.bank.calculators.vwap;

import com.bank.calculators.MutableReturnCalculator;
import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;
import com.bank.marketdata.mutable.MutableMarketUpdate;
import com.bank.marketdata.mutable.MutableMarketUpdateDefaultImpl;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.bank.marketdata.State.FIRM;

@State(Scope.Benchmark)
public class VwapCalculatorBenchmark {

    private final Random random = new Random();
    @Param({"FLYWEIGHT", "PREALLOC_MUTABLE", "INCREMENTAL"})
    private Implementation implementation;
    private MutableReturnCalculator vwapCalc;
    private MutableMarketUpdate updateToApplyToCalculator;

    @Setup
    public void setup() {
        if (implementation == Implementation.FLYWEIGHT) {
            vwapCalc = new VWAPCalculatorFullRecalcUsingFlyweight(Instrument.INSTRUMENT0);
        } else if (implementation == Implementation.PREALLOC_MUTABLE) {
            vwapCalc = new VWAPCalculatorFullRecalcUsingMutableUpdates(Instrument.INSTRUMENT0);
        } else if (implementation == Implementation.INCREMENTAL) {
            vwapCalc = new VWAPCalculatorIncrementUsingMutableUpdates(Instrument.INSTRUMENT0);
        } else {
            throw new IllegalArgumentException("Unsupported implementation: " + implementation);
        }


        for (Market market : Market.values()) {
            vwapCalc.applyMarketUpdate(generateRandomUpdate(Instrument.INSTRUMENT0, market));
        }

        updateToApplyToCalculator = generateRandomUpdate(Instrument.INSTRUMENT0, Market.MARKET0);
    }

    private MutableMarketUpdate generateRandomUpdate(Instrument instrument, Market market) {
        MutableMarketUpdateDefaultImpl update = new MutableMarketUpdateDefaultImpl(market, instrument);
        update.getTwoWayPrice().setBidPrice(random.nextDouble());
        update.getTwoWayPrice().setBidAmount(random.nextDouble());
        update.getTwoWayPrice().setOfferPrice(random.nextDouble());
        update.getTwoWayPrice().setOfferAmount(random.nextDouble());
        update.getTwoWayPrice().setState(FIRM);
        return update;
    }

    @Benchmark
    @Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    public void calculateVwap() {
        vwapCalc.applyMarketUpdate(updateToApplyToCalculator);
    }

    public enum Implementation {
        FLYWEIGHT,
        PREALLOC_MUTABLE,
        INCREMENTAL
    }
}
