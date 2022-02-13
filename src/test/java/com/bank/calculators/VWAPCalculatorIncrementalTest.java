package com.bank.calculators;

import com.bank.calculators.vwap.VWAPCalculatorIncremental;
import com.bank.instrumentref.Instrument;
import com.bank.marketdata.mutable.repository.MutableMarketUpdateRepository;

import java.util.function.Function;

public class VWAPCalculatorIncrementalTest extends VWAPCalculatorTest {
    @Override
    protected VWAPCalculatorIncremental createCalculator(Instrument instrument, Function<Instrument, ? extends MutableMarketUpdateRepository> repositoryFactory) {
        return new VWAPCalculatorIncremental(instrument, repositoryFactory);
    }
}
