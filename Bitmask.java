import java.util.ArrayList;
import java.util.Random;

public class Bitmask {
    ArrayList<Integer> mask;
    Random random;

    // Bit masks are defined as a list of 0 or 1
    Bitmask(int length){
        mask = new ArrayList<>();
        random = new Random();
        generateRandom(length);
    }

    void generateRandom(int length){
        for (int i = 0; i < mask.size(); i++)
            mask.add((random.nextInt(2) == 0) ? 0 : 1);
    }
}
