import java.util.ArrayList;
import java.util.Collections;

public class GAProj {

    FileDecoder fd = new FileDecoder();
    ArrayList<City> cities;
    ArrayList<Chromosome> chromosomes;

    GAProj() {
        System.out.println("GA Project - Travelling Salesman.");

        // Step 1: Get input data (cities and their locations)
        cities = fd.getInput();

        // Step 2: Initialize Initial population
        TimerOfMethods.startTimer();
        chromosomes = initializeRandomStartingPopulation(fd.askForPopSize());
        System.out.println("Initial population grew over " + TimerOfMethods.getEllapsedTime()/1000000 + "ms");

        // Step 3: Evaluate, select, and mutate
    }

    // Returns the performance metrics of populating a number of chromosomes
    void popInitPerformanceTest(){
        //TODO Finish this
    }

    // Returns an ArrayList of random chromosomes that adhere to the population size given
    ArrayList<Chromosome> initializeRandomStartingPopulation(int popSize){
        ArrayList<Chromosome> chromosomes = new ArrayList<>();
        for (int i = 0; i < popSize; i++)
            chromosomes.add(generateRandomChromosome());
        return chromosomes;
    }

    // Returns a random Chromosome
    Chromosome generateRandomChromosome(){
        // Create a new list, an populate it with integers that represent the city index
        ArrayList<Integer> present = new ArrayList<>();
        for (int i = 0; i < cities.size(); i++)
            present.add(i);
        // Randomize the list
        Collections.shuffle(present);
        // Return the resulting chromosome
        // Function taken from https://www.geeksforgeeks.org/arraylist-array-conversion-java-toarray-methods/
        // (I hope its efficient)
        return new Chromosome(present.stream().mapToInt(i->i).toArray());
    }

    // Displays the city data
    void printCities() {
        System.out.println("Printing city data:");
        for (int i = 0; i < cities.size(); i++) {
            City c = cities.get(i);
            System.out.println(i + ":" + c);
        }
        System.out.println("Finished printing City data.");
    }

    public static void main(String[] args) {
        GAProj p = new GAProj();
    }
}
