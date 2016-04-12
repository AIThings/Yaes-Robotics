package yaes.rcta.agents.gametheory.naiveBayesClassifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import yaes.rcta.agents.civilian.HumanCharacteristics.ChinType;
import yaes.rcta.agents.civilian.HumanCharacteristics.CivilianClassType;
import yaes.rcta.agents.civilian.HumanCharacteristics.HairType;

/**
 * This is the classifier builder helper class and helps in building up the
 * training data set for the Classifier
 * 
 * @author SaadKhan
 *
 */
public class ClassifierBuilder {

    public static Instances getTrainingData(int instancesPerClass,
        Random random) {

        ArrayList<Attribute> fvWekaAttributes = getAttributeVector();
        Instances trainingSet =
                new Instances("TrainingSet", fvWekaAttributes, 600);

        trainingSet.setClassIndex(trainingSet.numAttributes() - 1);

        createInstancesMaleChild(trainingSet, fvWekaAttributes,
                instancesPerClass, random);
        createInstancesFemaleChild(trainingSet, fvWekaAttributes,
                instancesPerClass, random);
        createInstancesMaleYoungster(trainingSet, fvWekaAttributes,
                instancesPerClass, random);
        createInstancesFemaleYoungster(trainingSet, fvWekaAttributes,
                instancesPerClass, random);
        createInstancesMaleSenior(trainingSet, fvWekaAttributes,
                instancesPerClass, random);
        createInstancesFemaleSenior(trainingSet, fvWekaAttributes,
                instancesPerClass, random);
        return trainingSet;
    }

    /**
     * This method initializes the featureVector
     * 
     * @param trainingSet
     * @param random
     * @return
     */
    public static ArrayList<Attribute> getAttributeVector() {
        Attribute height = new Attribute("Height");

        List<String> fvChinType = new ArrayList<String>();
        fvChinType.add(ChinType.SQUARE.toString());
        fvChinType.add(ChinType.ROUND.toString());
        fvChinType.add(ChinType.HEART.toString());
        Attribute fvChinAttr = new Attribute("fvChinAttr", fvChinType);

        List<String> fvHairType = new ArrayList<String>();
        fvHairType.add(HairType.BALD.toString());
        fvHairType.add(HairType.SHORT.toString());
        fvHairType.add(HairType.LONG.toString());
        Attribute fvHairAttr = new Attribute("fvHairAttr", fvHairType);

        // Declare the class attribute along with its values
        // Class1 = MaleChild -- Class1 = FemaleChild
        // Class2 = MaleYoung -- Class3 = FemaleYoung
        // Class4 = MaleOld -- Class5 = FemaleOld
        List<String> fvClassVal = new ArrayList<>();
        fvClassVal.add(CivilianClassType.MALE_CHILD.toString());
        fvClassVal.add(CivilianClassType.FEMALE_CHILD.toString());
        fvClassVal.add(CivilianClassType.MALE_YOUNGSTER.toString());
        fvClassVal.add(CivilianClassType.FEMALE_YOUNGSTER.toString());
        fvClassVal.add(CivilianClassType.MALE_SENIOR.toString());
        fvClassVal.add(CivilianClassType.FEMALE_SENIOR.toString());
        Attribute fvClassAttr = new Attribute("fvClassAttr", fvClassVal);

        ArrayList<Attribute> fvWekaAttributes = new ArrayList<Attribute>();
        fvWekaAttributes.add(height);
        fvWekaAttributes.add(fvChinAttr);
        fvWekaAttributes.add(fvHairAttr);
        fvWekaAttributes.add(fvClassAttr);

        return fvWekaAttributes;
    }

    private static void createInstancesMaleChild(Instances trainingSet,
        List<Attribute> fvWekaAttributes, int instanceCount, Random random) {
        int count = 0;
        do {
            Instance trainingInstance = new DenseInstance(4); // Create the
                                                              // instance
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(0),
                    random.nextInt(4) + 1);
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(1),
                    ChinType.ROUND.toString());
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(2),
                    HairType.SHORT.toString());
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(3),
                    CivilianClassType.MALE_CHILD.toString());
            trainingSet.add(trainingInstance);
            count++;
        } while (count != instanceCount);
    }

    private static void createInstancesFemaleChild(Instances trainingSet,
        List<Attribute> fvWekaAttributes, int instanceCount, Random random) {
        int count = 0;
        do {
            Instance trainingInstance = new DenseInstance(4); // Create the
                                                              // instance
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(0),
                    random.nextInt(3) + 1);
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(1),
                    ChinType.ROUND.toString());
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(2),
                    HairType.SHORT.toString());
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(3),
                    CivilianClassType.FEMALE_CHILD.toString());
            trainingSet.add(trainingInstance);

            count++;
        } while (count != instanceCount);
    }

    private static void createInstancesMaleYoungster(Instances trainingSet,
        List<Attribute> fvWekaAttributes, int instanceCount, Random random) {
        int count = 0;
        do {
            Instance trainingInstance = new DenseInstance(4); // Create the
                                                              // instance
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(0),
                    random.nextInt(2) + 5);
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(1),
                    ChinType.SQUARE.toString());
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(2),
                    HairType.SHORT.toString());
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(3),
                    CivilianClassType.MALE_YOUNGSTER.toString());
            trainingSet.add(trainingInstance);

            count++;
        } while (count != instanceCount);
    }

    private static void createInstancesFemaleYoungster(Instances trainingSet,
        List<Attribute> fvWekaAttributes, int instanceCount, Random random) {
        int count = 0;
        do {
            Instance trainingInstance = new DenseInstance(4); // Create the
                                                              // instance
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(0),
                    random.nextInt(1) + 5);
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(1),
                    ChinType.HEART.toString());
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(2),
                    HairType.LONG.toString());
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(3),
                    CivilianClassType.FEMALE_YOUNGSTER.toString());
            trainingSet.add(trainingInstance);

            count++;
        } while (count != instanceCount);
    }

    private static void createInstancesMaleSenior(Instances trainingSet,
        List<Attribute> fvWekaAttributes, int instanceCount, Random random) {
        int count = 0;
        do {
            Instance trainingInstance = new DenseInstance(4); // Create the
                                                              // instance
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(0),
                    random.nextInt(3) + 4);
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(1),
                    ChinType.SQUARE.toString());
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(2),
                    HairType.BALD.toString());
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(3),
                    CivilianClassType.MALE_SENIOR.toString());
            trainingSet.add(trainingInstance);

            count++;
        } while (count != instanceCount);
    }

    private static void createInstancesFemaleSenior(Instances trainingSet,
        List<Attribute> fvWekaAttributes, int instanceCount, Random random) {
        int count = 0;
        do {
            Instance trainingInstance = new DenseInstance(4); // Create the
                                                              // instance
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(0),
                    random.nextInt(3) + 3);
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(1),
                    ChinType.HEART.toString());
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(2),
                    HairType.LONG.toString());
            trainingInstance.setValue((Attribute) fvWekaAttributes.get(3),
                    CivilianClassType.FEMALE_SENIOR.toString());
            trainingSet.add(trainingInstance);

            count++;
        } while (count != instanceCount);
    }

}
