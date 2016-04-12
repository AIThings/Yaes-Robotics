package yaes.rcta.agents.robot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import yaes.framework.agent.ACLMessage;
import yaes.framework.agent.ICommunicatingAgent;
import yaes.framework.agent.IConversation;
import yaes.framework.agent.IMessage;
import yaes.framework.agent.communicationmodel.AbstractCommunicationModel;
import yaes.rcta.RctaContext;
import yaes.rcta.movement.DStarLitePP;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensingHistory;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PlannedPath;

public class RoboScout extends Robot implements ICommunicatingAgent {
	ArrayList<Location> robotScout_locations = new ArrayList<Location>();
	private AbstractCommunicationModel communicationModel = null;
	private final List<IConversation> conversations = new ArrayList<>();
	protected List<IMessage> receivedMessages = new ArrayList<>();
	private final SensorNetworkWorld sensorWorld;
	protected SensorNode node;

	public SensorNetworkWorld getSensorWorld() {
		return sensorWorld;
	}

	ArrayList<Location> tspPath;
	int locationIndex;

	/*
	 * public RoboScout(String name, Location start, double heading) {
	 * super(name, start, heading); }
	 */
	public RoboScout(String name, Location start, double heading, SensorNetworkWorld sensorWorld) {
		super(name, start, heading);
		this.sensorWorld = sensorWorld;
	}

	/**
	 * Creates the robot scout with a local destination
	 * 
	 * @param name
	 * @param currentLocation
	 * @param destination
	 * @param context
	 * @param speed
	 * 
	 */
	public RoboScout(String name, Location start, Location dest, RctaContext context, double speed,
			SensorNetworkWorld sensorWorld) {
		super(name, start, 0.0);
		this.setStartLocation(start);
		this.setStartTime(context.getWorld().getTime());
		this.setLocalDestination(dest);
		super.setPath(new PlannedPath(this.getLocation(), this.getLocalDestination()));
		super.setContext(context);
		this.setSpeed(speed);
		this.tspPath = new ArrayList<Location>();
		this.sensorWorld = sensorWorld;
	}

	@Override
	public void action() {
		/*
		 * Location loc = new Location(0,0); //loc = getIntendedMove();
		 * this.setNextLocation(this.getLocation()); loc =
		 * super.getPath().getNextLocation(this.getLocation(), (int)
		 * super.getSpeed()); if (loc == null) { loc = getLocalDestination(); }
		 * setLocation(loc);
		 */
		final SensingHistory sensingHistory = this.getSensorWorld().getSensingHistory(this.node);
		final List<Perception> perceptions = sensingHistory.extractNewPerceptions();
		for (final Perception p : perceptions) {
			switch (p.getType()) {
			case IntruderPresence:
			case NoPerception:
			case Overhearing:
			case ReceivedMessage:
			case SinkNodePrescene:
			default:
				handleReceivedMessage(p.getMessage());
			}
		}

		setLocation(getIntendedMove());
	}

	protected void handleReceivedMessage(ACLMessage message) {
		if (message.getValue(SensorNetworkMessageConstants.FIELD_CONTENT)
				.equals(SensorNetworkMessageConstants.MESSAGE_DATA)) {
			this.cleanConversations();
		}
	}

	public void transmit(ACLMessage message) {
		getSensorWorld().transmit(this.node, message);
	}

	@Override
	public Location getIntendedMove() {
		if (this.getLocation().equals(this.getLocalDestination())) {
			// If robot reaches a destination, then delete that from the main
			// scout list
			RctaContext.roboSearch_LocList.remove(this.getLocalDestination());

			// Get the next Location for the TSP List
			this.setLocalDestinationTSP();
			// Find the D*-lite path for moving towards next location
			DStarLitePP dStar = new DStarLitePP(super.getContext().getEnvironmentModel(), this.getLocation(),
					this.getLocalDestination());
			super.setPath(dStar.searchPath());
		}
		this.setNextLocation(this.getLocation());
		Location loc = super.getPath().getNextLocation(this.getLocation(), (int) super.getSpeed());
		if (loc != null)
			return loc;
		else
			return super.getLocalDestination();
	}

	// broadcast messages to all agents using communication model
	public void broadcast(IMessage message) {
		final List<ICommunicatingAgent> listOfAgents = super.getContext().getWorld().getDirectory().getAllAgents();
		for (final ICommunicatingAgent agent : listOfAgents) {
			if ((agent != null) && communicationModel.canDeliverMessage(agent)) {
				super.getContext().getWorld().getDirectory().sendMessage(message);
			} else {
				TextUi.println(getName() + ": broadcast(message) --> communication model can not deliver message to: "
						+ agent.getName());
			}
		}
	}

	public void cleanConversations() {
		for (final Iterator<IConversation> it = conversations.iterator(); it.hasNext();) {
			if (it.next().isFinished()) {
				it.remove();
			}
		}
	}

	public ACLMessage getACLMessage() {
		for (IMessage element : receivedMessages) {
			if (element instanceof ACLMessage) {
				receivedMessages.remove(element);
				return (ACLMessage) element;
			}
		}
		return null;
	}

	/**
	 * Returns from the queue an extended message which is part of a
	 * conversation or null if no message arrived.
	 * 
	 * @param id
	 * @return
	 */
	public ACLMessage getACLMessageFromConversation(String id) {
		for (final IMessage element : receivedMessages) {
			if (element instanceof ACLMessage) {
				final ACLMessage msg = (ACLMessage) element;
				if (id.equals(msg.getConversation())) {
					receivedMessages.remove(element);
					return msg;
				}
			}
		}
		return null;
	}

	public void setLocalDestinationTSP() {
		// the locationIndex: starts from zero and counts till last location of
		// TSP
		if (locationIndex < tspPath.size())
			this.setLocalDestination(tspPath.get(locationIndex++));
	}

	public ArrayList<Location> getTspPath() {
		return tspPath;
	}

	public void setTspPath(ArrayList<Location> tspPath) {
		this.tspPath = tspPath;
	}

	public ArrayList<Location> getRobotScout_locations() {
		return robotScout_locations;
	}

	public void setRobotScout_locations(ArrayList<Location> robotScout_locations) {
		this.robotScout_locations = robotScout_locations;
	}

	@Override
	public List<IConversation> getConversations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void receiveMessage(IMessage message) {
		// TODO Auto-generated method stub

	}

	public void setNode(SensorNode node) {
		this.node = node;
	}
}
