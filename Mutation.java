import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class Mutation {

    // Mutators
    // - Swap = Swap two indices
    // - Scramble = Mix up a range of indexes
    // - Inversion = Reverse the order of a range

    // Get two random indices and swap their values
    static void swap(Chromosome chromosome) {
        Random r = new Random();
        int city1 = r.nextInt(chromosome.data.length); // Get a random index
        int city2 = r.nextInt(chromosome.data.length);
        while (city1 == city2) // Make sure they are different
            city2 = r.nextInt(chromosome.data.length);
        int bucket = chromosome.data[city1]; // Store value 1
        chromosome.data[city1] = chromosome.data[city2]; // Swap from index 2
        chromosome.data[city2] = bucket; // Store the original value 1 into index 2
    }

    static void scramble(Chromosome chromosome){
        commonMutate(chromosome, "Scramble");
    }

    static void inversion(Chromosome chromosome){
        commonMutate(chromosome, "Inverse");
    }

    static private void commonMutate(Chromosome chromosome, String type){
        Random r = new Random();

        // Start will always be in the first half
        int start = r.nextInt(chromosome.data.length/2-1);
        // End will always be in the second half
        int end = r.nextInt(chromosome.data.length/4-1) + chromosome.data.length/2-1;
        // The total would occur when start = 0, and end = length/4 + length/2, max is 3/4 length
        if(end > chromosome.data.length) // Just in case
            end = chromosome.data.length;

        ArrayList<Integer> list = new ArrayList<>();
        // Take note of the order of numbers that appear
        for (int i = start; i <end ; i++)
            list.add(chromosome.data[i]);

        // Choose which type of collection change to do
        if(type.equals("Scramble"))
            Collections.shuffle(list);
        else if(type.equals("Inverse"))
            Collections.reverse(list);

        // Replace the old numbers with the new list
        for (int i = start; i <end ; i++)
            chromosome.data[i] = list.get(i);
    }
}
