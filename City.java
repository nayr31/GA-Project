public class City {

    // Stores the location of a city
    float x, y;

    City(int x, int y){
        this.x = x;
        this.y = y;
    }

    double dist(City c){
        return Math.sqrt(Math.pow(this.x-c.x, 2) + Math.pow(this.y-c.y, 2));
    }
}
