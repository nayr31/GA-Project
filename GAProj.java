import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GAProj {

    FileDecoder fd = new FileDecoder();
    ArrayList<City> cities;
    ArrayList<Chromosome> chromosomes;

    GAProj() {
        System.out.println("GA Project - Travelling Salesman.\n");

        while (true) {
            int selection = fd.askForType("""
                            Please choose an option:
                            [0] - Exit
                            [1] - Run standard program""", 1);
            if (selection == 0) {
                break;
            } else if (selection == 1) {
                runStandard();
            }
        }
    }

    void runStandard() {
        // Step 1: Get input data (cities and their locations)
        cities = fd.getInput();

        // Step 2: Initialize Initial population
        chromosomes = initializeRandomStartingPopulation(fd.askForInt("Please enter population size:"));

        // Step 3: Evaluate, select, and mutate
        runEval(fd.askMaxGen());
    }

    // Runs evaluation simulation and selection based on existing cities and chromosomes
    // - Evaluate fitness
    // - Select new population using selections
    // - Apply crossover and mutation
    void runEval(int maximumGenerations) {
        int crossoverRate = fd.askForInt("Enter crossover rate in % (as an Integer)");
        int mutationRate = fd.askForInt("Enter mutation rate in % (as an Integer)");
        int crossoverType = fd.askForType("""
                            Please choose an option:
                            [0] - UOX with bitmask
                            [1] - Run standard program""", 1); // TODO make second crossover
        int tournamentCandidateNum = fd.askForInt("Tournament k (candidates)?");
        int finalSize = chromosomes.size();

        for (int i = 0; i < maximumGenerations; i++) {
            // Evaluate fitness
            scoreChromosomeDistances(); // Calculate and store the scores within the chromosomes

            // Select new population using selections
            tournamentSelection(tournamentCandidateNum);

            // Apply crossover and mutation

        }
    }

    void tournamentSelection(int tournamentCandidateNum){
        ArrayList<Chromosome> winners = new ArrayList<>();

        // Keep choosing candidates
        while(chromosomes.size() != 0){
            if (chromosomes.size() < tournamentCandidateNum) break;
            Chromosome theBest = new Chromosome();
            // Find and fight contenders
            for (int i = 0; i < tournamentCandidateNum; i++) {
                Chromosome fighter = chromosomes.remove(new Random().nextInt(chromosomes.size()));
                if (fighter.score < theBest.score){
                    theBest = fighter;
                }
            }
            // Winner has been decided
            winners.add(theBest);
            // We keep the losers out of the remaining fighters
        }
        // We either have not enough fighters or an empty list
        chromosomes.addAll(winners); // Just add all of the winners
    }

    // Find the total path length of a chromosome
    void scoreChromosomeDistances(){ // I could imagine this is very very expensive
        float totalDist;
        for(Chromosome chromosome : chromosomes){ // For each chromosome
            totalDist = 0;
            for (int i = 0; i < chromosome.data.length-1; i++) { // For each city
                // Add the distance between increasing pairs of cities
                totalDist += cities.get(chromosome.data[i]).dist(cities.get(chromosome.data[i+1]));
            }
            chromosome.score = totalDist;
        }
    }

    // Crosses two chromosomes by a provided bitmask, then returns them as an array
    Chromosome[] crossoverUOX(Chromosome parent1, Chromosome parent2, Bitmask bitmask) {
        Object[] p1Copy = new Object[parent1.data.length];
        Object[] p2Copy = new Object[parent2.data.length];

        ArrayList<Integer> knownCities1 = new ArrayList<>();
        ArrayList<Integer> knownCities2 = new ArrayList<>();

        ArrayList<Integer> unknownCities1 = new ArrayList<>();
        ArrayList<Integer> unknownCities2 = new ArrayList<>();

        // Inherit the '1' bit cities
        for (int i = 0; i < bitmask.mask.size(); i++) {
            if (bitmask.mask.get(i) == 1) {
                p1Copy[i] = parent1.data[i];
                knownCities1.add(i);
                p2Copy[i] = parent2.data[i];
                knownCities2.add(i);
            }
        }

        // For each index in p1/p2copy
        for (int i = 0; i < p1Copy.length; i++) {
            // If the space is empty (0 in bit mask) and p1copy doesnt contain the same city index in parent2
            if (p1Copy[i] == null && !knownCities1.contains(parent2.data[i])) {
                p1Copy[i] = parent2.data[i]; // Copy the not yet satisfied city
                knownCities1.add(parent2.data[i]); // Make sure we learn the newly added city
            } else if (p1Copy[i] != null && !knownCities1.contains(parent2.data[i])) {
                unknownCities1.add(parent2.data[i]); // Otherwise, we mark it as "to be filled in later"
            }
            // Then do the same for p2
            if (p2Copy[i] == null && !knownCities2.contains(parent1.data[i])) {
                p2Copy[i] = parent1.data[i]; // Copy the not yet satisfied city
                knownCities2.add(parent1.data[i]); // Make sure we learn the newly added city
            } else if (p2Copy[i] != null && !knownCities2.contains(parent1.data[i])) {
                unknownCities2.add(parent1.data[i]);
            }
        }

        // Now we need to empty the unknown list
        for (int i = 0; i < p1Copy.length; i++) {
            if (p1Copy[i] == null){ // Find the empty spaces
                p1Copy[i] = unknownCities1.remove(0); // Fill it in with an unknown city to that path
            }
            if (p2Copy[i] == null){
                p2Copy[i] = unknownCities2.remove(0);
            }
        }

        if(unknownCities1.isEmpty() || unknownCities2.isEmpty()){
            System.out.println("Something bad happened in UOX splicing.");
        }

        return new Chromosome[] {new Chromosome(p1Copy), new Chromosome(p2Copy)};
    }

    // Returns the performance metrics of populating a number of chromosomes
    void popInitPerformanceTest() {
        System.out.println("Calculating performance for random chromosome generation...");
        TimeData[] timeData = new TimeData[7];
        timeData[0] = chromoPerfTest(5, 5);
        timeData[1] = chromoPerfTest(50, 50);
        timeData[2] = chromoPerfTest(500, 500);
        timeData[3] = chromoPerfTest(5000, 5000);
        timeData[4] = chromoPerfTest(5, 5000000);
        timeData[5] = chromoPerfTest(5000000, 5);
        timeData[6] = chromoPerfTest(50000, 50000);
        System.out.println("------------------");
        System.out.printf("%-10s %-10s %-10s\n", "numSets", "length", "timeTaken(ms)");
        for (TimeData d : timeData)
            System.out.printf("%-10s %-10s %-10s\n", d.numberOfSets, d.sizeOfChromosome, d.time / 1000000);
        System.out.println("------------------");
    }

    TimeData chromoPerfTest(int number, int size) {
        return new TimeData(number, size, testChromsomeGeneration(number, size));
    }

    long testChromsomeGeneration(int number, int size) {
        TimerOfMethods.startTimer();
        generateXChromosomes(number, size);
        return TimerOfMethods.getEllapsedTime();///1000000
    }

    void generateXChromosomes(int number, int size) {
        for (int i = 0; i < number; i++)
            generateRandomChromosome(size);
    }

    // Returns an ArrayList of random chromosomes that adhere to the population size given
    ArrayList<Chromosome> initializeRandomStartingPopulation(int popSize) {
        ArrayList<Chromosome> chromosomes = new ArrayList<>();
        for (int i = 0; i < popSize; i++)
            chromosomes.add(generateRandomChromosome(cities.size()));
        return chromosomes;
    }

    // Returns a random Chromosome
    Chromosome generateRandomChromosome(int size) {
        // Create a new list, an populate it with integers that represent the city index
        ArrayList<Integer> present = new ArrayList<>();
        for (int i = 0; i < size; i++)
            present.add(i);
        // Randomize the list
        Collections.shuffle(present);
        // Return the resulting chromosome
        // Function taken from https://www.geeksforgeeks.org/arraylist-array-conversion-java-toarray-methods/
        // (I hope its efficient)
        return new Chromosome(present.stream().mapToInt(i -> i).toArray());
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
