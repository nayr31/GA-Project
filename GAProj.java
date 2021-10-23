import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GAProj {

    FileDecoder fd = new FileDecoder();
    ArrayList<City> cities;
    ArrayList<Chromosome> chromosomes;
    CityLooker cityLooker;

    GAProj() {
        System.out.println("GA Project - Travelling Salesman.\n");

        /*cities = fd.getCities();
        CityLooker cityLooker = new CityLooker(cities);
        Chromosome chromosome = generateRandomChromosome(cities.size());
        cityLooker.draw(chromosome);
        */

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
            if (crossoverType == 0) { // Uniform with bitmask
                for (int j = 0; j < chromosomes.size() / 2; j += 2) { // Pick pairs
                    if (new Random().nextInt(100) <= crossoverRate) { // Determine if that crossover happens
                        // Get the resulting crossed
                        Chromosome[] crossed =
                                crossoverUOX(chromosomes.get(j), chromosomes.get(j + 1), new Bitmask(cities.size()));
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
            for (Chromosome chromosome : chromosomes) { // For each chromosome
                if (new Random().nextInt(100) <= mutationRate) { // Determine if the chromosome mutates
                    if (mutationType == 0) {
                        chromosome.swapMutate(); // Preform a swap mutation
                    }
                }
            }

            // Finally, fill in the missing chromosomes to keep the same population size
            chromosomes.addAll(generateXChromosomes(finalSize - chromosomes.size(), cities.size()));
        }
        // This is after all generations of the program have been completed
        // Lets display the final chromosomes with their scores
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

    // Crosses two chromosomes by a provided bitmask, then returns them as an array
    Chromosome[] crossoverUOX(Chromosome parent1, Chromosome parent2, Bitmask bitmask) {
        // We use arrays here since we need to keep the ordering the same, instead of removing the non 1 bit members
        Object[] p1Copy = new Object[parent1.data.length];
        Object[] p2Copy = new Object[parent2.data.length];

        // These lists keep track of which cities are in their respective lists
        ArrayList<Integer> knownCities1 = new ArrayList<>();
        ArrayList<Integer> knownCities2 = new ArrayList<>();

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
            }
            // Then do the same for p2
            if (p2Copy[i] == null && !knownCities2.contains(parent1.data[i])) {
                p2Copy[i] = parent1.data[i];
                knownCities2.add(parent1.data[i]);
            }
        }

        // The lists are done, but there are some index spaces not yet filled
        ArrayList<Integer> notYetInList1 = findNotYetInKnown(knownCities1); // Get the missing cities
        for (int i = 0; i < p1Copy.length; i++) { // Check through the array
            if(p1Copy[i] == null){ // If there is an empty space (0 bit)
                int notYetKnown = notYetInList1.remove(0);
                p1Copy[i] = notYetKnown; // Fill it in with one not yet represented
                //knownCities1.add(notYetKnown); // Not really needed, but this would go here
            }
        }
        ArrayList<Integer> notYetInList2 = findNotYetInKnown(knownCities2);
        for (int i = 0; i < p2Copy.length; i++) {
            if(p2Copy[i] == null){
                int notYetKnown = notYetInList2.remove(0);
                p2Copy[i] = notYetKnown;
                //knownCities2.add(notYetKnown);
            }
        }

        return new Chromosome[]{new Chromosome(p1Copy), new Chromosome(p2Copy)};
    }

    // Determines which
    ArrayList<Integer> findNotYetInKnown(ArrayList<Integer> known) {
        ArrayList<Integer> compare = new ArrayList<>();
        for (int i = 0; i < cities.size(); i++) {
           compare.add(i);
        }
        compare.removeIf(known::contains);
        return compare;
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
