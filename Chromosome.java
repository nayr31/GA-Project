import java.util.ArrayList;

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

    public String toString() {
        StringBuilder s = new StringBuilder("[");
        for (int datum : data) s.append(datum).append(",");
        s = new StringBuilder(s.substring(0, s.length() - 1));
        s.append("]");
        return s.toString();
    }
}
