package com.bank.calculators;

import com.bank.calculators.vwap.VWAPCalculatorFullRecalcUsingFlyweight;
import com.bank.calculators.vwap.VWAPCalculatorFullRecalcUsingMutableUpdates;
import com.bank.calculators.vwap.VWAPCalculatorIncrementUsingMutableUpdates;
import com.bank.instrumentref.Instrument;
import com.bank.instrumentref.Market;
import com.bank.marketdata.MarketUpdate;
import com.bank.marketdata.State;
import com.bank.marketdata.TwoWayPrice;
import com.bank.marketdata.mutable.MutableMarketUpdateDefaultImpl;
import com.bank.marketdata.mutable.MutableTwoWayPrice;
import com.bank.marketdata.mutable.MutableTwoWayPriceDefaultImpl;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class VWAPStepDefs {

    private final Map<Instrument, List<MutableReturnCalculator>> vwapCalcCache = new HashMap<>();
    private final Map<Instrument, Map<MutableReturnCalculator, TwoWayPrice>> lastVwapCalculatorReturn = new HashMap<>();

    private List<MutableReturnCalculator> initAllVwapCalcsForInstrument(Instrument instr) {
        return List.of(
                new VWAPCalculatorFullRecalcUsingFlyweight(instr),
                new VWAPCalculatorFullRecalcUsingMutableUpdates(instr),
                new VWAPCalculatorIncrementUsingMutableUpdates(instr));
    }


    @SuppressWarnings("unused")
    @DataTableType
    public MarketUpdate marketUpdate(Map<String, String> row) {
        MutableMarketUpdateDefaultImpl mutableMarketUpdate = new MutableMarketUpdateDefaultImpl(Market.valueOf(row.get("market")), Instrument.valueOf(row.get("instrument")));
        mutableMarketUpdate.getTwoWayPrice().copyFrom(twoWayPrice(row));
        return mutableMarketUpdate;
    }

    @DataTableType
    public TwoWayPrice twoWayPrice(Map<String, String> row) {
        Instrument instrument = Instrument.valueOf(row.get("instrument"));
        MutableTwoWayPriceDefaultImpl ret = new MutableTwoWayPriceDefaultImpl(instrument);
        ret.setBidPrice(Double.parseDouble(row.getOrDefault("bidPrice", "0")));
        ret.setOfferPrice(Double.parseDouble(row.getOrDefault("offerPrice", "0")));
        ret.setBidAmount(Double.parseDouble(row.getOrDefault("bidAmount", "0")));
        ret.setOfferAmount(Double.parseDouble(row.getOrDefault("offerAmount", "0")));
        ret.setState(State.valueOf(row.getOrDefault("state", State.INDICATIVE.name())));
        return ret;
    }

    @Given("^VWAP calculators receive updates:$")
    public void vwapCalculatorAppliesInstrumentUpdates(List<MarketUpdate> updates) {
        for (MarketUpdate update : updates) {
            vwapCalculatorAppliesUpdate(update);
        }
    }

    private void vwapCalculatorAppliesUpdate(MarketUpdate update) {
        List<MutableReturnCalculator> vwapCalcsForInstrument = vwapCalcCache.computeIfAbsent(update.getTwoWayPrice().getInstrument(), this::initAllVwapCalcsForInstrument);
        vwapCalcsForInstrument.forEach(calc -> {
            MutableTwoWayPrice result = calc.applyMarketUpdate(update);
            lastVwapCalculatorReturn
                    .computeIfAbsent(update.getTwoWayPrice().getInstrument(), $ -> new IdentityHashMap<>())
                    .put(calc, result);
        });
    }


    @Then("^Last calculated VWAPs were:$")
    public void lastCalculatedVWAPWere(List<TwoWayPrice> expectedVwaps) {
        expectedVwaps.forEach(this::lastCalculatedVWAPWas);
    }

    private void lastCalculatedVWAPWas(TwoWayPrice expected) {
        Set<Map.Entry<MutableReturnCalculator, TwoWayPrice>> entries = lastVwapCalculatorReturn.get(expected.getInstrument()).entrySet();
        for (Map.Entry<MutableReturnCalculator, TwoWayPrice> entry : entries) {
            Calculator calculatorWhichGeneratedThisResult = entry.getKey();
            TwoWayPrice result = entry.getValue();

            assertThat(result).describedAs("Calculator with impl " + calculatorWhichGeneratedThisResult.getClass() + " must produce a value which is equal to expected")
                    .extracting(TwoWayPrice::getInstrument,
                            TwoWayPrice::getBidPrice, TwoWayPrice::getBidAmount,
                            TwoWayPrice::getOfferPrice, TwoWayPrice::getOfferAmount,
                            TwoWayPrice::getState).containsExactly(expected.getInstrument(),
                            expected.getBidPrice(), expected.getBidAmount(),
                            expected.getOfferPrice(), expected.getOfferAmount(),
                            expected.getState());
        }
    }
}
