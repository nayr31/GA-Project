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
                else {
                    float bestOfResult = seedStandardDTO.getTheBest();
                    if (bestOfResult < theBestIteration.getTheBest())
                        theBestIteration = seedStandardDTO;
                }
            }
        }
        lines.add("The best iteration details: ---------");
        assert theBestIteration != null;
        lines.add(theBestIteration.toString());
        ReportWriter.print(lines, "fiveSeedStandardReport.txt");
        //TODO make averages for each iteration type
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
