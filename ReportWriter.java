import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

// This class handles writing the report given a set of data
public class ReportWriter {

    // Used to generate a single file for testing purposes (in the option run standard)
    // This was used to generate the graphs
    static void printResults(int maxGen, int k, int crossoverType, int crossoverRate, int mutationType,
                             int mutationRate, ArrayList<Float> bestPerGeneration, ArrayList<Float> avgPerGeneration,
                             int numCities, int numChromosomes) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("File: " + FileDecoder.filename);
        lines.add("------------Properties------------");
        lines.add("Number of cities: " + numCities);
        lines.add("Number of chromosomes: " + numChromosomes);
        lines.add("Number of generations: " + maxGen);
        lines.add("Tournament candidates: " + k);
        lines.addAll(SeedStandardDTO.createIterationLines(crossoverType, crossoverRate, mutationType, mutationRate,
                        avgPerGeneration, bestPerGeneration));
        lines.add("------Average per generation------");
        for (Float aFloat : avgPerGeneration) lines.add(Float.toString(aFloat));
        lines.add("-------Best per generation--------");
        for (Float aFloat : bestPerGeneration) lines.add(Float.toString(aFloat));
        lines.add("EOF");
        String title = "";
        title += numCities + "-" + numChromosomes + "-" + maxGen + "-" + k + "-" +
                (crossoverType == 0 ? "UOX with bitmask" : "PMX") + "-" +
                crossoverRate + "%-" +
                (mutationType == 0 ? "Swap" : mutationType == 1 ? "Scramble" : "Inversion") + "-" +
                mutationRate + "%";
        ReportWriter.print(lines, title + ".txt");
    }

    // Prints the results of a five seed standard
    // The ArrayList<ArrayList<SeedStandardDTO>> stores 5 items, each an ArrayList<SeedStandardDTO> of a seed
    // This way, we can preform 5 seeds, of 5 generation iterations, getting 5 results of a single seed input data
    public static void printSeedResults(ArrayList<ArrayList<SeedStandardDTO>> finalSeedData) {
        ArrayList<String> fullLines = new ArrayList<>();
        ArrayList<String> shortLines = new ArrayList<>();
        doubleAdd(fullLines, shortLines, "File: " + FileDecoder.filename);
        doubleAdd(fullLines,  shortLines, "Number of cities: " + finalSeedData.get(0).get(0).finalChromosomeList.get(0).data.length);
        doubleAdd(fullLines,  shortLines, "Number of chromosomes: " + finalSeedData.get(0).get(0).finalChromosomeList.size());
        doubleAdd(fullLines,  shortLines, "Number of generations: " + finalSeedData.get(0).get(0).avgPerGeneration.size());
        doubleAdd(fullLines,  shortLines, "Tournament candidates: " + finalSeedData.get(0).get(0).tournamentCandidateNum);

        // The full lines list differs by printing the results of every seed generation iteration data
        fullLines.add("------------Printing per seed per iteration data...------------");
        // For each seed
        for (int i = 0; i < finalSeedData.size(); i++) {
            fullLines.add("-------Seed: " + i);
            // For each iteration type
            for (int j = 0; j < finalSeedData.get(i).size(); j++) {
                fullLines.add("-----------Iteration: " + j);
                SeedStandardDTO seedData = finalSeedData.get(i).get(j);
                fullLines.add("Crossover type: " + (seedData.crossoverType == 0 ? "UOX with bitmask" : "PMX"));
                fullLines.add("Crossover rate: " + seedData.crossoverRate + "%");
                fullLines.add("Mutation type: " + (seedData.mutationType == 0 ? "Swap" : seedData.mutationType == 1 ? "Scramble" : "Inversion"));
                fullLines.add("Mutation rate: " + seedData.mutationRate + "%");
                fullLines.add("--------------Results-------------");
                fullLines.add("Average distance of first generation: " + seedData.avgPerGeneration.get(0));
                fullLines.add("Average distance of last generation: " + seedData.avgPerGeneration.get(seedData.avgPerGeneration.size()-1));
                fullLines.add("Best distance of first generation: " + seedData.bestPerGeneration.get(0));
                fullLines.add("Best distance of last generation: " + seedData.bestPerGeneration.get(seedData.bestPerGeneration.size()-1));
            }
        }
        fullLines.add("------------Finished writing per iteration data.------------");
        // Best iteration data
        SeedStandardDTO theBestIteration = null;
        for (ArrayList<SeedStandardDTO> finalSeedDatum : finalSeedData) {
            for (SeedStandardDTO seedStandardDTO : finalSeedDatum) {
                if (theBestIteration == null) theBestIteration = seedStandardDTO;
                else if (seedStandardDTO.getTheBest() < theBestIteration.getTheBest())
                        theBestIteration = seedStandardDTO;
            }
        }
        doubleAdd(fullLines,  shortLines, "The best iteration details: ---------");
        assert theBestIteration != null;
        doubleAdd(fullLines,  shortLines, theBestIteration.printTheBest());
        doubleAdd(fullLines,  shortLines, theBestIteration.toString());

        doubleAdd(fullLines,  shortLines, "-------------Average of vectors over a 5 set seed-------------");
        // Averages and bests of the different types
        doubleAdd(fullLines,  shortLines, "Crossover 100% No Mutation:  ---------");
        doubleAddAll(fullLines,  shortLines, buildAveragesOnSeed(0, finalSeedData));
        doubleAdd(fullLines,  shortLines, "Crossover 100% Inversion 10%:  ---------");
        doubleAddAll(fullLines,  shortLines, buildAveragesOnSeed(1, finalSeedData));
        doubleAdd(fullLines,  shortLines, "Crossover 90% No Mutation:  ---------");
        doubleAddAll(fullLines,  shortLines, buildAveragesOnSeed(2, finalSeedData));
        doubleAdd(fullLines,  shortLines, "Crossover 90% Inversion 10%:  ---------");
        doubleAddAll(fullLines,  shortLines, buildAveragesOnSeed(3, finalSeedData));
        doubleAdd(fullLines,  shortLines, "Crossover 90% Inversion 15%:  ---------");
        doubleAddAll(fullLines,  shortLines, buildAveragesOnSeed(4, finalSeedData));

        // Print the full and short details of the results
        ReportWriter.print(fullLines, "fiveSeedStandardFullReport.txt");
        ReportWriter.print(shortLines, "fiveSeedStandardShortReport.txt");
    }

    // Adds a list of strings to two different lists of strings
    private static void doubleAddAll(ArrayList<String> lines1, ArrayList<String> lines2, ArrayList<String> strings){
        lines1.addAll(strings);
        lines2.addAll(strings);
    }

    // Adds a string to two different lists of strings
    private static void doubleAdd(ArrayList<String> lines1, ArrayList<String> lines2, String string){
        lines1.add(string);
        lines2.add(string);
    }

    // Returns a list of lines that pertains to average information about a seed data seed number
    private static ArrayList<String> buildAveragesOnSeed(int seedIndex,
                                                         ArrayList<ArrayList<SeedStandardDTO>> finalSeedData){
        ArrayList<String> lines = new ArrayList<>();
        int length = finalSeedData.size();

        float avgSeedDataVal = 0;
        float bestSeedDataVal = 0;
        int genOfBestAvg = 0;
        float valOfBestAvg = 0;
        for (ArrayList<SeedStandardDTO> finalSeedDatum : finalSeedData) {
            SeedStandardDTO seedStandardDTO = finalSeedDatum.get(seedIndex);
            float lastAvg = seedStandardDTO.avgPerGeneration.get(seedStandardDTO.avgPerGeneration.size() - 1);
            avgSeedDataVal += lastAvg;
            float lastBest = seedStandardDTO.bestPerGeneration.get(seedStandardDTO.bestPerGeneration.size() - 1);
            bestSeedDataVal += lastBest;
            int bestGen = seedStandardDTO.getTheBestGen();
            genOfBestAvg += bestGen;
            float firstGen = seedStandardDTO.getValOfFirstBest();
            valOfBestAvg += firstGen;
        }
        avgSeedDataVal = avgSeedDataVal / length;
        bestSeedDataVal = bestSeedDataVal / length;
        genOfBestAvg = genOfBestAvg / length;
        valOfBestAvg = valOfBestAvg / length;
        lines.add("Average of all last averages: " + avgSeedDataVal);
        lines.add("Average of all last bests: " + bestSeedDataVal);
        lines.add("Average of first bests: " + valOfBestAvg);
        lines.add("Average of first best generation: " + genOfBestAvg);
        return lines;
    }

    // Uses provided list of lings to print to disk a file of that data
    private static void print(ArrayList<String> lines, String title) {
        try{
            Files.write(Paths.get(title), lines, StandardOpenOption.CREATE);
        } catch (IOException e){
            TerminalControl.sendStatusMessage("Error writing report file.");
            System.out.println("Error writing report file.");
        }
    }
}
