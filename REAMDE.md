# Intro
I have spent a bit of extra time on my submission to showcase some of skills, thinking and progress towards an optimal solution.

I have assumed that we want low object allocation, so went for a zero GC solution.

Should GC not be a concern, I would probably have used more immutables for e.g. return types, in order to simply the programming model.

I have kept all the solutions that I considered. My final decision would be to use the Incremental solution with a preallocated mutable value repository, but I've kept the others to illustrate how I came to the solution.

This involved extending the interfaces that were given. It is assumed that clients of the calculators do not cache the returned values.

# Solutions

## Data repositories
Under the solutions I could think of, I knew that I would have to store market data that was received by the calculators. 
Usually these would be shared by multiple calculators, but without that constraint, I went ahead and allowed the calculators to own them. 

Both of the repository implementations pre-allocate all the instrument / market combinations possible. There is a reasonable number of combinations in our system so I expect this to be OK, and allows us to avoid allocating at runtime. 

The two repository implementations are:
- Flyweight. This allows us to store the price data in contiguous memory, much like a column based database would do
- Mutable Updates. This preallocates all the price update objects we want to store for a calculator, but does so in a row by row fashion

The idea behind the flyweight was that as we are aggregating values, the calculation may benefit from the column based memory layout. As I found out, the VWAP calculation specifically doesn't benefit from this much due to the way it accesses the prices.

## VWAP Calcuation
### Full recalc
I first went with the FullRecalc VWAP calculation as I thought the programming model would be simpler. This is represented in the subclasses of VWAPCalculatorFullRecalc 

Overall outline is: 
1. Apply update to it's repository
2. Start from blank calculation results
3. Loop through all the markets and apply the latest update on each market to the calculation

This kind of full-recalc model provides an easy to think about model, however comes at the cost of potentially doing work that doesn't need to be done (e.g. many of the values in the previous sum could be reused when a new upate comes in)

An interesting point I came across when developing this was that the iterator for Market.values() was creating garbage (it came up on JMH). This is why I decided to cache the return type at the top of the class, and is infact the strategy followed by e.g. EnumBitSet

### Incremental
I was not fully satisified with the performance of the full recalc version. So I thought a bit more deeply and decided that I could simply `undo` any values added in the previously published calculation for the market update that we have just received. This would avoid having to loop through all the markets.

This generated a decent speedup (see the jmh results text file). The programming model is more complicated. There would need to be a team decision whether this is worth the speed up, and really depends on the type of application we are writing, as well as team composition (senior vs junior etc).

### Dealing with indicative vs firm state
When writing the Full recalc solution, I was simply initialising the state to set on the result as FIRM and then flip to indicative if I came across any indicative price. 

However, I didn't want to do this for the incremental solution. 

I decided to use an EnumSet in the repository to keep track of all markets which are INDICATIVE. This gives us ultimate performance as we can update it as events come in and it is backed by a bitset for small enum universes.

Due to the speed of this operation, there was no reason to not use it in the full recalc solution so I used it there too.

# Functional testing
I went for a TDD style approach to produce the individual 'units' which had no business logic, for example the repositories. This meant trying to write tests first in order to drive out the design.
I refactored the Junit classes so, where I produced multiple solutions to a given interface, I re-used the same tests against all. This is to keep in line with the Liskov Susbtition Principle.

I have also produced a cucumber file which describes the behaviour of the system at a higher level. I would feel comfortable sharing / working on those with more technically minded business users.

The cucumber tests were also quite useful when I went for multiple solutions - the backing glue is ran against all of them. I wrote the .feature file when I was developing the full recalc version. Afterwards, when I wanted to add the incremental calculator, I simply re-ran the same .feature file against the new solution. 

# Performance testing
I have produced 2 benchmarks using JMH. They can be ran using the gradle `jmh` task. 

The main results on my machine (Macbook M1 Pro):
- For repositories, flyweight is slower. 
- For WWAP calculation, incremental VWAP is a bit faster than full recalc
- There is zero garbage produced with any of these solutions

I have kept the results in jmh-benchmark-results.txt

This then gives me enough information to drive decisions that involve performance considerations.

# Design considerations
## Lifetime of returned value
It is assumed that clients of the calculators do not cache the return values. 

If we wanted to allow clients to cache the return values, a better solution would involve allowing the client to supply an object to write the results in to. 

This would allow the calculator to cache it's previously calculated value, while giving control to the client about how it wants to use the returned calculation.

## Wiring classes
There are classes, e.g. VWAPCalculatorFullRecalcUsingFlyweight, which are simply formalising wiring. Usually this would be done in some dependecy injection framework / approach. 