package edu.cmu.side.plugin;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.side.model.Recipe;
import edu.cmu.side.model.RecipeManager.Stage;
import edu.cmu.side.recipe.Chef;
import edu.cmu.side.recipe.converters.ConverterControl;

/* Things to test:
 * training models
 * 	with no features
 *  with boolean features
 *  with numeric features
 *  numeric class labels
 *  nominal class labels
 *  
 *  with all validation schemes
 *  	cross validation by annotation
 *  	cross validation by annotation - no annotation present
 *  	cross validation by file - multiple files
 *  	cross validation by file - one file
 *  	cross validation randomly
 *  	supplied test set - matches original data format
 *  	supplied test set - incompatible columns
 *  with each learning plugin? or subclasses of this test case, per plugin?
 *  
 * evaluating models - might be a separate test case, per eval plugin
 *  
 * predicting on unlabeled data from saved model - PredictorTest
 * */

public class LearningPluginTest
{
	
	@BeforeClass
	public static void setUp()
	{
	}

	@Test
	public void testTrainNominal() throws Exception
	{
//		 Recipe wholeRecipe = ConverterControl.readFromXML("lib/testData/test.model.side.xml");
		 Recipe wholeRecipe = ConverterControl.readFromXML("testData/test.model.side.xml");
		 System.err.println("LearningPluginTest.java, testTrainNominal: wholeRecipe: " + wholeRecipe.toString());
//		 Recipe wholeRecipe = ConverterControl.readFromXML("test.model.side.xml");
		 Recipe trained = Chef.followRecipe(wholeRecipe, wholeRecipe.getDocumentList(), Stage.TRAINED_MODEL, wholeRecipe.getFeatureTable().getThreshold());
	     System.err.println("LearningPluginTest.java, testTrainNominal: trained recipe: " + trained.toString());
		 assert (trained.equals(wholeRecipe));
	}
	
	@Test
	public void testTrainNumeric() throws Exception
	{
		 Recipe wholeRecipe = ConverterControl.readFromXML("testData/test.numeric.model.side.xml");
		 System.err.println("LearningPluginTest.java, testTrainNumeric: wholeRecipe: " + wholeRecipe.toString());
		 Recipe trained = Chef.followRecipe(wholeRecipe, wholeRecipe.getDocumentList(), Stage.TRAINED_MODEL, wholeRecipe.getFeatureTable().getThreshold());
		 System.err.println("LearningPluginTest.java, testTrainNumeric: trained recipe: " + trained.toString());
		 assert (trained.equals(wholeRecipe));
	}

}