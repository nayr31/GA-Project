import java.util.ArrayList;

// Stores the generation iteration data from the program
// This records all data relevant to the gentic algorithm
// This is stored 5 times for averages per seed, after 5 seeds there are 25 of these per required crossover/mutation set
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
    private float valOfFirstBest = 9999999;

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

    float getValOfFirstBest(){
        if(theBest == -1)
            genBest();
        return valOfFirstBest;
    }

    // Returns a string of the best value and first bet values
    String printTheBest(){
        if(theBest == -1)
            genBest();
        return "Best value: " + theBest + ". " +
                "Last best was generated at generation " + genOfFirstBest + " with value " + valOfFirstBest;
    }

    // Finds the best overall value
    //  and the point at which the program stagnates and stops generating sufficiently better solutions
    private void genBest(){
        float theBest = 99999;
        for (int i = 0; i < bestPerGeneration.size(); i++) {
            float aFloat = bestPerGeneration.get(i);
            if (aFloat < theBest) {
                theBest = aFloat;
                // This next part checks to see if the last best value are at least better by 10%
                if(valOfFirstBest/aFloat > 1.1){
                    genOfFirstBest = i;
                    valOfFirstBest = aFloat;
                }
            }
        }
        this.theBest = theBest;
    }

    public String toString(){
        ArrayList<String> lines = createIterationLines(crossoverType, crossoverRate, mutationType, mutationRate, avgPerGeneration, bestPerGeneration);
        StringBuilder builder = new StringBuilder();
        for (String line : lines)
            builder.append(line).append("\n");
        builder.append("Stopped improving at generation: ").append(genOfFirstBest);
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