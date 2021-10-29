import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

// This class handles writing the report given a set of data
public class ReportWriter {

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

    public static void printSeedResults(ArrayList<ArrayList<SeedStandardDTO>> finalSeedData) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("File: " + FileDecoder.filename);
        lines.add("Number of cities: " + finalSeedData.get(0).get(0).finalChromosomeList.get(0).data.length);
        lines.add("Number of chromosomes: " + finalSeedData.get(0).get(0).finalChromosomeList.size());
        lines.add("Number of generations: " + finalSeedData.get(0).get(0).avgPerGeneration.size());
        lines.add("Tournament candidates: " + finalSeedData.get(0).get(0).tournamentCandidateNum);
        lines.add("------------Printing per seed per iteration data...------------");
        // For each seed
        for (int i = 0; i < finalSeedData.size(); i++) {
            lines.add("-------Seed: " + i);
            // For each iteration type
            for (int j = 0; j < finalSeedData.get(i).size(); j++) {
                lines.add("-----------Iteration: " + j);
                SeedStandardDTO seedData = finalSeedData.get(i).get(j);
                lines.add("Crossover type: " + (seedData.crossoverType == 0 ? "UOX with bitmask" : "PMX"));
                lines.add("Crossover rate: " + seedData.crossoverRate + "%");
                lines.add("Mutation type: " + (seedData.mutationType == 0 ? "Swap" : seedData.mutationType == 1 ? "Scramble" : "Inversion"));
                lines.add("Mutation rate: " + seedData.mutationRate + "%");
                lines.add("--------------Results-------------");
                lines.add("Average distance of first generation: " + seedData.avgPerGeneration.get(0));
                lines.add("Average distance of last generation: " + seedData.avgPerGeneration.get(seedData.avgPerGeneration.size()-1));
                lines.add("Best distance of first generation: " + seedData.bestPerGeneration.get(0));
                lines.add("Best distance of last generation: " + seedData.bestPerGeneration.get(seedData.bestPerGeneration.size()-1));
            }
        }
        lines.add("------------Finished writing per iteration data.------------");
        lines.add("The following are analysis vectors of the written data.");
        // Best iteration data
        SeedStandardDTO theBestIteration = null;
        for (ArrayList<SeedStandardDTO> finalSeedDatum : finalSeedData) {
            for (SeedStandardDTO seedStandardDTO : finalSeedDatum) {
                if (theBestIteration == null) theBestIteration = seedStandardDTO;
                else if (seedStandardDTO.getTheBest() < theBestIteration.getTheBest())
                        theBestIteration = seedStandardDTO;
            }
        }
        lines.add("The best iteration details: ---------");
        assert theBestIteration != null;
        lines.add(theBestIteration.printTheBest());
        lines.add(theBestIteration.toString());

        lines.add("Average of vectors over a 5 set seed: ");
        // Averages and bests of the different types
        lines.add("Crossover 100% No Mutation:  ---------"); // Index 0 from each seed
        lines.addAll(buildAveragesOnSeed(0, finalSeedData));
        lines.add("Crossover 100% Inversion 10%:  ---------"); // Index 1 from each seed
        lines.addAll(buildAveragesOnSeed(1, finalSeedData));
        lines.add("Crossover 90% No Mutation:  ---------"); // Index 2 from each seed
        lines.addAll(buildAveragesOnSeed(2, finalSeedData));
        lines.add("Crossover 90% Inversion 10%:  ---------"); // Index 3 from each seed
        lines.addAll(buildAveragesOnSeed(3, finalSeedData));
        lines.add("Crossover 90% Inversion 15%:  ---------"); // Index 4 from each seed
        lines.addAll(buildAveragesOnSeed(4, finalSeedData));

        //TODO figure out where this is re-creating the printresults
        ReportWriter.print(lines, "fiveSeedStandardReport.txt");
    }

    private static ArrayList<String> buildAveragesOnSeed(int seedIndex,
                                                         ArrayList<ArrayList<SeedStandardDTO>> finalSeedData){
        ArrayList<String> lines = new ArrayList<>();
        int length = finalSeedData.size();

        float avgSeedDataVal = 0;
        float bestSeedDataVal = 0;
        int genOfBestAvg = 0;
        for (ArrayList<SeedStandardDTO> finalSeedDatum : finalSeedData) {
            SeedStandardDTO seedStandardDTO = finalSeedDatum.get(seedIndex);
            float lastAvg = seedStandardDTO.avgPerGeneration.get(seedStandardDTO.avgPerGeneration.size() - 1);
            avgSeedDataVal += lastAvg;
            float lastBest = seedStandardDTO.bestPerGeneration.get(seedStandardDTO.bestPerGeneration.size() - 1);
            bestSeedDataVal += lastBest;
            int bestGen = seedStandardDTO.getTheBestGen();
            genOfBestAvg += bestGen;
        }
        avgSeedDataVal = avgSeedDataVal / length;
        bestSeedDataVal = bestSeedDataVal / length;
        genOfBestAvg = genOfBestAvg / length;
        lines.add("Average of all last averages: " + avgSeedDataVal);
        lines.add("Average of all last bests: " + bestSeedDataVal);
        lines.add("Average generation of best generation score (it stopped getting better): " + genOfBestAvg);
        return lines;
    }

    private static void print(ArrayList<String> lines, String title) {
        try{
            Files.write(Paths.get(title), lines, StandardOpenOption.CREATE);
        } catch (IOException e){
            TerminalControl.sendStatusMessage("Error writing report file.");
            System.out.println("Error writing report file.");
        }
    }
}
