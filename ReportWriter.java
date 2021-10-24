import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class ReportWriter {



    static void print(ArrayList<String> lines) {
        try{
            Files.write(Paths.get("report.txt"), lines, StandardOpenOption.CREATE);
        } catch (IOException e){
            TerminalControl.sendStatusMessage("Error writing report file.");
            System.out.println("Error writing report file.");
        }
    }
}
