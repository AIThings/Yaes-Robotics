package yaes.rcta.agentBuilder;

import java.io.Serializable;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import yaes.framework.simulation.SimulationInput;
import yaes.rcta.RctaContext;
import yaes.rcta.constRCTA;
import yaes.rcta.agents.Human;
import yaes.rcta.movement.SteeringHelper;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PlannedPath;

public class HumanBuilder implements constRCTA, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6005941788903797591L;

    static Human createHuman(SimulationInput sip, RctaContext context,
        String name, Location start, Location dest, double speed,
        double totalMass, double maxVelocity, EnvironmentModel em) {
        Human soldier = new Human(name, start, 0.0);
        soldier.setPath(new PlannedPath(start, dest));
        soldier.setGlobalDestination(dest);
        soldier.setMaxSpeed(3);
        soldier.setSpeed(speed);
        soldier.setMass(totalMass);

        soldier.setBoid(start);
        // soldier.getBoid().setMass(totalMass);
        // soldier.getBoid().setMaxSpeed((int)maxVelocity);
        if (sip.getParameterInt(SOLDIER_TRACE) == 1) {
            soldier.setDisplayTrace(true);
        }
        if (sip.getParameterInt(SOLDIER_TRACE_TICKS) == 1) {
            soldier.setDisplayTraceTicks(true);
        }
        if (sip.getParameterInt(SOLDIER_FINAL_DESTINATION) == 1) {
            soldier.setDisplayDestination(true);
        }

        soldier.setVelocity(new Vector2D(-1, 0));
        soldier.setSteering(new SteeringHelper(soldier, em));
        soldier.setContext(context);
        soldier.setWidthShoulder(sip.getParameterDouble(HUMAN_WIDTH_SHOULDER));
        soldier.setRadiusPersonalSpace(
                sip.getParameterDouble(HUMAN_RADIUS_PERSONAL_SPACE));
        soldier.setAngleMovementCone(
                sip.getParameterDouble(HUMAN_ANGLE_MOVEMENT_CONE));
        soldier.setDiameterMovementCone(
                sip.getParameterDouble(HUMAN_DIAMETER_MOVEMENT_CONE));
        soldier.createZones();

        return soldier;
    }

    /**
     * Creates a number of humans
     * 
     * @param count
     * @return
     */
    public static Human createHuman(SimulationInput sip, RctaContext context,
        int count) {
        Human civilian = null;
        double speed = (double) context.getRandom().nextInt(3) + 1;
        double mass = (double) context.getRandom().nextInt(10);

        if (context.getSceneType().equals(ScenarioType.CROWD_SIMULATION)) {
            switch (sip.getParameterEnum(CrowdScenarioType.class)) {
            case SHOPPING_MALL:
                CrowdBuilder.createTourShoppingMall(sip, context);
                break;
            case OFFICE_SPACE:
                if (count < 11) {
                    CrowdBuilder.createTourOfficeSpace(sip, context, OfficeDesignation.Perm);
                } else {
                    CrowdBuilder.createTourOfficeSpace(sip, context, OfficeDesignation.Temp);
                }

                break;
            case RED_CARPET:
                CrowdBuilder.createTourRedCarpet(sip, context);
                break;
            default:
                break;
            }

        }
        // TextUi.println(plannedRoute);
        civilian = createHuman(sip, context, "CIV-" + context.createID(),
                context.getPlannedRoute().get(0),
                context.getPlannedRoute().get(0), speed, mass, 3,
                context.getEnvironmentModel());

        for (Location loc : context.getPlannedRoute()) {
            civilian.getPlannedTour().addLocation(loc);
            civilian.setWaitingTime(loc.toString(),
                    context.getWaitingTime().get(loc.toString()));
        }
        context.getPlannedRoute().clear();
        context.getWaitingTime().clear();
        return civilian;

    }


}
