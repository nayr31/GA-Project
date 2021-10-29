import java.util.ArrayList;

// Main class
// Run this to be prompted for information or run standardized testing
public class GAProj {

    TerminalControl control = new TerminalControl();
    FileDecoder fd = new FileDecoder();
    ArrayList<City> cities;
    ArrayList<Chromosome> chromosomes;
    CityLooker cityLooker;

    GAProj() {
        System.out.println("GA Project - Travelling Salesman.");

        while (true) {
            int selection = fd.askForType(
                    "Please choose an option:\n" +
                    "[0] - Exit\n" +
                    "[1] - Run standard program\n" +
                    "[2] - Show last result (node network)" + 
                    "[3] - Run 5 seed set", 3);
            if (selection == 0) {
                break;
            } else if (selection == 1) {
                runStandard();
            } else if (selection == 2){
                if(cityLooker != null)
                    cityLooker.showWindow();
                else
                    TerminalControl.sendStatusMessage("No last result present!");
            } else if (selection == 3){
                runFiveSeedStandard();
            }
        }
        TerminalControl.sendStatusMessage("No longer receiving inputs.\nHave a nice day.");
    }

    void runFiveSeedStandard(){
        // Input will be the same set of cities
        cities = fd.getCities();
        // Set the standard testing parameters for the standard 5 seed output
        final int popSize = 1000;
        final int maximumGenerations = 1000;
        final int crossoverType = 1;
        final int[] crossoverRate = new int[] {100, 100, 90, 90, 90};
        final int mutationType = 2;
        final int[] mutationRate = new int[] {0, 10, 0, 10, 15};
        final int tournamentCandidateNum = 3;
        final boolean print = false;
        final boolean show = false;
        ArrayList<ArrayList<SeedStandardDTO>> finalSeedData = new ArrayList<>();

        // For each seed
        for(int i=0; i<5; i++){
            // Set the seed of this generation
            Seeder.setSeed(i); // Doesn't need a 'new' constructor, set seed does that naturally

            // Keep track of our starting chromosomes for reuse (we don't really need to do this, but its efficient)
            ArrayList<Chromosome> defaultChromosomeSet = initializeRandomStartingPopulation(popSize);

            // Keep track of each seed data provided by the different attempts
            ArrayList<SeedStandardDTO> seedData = new ArrayList<>();
            // For each iteration method (crossover type and
            for (int j = 0; j < 5; j++) {
                // Reset the default seed set so that we don't generate them every time
                chromosomes.clear();
                chromosomes.addAll(defaultChromosomeSet);

                // Preform the generation and store the data used from it
                SeedStandardDTO dto = preformGenerations(maximumGenerations, crossoverType, crossoverRate[j],
                        mutationType, mutationRate[j], tournamentCandidateNum, print, show);
                dto.seedNum = i; // I made a boo-boo and this is the laziest way to fix it
                seedData.add(dto);
            }
            // After all 5 runs on that seed, add the list to the final data
            finalSeedData.add(seedData);
        }

        // We now have 5 data sets of each 5 runtime parameters, print them
        ReportWriter.printSeedResults(finalSeedData);

        // Just to clarify what is stored in the finalSeedData:
        // It is a list of lists of seed data objects
        // A seed data object contains everything that I could think of that would be useful during a generation set
        // Each list of seed data are the different parameters
        // This means that finalSeedData.get(0).get(0) would get the the 0th entry (c=100,m=0) of the 0th seed
        // In essence, [i,j] is [seedNum,iterationDetails]
    }

    void runStandard() {
        // Step 1: Get input data (cities and their locations)
        cities = fd.getCities();

        // Step 2: Initialize Initial population
        chromosomes = initializeRandomStartingPopulation(fd.askForInt("Please enter population size:"));

        // Step 3: Evaluate, select, and mutate
        runEval();
    }

