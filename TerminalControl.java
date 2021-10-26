import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

// The main interface with interacting with the program
// Displays user prompts, and program responses
public class TerminalControl extends JFrame {

    static final int width = 600;
    static final int height = 240;
    static final int commonColumns = 50;
    static String lastCommandInput = "Last input: ";
    static JTextArea commandsReceivedArea = new JTextArea(4, commonColumns);
    static JTextArea statusArea = new JTextArea(3, commonColumns);
    static ArrayList<String> lastMessages = new ArrayList<>();
    static Semaphore semaphore = new Semaphore(0);

    TerminalControl(){
        setup();
    }

    private void setup(){
        FrameSetup.setup(this,"-Traveling Salesman Input-", width, height, true, EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        initField();
    }

    // Creates and places the fields on the screen
    private void initField() {
        // User prompts
        add(commandsReceivedArea);
        // User inputs
        JTextField field = new JTextField(commonColumns);
        add(field);
        // User input echo
        JTextArea commandArea = new JTextArea(1, commonColumns);
        add(commandArea);
        commandArea.setText(lastCommandInput);
        commandArea.setEditable(false);
        // Set the enter key to release the semaphore for user input
        field.addActionListener(event ->{
            String textFromField = field.getText();
            field.setText("");
            lastCommandInput = textFromField;
            commandArea.setText("Last input: " + lastCommandInput);
            semaphore.release();
        });
        commandsReceivedArea.setEditable(false);
        add(statusArea);
        statusArea.setEditable(false);
    }

    // Causes the program to wait until the enter key is pressed in the command field
    static String getInput() throws InterruptedException {
        semaphore.acquire();
        return lastCommandInput;
    }

    // Sends text to the command text area
    static void sendCommandText(String text){
        commandsReceivedArea.setText(text);
    }

    static void sendStatusMessage(String text){
        statusArea.setText(text);
    }

    static private String buildStatusString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < lastMessages.size(); i++) {
            stringBuilder.append(lastMessages.get(i));
            if(i < lastMessages.size()-1) stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
