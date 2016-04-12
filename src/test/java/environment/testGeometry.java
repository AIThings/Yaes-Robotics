package environment;

import java.awt.geom.Line2D;

import org.junit.Test;

import yaes.rcta.environment.HeatmapHelper;

public class testGeometry {

    @Test
    public void test() {
        Line2D referenceLine = new Line2D.Double(0.0, 0.0, 5.0, 0.0);
        Line2D pointLine = new Line2D.Double(0.0, 0.0, 10.0, 0.0);
        System.out.println(Math
                .abs(HeatmapHelper.angleBetween2Lines(referenceLine, pointLine))
                + "   " + (0 * Math.PI / 180));
        pointLine = new Line2D.Double(0.0, 0.0, 0.0, 10.0);
        System.out.println(Math
                .abs(HeatmapHelper.angleBetween2Lines(referenceLine, pointLine))
                + "   " + (90 * Math.PI / 180));
        pointLine = new Line2D.Double(0.0, 0.0, -10.0, 0.0);
        System.out.println(Math
                .abs(HeatmapHelper.angleBetween2Lines(referenceLine, pointLine))
                + "   " + (180 * Math.PI / 180));
        pointLine = new Line2D.Double(0.0, 0.0, 0.0, -10.0);
        System.out.println(Math
                .abs(HeatmapHelper.angleBetween2Lines(referenceLine, pointLine))
                + "   " + (270 * Math.PI / 180));

        if ((HeatmapHelper.angleBetween2Lines(referenceLine, pointLine) < Math
                .atan(30))) {

        }

    }

}