    SeedStandardDTO preformGenerations(int maximumGenerations, int crossoverType, int crossoverRate, int mutationType,
                                int mutationRate, int tournamentCandidateNum, boolean print, boolean show){
        cityLooker = new CityLooker(cities);
        if(show) cityLooker.showWindow();
        int finalSize = chromosomes.size();
        ArrayList<Float> avgPerGeneration = new ArrayList<>();
        ArrayList<Float> bestPerGeneration = new ArrayList<>();

        for (int i = 0; i < maximumGenerations; i++) {
            // Evaluate fitness
            scoreChromosomeDistances(); // Calculate and store the scores within the chromosomes
            // It is really important that this is the first thing to happen!
            Chromosome theBestChromosome = bestOfTheBest();
            bestPerGeneration.add(theBestChromosome.score);
            cityLooker.draw(theBestChromosome); // Draw the current best of the best chromosome
            avgPerGeneration.add(averageOfTheAverage()); // Record the average of this generation

            // Select new population using selections
            // This should crop the total population by a factor of the number of candidates k
            // The outcome should be a list of the list size * (1/k) best chromosomes, providing elitism of this size
            tournamentSelection(tournamentCandidateNum);
            // This list will be all of the winners, and those that didn't compete due to size limitation

            // Set up the elitism to preserve the genes through mutation
            ArrayList<Chromosome> winners = new ArrayList<>(chromosomes);
            chromosomes.clear();
            // This means that we iterate through the winners for repopulating the chromosome pool
            //  and then we mutate the chromosome pool only
            // Afterwards we recombine the winners (elites) with the offspring (mutated children)


            // Apply crossover and mutation
            // Choose which type of crossover is happening
            for (int j = 0; j < (winners.size()-1) / 2 ; j += 2) { // Pick pairs
                int r = Seeder.looseNextInt(99) + 1; // [0...99] -> [1...100]
                if (r <= crossoverRate) { // Determine if that crossover happens
                    // Get the resulting crossed
                    Chromosome[] crossed = new Chromosome[2];
                    // Determine the cross type
                    if(crossoverType == 0)
                        crossed = Crossover.uniformOrder(winners.get(j), winners.get(j + 1));
                    else if(crossoverType == 1)
                        crossed = Crossover.pmx(winners.get(j), winners.get(j + 1));
                    // Add them to the list
                    // There is a chance that the list could get bigger?
                    // Limit it to the current children plus the parent size
                    if (chromosomes.size() + winners.size() + 2 <= finalSize) {
                        chromosomes.add(crossed[0]);
                        chromosomes.add(crossed[1]);
                    } // Otherwise, there is no more space for them (but this should never happen)
                }
            }
            // Mutation
            for (Chromosome chromosome : chromosomes) { // For each child
                int r = Seeder.looseNextInt(99) + 1; // [0...99] -> [1...100]
                if (r <= mutationRate) { // Determine if it mutates
                    if (mutationType == 0)
                        Mutation.swap(chromosome);
                    else if (mutationType == 1)
                        Mutation.scramble(chromosome);
                    else if (mutationType == 2)
                        Mutation.inversion(chromosome);
                }
            }

            // Need add the winners back into the pool
            chromosomes.addAll(winners);
            // Make sure that the best of the best is always added
            if(!chromosomes.contains(theBestChromosome))
                chromosomes.add(theBestChromosome);

            // Finally, fill in the missing chromosomes to keep the same population size
            chromosomes.addAll(generateXChromosomes(finalSize - chromosomes.size(), cities.size()));
        }
        TerminalControl.sendStatusMessage("Generations complete!");
        if(print){
             // This is after all generations of the program have been completed
            ReportWriter.printResults(maximumGenerations, tournamentCandidateNum, crossoverType, crossoverRate,
                mutationType, mutationRate, bestPerGeneration, avgPerGeneration, cities.size(), chromosomes.size());
        }
        ArrayList<Chromosome> finalList = new ArrayList<>(chromosomes);
        return new SeedStandardDTO(chromosomes.size(), maximumGenerations, crossoverType, crossoverRate, mutationType, mutationRate, tournamentCandidateNum, finalList, avgPerGeneration, bestPerGeneration, -1);
    }

