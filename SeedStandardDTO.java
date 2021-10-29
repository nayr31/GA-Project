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
    private int genOfFirstBest = -1;

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
            genBest();
        return theBest;
    }

    int getTheBestGen(){
        if(theBest == -1)
            genBest();
        return genOfFirstBest;
    }

    String printTheBest(){
        if(theBest == -1)
            genBest();
        return "Best value: " + theBest + " was generated at generation " + genOfFirstBest;
    }

    private void genBest(){
        float theBest = 99999;
        for (int i = 0; i < bestPerGeneration.size(); i++) {
            float aFloat = bestPerGeneration.get(i);
            if (aFloat < theBest) {
                theBest = aFloat;
                genOfFirstBest = i;
            }
        }
        this.theBest = theBest;
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