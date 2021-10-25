import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

// Handles all input of user information
public class FileDecoder {

    static String filename = "";
    // The nice thing about array lists is that they are both arrays, and lists

    // Asks the user for input for the input file
    ArrayList<City> getCities() {
        while (true) { // Continually ask the user for a valid filename
            TerminalControl.sendCommandText("Please enter the file name you wish to use as data.");
            filename = getInputFromTerminalControl();
            TerminalControl.sendStatusMessage("Trying \"" + filename + "\"");
            if (tryInput(filename)) break;
        }
        TerminalControl.sendStatusMessage("Successfully read " + filename);
        return decode(filename);
    }

    // Reserves the semaphore and returns the gotten value after it is released
    static String getInputFromTerminalControl(){
        try{
            return TerminalControl.getInput();
        } catch (InterruptedException e){
            TerminalControl.sendStatusMessage("Got interrupted reading the terminal control.");
            return "";
        }
    }

    // Might make this an option later idk
    /*
    City manuallyInputCity(){
        while (true){
            try {
                TerminalControl.sendCommandText("Enter x:");
                float x = Float.parseFloat(getInputFromTerminalControl());
                TerminalControl.sendCommandText("Enter y:");
                float y = Float.parseFloat(getInputFromTerminalControl());
                return new City(x, y);
            } catch (InputMismatchException e){
                TerminalControl.sendStatusMessage("Not a number, try again.");
            }
        }
    }*/

    // This is used for multi-type int input, with a bound
    // You can see this used when selecting options like crossover type
    int askForType(String message, int selectionBound){
        while(true){
            TerminalControl.sendCommandText(message);
            try {
                int selection = Integer.parseInt(getInputFromTerminalControl());
                if(selection >= 0 && selection <= selectionBound)
                    return selection;
                TerminalControl.sendStatusMessage("Number not valid, input only the numbers listed.");
            } catch (InputMismatchException e){
                TerminalControl.sendStatusMessage("Not an int, try again.");
            }
        }
    }

    // Asks the user for an int input, with a message to display to the TerminalControl
    int askForInt(String message){
        while(true){
            TerminalControl.sendCommandText(message);
            try{
                return Integer.parseInt(getInputFromTerminalControl());
            } catch (InputMismatchException e){
                TerminalControl.sendStatusMessage("Incorrect format, requires an Integer.");
            } catch (Exception e){
                TerminalControl.sendStatusMessage("Unknown error occurred:\n" + e);
            }
        }
    }

    // Attempts to read a file by filename
    boolean tryInput(String filename) {
        // To test the file, we try opening it
        try {
            // This is done using a scanner
            new Scanner(new File(filename)).close();
            return true;
        } catch (FileNotFoundException e) {
            TerminalControl.sendStatusMessage("File not found.");
            return false;
        } catch (Exception e) {
            TerminalControl.sendStatusMessage("Unknown error occurred:\n" + e);
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
            TerminalControl.sendStatusMessage("This shouldn't have happened, what happened?");
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
