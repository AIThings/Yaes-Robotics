package environment;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import yaes.rcta.RctaContext;
import yaes.rcta.agents.civilian.Civilian;
import yaes.ui.text.TextUi;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.environment.LinearColorToValue;
import yaes.world.physical.location.Location;

/**
 * This is junit test for heatmaps
 * 
 * @author Saad Khan
 *
 */
public class testHeatMap {

    @Test
    public void testHeapMaps() {

        RctaContext context = new RctaContext();
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

        // LinearColorToValue lctv = new LinearColorToValue(0, 255);

        Civilian George =
                new Civilian(context, "George", new Location(10, 10), 10.0);
        Civilian Naveed =
                new Civilian(context, "George", new Location(20, 20), 10.0);

        List<Civilian> civilians = new ArrayList<>();
        civilians.add(George);
        civilians.add(Naveed);

        // HeatMap CivilianHeatMap = new HeatMap("CivilianHeatMap",
        // "SingleHueProgression", civilians, em);
        TextUi.print("Properties after adding civilians");
        System.out.println(em.getProperties());
        // System.out.println(em.getPropertyAt(CivilianHeatMap.getHeatMapName(),
        // 10, 10));

        /*
         * for(double x = 0; x < 1000; x += 10) { double val =
         * (double)em.getPropertyAt("CivilianHeatMap", x, 10);
         * TextUi.println(CivilianHeatMap.getHeatMapName() + x + ", 10) = " +
         * val); }
         */
        // CivilianHeatMap.update(10.0, true);

        // System.out.println(em.getPropertyAt(CivilianHeatMap.getHeatMapName(),
        // 20, 20));
    }

}
