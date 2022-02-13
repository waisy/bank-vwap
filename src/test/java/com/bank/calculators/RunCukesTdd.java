package com.bank.calculators;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.core.options.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com/bank/calculators")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.bank.calculators")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@tdd")
public class RunCukesTdd {
}
