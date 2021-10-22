import java.util.ArrayList;
import java.util.Random;

public class Bitmask {
    ArrayList<Integer> mask;

    // Bit masks are defined as a list of 0 or 1
    Bitmask(int length){
        mask = new ArrayList<>();
        generateRandom(length);
    }

    void generateRandom(int length){
        for (int i = 0; i < length; i++)
            mask.add(new Random().nextInt(2)); // [0..1], starts at 0, goes to num-1
    }
}
