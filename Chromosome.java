import java.util.ArrayList;
import java.util.Random;

public class Chromosome {

    int[] data;
    float score;

    Chromosome(int length) {
        this.data = new int[length];
    }

    Chromosome(int[] data){
        this.data = data;
    }

    Chromosome(Object[] data){
        this.data = new int[data.length];
        for (int i = 0; i < data.length ; i++) {
            this.data[i] = (int)data[i];
        }
    }

    Chromosome(){
        score = 99999999;
    }

    // Mutators
    // - Swap = Swap two indices
    // - Scramble = Mix up a range of indexes
    // - Inversion = Reverse the order of a range

    // Get two random indices and swap their values
    void swapMutate(int timesToSwap){
        for (int i = 0; i < timesToSwap; i++) {
            Random r = new Random();
            int city1 = r.nextInt(data.length); // Get a random index
            int city2 = r.nextInt(data.length);
            while (city1 == city2) // Make sure they are different
                city2 = r.nextInt(data.length);
            int bucket = data[city1]; // Store value 1
            data[city1] = data[city2]; // Swap from index 2
            data[city2] = bucket; // Store the original value 1 into index 2
        }
    }

    void swapMutate(){
        swapMutate(1);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < data.length; i++) {
            stringBuilder.append(data[i]);
            if(i!=data.length-1) stringBuilder.append(","); // Skip the comma on the last index
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
