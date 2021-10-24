# GA-Project

This was made with [IntelliJ](https://www.jetbrains.com/idea/) 2020, Java 15, and [VSCode](https://code.visualstudio.com).

## Notes

- User prompts and inputs have a level of error that it can correct for, but it won't catch something like a negative population size. This may include oversights on `scanner.nextLine()`, which can sometimes skip your first input.
- The display which shows the progression of the generations can only show points within the limit of its bounding box. I've tried to offset this with a scaling factor, but points that are negative will always be off of the preview window.

## File details

### GAProj.java

The main class. Holds the crossover methods and 

### FileDecoder.java

Takes user inputs and returns formatted data structures for usages in chromosome and city creation.

### City.java

A data storage object. Stores and calculations locations and distances to other cities.

### Chromosome.java

A data storage object. Keeps a list of cities (integers) at which can be searched through and manipulated. Also handles mutations on itself.

### TimerOfMethods.java

A static class for calculating times.

### CityLooker.java

A JFrame window. Contains everything required to display the cities and chromosomes provided.
