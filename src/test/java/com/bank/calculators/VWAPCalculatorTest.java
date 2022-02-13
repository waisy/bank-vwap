package com.bank.calculators;

import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;
import com.bank.marketdata.mutable.MutableMarketUpdateDefaultImpl;
import com.bank.marketdata.mutable.MutableMarketUpdateObjectMother;
import com.bank.marketdata.mutable.repository.FlyweightMutableMarketUpdateRepository;
import com.bank.marketdata.mutable.repository.MutableMarketUpdateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


abstract class VWAPCalculatorTest {

    private final Instrument instrument = Instrument.INSTRUMENT0;
    private final MutableMarketUpdateRepository repo = spy(new FlyweightMutableMarketUpdateRepository(instrument));
    private final MutableMarketUpdateObjectMother mutableMarketUpdateObjectMother = new MutableMarketUpdateObjectMother();
    private Calculator calc;


    @BeforeEach
    public void setup(){
        calc = createCalculator(instrument, $ -> repo);
    }

    protected abstract Calculator createCalculator(Instrument instrument, Function<Instrument, ? extends MutableMarketUpdateRepository> repositoryFactory);

    @Test
    public void whenNullPassedInThenThrow() {
        assertThatThrownBy(() -> calc.applyMarketUpdate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Null price object passed in. If intention is to mark a previous price as invalid, follow the NullObject pattern");
    }

    @Test
    public void whenUpdatePassedInForWrongInstrumentThenThrow() {
        MutableMarketUpdateDefaultImpl update = new MutableMarketUpdateDefaultImpl(Market.MARKET0, Instrument.INSTRUMENT1);
        assertThatThrownBy(() -> calc.applyMarketUpdate(update))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expecting updates with instrument: INSTRUMENT0 but was: INSTRUMENT1");
    }


    @Test
    public void whenApplyCalledONCalcThenItIsPassedToRepository() {
        MutableMarketUpdateDefaultImpl update = mutableMarketUpdateObjectMother.getUpdateWithSomeValues();
        calc.applyMarketUpdate(update);

        verify(repo).applyMarketUpdate(same(update));
    }
}