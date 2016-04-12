package yaes.rcta.simulationHelper;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import yaes.framework.simulation.AbstractContext;
import yaes.rcta.agents.AbstractPhysicalAgent;
import yaes.world.physical.location.Location;

/**
 * To decide which mode of experiment to run;
 * <ol>
 * <li>0: Run normally</li>
 * <li>1: Programmed Mode
 * <ol>
 * <li>Enter button: true: Run preprogrammed simulation visually</li>
 * <li>Enter button: false: Run preprogrammed simulation step by step using
 * [SPACE] button</li>
 * </ol>
 * <li>2: Manual Mode
 * <ol>
 * <li>Enter button true: Robot take action when any agent in the simulation
 * moved manually</li>
 * <li>Enter button false: Robot take action when [SPACE] button pressed</li>
 * </ol>
 * </li>
 * </ol>
 */
public class InteractiveSimulation implements MouseListener, KeyListener, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2484392360732557978L;
	// To decide which mode of experiment to run;
	// 0: Run normally
	// 1: Programmed Mode
	// --> Enter button: true: Run preprogrammed simulation visually
	// --> Enter button: false: Run preprogrammed simulation step by step using
	// [SPACE] button
	// 2: Manual Mode
	// --> Enter button true: Robot take action when any agent in the simulation
	// moved manually
	// --> Enter button false: Robot take action when [SPACE] button pressed
	private int experimentMode = 0;
	private boolean simulationContinue = true;
	private boolean enterButton = true;
	private boolean spaceButton = false;
	private Set<AbstractPhysicalAgent> autonomousAgents;
	private Set<AbstractPhysicalAgent> manualAgents;
	private int keypress = 0;
	private Location locMouse;
	private InputEvent eventType;

	public static enum InputEvent{
		KeyEvent, MouseEvent;
	}
	
	private AbstractContext context;

	// Terminate program on Esc key press
	int terminate = 1;

	/**
	 * To decide which mode of experiment to run;
	 * <ol>
	 * <li>0: Run normally</li>
	 * <li>1: Programmed Mode
	 * <ol>
	 * <li>Enter button: true: Run preprogrammed simulation visually</li>
	 * <li>Enter button: false: Run preprogrammed simulation step by step using
	 * [SPACE] button</li>
	 * </ol>
	 * <li>2: Manual Mode
	 * <ol>
	 * <li>Enter button true: Robot take action when any agent in the simulation
	 * moved manually</li>
	 * <li>Enter button false: Robot take action when [SPACE] button pressed
	 * </li>
	 * </ol>
	 * </li>
	 * </ol>
	 * 
	 * @param context
	 */
	public InteractiveSimulation(AbstractContext context) {
		this.autonomousAgents = new HashSet<AbstractPhysicalAgent>();
		this.manualAgents = new HashSet<AbstractPhysicalAgent>();
		this.context = context;
	}

	public void runAgent() {
		while (!this.simulationContinue) {
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (this.experimentMode == 2) {
			if (this.enterButton) {
				for (AbstractPhysicalAgent agent : autonomousAgents) {
					agent.action();
				}
			} else {
				if (this.spaceButton) {
					for (AbstractPhysicalAgent agent : autonomousAgents) {
						agent.action();
					}
					this.spaceButton = false;
				}
			}
			this.simulationContinue = false;
		} else if (this.experimentMode == 1) {
			if (this.enterButton) {
				this.simulationContinue = true;
				for (AbstractPhysicalAgent agent : autonomousAgents) {
					agent.action();
				}
				for (AbstractPhysicalAgent agent : manualAgents) {
					agent.action();
				}
			} else {

				if (this.spaceButton) {
					for (AbstractPhysicalAgent agent : autonomousAgents) {
						agent.action();
					}
					for (AbstractPhysicalAgent agent : manualAgents) {
						agent.action();
					}
					this.spaceButton = false;
				}
				this.simulationContinue = false;
			}
		}
	}

	public int getKeypress() {
		return keypress;
	}

	public Location getLocMouse() {
		return locMouse;
	}

	public Set<AbstractPhysicalAgent> getManualAgents() {
		return manualAgents;
	}

	public void addManualAgent(AbstractPhysicalAgent manualAgents) {
		this.manualAgents.add(manualAgents);
	}

	public void removeManualAgent(AbstractPhysicalAgent manualAgents) {
		this.manualAgents.remove(manualAgents);
	}

	public Set<AbstractPhysicalAgent> getAutonomousAgents() {
		return autonomousAgents;
	}

	public void addAutonomousAgent(AbstractPhysicalAgent autonomousAgent) {
		this.autonomousAgents.add(autonomousAgent);
	}

	public void removeAutonomousAgent(AbstractPhysicalAgent autonomousAgent) {
		this.autonomousAgents.remove(autonomousAgent);
	}

	public int getExperimentMode() {
		return experimentMode;
	}

	public void setExperimentMode(int experimentMode) {
		this.experimentMode = experimentMode;
	}

	public boolean isSimulationContinue() {
		return simulationContinue;
	}

	public void setSimulationContinue(boolean simulationContinue) {
		this.simulationContinue = simulationContinue;
	}

	public boolean isEnterButton() {
		return enterButton;
	}

	public void setEnterButton(boolean enterButton) {
		this.enterButton = enterButton;
	}

	public boolean isSpaceButton() {
		return spaceButton;
	}

	public void setSpaceButton(boolean spaceButton) {
		this.spaceButton = spaceButton;
	}

	@Override
	public void keyPressed(KeyEvent key) {
		eventType = InputEvent.KeyEvent;
		if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
			terminate = 0;
			this.simulationContinue = true;
		}

		if (key.getKeyCode() == KeyEvent.VK_SPACE) {
			this.simulationContinue = true;
			this.spaceButton = true;

		}
		if (key.getKeyCode() == KeyEvent.VK_ENTER) {
			this.enterButton = !this.enterButton;
			this.simulationContinue = !this.simulationContinue;
		}
		if (this.experimentMode == 2) {
			switch (key.getKeyCode()) {
			case KeyEvent.VK_UP:
				keypress = KeyEvent.VK_UP;
				break;
			case KeyEvent.VK_DOWN:
				keypress = KeyEvent.VK_DOWN;
				break;
			case KeyEvent.VK_LEFT:
				keypress = KeyEvent.VK_LEFT;
				break;
			case KeyEvent.VK_RIGHT:
				keypress = KeyEvent.VK_RIGHT;
				break;
			default:
				break;
			}

			for (AbstractPhysicalAgent agent : manualAgents) {
				if (agent.isFocus()) {
					agent.action();
				}
			}
			this.simulationContinue = true;

		}

		this.context.getVisualizer().getVisualCanvas().update();

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		eventType = InputEvent.MouseEvent;
		// TextUi.println("Focus:" + visualizer.getFrame().getFocusOwner());
		// TextUi.println("Mouse pressed");
		this.context.getVisualizer().getVisualCanvas().getInternalPanel().setFocusable(true);
		this.context.getVisualizer().getVisualCanvas().getInternalPanel().requestFocus();
		if (this.experimentMode == 2) {
			Point position = e.getPoint();
			AffineTransform transform = this.context.getVisualizer().getVisualCanvas().getTheTransform();
			Point2D.Double realpoint = new Point2D.Double();
			try {
				transform.inverseTransform(position, realpoint);
			} catch (NoninvertibleTransformException nive) {

				nive.printStackTrace();
			}

			locMouse = new Location(realpoint.getX(), realpoint.getY());
			for (AbstractPhysicalAgent agent : manualAgents) {
				agent.action();
			}
			this.context.getVisualizer().getVisualCanvas().update();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	public InputEvent getEventType() {
		return eventType;
	}
	
}
