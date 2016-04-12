package yaes.rcta.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.painters.paintPath;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.Path;

public class paintAgentPath extends paintPath {

	public paintAgentPath(Color color, Stroke stroke) {
		super(color, stroke);
	}

	public paintAgentPath(Color color) {
		super(color, LINE_THIN);
	}

	public paintAgentPath() {
		super(Color.black, LINE_THIN);
	}

	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		final Path path = (Path) o;
		// TextUi.println("Painting path " + o);
		Location lastLocation = null;

		g.setStroke(getStroke());
		g.setColor(getColor());
		final GeneralPath gp = new GeneralPath();
		synchronized (path) {
			for (final Location element : path.getLocationList()) {
				if (lastLocation != null) {
					Point2D.Double point = new Point2D.Double(
							(float) element.getX(), (float) element.getY());
					panel.getTheTransform().transform(point, point);
					gp.lineTo(point.x, point.y);
				} else {
					lastLocation = element;
					Point2D.Double point = new Point2D.Double(
							(float) lastLocation.getX(),
							(float) lastLocation.getY());
					panel.getTheTransform().transform(point, point);
					gp.moveTo(point.x, point.y);
				}
			}
		}

		g.draw(gp);

		
	}

}
