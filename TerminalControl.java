import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

public class TerminalControl extends JFrame {

    static final int width = 600;
    static final int height = 200;
    static final int commonColumns = 50;
    static String lastCommandInput = "Last input: ";
    static JTextArea commandsReceivedArea = new JTextArea(3, commonColumns);
    static JTextArea statusArea = new JTextArea(3, commonColumns);
    static ArrayList<String> lastMessages = new ArrayList<>();
    static Semaphore semaphore = new Semaphore(0);

    TerminalControl(){
        setup();
    }

    private void setup(){
        // Make it so the window appears in the middle of the screen
        int sWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2;
        int sHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2;
        int x = sWidth - (width / 2);
        int y = sHeight - (height / 2);
        setLocation(x, y);
        pack();
        setLayout(new FlowLayout());
        initField();
        // Normal arguments
        setTitle("-Traveling Salesman Input-");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(width, height);
        setVisible(true);
    }

    private void initField() {
        add(commandsReceivedArea);
        JTextField field = new JTextField(50);
        add(field);
        JTextArea commandArea = new JTextArea(1, commonColumns);
        add(commandArea);
        commandArea.setText(lastCommandInput);
        commandArea.setEditable(false);
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
        sendStatusMessage(new String[] {text});
    }

    static void sendStatusMessage(String[] text){
        lastMessages.clear();
        Collections.addAll(lastMessages, text);
        statusArea.setText(buildStatusString());
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
