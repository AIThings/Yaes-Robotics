package environment;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import yaes.ui.text.TextUi;
import yaes.ui.visualization.Visualizer;
import yaes.ui.visualization.painters.paintEnvironmentModel;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.environment.LinearColorToValue;

public class testEnvironment {
    /**
     * Tests the loading from an image
     */
    @Test
    public void testLoadFromImage() {
        URL url = this.getClass().getResource("/HEC-Obstacles.jpg");
        File file = new File(url.getFile());
        
        EnvironmentModel em =
                new EnvironmentModel("TheModel", 0, 0, 942, 720, 1, 1);
        em.createProperty("obstacle");
        LinearColorToValue lctv = new LinearColorToValue(0, 100);
        em.loadDataFromImage("obstacle", file, lctv);
        for (double x = 0; x < 1000; x += 50) {
            double val = (double) em.getPropertyAt("obstacle", x, 500);
            TextUi.println("interest(" + x + ", 500) = " + val);
        }
    }

    /**
     * Tests the visualization
     */
    @Test
    public void testEMVisualization() {
        URL url = this.getClass().getResource("/HEC-Obstacles.jpg");
        File file = new File(url.getFile());

        EnvironmentModel em =
                new EnvironmentModel("TheModel", 0, 0, 942, 720, 1, 1);
        em.createProperty("interest");
        LinearColorToValue lctv = new LinearColorToValue(0, 100);
        em.loadDataFromImage("interest", file, lctv);
        Visualizer vis = new Visualizer(800, 800, null, "Run");
        vis.addObject(em, new paintEnvironmentModel());
        vis.setVisible(true);
        TextUi.confirm("Exit?", true);
    }
}
