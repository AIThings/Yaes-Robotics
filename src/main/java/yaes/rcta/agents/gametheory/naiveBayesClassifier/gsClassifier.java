package yaes.rcta.agents.gametheory.naiveBayesClassifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.stream.Collectors;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import yaes.rcta.RctaContext;
import yaes.rcta.agents.civilian.Civilian;
import yaes.rcta.agents.civilian.HumanCharacteristics.ChinType;
import yaes.rcta.agents.civilian.HumanCharacteristics.CivilianClassType;
import yaes.rcta.agents.civilian.HumanCharacteristics.HairType;
import yaes.rcta.agents.gametheory.Game;
import yaes.rcta.agents.gametheory.gsStochastic;
import yaes.ui.text.TextUi;

public class gsClassifier extends gsStochastic {
    private Classifier cModel;
    private RctaContext context;
    private HashMap<String, Civilian> civilianHashMap;
    private Instances trainingSet;

    public gsClassifier(RctaContext context, double assumptionC) {
        super(context.getRandom(), assumptionC);
        this.context = context;
        this.cModel = (Classifier) new NaiveBayes();
        initializeClassifier();
        initializeCivilianHashMap();

        // TODO Auto-generated constructor stub
    }

    @Override
    public String move(String player, Game game) {
        // First find and classify the player
        int myId = -1;
        int opponentId = -1;
        for (int i = 0; i != game.getPlayers().size(); i++) {
            String pl = game.getPlayers().get(i);
            if (pl.equals(player)) {
                myId = i;
            } else {
                opponentId = i;
            }
        }

        String opponentName = game.getPlayers().get(opponentId);
        Civilian civ = this.civilianHashMap.get(opponentName);
        if (civ != null) {
            Instance instance = setInstance(civ);

            // set the assumption based on the classification
            int category = classifyInstance(instance);
            if (category == 0 || category == 1)
                super.setAssumptionC(0.25);
            if (category == 2 || category == 3)
                super.setAssumptionC(1.0);
            if (category == 4 || category == 5)
                super.setAssumptionC(0.0);

        }
        return super.move(player, game);

    }

    private void initializeClassifier() {
        this.trainingSet =
                ClassifierBuilder.getTrainingData(100, new Random());
        try {
            this.cModel.buildClassifier(this.trainingSet);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public int classifyInstance(Instance instance) {
        double[] fDistribution = null;
        try {
            fDistribution = cModel.distributionForInstance(instance);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        OptionalDouble maximum = Arrays.stream(fDistribution).max();
        List distributionList = Arrays.stream(fDistribution).boxed()
                .collect(Collectors.toList());
        return distributionList.indexOf(maximum.getAsDouble());
        // TODO: Set the assumptionC based on the classification

    }

    private Instance setInstance(Civilian civ) {
        Instance testingInstance =
                new DenseInstance(this.trainingSet.numAttributes());
        testingInstance.setDataset(this.trainingSet);
        testingInstance.setValue(0, civ.getHeight());
        testingInstance.setValue(1, civ.getChinType().toString());
        testingInstance.setValue(2, civ.getHairType().toString());
        return testingInstance;
    }

    private void initializeCivilianHashMap() {
        this.civilianHashMap = new HashMap<String, Civilian>();
        for (Civilian civ : this.context.getCivilians())
            this.civilianHashMap.put(civ.getName(), civ);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -1587076979435901658L;

}
