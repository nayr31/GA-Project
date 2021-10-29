import java.util.Random;

public class Seeder {
    private static final Long[] seeds = new Long[] { 1L, 2L, 3L, 4L, 5L};
    private static final Random random = new Random();
    private static final Random looseRandom = new Random();

    static void setSeed(int i){
        random.setSeed(seeds[i]);
    }

    static int nextInt(int bound){
        return random.nextInt(bound);
    }

    static void setLooseSeed(Long seed){
        looseRandom.setSeed(seed);
    }

    static int looseNextInt(int bound){
        return looseRandom.nextInt(bound);
    }
}
