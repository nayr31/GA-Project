public class City {

    // Stores the location of a city
    float x, y;

    City(String x, String y) {
        this.x = Float.parseFloat(x);
        this.y = Float.parseFloat(y);
    }

    double dist(City c) {
        return Math.sqrt(Math.pow(this.x - c.x, 2) + Math.pow(this.y - c.y, 2));
    }

    public String toString() {
        return "[" + x + "," + y + "]";
    }
}
