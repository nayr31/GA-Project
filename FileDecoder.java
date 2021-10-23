import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class FileDecoder {

    Scanner scanner;
    // The nice thing about array lists is that they are both arrays, and lists

    FileDecoder() {
        scanner = new Scanner(System.in);
    }

    // Asks the user for input for the input file
    ArrayList<City> getCities() {
        String filename;
        while (true) { // Continually ask the user for a valid filename
            System.out.println("Please enter the file name you wish to use as data.");
            scanner.nextLine();
            filename = scanner.nextLine();
            System.out.println("Trying \"" + filename + "\"");
            if (tryInput(filename)) break;
        }
        System.out.println("Successfully read " + filename);
        return decode(filename);
    }

    City manuallyInputCity(){
        while (true){
            try {
                System.out.println("Enter x:");
                float x = scanner.nextFloat();
                System.out.println("Enter y:");
                float y = scanner.nextFloat();
                return new City(x, y);
            } catch (InputMismatchException e){
                System.out.println("Not a number, start again.");
            }
        }
    }

    int askMaxGen(){
        while (true){
            try {
                System.out.println("Enter maximum chromosome generation:");
                return scanner.nextInt();
            } catch (InputMismatchException e){
                System.out.println("Not a number, try again.");
            }
        }
    }

    int askForType(String message, int selectionBound){
        while(true){
            System.out.println(message);
            try {
                int selection = scanner.nextInt();
                if(selection >= 0 && selection <= selectionBound)
                    return selection;
                System.out.println("Number not valid, input only the numbers listed.");
            } catch (InputMismatchException e){
                System.out.println("Not an int, try again.");
            }
        }
    }

    int askForInt(String message){
        while(true){
            System.out.println(message);
            try{
                return scanner.nextInt();
            } catch (InputMismatchException e){
                System.out.println("Incorrect format, requires an Integer.");
            } catch (Exception e){
                System.out.println("Unknown error occurred:\n" + e);
            }
        }
    }

    boolean tryInput(String filename) {
        // To test the file, we try opening it
        try {
            // This is done using a scanner
            new Scanner(new File(filename)).close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            return false;
        } catch (Exception e) {
            System.out.println("Unknown error occurred:\n" + e);
            return false;
        }
    }

    // Will always assume that the file is the right format, see test1.txt and test2.txt for examples
    ArrayList<City> decode(String filename) {
        ArrayList<City> cities = new ArrayList<>();

        try {
            // Initialize the reader
            File file = new File(filename);
            Scanner reader = new Scanner(file);

            String line;
            // Run over every line to skip the unimportant information
            while (reader.hasNextLine()) {
                line = reader.nextLine(); // Store it
                String[] key = line.split(" ");

                // Check to see if the read line is a line with a City on it
                // Can't just check for is a number, as one of the examples given has a space as the first entry (empty)
                // " 1 2 3" will be [0] = "", [1] = "1", [2] = "2", [3] = "3"
                if (key[0].equals("") || isNumeric(key[0])) {
                    int indexStart = 1; // So we use this number to create an offset of our inputted parsed values
                    if (key[0].equals("")) indexStart++;
                    // Then we can add the x and y values for the city to our list
                    cities.add(new City(key[indexStart], key[indexStart + 1]));
                }
            }
            reader.close();
            return cities;
        } catch (FileNotFoundException e) { // Scanner requires a try catch anyway
            System.out.println("This shouldn't have happened, what happened?");
            return null;
        }
    }

    boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
