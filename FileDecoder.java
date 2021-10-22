import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileDecoder {
    
    Scanner scanner;
    ArrayList<City> cities = new ArrayList<>();

    FileDecoder(){
        scanner = new Scanner(System.in);
    }

    // Asks the user for input for the input file
    ArrayList<City> getInput(){
        String filename;
        while(true){ // Continually ask the user for a valid filename
            System.out.println("Please enter the file name you wish to use as data.\nNote: Must be beside compiled files and ");
            filename = scanner.nextLine();
            if(!tryInput(filename)) break;
        }
        return decode(filename);
    }

    boolean tryInput(String filename){
        // To test the file, we try opening it
        try{
            // This is done using a scanner
            new Scanner(new File(filename)).close();
            return true;
        } catch (FileNotFoundException e){
            System.out.println("File not found.");
            return false;
        } catch (Exception e){
            System.out.println("Unknown error:" + e);
            return false;
        }
    }

    // Will always assume that the file is the right format, see test1.txt and test2.txt for examples
    ArrayList<City> decode(String filename){
        ArrayList<City> cities = new ArrayList<>();
        
        try{
            // Initialize the reader
            File file = new File(filename);
            Scanner reader = new Scanner(file);

            while(reader.hasNextLine()){
                switch(reader.nextLine()){
                    tete
                }
                
            }
            reader.close();
            return cities;
        } catch (FileNotFoundException e){ // Scanner requires a try catch anyway
            System.out.println("This shouldn't have happened, what happened?");
            return null;
        }
    }

}
