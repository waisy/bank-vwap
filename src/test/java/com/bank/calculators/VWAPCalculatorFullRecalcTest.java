package com.bank.calculators;

import com.bank.calculators.vwap.VWAPCalculatorFullRecalc;
import com.bank.instrumentref.Instrument;
import com.bank.marketdata.mutable.repository.MutableMarketUpdateRepository;

import java.util.function.Function;

public class VWAPCalculatorFullRecalcTest extends VWAPCalculatorTest{
    @Override
    protected VWAPCalculatorFullRecalc createCalculator(Instrument instrument, Function<Instrument, ? extends MutableMarketUpdateRepository> repositoryFactory) {
        return new VWAPCalculatorFullRecalc(instrument, repositoryFactory);
    }
}
