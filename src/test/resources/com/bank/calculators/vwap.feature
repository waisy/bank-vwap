Feature: VWAP Calculator

  Scenario: When a single update is received, the vwap is the default amount

    Given VWAP calculators receive updates:
      | market  | instrument  | state | bidPrice | bidAmount | offerPrice | offerAmount |
      | MARKET0 | INSTRUMENT0 | FIRM  | 2        | 3         | 4          | 5           |

    Then Last calculated VWAPs were:
      | instrument  | state | bidPrice | bidAmount | offerPrice | offerAmount |
      | INSTRUMENT0 | FIRM  | 2        | 3         | 4          | 5           |

  Scenario: Only the latest update is used for a given market instrument pair
    Given VWAP calculators receive updates:
      | market  | instrument  | state      | bidPrice | bidAmount | offerPrice | offerAmount |
      | MARKET0 | INSTRUMENT0 | INDICATIVE | 100      | 100       | 100        | 100         |
      | MARKET0 | INSTRUMENT0 | FIRM       | 2        | 3         | 4          | 5           |

    Then Last calculated VWAPs were:
      | instrument  | state | bidPrice | bidAmount | offerPrice | offerAmount |
      | INSTRUMENT0 | FIRM  | 2        | 3         | 4          | 5           |

  Scenario: When updates come from multiple markets,
  then bidPrice/offerPrice represent the VWAP bid/offer price.
  and bidAmount/offerAmount represent the total amounts seen across all the markets for that instrument
    Given VWAP calculators receive updates:
      | market  | instrument  | state | bidPrice | bidAmount | offerPrice | offerAmount |
      | MARKET0 | INSTRUMENT0 | FIRM  | 2        | 3         | 4          | 5           |
      | MARKET1 | INSTRUMENT0 | FIRM  | 3        | 20        | 5          | 5           |

    Then Last calculated VWAPs were:
      | instrument  | state | bidPrice          | bidAmount | offerPrice | offerAmount |
      | INSTRUMENT0 | FIRM  | 2.869565217391304 | 23        | 4.5        | 10          |

  Scenario: If any of the market prices are marked as indicative, then the overall vwap price is marked as indicative
    Given VWAP calculators receive updates:
      | market  | instrument  | state      | bidPrice | bidAmount | offerPrice | offerAmount |
      | MARKET0 | INSTRUMENT0 | FIRM       | 0        | 0         | 0          | 0           |
      | MARKET1 | INSTRUMENT0 | INDICATIVE | 0        | 0         | 0          | 0           |

    Then Last calculated VWAPs were:
      | instrument  | state      | bidPrice | bidAmount | offerPrice | offerAmount |
      | INSTRUMENT0 | INDICATIVE | NaN      | 0         | NaN        | 0           |


  Scenario: When a firm price arrives for a previously indicative market, if other markets are still indicative, then the whole price is marked as indicative
    Given VWAP calculators receive updates:
      | market  | instrument  | state      | bidPrice | bidAmount | offerPrice | offerAmount |
      | MARKET0 | INSTRUMENT0 | FIRM       | 0        | 0         | 0          | 0           |
      | MARKET1 | INSTRUMENT0 | INDICATIVE | 0        | 0         | 0          | 0           |
      | MARKET2 | INSTRUMENT0 | INDICATIVE | 0        | 0         | 0          | 0           |
      | MARKET1 | INSTRUMENT0 | FIRM       | 0        | 0         | 0          | 0           |

    # MARKET 2 still indicative
    Then Last calculated VWAPs were:
      | instrument  | state      | bidPrice | bidAmount | offerPrice | offerAmount |
      | INSTRUMENT0 | INDICATIVE | NaN      | 0         | NaN        | 0           |

    Given VWAP calculators receive updates:
      | market  | instrument  | state | bidPrice | bidAmount | offerPrice | offerAmount |
      | MARKET2 | INSTRUMENT0 | FIRM  | 0        | 0         | 0          | 0           |

    # MARKET2 no longer indicative, so overall FIRM
    Then Last calculated VWAPs were:
      | instrument  | state | bidPrice | bidAmount | offerPrice | offerAmount |
      | INSTRUMENT0 | FIRM  | NaN      | 0         | NaN        | 0           |

  Scenario Outline: If a bid/offer price/amount are NaN, then that price is not used in the VWAP calculation
    Given VWAP calculators receive updates:
      | market  | instrument  | state | bidPrice        | bidAmount        | offerPrice        | offerAmount        |
      | MARKET1 | INSTRUMENT0 | FIRM  | 3               | 20               | 5                 | 5                  |
      | MARKET0 | INSTRUMENT0 | FIRM  | <firstBidPrice> | <firstBidAmount> | <firstOfferPrice> | <firstOfferAmount> |

    Then Last calculated VWAPs were:
      | instrument  | state | bidPrice | bidAmount | offerPrice | offerAmount |
      | INSTRUMENT0 | FIRM  | 3        | 20        | 5          | 5           |
    Examples:
      | firstBidPrice | firstBidAmount | firstOfferPrice | firstOfferAmount |
      | NaN           | 10             | NaN             | 10               |
      | 10            | NaN            | 10              | NaN              |
      | NaN           | NaN            | NaN             | NaN              |