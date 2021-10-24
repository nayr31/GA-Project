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