    // Runs evaluation simulation and selection based on existing cities and chromosomes
    // - Evaluate fitness
    // - Select new population using selections
    // - Apply crossover and mutation
    void runEval() {
        int maximumGenerations = fd.askForInt("Enter maximum chromosome generation:");
        int crossoverType = fd.askForType(
                "Please choose an option for crossover type:\n" +
                "[0] - UOX with bitmask\n" +
                "[1] - PMX", 1);
        int crossoverRate = fd.askForInt("Enter crossover rate in % (as an Integer)");
        int mutationType = fd.askForType(
                "Please choose an option for mutation type:\n" +
                "[0] - Swap\n" +
                "[1] - Scramble\n" +
                "[2] - Inversion", 2);
        int mutationRate = fd.askForInt("Enter mutation rate in % (as an Integer)");
        int tournamentCandidateNum = fd.askForInt(
                        "Tournament k (candidates)?" +
                        "\nThis will also be the elitism size." +
                        "\nElitism = numChromosomes  / numCandidates");
        
        preformGenerations(maximumGenerations, crossoverType, crossoverRate, mutationType, mutationRate, tournamentCandidateNum, true, true);
    }

    Chromosome bestOfTheBest() {
        Chromosome theBest = new Chromosome();
        for (Chromosome entry : chromosomes) {
            if (entry.score < theBest.score)
                theBest = entry;
        }
        return theBest;
    }

    float averageOfTheAverage(){
        float sum = 0;
        for(Chromosome chromosome:chromosomes)
            sum += chromosome.score;
        return sum/chromosomes.size();
    }

    void tournamentSelection(int tournamentCandidateNum) {
        ArrayList<Chromosome> winners = new ArrayList<>();

        // Keep choosing candidates
        while (chromosomes.size() != 0) {
            if (chromosomes.size() < tournamentCandidateNum) break;
            Chromosome theBest = new Chromosome();
            // Find and fight contenders
            for (int i = 0; i < tournamentCandidateNum; i++) {
                Chromosome fighter = chromosomes.remove(Seeder.looseNextInt(chromosomes.size()));
                if (fighter.score < theBest.score)
                    theBest = fighter;
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

    ArrayList<Chromosome> initializeRandomStartingPopulation(int popSize) {
        return generateXChromosomes(popSize, cities.size());
    }

    // This method call returns a list of random chromosomes that number the original size of the
    //  population minus the current size, with lengths of the number of cities
    ArrayList<Chromosome> generateXChromosomes(int amountToHatch, int length) {
        ArrayList<Chromosome> hatchling = new ArrayList<>();
        for (int i = 0; i < amountToHatch; i++)
            hatchling.add(generateRandomChromosome(length));
        return hatchling;
    }

    // Returns a random Chromosome (ie, a sequence of cities in random order)
    Chromosome generateRandomChromosome(int size) {
        // Create a new list, an populate it with integers that represent the city index
        ArrayList<Integer> present = new ArrayList<>();
        for (int i = 0; i < size; i++)
            present.add(i);
        // Randomize the list
        //Collections.shuffle(present); Although this would be easiest, we need seed support
        ArrayList<Integer> perfect = new ArrayList<>();
        while(present.size()!=0){ // Remove them all in an order determined by the seed
            perfect.add(present.remove(Seeder.nextInt(present.size())));
        }
        // Return the resulting chromosome
        // Return function taken from https://www.geeksforgeeks.org/arraylist-array-conversion-java-toarray-methods/
        return new Chromosome(perfect.stream().mapToInt(i -> i).toArray());
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

    public static void main(String[] args) {
        GAProj p = new GAProj();
    }
}
