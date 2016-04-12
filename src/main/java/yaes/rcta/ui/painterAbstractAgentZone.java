package yaes.rcta.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

import yaes.rcta.agents.zones.AbstractAgentZone;
import yaes.ui.visualization.ILayers;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.VisualizationProperties;
import yaes.ui.visualization.painters.IPainter;

/**
 * A generic painter function for all the abstract zones. It paints with a
 * specific color the complete shape and or the outline. It does not currently
 * adapt the painting to the value.
 * 
 * FIXME: adapt the painting to the value - maxbe by transparency and dithering?
 * 
 * yaes.ui.visualization.painters.paintEnvironment
 * 
 * @author Lotzi Boloni
 *
 */
public class painterAbstractAgentZone implements IPainter {

	/**
	 * The color of the outline: if null, no outline is painted
	 */
	private Color colorOutline = null;
	/**
	 * The color of the fill: if null, nothing is filled
	 */
	private Color colorFill = null;
	/**
	 * The transparency applied to the filling
	 */
	private float transparencyFill = 1.0f;
	/**
	 * The stroke of the line: if null, nothing is applied
	 */
	private Stroke lineStroke = null;

	/**
	 * Constructor: allows the specification of the outline, fill and
	 * transparency
	 * 
	 * @param colorOutline
	 * @param colorFill
	 * @param transparenceyFill
	 */
	public painterAbstractAgentZone(Color colorOutline, Color colorFill,
			float transparenceyFill) {
		super();
		this.colorOutline = colorOutline;
		this.colorFill = colorFill;
		this.transparencyFill = transparenceyFill;
		this.lineStroke = null;
	}

	public painterAbstractAgentZone(Color colorOutline, Color colorFill,
			float transparenceyFill, Stroke lineStroke) {
		super();
		this.colorOutline = colorOutline;
		this.colorFill = colorFill;
		this.transparencyFill = transparenceyFill;
		this.lineStroke = lineStroke;
	}

	@Override
	public int getLayer() {
		return ILayers.BACKGROUND_EVENT_LAYER;
	}

	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		AbstractAgentZone aaz = (AbstractAgentZone) o;
		Shape s = aaz.getShape();
		s = panel.getTheTransform().createTransformedShape(s);
		if (s == null) {
			return;
		}
		// draw the inside
		if (colorFill != null) {
			final Composite save = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					transparencyFill));
			g.setColor(colorFill);
			g.fill(s);
			g.setComposite(save);
		}

		// draw the outline
		if (colorOutline != null) {
			g.setColor(colorOutline);
			g.draw(s);

		}
		
		// draw the stroke
		if(lineStroke != null){
			g.setStroke(lineStroke);
			g.draw(s);
		}

	}

	@Override
	public void registerParameters(
			VisualizationProperties visualizationProperties) {
		// TODO Auto-generated method stub

	}

}
