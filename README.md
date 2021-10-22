# GA-Project

This was made with [IntelliJ](https://www.jetbrains.com/idea/) 2020, Java 15, and [VSCode](https://code.visualstudio.com).

## Notes

- User prompts and inputs have a level of error that it can correct for, but it won't catch something like a negative population size.
- 

## File details

### GAProj.java

The main class. This takes care of populating, calculating, and in general all things that need to happen when it comes to number crunching.

### FileDecoder.java

Takes user inputs and returns formatted data structures for usages in chromosome and city creation.

### City.java

A data storage object. Stores and calculations locations and distances to other cities.

### Chromosome.java

A data storage object. Keeps a list of cities (integers) at which can be searched through and manipulated.

### TimeData.java

A data storage object. Stores the time, number of iterations, and length of a chromosomes.

### TimerOfMethods.java

A static class for calculating times.
