package yaes.rcta.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import yaes.rcta.agents.AbstractPhysicalAgent;
import yaes.ui.visualization.ILayers;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.VisualizationProperties;
import yaes.ui.visualization.painters.IPainter;
import yaes.ui.visualization.painters.paintMobileNode;

/**
 * A generic purpose painter for the physical agent
 * 
 * @author Lotzi Boloni
 * 
 */

public class painterPhysicalAgent implements IPainter {

	painterAbstractAgentZone pntPhysical;
	painterAbstractAgentZone pntPersonalSpace;
	painterAbstractAgentZone pntMovementCone;
	painterAbstractAgentZone pntFocusBox;
	paintMobileNode pntMobileNode;
	paintAgentPath pntPath;
	paintAgentPathTicks pntPathTicks;
	paintSpecialLocations pntGlobalDestination;
	paintSpecialLocations pntLocalDestination;

	private Color colorPhysical = Color.black;
	private Color colorPersonalSpace = Color.blue;
	private float transparencyPersonalSpace = 0.05f;
	private Color colorMovementCone = Color.yellow;
	private float transparencyMovementCone = 0.05f;
	private float[] dashingPattern2 = { 10f, 4f };
	private Stroke stroke = new BasicStroke(4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dashingPattern2,
			0.0f);

	/**
	 * Constructor: initialize the colors
	 * 
	 * @param colorPhysical
	 * @param colorPersonalSpace
	 * @param transparencyPersonalSpace
	 * @param colorMovementCone
	 * @param transparencyMovementCone
	 */
	public painterPhysicalAgent(Color colorPhysical, Color colorPersonalSpace, float transparencyPersonalSpace,
			Color colorMovementCone, float transparencyMovementCone) {
		super();
		this.colorPhysical = colorPhysical;
		this.colorPersonalSpace = colorPersonalSpace;
		this.transparencyPersonalSpace = transparencyPersonalSpace;
		this.colorMovementCone = colorMovementCone;
		this.transparencyMovementCone = transparencyMovementCone;
		// now create the sub painters
		createSubPainters();

	}

	/**
	 * Create the sub-painters
	 */
	private void createSubPainters() {
		pntPhysical = new painterAbstractAgentZone(null, colorPhysical, 1.0f);
		pntPersonalSpace = new painterAbstractAgentZone(colorPersonalSpace, colorPersonalSpace,
				transparencyPersonalSpace);
		pntMovementCone = new painterAbstractAgentZone(Color.black, colorMovementCone, transparencyMovementCone);
		pntFocusBox = new painterAbstractAgentZone(Color.GREEN, null, 1.0f, stroke);
		pntMobileNode = new paintMobileNode(1, Color.black, Color.black);
		pntPath = new paintAgentPath(this.colorPhysical);
		pntPathTicks = new paintAgentPathTicks(this.colorPhysical);
		pntGlobalDestination = new paintSpecialLocations(Color.RED, Color.RED);
		pntLocalDestination = new paintSpecialLocations(Color.ORANGE, Color.ORANGE);

	}

	@Override
	public int getLayer() {
		return ILayers.ALL_LAYERS;
	}

	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		AbstractPhysicalAgent pha = (AbstractPhysicalAgent) o;
		if (pha.getAzMovementCone() != null) {
			pntMovementCone.paint(g, pha.getAzMovementCone(), panel);
		}
		if (pha.getAzPersonalSpace() != null) {
			pntPersonalSpace.paint(g, pha.getAzPersonalSpace(), panel);
		}
		if (pha.getAzPhysical() != null) {
			pntPhysical.paint(g, pha.getAzPhysical(), panel);
		}
		if ((pha.getAzFocusFrame() != null) && (pha.isFocus())) {
			pntFocusBox.paint(g, pha.getAzFocusFrame(), panel);
		}

		if (pha.isDisplayTrace()) {
			pntPath.paint(g, pha.getPath(), panel);

		}
		if (pha.isDisplayTraceTicks()) {
			pntPathTicks.paint(g, pha.getPath(), panel);
		}
		if (pha.isDisplayDestination()) {
			if (pha.getGlobalDestination() != null)
				pntGlobalDestination.paint(g, pha.getGlobalDestination(), panel);
			if (pha.getLocalDestination() != null)
				pntLocalDestination.paint(g, pha.getLocalDestination(), panel);
		}

		// Enable and disable Agent's Label
		pntMobileNode.setPaintLabel(false);
		// Agent Name Label Size Set Here
		pntMobileNode.setPaintLabelSize(6);
		pntMobileNode.paint(g, pha, panel);

	}

	@Override
	public void registerParameters(VisualizationProperties visualizationProperties) {

	}

}
