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
        lines.add("Crossover type: " + (crossoverType == 0 ? "UOX with bitmask" : "PMX"));
        lines.add("Crossover rate: " + crossoverRate + "%");
        lines.add("Mutation type: " + (mutationType == 0 ? "Swap" : mutationType == 1 ? "Scramble" : "Inversion"));
        lines.add("Mutation rate: " + mutationRate + "%");
        lines.add("--------------Results-------------");
        lines.add("Average distance of first generation: " + avgPerGeneration.get(0));
        lines.add("Average distance of last generation: " + avgPerGeneration.get(avgPerGeneration.size()-1));
        lines.add("Best distance of first generation: " + bestPerGeneration.get(0));
        lines.add("Best distance of last generation: " + bestPerGeneration.get(bestPerGeneration.size()-1));
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

    private static void print(ArrayList<String> lines, String title) {
        try{
            Files.write(Paths.get(title), lines, StandardOpenOption.CREATE);
        } catch (IOException e){
            TerminalControl.sendStatusMessage("Error writing report file.");
            System.out.println("Error writing report file.");
        }
    }
}
