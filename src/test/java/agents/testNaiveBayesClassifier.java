package agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.Test;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import yaes.rcta.agents.civilian.HumanCharacteristics.ChinType;
import yaes.rcta.agents.civilian.HumanCharacteristics.CivilianClassType;
import yaes.rcta.agents.civilian.HumanCharacteristics.HairType;
import yaes.rcta.agents.gametheory.naiveBayesClassifier.ClassifierBuilder;
import yaes.ui.text.TextUi;

public class testNaiveBayesClassifier {
	Random random = new Random();

	// @Test
	public static void main(String[] args) throws Exception {

		Instances isTrainingSet = ClassifierBuilder.getTrainingData(100, new Random());

		// Create a naïve bayes classifier
		Classifier cModel = (Classifier) new NaiveBayes();
		cModel.buildClassifier(isTrainingSet);

		// Test the model
		Evaluation eTest = new Evaluation(isTrainingSet);
		eTest.evaluateModel(cModel, isTrainingSet);

		String strSummary = eTest.toSummaryString();
		System.out.println(strSummary);

		ArrayList<Attribute> fvWekaAttributes = ClassifierBuilder.getAttributeVector();

		Instance testingInstance = new DenseInstance(isTrainingSet.numAttributes());
		testingInstance.setDataset(isTrainingSet);
		testingInstance.setValue(0, 2.0);
		testingInstance.setValue(1, ChinType.ROUND.toString());
		testingInstance.setValue(2, HairType.SHORT.toString());
		//testingInstance.setValue(3, CivilianClassType.MALE_CHILD.toString());
		
		 double[] fDistribution =
		 cModel.distributionForInstance(testingInstance);
		 TextUi.println(Arrays.toString(fDistribution));
		
		 OptionalDouble maximum = Arrays.stream(fDistribution).max();
		 List distributionList =  Arrays.stream(fDistribution).boxed().collect(Collectors.toList());
		 int index = distributionList.indexOf(maximum.getAsDouble());
		 TextUi.println("The index of the maximum element:" +	index	 );

	}

	@Test
	public static void WekaTest() throws Exception {
		// Declare two numeric attributes
		Attribute Attribute1 = new Attribute("firstNumeric");
		Attribute Attribute2 = new Attribute("secondNumeric");

		// Declare a nominal attribute along with its values
		List<String> fvNominalVal = new ArrayList<String>();
		fvNominalVal.add("blue");
		fvNominalVal.add("gray");
		fvNominalVal.add("black");
		Attribute Attribute3 = new Attribute("aNominal", fvNominalVal);

		// Declare the class attribute along with its values
		List<String> fvClassVal = new ArrayList<String>();
		fvClassVal.add("positive");
		fvClassVal.add("negative");
		Attribute ClassAttribute = new Attribute("theClass", fvClassVal);

		// Declare the feature vector
		ArrayList<Attribute> fvWekaAttributes = new ArrayList<Attribute>();
		fvWekaAttributes.add(Attribute1);
		fvWekaAttributes.add(Attribute2);
		fvWekaAttributes.add(Attribute3);
		fvWekaAttributes.add(ClassAttribute);

		// Create an empty training set
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, 10);
		// Set class index
		isTrainingSet.setClassIndex(3);

		// Create the instance
		Instance iExample = new DenseInstance(4);
		iExample.setValue((Attribute) fvWekaAttributes.get(0), 1.0);
		iExample.setValue((Attribute) fvWekaAttributes.get(1), 0.5);
		iExample.setValue((Attribute) fvWekaAttributes.get(2), "gray");
		iExample.setValue((Attribute) fvWekaAttributes.get(3), "positive");

		// add the instance
		isTrainingSet.add(iExample);

		// Create a naïve bayes classifier
		Classifier cModel = (Classifier) new NaiveBayes();
		cModel.buildClassifier(isTrainingSet);

		// Test the model
		Evaluation eTest = new Evaluation(isTrainingSet);
		// TODO: Change it to the testingSet. For that, generate a new testing
		// set and the replace isTrainingSet to isTestingSet
		eTest.evaluateModel(cModel, isTrainingSet);

		// Print the result à la Weka explorer:
		String strSummary = eTest.toSummaryString();
		System.out.println(strSummary);

		// Get the confusion matrix
		double[][] cmMatrix = eTest.confusionMatrix();

		// // Specify that the instance belong to the training set
		// // in order to inherit from the set description
		// iUse.setDataset(isTrainingSet);
		//
		// // Get the likelihood of each classes
		// // fDistribution[0] is the probability of being “positive”
		// // fDistribution[1] is the probability of being “negative”
		// double[] fDistribution = cModel.distributionForInstance(iUse);

	}
}
