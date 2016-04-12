package yaes.rcta.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.VisualizationProperties;
import yaes.ui.visualization.painters.IPainter;
import yaes.ui.visualization.painters.PainterHelper;
import yaes.world.physical.location.Location;

/**
 * Method to specify a color to given location
 * 
 * @author Taranjeet Singh Bhatia
 *
 */
public class paintSpecialLocations implements IPainter {

	/**
	 * The color of the outline: if null, no outline is painted
	 */
	private Color borderColor = null;
	/**
	 * The color of the fill: if null, nothing is filled
	 */
	private Color fillColor = null;

	public paintSpecialLocations(Color borderColor, Color fillColor) {
		super();
		this.borderColor = borderColor;
		this.fillColor = fillColor;
	}

	@Override
	public int getLayer() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		Location loc = (Location) o;
		PainterHelper.paintRectangleAtLocation(loc, 1, borderColor, fillColor, g, panel);
	}

	@Override
	public void registerParameters(VisualizationProperties visualizationProperties) {
		// TODO Auto-generated method stub

	}

}
