// A data storage object that houses the required information for a city
// Also can determine the distance to another city from itself
public class City {

    // Stores the location of a city
    float x, y;

    City(String x, String y) {
        this.x = Float.parseFloat(x);
        this.y = Float.parseFloat(y);
    }

    City(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Distance between cities is d = sqrt( delX^2 + delY^2 )
    double dist(City c) {
        return Math.sqrt(Math.pow(this.x - c.x, 2) + Math.pow(this.y - c.y, 2));
    }

    public String toString() {
        return "[" + x + "," + y + "]";
    }
}
