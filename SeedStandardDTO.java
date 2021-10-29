import java.util.ArrayList;

class SeedStandardDTO{
    int popSize;
    int maximumGenerations;
    int crossoverType;
    int crossoverRate;
    int mutationType;
    int mutationRate;
    int tournamentCandidateNum;
    ArrayList<Chromosome> finalChromosomeList;
    ArrayList<Float> avgPerGeneration;
    ArrayList<Float> bestPerGeneration;
    long seedNum;
    private float theBest = -1;

    public SeedStandardDTO(int popSize, int maximumGenerations, int crossoverType, int crossoverRate, int mutationType, int mutationRate, int tournamentCandidateNum, ArrayList<Chromosome> finalChromosomeList, ArrayList<Float> avgPerGeneration, ArrayList<Float> bestPerGeneration, long seedNum) {
        this.popSize = popSize;
        this.maximumGenerations = maximumGenerations;
        this.crossoverType = crossoverType;
        this.crossoverRate = crossoverRate;
        this.mutationType = mutationType;
        this.mutationRate = mutationRate;
        this.tournamentCandidateNum = tournamentCandidateNum;
        this.finalChromosomeList = finalChromosomeList;
        this.avgPerGeneration = avgPerGeneration;
        this.bestPerGeneration = bestPerGeneration;
        this.seedNum = seedNum;
    }

    float getTheBest(){
        if(theBest == -1)
            theBest = genBest();
        return theBest;
    }

    private float genBest(){
        float theBest = 99999;
        for (Float aFloat : bestPerGeneration) {
            if (aFloat < theBest)
                theBest = aFloat;
        }
        return theBest;
    }

    public String toString(){
        ArrayList<String> lines = createIterationLines(crossoverType, crossoverRate, mutationType, mutationRate, avgPerGeneration, bestPerGeneration);
        StringBuilder builder = new StringBuilder();
        for (String line : lines)
            builder.append(line).append("\n");
        return builder.toString();
    }

    public static ArrayList<String> createIterationLines(int crossoverType, int crossoverRate, int mutationType, int mutationRate, ArrayList<Float> avgPerGeneration, ArrayList<Float> bestPerGeneration) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("Crossover type: " + (crossoverType == 0 ? "UOX with bitmask" : "PMX"));
        lines.add("Crossover rate: " + crossoverRate + "%");
        lines.add("Mutation type: " + (mutationType == 0 ? "Swap" : mutationType == 1 ? "Scramble" : "Inversion"));
        lines.add("Mutation rate: " + mutationRate + "%");
        lines.add("--------------Results-------------");
        lines.add("Average distance of first generation: " + avgPerGeneration.get(0));
        lines.add("Average distance of last generation: " + avgPerGeneration.get(avgPerGeneration.size()-1));
        lines.add("Best distance of first generation: " + bestPerGeneration.get(0));
        lines.add("Best distance of last generation: " + bestPerGeneration.get(bestPerGeneration.size()-1));
        return lines;
    }
}