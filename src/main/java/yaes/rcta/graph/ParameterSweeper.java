package yaes.rcta.graph;

import yaes.framework.simulation.parametersweep.ParameterSweep;
import yaes.framework.simulation.parametersweep.ScenarioDistinguisher;
import yaes.rcta.constRCTA;
import yaes.rcta.constRCTA.RobotMicroConfictStrategy;

public class ParameterSweeper implements constRCTA {
    public static ParameterSweep getStochasticCivilianTypes() {
        ParameterSweep sweepDiscrete = new ParameterSweep("CivilianPolicies");
        ScenarioDistinguisher sd = null;
        // Civilian: meta-strategy MS1 - Respectful
        sd = new ScenarioDistinguisher("Respectful");
        sd.setDistinguisher(CivilianMicroConfictStrategy.STOCHASTIC);
        sd.setDistinguisher(CIVILIAN_MICRO_CONFLICT_STRATEGY_PARAMETER, 0.0);
        sweepDiscrete.addDistinguisher(sd);
        // Civilian: meta-strategy MS2 - Tight after
        /*
         * sd = new ScenarioDistinguisher("Tight-after");
         * sd.setDistinguisher(CivilianMicroConfictStrategy.STOCHASTIC);
         * sd.setDistinguisher(CIVILIAN_MICRO_CONFLICT_STRATEGY_PARAMETER,
         * 0.25); sweepDiscrete.addDistinguisher(sd); // Civilian: meta-strategy
         * MS3 - Tight-front sd = new ScenarioDistinguisher("Tight-front");
         * sd.setDistinguisher(CivilianMicroConfictStrategy.STOCHASTIC);
         * sd.setDistinguisher(CIVILIAN_MICRO_CONFLICT_STRATEGY_PARAMETER,
         * 0.75); sweepDiscrete.addDistinguisher(sd); // Civilian: meta-strategy
         * MS4 - Bully sd = new ScenarioDistinguisher("Bully");
         * sd.setDistinguisher(CivilianMicroConfictStrategy.STOCHASTIC);
         * sd.setDistinguisher(CIVILIAN_MICRO_CONFLICT_STRATEGY_PARAMETER, 1.0);
         * sweepDiscrete.addDistinguisher(sd); // Robot: meta-strategy Adaptive
         * //sd = new ScenarioDistinguisher("Adaptive");
         * //sd.setDistinguisher(RobotMicroConfictStrategy.ADAPTIVE_STOCHASTIC);
         * //sweepDiscrete.addDistinguisher(sd);
         */
        return sweepDiscrete;

    }

    /**
     * Creates distinct sweep parameters for different decision policies that
     * the robot can adopt
     * 
     * @return
     */
    public static ParameterSweep getRobotPolicyTypes() {
        ParameterSweep sweepDiscrete = new ParameterSweep("RobotPolicies");
        ScenarioDistinguisher sd = null;
        // Robot: meta-strategy MS1 - Respectful
        sd = new ScenarioDistinguisher("Respectful");
        sd.setDistinguisher(RobotMicroConfictStrategy.STOCHASTIC);
        sd.setDistinguisher(ROBOT_MICRO_CONFLICT_STRATEGY_PARAMETER, 0.0);
        sweepDiscrete.addDistinguisher(sd);
        // Robot: meta-strategy MS2 - Tight after
        sd = new ScenarioDistinguisher("Tight-after");
        sd.setDistinguisher(RobotMicroConfictStrategy.STOCHASTIC);
        sd.setDistinguisher(ROBOT_MICRO_CONFLICT_STRATEGY_PARAMETER, 0.25);
        sweepDiscrete.addDistinguisher(sd);
        // Robot: meta-strategy MS3 - Tight-front
        sd = new ScenarioDistinguisher("Tight-front");
        sd.setDistinguisher(RobotMicroConfictStrategy.STOCHASTIC);
        sd.setDistinguisher(ROBOT_MICRO_CONFLICT_STRATEGY_PARAMETER, 0.75);
        sweepDiscrete.addDistinguisher(sd);
        // Robot: meta-strategy MS4 - Bully
        sd = new ScenarioDistinguisher("Bully");
        sd.setDistinguisher(RobotMicroConfictStrategy.STOCHASTIC);
        sd.setDistinguisher(ROBOT_MICRO_CONFLICT_STRATEGY_PARAMETER, 1.0);
        sweepDiscrete.addDistinguisher(sd);
        
        // Robot: meta-strategy Classifier
        sd = new ScenarioDistinguisher("Classifier");
        sd.setDistinguisher(RobotMicroConfictStrategy.CLASSIFIER);
        sweepDiscrete.addDistinguisher(sd);

        // Robot: meta-strategy Adaptive
        // sd = new ScenarioDistinguisher("Adaptive");
        // sd.setDistinguisher(RobotMicroConfictStrategy.ADAPTIVE_STOCHASTIC);
        // sweepDiscrete.addDistinguisher(sd);

        return sweepDiscrete;
    }

    public static ParameterSweep getRobotClassifierTypes() {
        ParameterSweep sweepDiscrete = new ParameterSweep("RobotPolicies");
        ScenarioDistinguisher sd = null;
        // Robot: meta-strategy MS1 - Respectful
        sd = new ScenarioDistinguisher("Respectful");
        sd.setDistinguisher(RobotMicroConfictStrategy.STOCHASTIC);
        sd.setDistinguisher(ROBOT_MICRO_CONFLICT_STRATEGY_PARAMETER, 0.0);
        sweepDiscrete.addDistinguisher(sd);

        // Robot: meta-strategy MS4 - Bully
        sd = new ScenarioDistinguisher("Bully");
        sd.setDistinguisher(RobotMicroConfictStrategy.STOCHASTIC);
        sd.setDistinguisher(ROBOT_MICRO_CONFLICT_STRATEGY_PARAMETER, 1.0);
        sweepDiscrete.addDistinguisher(sd);

        // Robot: meta-strategy Classifier
        sd = new ScenarioDistinguisher("Classifier");
        sd.setDistinguisher(RobotMicroConfictStrategy.CLASSIFIER);
        sweepDiscrete.addDistinguisher(sd);
        return sweepDiscrete;
    }

    public static ParameterSweep getVariableDayDivision() {
        ParameterSweep sweepDiscrete = new ParameterSweep("TimesOfTheDay");
        ScenarioDistinguisher sd = null;
        // Day-time
        sd = new ScenarioDistinguisher("Morning");
        sd.setDistinguisher(CHILDREN_DISTRIBUTION, 0.1);
        sd.setDistinguisher(YOUNGSTERS_DISTRIBUTION, 0.1);
        sd.setDistinguisher(SENIORS_DISTRIBUTION, 0.8);
        sweepDiscrete.addDistinguisher(sd);

        sd = new ScenarioDistinguisher("Evening");
        sd.setDistinguisher(CHILDREN_DISTRIBUTION, 0.5);
        sd.setDistinguisher(YOUNGSTERS_DISTRIBUTION, 0.2);
        sd.setDistinguisher(SENIORS_DISTRIBUTION, 0.3);
        sweepDiscrete.addDistinguisher(sd);

        sd = new ScenarioDistinguisher("Night");
        sd.setDistinguisher(CHILDREN_DISTRIBUTION, 0.2);
        sd.setDistinguisher(YOUNGSTERS_DISTRIBUTION, 0.7);
        sd.setDistinguisher(SENIORS_DISTRIBUTION, 0.1);
        sweepDiscrete.addDistinguisher(sd);

        return sweepDiscrete;

    }

}
