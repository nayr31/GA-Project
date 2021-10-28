import java.util.ArrayList;

class SeedStandardDTO{
    int popSize;
    int maximumGenerations;
    int crossoverType;
    int crossoverRate;
    int mutationType;
    int  mutationRate;
    int tournamentCandidateNum;
    ArrayList<Chromosome> finalChromosomeList = new ArrayList<>();
    ArrayList<Float> avgPerGeneration = new ArrayList<>();
    ArrayList<Float> bestPerGeneration = new ArrayList<>();
    //TODO Create constructor
}