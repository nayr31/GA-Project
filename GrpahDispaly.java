import javax.swing.*;
import java.awt.*;
import java.io.File;

// This class was supposed to display the graph, but I gave up
// Leaving it here if I ever want to do anythin about it

public class GrpahDispaly extends JFrame {
    static File inputFile = null;
    static int width = 100;
    static int height = 100;

    GrpahDispaly(){
        setup();
    }

    private void setup(){
        FrameSetup.setup(this,"-Traveling Salesman problem-", width, height, false, HIDE_ON_CLOSE);
    }

    private static class GraphReader {

    }
}
