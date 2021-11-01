import java.util.ArrayList;
import java.util.Random;

// This class houses all of the crossover methods
public class Crossover {

    // Uniform order crossover with bitmask
    // - Takes two parents
    // - Creates two children with the same cities at the index where the bit is 1
    // - Checks over the empty spaces to see if the other parent has a not yet represented city (otherwise its known missing)
    // - Fill in the known missing values
    // Adapted from this python code https://puphaiboon-kreecha.medium.com/uniform-order-based-crossover-uox-186e137cd9bd
    static Chromosome[] uniformOrder(Chromosome parent1, Chromosome parent2) {
        Object[] child1 = new Object[parent1.data.length];
        Object[] child2 = new Object[parent2.data.length];

        ArrayList<Integer> knownInC1 = new ArrayList<>();
        ArrayList<Integer> knownInC2 = new ArrayList<>();
        ArrayList<Integer> knownNotInC1 = new ArrayList<>();
        ArrayList<Integer> knownNotInC2 = new ArrayList<>();

        Bitmask bitmask = new Bitmask(parent1.data.length);

        // Take which bits are 1 into the children by default
        for (int i = 0; i < bitmask.mask.size(); i++) {
            if (bitmask.mask.get(i) == 1) {
                child1[i] = parent1.data[i];
                child2[i] = parent2.data[i];
                // Take note of which cities we took from the parents
                knownInC1.add(parent1.data[i]);
                knownInC2.add(parent2.data[i]);
            }
        }

        for (int i = 0; i < child1.length; i++) {
            // If the index at the other parent is not in this child
            if(!knownInC1.contains(parent2.data[i])){
                if (child1[i] == null) { // If its null, we inherit
                    child1[i] = parent2.data[i];
                    knownInC1.add(parent2.data[i]);
                } else if (child1[i] != null) { // If it isn't, then we know that it is missing
                    knownNotInC1.add(parent2.data[i]); // Otherwise we know its not in yet
                }
            }
            // The same happens with child 2
            if (!knownInC2.contains(parent1.data[i])) {
                if (child2[i] == null) {
                    child2[i] = parent1.data[i];
                    knownInC2.add(parent1.data[i]);
                } else if (child2[i] != null) {
                    knownNotInC2.add(parent1.data[i]);
                }
            }
        }

        // Fill in the blanks with cities we know aren't in the sequence
        for (int i = 0; i < child1.length; i++) {
            if (child1[i] == null)
                child1[i] = knownNotInC1.remove(0);
            if (child2[i] == null)
                child2[i] = knownNotInC2.remove(0);
        }
        // Running through the chromosome length will have a length of n
        // The amount of null operators are equal to n - (number of 0 bits) - (number of parent 1/2 inherited) = m
        // The amount in knownNotInCX is always equal to this value, so it should never be larger than or equal to n
        // This means that this method will always work without checks to make sure it is empty

        return new Chromosome[]{new Chromosome(child1), new Chromosome(child2)};
    }

    // Preforms a partial crossover
    // Method derived from http://www.ijsrp.org/research-paper-1012/ijsrp-p1094.pdf
    static Chromosome[] pmx(Chromosome parent1, Chromosome parent2){
        Object[] child1 = new Object[parent1.data.length];
        Object[] child2 = new Object[parent2.data.length];

        Object[] lookupC1 = new Object[child1.length];
        Object[] lookupC2 = new Object[child2.length];

        // This will always take half of the chromosome, but it should differ
        int swathSize = parent1.data.length/2; // Get the size of the swath we are using
        int startPoint = new Random().nextInt(swathSize-1); // The start will be in the first half
        int endPoint = startPoint + swathSize; // The end will be plus the swath

        // Copy the parents
        for (int i = 0; i < parent1.data.length; i++) {
            child1[i] = parent1.data[i];
            child2[i] = parent2.data[i];
            // Note at which index is the order
            lookupC1[(int)child1[i]] = i;
            lookupC2[(int)child2[i]] = i;
        }

        // For the points in the swath
        for (int i = startPoint; i < endPoint ; i++) {
            int j = i;// % parent1.data.length; // This will always equal i

            // Find the index that was used in that order and store it
            int bucketIndex = (int) lookupC1[parent2.data[i]];
            // Swap that ordered value and the other value from the order
            Object bucket = child1[bucketIndex];
            child1[bucketIndex] = child1[j];
            child1[j] = bucket;
            // Store the new other value
            lookupC1[(int) child1[bucketIndex]] = bucketIndex;

            // Do the same for the other child
            bucketIndex = (int) lookupC2[parent1.data[i]];
            bucket = child2[bucketIndex];
            child2[bucketIndex] = child2[j];
            child2[j] = bucket;
            lookupC2[(int) child2[bucketIndex]] = bucketIndex;
        }

        return new Chromosome[]{new Chromosome(child1), new Chromosome(child2)};
    }
}
