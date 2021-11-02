import java.util.ArrayList;
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

    //TODO make this Seeder.looseSeed based

    static void scramble(Chromosome chromosome){
        commonMutate(chromosome, "Scramble");
    }

    static void inversion(Chromosome chromosome){
        commonMutate(chromosome, "Inverse");
    }

    // Common mutation method used between scramble and inversion
    static private void commonMutate(Chromosome chromosome, String type){
        //TODO Make these do wrap instead of range (I think it has something to do with [%])
        // Then figure out if it would actually be worth the hassle performance wise or just make the code shorter
        Random r = new Random();

        // Get a range in the sequence that is between 1/4 and 1/3 of the length of the sequence
        int startNum = r.nextInt(chromosome.data.length-2); // Start index is anywhere in the array minus 2
        // This makes the final range always equal at least 2 (-1 for the end, -1 for the one before it)
        int diff = chromosome.data.length-1 - startNum; // The range in between the start and the end of the array
        if(diff < chromosome.data.length/4) { // Make sure it at least mutates 1/4 of the sequence
            startNum = chromosome.data.length-chromosome.data.length/4;
            diff = chromosome.data.length-1 - startNum;
        }
        if(diff >= chromosome.data.length/3) diff = chromosome.data.length/3; // Limit it to a third of the array
        int endNum = startNum + r.nextInt(diff) + 1; // Generate a random number in between the start and the end
        // Since it can get to equal 0, we add 1 so that we can have at least 2 elements

        if(endNum > chromosome.data.length) // Just in case my math was wrong
            endNum = chromosome.data.length;

        // Use the range to make a mutation, depending on the type that was given
        ArrayList<Integer> list = new ArrayList<>();
        // Take note of the order of numbers that appear in the swath
        for (int i = startNum; i <endNum ; i++)
            list.add(chromosome.data[i]);

        // Choose which type of collection change to do
        if(type.equals("Scramble"))
            Collections.shuffle(list);
        else if(type.equals("Inverse"))
            Collections.reverse(list);

        // Replace the old numbers with the new list
        for (int i = startNum; i <endNum ; i++)
            chromosome.data[i] = list.remove(0);
    }
}
