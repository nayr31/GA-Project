import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// A frame for displaying the information for the tsp problem
// Currently doesn't display them all correctly, but it does the job
public class CityLooker extends JFrame {

    static final int width = 1000;
    static final int height = 1000;
    static double scaleX = 0, scaleY = 0;
    static final int offset = 40;
    static final int citySize = 6;

    DrawPane panel;
    ArrayList<City> cities;
    Chromosome theBest;

    CityLooker(ArrayList<City> cities){
        this.cities = cities;
        setup();
        panel = setPanel();
        setScale();
    }

    // These scale values take in the maximum city depths (far-ness) and generate a scale based on the width/height
    private void setScale() {
        float maxX = 0;
        float maxY = 0;
        for (City c : cities) {
            if (c.x > maxX)
                maxX = c.x;
            if (c.y > maxY)
                maxY = c.y;
        }
        scaleX = maxX / (width-offset);
        scaleY = maxY / (height-offset);
    }

    // Creates and sets the panel to fit into the frame
    private DrawPane setPanel(){
        DrawPane panel = new DrawPane();
        Container container = getContentPane();
        container.add(panel);
        panel.setPreferredSize(new Dimension(width, height));
        return panel;
    }

    private void setup(){
        FrameSetup.setup(this,"-Traveling Salesman problem-", width, height, false, HIDE_ON_CLOSE);
    }

    public void showWindow(){
        setVisible(true);
    }

    // Call this whenever we need to redraw a new chromosome
    public void draw(Chromosome chromosome){
        theBest = chromosome;
        panel.repaint();
    }

    // This panel is solely used here for drawing stuffs related to our problem
    class DrawPane extends JPanel{

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintSolution((Graphics2D)g);
        }

        private void paintSolution(Graphics2D graphics) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            paintCityNames(graphics);
            if (theBest != null) {
                paintChromosome(graphics);
            }
            paintCities(graphics);
        }

        private void paintChromosome (Graphics2D graphics) {
            graphics.setColor(Color.darkGray);

            // Get the x and y from each city index from the chromosome entry and make a line
            for (int i = 0; i < theBest.data.length-1; i++) {
                City city1 = cities.get(theBest.data[i]);
                City city2 = cities.get(theBest.data[i+1]);
                drawBetweenTwoCities(graphics, city1, city2);
            }

            // The last connecting line
            City city1 = cities.get(theBest.data[0]);
            City city2 = cities.get(theBest.data[theBest.data.length-1]);
            // We need to normalize the locations in line with the scale
            drawBetweenTwoCities(graphics, city1, city2);
        }

        private void drawBetweenTwoCities(Graphics2D graphics, City city1, City city2) {
            // We need to normalize the locations in line with the scale (offset for each size of the window)
            int x1 = (int)(city1.x/scaleX + offset/2);
            int x2 = (int)(city2.x/scaleX + offset/2);
            int y1 = (int)(city1.y/scaleY + offset/2);
            int y2 = (int)(city2.y/scaleY + offset/2);
            graphics.drawLine(x1, y1, x2, y2);
        }

        private void paintCities (Graphics2D graphics) {
            graphics.setColor(Color.darkGray);
            for (City c : cities) {
                int x = (int)(c.x/scaleX - citySize/2 + offset/2);
                int y = (int)(c.y/scaleY - citySize/2 + offset/2);
                graphics.fillOval(x, y, citySize,  citySize);
            }
        }

        private void paintCityNames (Graphics2D graphics) {
            graphics.setColor(new Color(180, 180, 180));
            for (int i=0; i<cities.size(); i++) {
                City c = cities.get(i);
                int x = (int)(c.x/scaleX - citySize/2 + offset/2);
                int y = (int)(c.y/scaleY - citySize/2 + offset/2);
                graphics.fillOval(x, y, citySize, citySize);
                int fontOffset = getFontMetrics(graphics.getFont()).stringWidth(String.valueOf(i))/2-2;
                graphics.drawString(String.valueOf(i), x-fontOffset, y-3);
            }
        }
    }
}
