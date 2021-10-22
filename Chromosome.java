public class Chromosome {

    int[] data;

    Chromosome(int length) {
        this.data = new int[length];
    }

    Chromosome(int[] data){
        this.data = data;
    }

    public String toString() {
        StringBuilder s = new StringBuilder("[");
        for (int datum : data) s.append(datum).append(",");
        s = new StringBuilder(s.substring(0, s.length() - 1));
        s.append("]");
        return s.toString();
    }
}
