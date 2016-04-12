package environment;

import java.io.File;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;

import yaes.rcta.environment.HeatmapHelper;
import yaes.rcta.environment.V2CHeatmap;
import yaes.rcta.environment.V2CHeatmap.ColorScheme;
import yaes.ui.text.TextUi;
import yaes.ui.visualization.Visualizer;
import yaes.ui.visualization.painters.IValueToColor;
import yaes.ui.visualization.painters.paintEnvironmentModel;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.environment.LinearColorToValue;
import yaes.world.physical.location.Location;

public class testHeatmapHelper {

    /**
     * Test the various functions of the HeatmapHelper by calling them and
     * visualizing them.
     * 
     * This also tests the paintEnvironmentModel's functionality
     * 
     */
    @Test
    public void testHeatmapHelperFunctions() {
        String propDensity = "crowd-density";
        String propColor = "simpleColor";
        
        URL url = this.getClass().getResource("/HEC-Obstacles.jpg");
        File file = new File(url.getFile());

        EnvironmentModel em =
                new EnvironmentModel("TheModel", 0, 0, 942, 720, 1, 1);
        // load something
        em.createProperty("interest");
        LinearColorToValue lctv = new LinearColorToValue(0, 100);
        em.loadDataFromImage("interest", file, lctv);
        // now, create a property for the heatmap
        em.createProperty(propDensity);
        em.createProperty(propColor);
        // HeatmapHelper.addSector(em, propDensity, new Location(50, 50), new
        // Location(100, 100), 30,
        // 100.0, true);
        // HeatmapHelper.addSocialZoneCircle(new Civilian(), em, propDensity,
        // new Location(80, 80), 50, 50.0);
        HeatmapHelper.addCircle(em, propDensity, new Location(70, 70), 30,
                100.0, false);
        HeatmapHelper.addCircle(em, propColor, new Location(100, 120), 30,
                100.0, false);
        Visualizer vis = new Visualizer(800, 800, null, "Run");
        paintEnvironmentModel paintEM = new paintEnvironmentModel();
        // add a specific color scheme for the heatmap
        IValueToColor v2c =
                new V2CHeatmap(ColorScheme.UcfTwoColorProgression, 0, 100);
        paintEM.addV2C(propDensity, v2c);
        // em.setPropertyToValue(propDensity, 50.0);
        // paintEM.addV2C(propColor, v2c);
        vis.addObject(em, paintEM);
        vis.setVisible(true);
        TextUi.confirm("Exit?", true);
    }

    @Ignore
    public void testSectorOverlap() {
        /*
         * String propDensity = "crowd-density"; File file =
         * ClassResourceHelper.getResourceFile( RctaEnvironmentHelper.class,
         * "HEC-Obstacles.jpg"); EnvironmentModel em = new
         * EnvironmentModel("TheModel", 0, 0, 942, 720, 1, 1); // load something
         * em.createProperty("interest"); LinearColorToValue lctv = new
         * LinearColorToValue(0, 100); em.loadDataFromImage("interest", file,
         * lctv); // now, create a property for the heatmap
         * em.createProperty(propDensity); Location predictedTargetCenter = new
         * Location(50, 50); Location predictedDestination = new Location (0,
         * 0); double predictedRadialLength = 32;
         * 
         * Location agentCurrentLocation = new Location (80, 80); Location
         * agentNextLocation = new Location (100, 100); double
         * socialZoneRadialLength = 20;
         * 
         * HeatmapHelper.addSector(em, propDensity, predictedTargetCenter,
         * predictedDestination, predictedRadialLength, 100.0);
         * HeatmapHelper.addSocialZoneCircle(new Civilian(), em,
         * propDensity,agentCurrentLocation, 10, 50.0);
         * 
         * System.out.println(HeatmapHelper.predictedIntrusionInSector(
         * predictedTargetCenter, predictedDestination, predictedRadialLength,
         * agentCurrentLocation, agentNextLocation, socialZoneRadialLength));
         * 
         * Visualizer vis = new Visualizer(800, 800, null, "Run");
         * paintEnvironmentModel paintEM = new paintEnvironmentModel(); // add a
         * specific color scheme for the heatmap IValueToColor v2c = new
         * V2CHeatmap(ColorScheme.UcfTwoColorProgression, 0, 100);
         * paintEM.addV2C(propDensity, v2c); vis.addObject(em, paintEM);
         * vis.setVisible(true); TextUi.confirm("Exit?", true);
         */
    }

}
