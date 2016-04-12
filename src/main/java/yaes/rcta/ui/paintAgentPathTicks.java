package yaes.rcta.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.painters.paintPath;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.Path;

public class paintAgentPathTicks extends paintPath {
	
	public paintAgentPathTicks() {
		super(Color.BLUE, LINE_THIN);
	}
	
	public paintAgentPathTicks(Color color) {
		super(color, LINE_THIN);
	}

	public paintAgentPathTicks(Color color, Stroke stroke) {
		super(color, stroke);
	}

	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		final Path path = (Path) o;
		g.setStroke(getStroke());
		g.setColor(getColor());
		synchronized (path) {
			for (final Location element : path.getLocationList()) {
				Point2D.Double point = new Point2D.Double(
						(float) element.getX(), (float) element.getY());
				panel.getTheTransform().transform(point, point);
				g.drawString("o", (int) point.x, (int) point.y);
			}

		}
	}

	
	
}
