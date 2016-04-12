package yaes.rcta.graph;

import java.io.Serializable;

import yaes.framework.simulation.parametersweep.ExperimentPackage;
import yaes.rcta.constRCTA;

public class GraphUtil implements constRCTA, Serializable{
	private static final long serialVersionUID = -7602375598143598271L;

	public static void generateGraphs(ExperimentPackage pack) {
        pack.setVariableDescription(METRICS_SOCIAL_COST, "Social cost");
        pack.setVariableDescription(METRICS_MAX_SOCIAL_COST, "Maximal social cost");
        pack.setVariableDescription(METRICS_MISSION_COST, "Mission cost");
 //       pack.setVariableDescription(CIVILIANS_COUNT, "Crowd size");
        pack.initialize();
        pack.run();
        pack.generateGraph(METRICS_SOCIAL_COST, null, "market_social_cost");
        pack.generateGraph(METRICS_MAX_SOCIAL_COST, null, "market_max_social_cost");
        pack.generateGraph(METRICS_MISSION_COST, null, "market_mission_cost");
    }
}
