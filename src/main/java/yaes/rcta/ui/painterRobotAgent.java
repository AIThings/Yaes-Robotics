package yaes.rcta.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import yaes.rcta.agents.AbstractRobotAgent;
import yaes.ui.visualization.VisualCanvas;

public class painterRobotAgent extends painterPhysicalAgent {

	paintAgentPath pntDesiredPath;
	paintAgentPathTicks pntDesiredPathTicks;
	paintAgentPath pntActualPath;
	paintAgentPathTicks pntActualPathTicks;
	
	

	public painterRobotAgent(Color colorPhysical, Color colorPersonalSpace,
			float transparencyPersonalSpace, Color colorMovementCone,
			float transparencyMovementCone) {
		super(colorPhysical, colorPersonalSpace, transparencyPersonalSpace,
				colorMovementCone, transparencyMovementCone);
		pntDesiredPath = new paintAgentPath(colorPersonalSpace);
		pntDesiredPathTicks = new paintAgentPathTicks(colorPersonalSpace);
		
		pntActualPath = new paintAgentPath(colorPhysical);
		pntActualPathTicks = new paintAgentPathTicks(colorPhysical);

	}

	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		AbstractRobotAgent pha = (AbstractRobotAgent) o;
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
			pntActualPath.paint(g, pha.getActualPath(), panel);
			pntDesiredPath.paint(g, pha.getDesiredPath(), panel);
		}
		if (pha.isDisplayTraceTicks()) {
			pntActualPathTicks.paint(g, pha.getActualPath(), panel);
			pntDesiredPathTicks.paint(g, pha.getDesiredPath(), panel);
		}

		// Enable and disable Agent's Label
		pntMobileNode.setPaintLabel(false);
		// Agent Name Label Size Set Here
		pntMobileNode.setPaintLabelSize(6);
		pntMobileNode.paint(g, pha, panel);

	}

}
