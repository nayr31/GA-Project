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

        while (true) {
            int selection = fd.askForType("""
                    Please choose an option:
                    [0] - Exit
                    [1] - Run standard program
                    [2] - Show last result (node network)""", 2);
            if (selection == 0) {
                break;
            } else if (selection == 1) {
                runStandard();
            } else if (selection == 2){
                if(cityLooker != null)
                    cityLooker.showWindow();
                else
                    TerminalControl.sendStatusMessage("No last result present!");
            }
        }
        TerminalControl.sendStatusMessage("No longer receiving inputs.\nHave a nice day.");
    }

    void runStandard() {
        // Step 1: Get input data (cities and their locations)
        cities = fd.getCities();

        // Step 2: Initialize Initial population
        chromosomes = initializeRandomStartingPopulation(fd.askForInt("Please enter population size:"));

        // Step 3: Evaluate, select, and mutate
        runEval(fd.askForInt("Enter maximum chromosome generation:"));
    }

    // Runs evaluation simulation and selection based on existing cities and chromosomes
    // - Evaluate fitness
    // - Select new population using selections
    // - Apply crossover and mutation
    void runEval(int maximumGenerations) {
        ArrayList<Float> avgPerGeneration = new ArrayList<>();
        ArrayList<Float> bestPerGeneration = new ArrayList<>();

        int crossoverType = fd.askForType("""
                Please choose an option for crossover type:
                [0] - UOX with bitmask
                [1] - PMX""", 1);
        int crossoverRate = fd.askForInt("Enter crossover rate in % (as an Integer)");
        int mutationType = fd.askForType("""
                Please choose an option for mutation type:
                [0] - Swap
                [1] - Scramble
                [2] - Inversion""", 2);
        int mutationRate = fd.askForInt("Enter mutation rate in % (as an Integer)");
        int tournamentCandidateNum = fd.askForInt("Tournament k (candidates)?");
        int finalSize = chromosomes.size();

        cityLooker = new CityLooker(cities);
        cityLooker.showWindow();

        for (int i = 0; i < maximumGenerations; i++) {
            // Evaluate fitness
            scoreChromosomeDistances(); // Calculate and store the scores within the chromosomes
            // It is really important that this is the first thing to happen!
            Chromosome theBestChromosome = bestOfTheBest();
            bestPerGeneration.add(theBestChromosome.score);
            cityLooker.draw(theBestChromosome); // Draw the current best of the best chromosome
            avgPerGeneration.add(averageOfTheAverage()); // Record the average of this generation

            // Select new population using selections
            tournamentSelection(tournamentCandidateNum);

            // Set up the elitism to preserve the genes through mutation
            ArrayList<Chromosome> winners = new ArrayList<>(chromosomes);
            chromosomes.clear();
            // This means that we iterate through the winners for repopulating the chromosome pool
            //  and then we mutate the chromosome pool only
            // Afterwards we recombine the winners (elites) with the offspring (mutated children)


            // Apply crossover and mutation
            // Choose which type of crossover is happening
            Random crossoverRandom = new Random();
            for (int j = 0; j < (winners.size()-1) / 2 ; j += 2) { // Pick pairs
                int r = crossoverRandom.nextInt(99) + 1; // [0...99] -> [1...100]
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
            Random mutatorRandom = new Random();
            for (Chromosome chromosome : chromosomes) { // For each child
                int r = mutatorRandom.nextInt(99) + 1; // [0...99] -> [1...100]
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

            // Finally, fill in the missing chromosomes to keep the same population size
            chromosomes.addAll(generateXChromosomes(finalSize - chromosomes.size(), cities.size()));
        }
        // This is after all generations of the program have been completed
        TerminalControl.sendStatusMessage("Generations complete!");
        ReportWriter.printResults(maximumGenerations, tournamentCandidateNum, crossoverType, crossoverRate,
                mutationType, mutationRate, bestPerGeneration, avgPerGeneration, cities.size(), chromosomes.size());
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

    public static void main(String[] args) {
        GAProj p = new GAProj();
    }
}
