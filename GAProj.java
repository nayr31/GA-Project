import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GAProj {

    TerminalControl control = new TerminalControl();
    FileDecoder fd = new FileDecoder();
    ArrayList<City> cities;
    ArrayList<Chromosome> chromosomes;
    CityLooker cityLooker;

    GAProj() {
        System.out.println("GA Project - Travelling Salesman.");

        /*cities = fd.getCities();
        CityLooker cityLooker = new CityLooker(cities);
        Chromosome chromosome = generateRandomChromosome(cities.size());
        cityLooker.draw(chromosome);*/



        //CityLooker cityLooker = new CityLooker();
        while (true) {
            int selection = fd.askForType("""
                    Please choose an option:
                    [0] - Exit
                    [1] - Run standard program""", 1);
            if (selection == 0) {
                break;
            } else if (selection == 1) {
                runStandard();
                break;
            }
        }
    }

    void runStandard() {
        // Step 1: Get input data (cities and their locations)
        cities = fd.getCities();

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
                Please choose an option for crossover type:
                [0] - UOX with bitmask
                [1] - Yes""", 0); // TODO make second crossover
        int mutationType = fd.askForType("""
                Please choose an option for mutation type:
                [0] - Swap
                [1] - Yes""", 0); // TODO make more mutations
        int tournamentCandidateNum = fd.askForInt("Tournament k (candidates)?");
        int finalSize = chromosomes.size();

        openCityLooker();

        for (int i = 0; i < maximumGenerations; i++) {
            // Evaluate fitness
            scoreChromosomeDistances(); // Calculate and store the scores within the chromosomes
            cityLooker.draw(bestOfTheBest()); // Draw the current best of the best chromosome

            // Select new population using selections
            tournamentSelection(tournamentCandidateNum);

            // Apply crossover and mutation
            // Choose which type of crossover is happening
            Random crossoverRandom = new Random();
            if (crossoverType == 0) { // Uniform with bitmask
                for (int j = 0; j < chromosomes.size() / 2; j += 2) { // Pick pairs
                    int r = crossoverRandom.nextInt(99) + 1;
                    if (r <= crossoverRate) { // Determine if that crossover happens
                        // Get the resulting crossed
                        Chromosome[] crossed = uniformOrder(chromosomes.get(j), chromosomes.get(j + 1));
                        // Add them to the list
                        // There is a chance that the list could get bigger?
                        if (chromosomes.size() + 2 <= finalSize) {
                            chromosomes.add(crossed[0]);
                            chromosomes.add(crossed[1]);
                        } else { // If it gets past our limit, we will replace the parents
                            chromosomes.set(j, crossed[0]);
                            chromosomes.set(j + 1, crossed[1]);
                        } // Of course, it could also be that we do nothing, but I'll leave that to the report
                    }
                }
            }
            // Mutation
            Random mutatorRandom = new Random();
            for (Chromosome chromosome : chromosomes) { // For each chromosome
                int r = mutatorRandom.nextInt(99) + 1;
                if (r <= mutationRate) { // Determine if the chromosome mutates
                    if (mutationType == 0) {
                        chromosome.swapMutate(); // Preform a swap mutation
                    }
                }
            }

            // Finally, fill in the missing chromosomes to keep the same population size
            // We don't mutate before this because they would be random anyway
            chromosomes.addAll(generateXChromosomes(finalSize - chromosomes.size(), cities.size()));
        }
        // This is after all generations of the program have been completed
        System.out.println("Generations complete!");
    }

    Chromosome bestOfTheBest(){
        Chromosome theBest = new Chromosome();
        for (Chromosome entry: chromosomes){
            if (entry.score < theBest.score)
                theBest = entry;
        }
        return theBest;
    }

    void tournamentSelection(int tournamentCandidateNum) {
        ArrayList<Chromosome> winners = new ArrayList<>();

        // Keep choosing candidates
        while (chromosomes.size() != 0) {
            if (chromosomes.size() < tournamentCandidateNum) break;
            Chromosome theBest = new Chromosome();
            // Find and fight contenders
            for (int i = 0; i < tournamentCandidateNum; i++) {
                Chromosome fighter = chromosomes.remove(new Random().nextInt(chromosomes.size()));
                if (fighter.score < theBest.score) {
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
    void scoreChromosomeDistances() { // I could imagine this is very very expensive
        float totalDist;
        for (Chromosome chromosome : chromosomes) { // For each chromosome
            totalDist = 0;
            for (int i = 0; i < chromosome.data.length - 1; i++) { // For each city
                // Add the distance between increasing pairs of cities
                totalDist += cities.get(chromosome.data[i]).dist(cities.get(chromosome.data[i + 1]));
            }
            chromosome.score = totalDist;
        }
    }

    Chromosome[] uniformOrder (Chromosome p1, Chromosome p2) {
        Object[] child1 = new Object[p1.data.length];
        Object[] child2 = new Object[p2.data.length];

        ArrayList<Integer> knownInC1 = new ArrayList<>();
        ArrayList<Integer> knownInC2 = new ArrayList<>();
        ArrayList<Integer> knownNotInC1 = new ArrayList<>();
        ArrayList<Integer> knownNotInC2 = new ArrayList<>();

        Bitmask bitmask = new Bitmask(p1.data.length);

        // Take which bits are 1 into the children by default
        for (int i = 0; i < bitmask.mask.size(); i++) {
            if (bitmask.mask.get(i) == 1) {
                child1[i] = p1.data[i];
                child2[i] = p2.data[i];
                // Take note of which cities we took from the parents
                knownInC1.add(p1.data[i]);
                knownInC2.add(p2.data[i]);
            }
        }

        // Get the cities of the opposite parent if the child does not already contain them.
        for (int i = 0; i < child1.length; i++) {
            if (child1[i] == null && !knownInC1.contains(p2.data[i])) {
                child1[i] = p2.data[i];
                knownInC1.add(p2.data[i]);
            } else if (child1[i] != null && !knownInC1.contains(p2.data[i])) {
                knownNotInC1.add(p2.data[i]);
            }
            if (child2[i] == null && !knownInC2.contains(p1.data[i])) {
                child2[i] = p1.data[i];
                knownInC2.add(p1.data[i]);
            } else if (child2[i] != null && !knownInC2.contains(p1.data[i])) {
                knownNotInC2.add(p1.data[i]);
            }
        }

        // Fill in the blanks.
        for (int i = 0; i < child1.length; i++) {
            if (child1[i] == null)
                child1[i] = knownNotInC1.remove(0);
            if (child2[i] == null)
                child2[i] = knownNotInC2.remove(0);
        }

        return new Chromosome[]{new Chromosome(child1), new Chromosome(child2)};
    }

    ArrayList<Chromosome> initializeRandomStartingPopulation(int popSize) {
        return generateXChromosomes(popSize, cities.size());
    }

    // This method call returns a list of random chromosomes that number the original size of the
    //  population minus the current size, with lengths of the number of cities
    ArrayList<Chromosome> generateXChromosomes(int amountToHatch, int length) {
        ArrayList<Chromosome> hatchlings = new ArrayList<>();
        for (int i = 0; i < amountToHatch; i++)
            hatchlings.add(generateRandomChromosome(length));
        return hatchlings;
    }

    // Returns a random Chromosome (ie, a sequence of cities in random order)
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

    void printChromosomes() {
        for (int i = 0; i < chromosomes.size(); i++) {
            System.out.println(i + ":" + chromosomes.get(i));
        }
    }

    void openCityLooker(){
        cityLooker = new CityLooker(cities);
    }

    public static void main(String[] args) {
        GAProj p = new GAProj();
    }
}
