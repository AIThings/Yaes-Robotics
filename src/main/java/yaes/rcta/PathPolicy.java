package yaes.rcta;

import java.io.Serializable;
import java.util.List;

import yaes.framework.algorithm.search.IHeuristic;
import yaes.rcta.agents.AbstractPhysicalAgent;
import yaes.rcta.agents.Human;
import yaes.rcta.agents.civilian.Civilian;
import yaes.rcta.movement.DStarLitePP;
import yaes.rcta.movement.MapLocationAccessibility;
import yaes.ui.text.TextUi;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.path.AbstractPathCost;
import yaes.world.physical.path.PathLength;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.pathplanning.AStarPP;
import yaes.world.physical.pathplanning.DistanceHeuristic;

public class PathPolicy implements Serializable, constRCTA {

	private static final long serialVersionUID = -7660649559360050830L;
	private EnvironmentModel em;
	private RctaContext context;
	private static List<Civilian> civilians;
	private static List<Human> civ;
	private static DStarLitePP dStar;

	public PathPolicy(EnvironmentModel em, RctaContext context) {
		this.context = context;
	}

	public void generateAStarPaths() {
		civilians = context.getCivilians();
		for (Civilian c : civilians) {
			if (c.getLocation().equals(c.getLocalDestination())) {
				c.setLocalDestinationAsRandomPoI();
			}
			setAgentAstarPath(c, context.getEnvironmentModel());
		}
	}
	
	public void generateAStarPathsSoldiers() {
		civ = context.getSoldiers();
		for (Human c : civ) {
			if (c.getName().contains("CIV")) {
				if (c.getLocation().equals(c.getGlobalDestination())) {
					c.setGlobalDestinationAsRandomPoI();
				}
				setAgentsAstarPath(c, context.getEnvironmentModel());	
			}
			
		}
	}

	

	public void generateDStarLitePaths() {
		civilians = context.getCivilians();
		for(Civilian c:civilians) {
			dStar = new DStarLitePP(em, c.getLocation(), c.getLocalDestination());
			if(c.getLocation().equals(c.getLocalDestination())) {
				c.setLocalDestinationAsRandomPoI();
			}
			PlannedPath path = dStar.searchPath();
			c.setPath(path);
		}		
	}
	
	private static void setAgentsAstarPath(Human c, EnvironmentModel em) {
		AbstractPathCost pathCost = new PathLength();
		IHeuristic heuristic = new DistanceHeuristic(c.getGlobalDestination());
		PlannedPath path = c.getPath();
		AStarPP aStar = new AStarPP(path, em, pathCost, heuristic,
				new MapLocationAccessibility());
		aStar.setReturnFirst(true);
		aStar.planPath(path, em);
		TextUi.println(c.getPath());
		c.setPath(path);
	}
	
	public static void setAgentAstarPath(AbstractPhysicalAgent agent, EnvironmentModel em) {
		PlannedPath path = agent.getPath();
		AbstractPathCost pathCost = new PathLength();
		IHeuristic heuristic = new DistanceHeuristic(agent.getLocalDestination());
//		path.setDestination(c.getLocalDestination());
		AStarPP aStar = new AStarPP(path, em, pathCost, heuristic,
				new MapLocationAccessibility());
		aStar.setReturnFirst(true);
		aStar.planPath(path, em);
		TextUi.println(agent.getPath());
		agent.setPath(path);
	}


	public static List<Civilian> getCivilians() {
		return civilians;
	}

	public static void setCivilians(List<Civilian> civilians) {
		PathPolicy.civilians = civilians;
	}

	
}
