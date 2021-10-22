import java.util.ArrayList;
import java.util.Collections;

public class GAProj {

    FileDecoder fd = new FileDecoder();
    ArrayList<City> cities;
    ArrayList<Chromosome> chromosomes;

    GAProj() {
        System.out.println("GA Project - Travelling Salesman.\n");

        while (true){
            int selection = fd.askForProgramType();
            if (selection == 0) {
                break;
            } else if(selection == 1){
                runStandard();
            }
        }
    }

    void runStandard(){
        // Step 1: Get input data (cities and their locations)
        cities = fd.getInput();

        // Step 2: Initialize Initial population
        chromosomes = initializeRandomStartingPopulation(fd.askForInt("Please enter population size:"));

        // Step 3: Evaluate, select, and mutate
        runEval(fd.askMaxGen());
    }
    
    // Runs evaluation simulation and selection based on existing cities and chromosomes
    void runEval(int maximumGenerations){
        int crossoverRate = fd.askForInt("Enter crossover rate in % (as an Integer)");
        int mutationRate = fd.askForInt("Enter mutation rate in % (as an Integer)");

        for (int i = 0; i < maximumGenerations; i++) {

        }
    }

    // Crosses two chromosomes by a provided bitmask
    void crossoverUOX(Chromosome parent1, Chromosome parent2, Bitmask bitmask){

    }

    // Returns the performance metrics of populating a number of chromosomes
    void popInitPerformanceTest(){
        System.out.println("Calculating performance for random chromosome generation...");
        TimeData[] timeData = new TimeData[7];
        timeData[0] = chromoPerfTest(5,5);
        timeData[1] = chromoPerfTest(50,50);
        timeData[2] = chromoPerfTest(500,500);
        timeData[3] = chromoPerfTest(5000,5000);
        timeData[4] = chromoPerfTest(5,5000000);
        timeData[5] = chromoPerfTest(5000000,5);
        timeData[6] = chromoPerfTest(50000,50000);
        System.out.println("------------------");
        System.out.printf("%-10s %-10s %-10s\n", "numSets", "length" , "timeTaken(ms)");
        for (TimeData d : timeData)
            System.out.printf("%-10s %-10s %-10s\n", d.numberOfSets, d.sizeOfChromosome , d.time/1000000);
        System.out.println("------------------");
    }

    TimeData chromoPerfTest(int number, int size){
        return new TimeData(number, size, testChromsomeGeneration(number, size));
    }

    long testChromsomeGeneration(int number, int size){
        TimerOfMethods.startTimer();
        generateXChromosomes(number, size);
        return TimerOfMethods.getEllapsedTime();///1000000
    }

    void generateXChromosomes(int number, int size){
        for (int i = 0; i < number; i++)
            generateRandomChromosome(size);
    }

    // Returns an ArrayList of random chromosomes that adhere to the population size given
    ArrayList<Chromosome> initializeRandomStartingPopulation(int popSize){
        ArrayList<Chromosome> chromosomes = new ArrayList<>();
        for (int i = 0; i < popSize; i++)
            chromosomes.add(generateRandomChromosome(cities.size()));
        return chromosomes;
    }

    // Returns a random Chromosome
    Chromosome generateRandomChromosome(int size){
        // Create a new list, an populate it with integers that represent the city index
        ArrayList<Integer> present = new ArrayList<>();
        for (int i = 0; i < size; i++)
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
