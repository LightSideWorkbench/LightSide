/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 * LibLINEAR.java
 * Copyright (C) Benedikt Waldvogel
 */
package weka.classifiers.functions;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.WekaException;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;


/**
  <!-- globalinfo-start -->
  * A wrapper class for the liblinear classifier.<br/>
  * Rong-En Fan, Kai-Wei Chang, Cho-Jui Hsieh, Xiang-Rui Wang, Chih-Jen Lin (2008). LIBLINEAR - A Library for Large Linear Classification. URL http://www.csie.ntu.edu.tw/~cjlin/liblinear/.
  * <p/>
  <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;misc{Fan2008,
 *    author = {Rong-En Fan and Kai-Wei Chang and Cho-Jui Hsieh and Xiang-Rui Wang and Chih-Jen Lin},
 *    note = {The Weka classifier works with version 1.33 of LIBLINEAR},
 *    title = {LIBLINEAR - A Library for Large Linear Classification},
 *    year = {2008},
 *    URL = {http://www.csie.ntu.edu.tw/\~cjlin/liblinear/}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -S &lt;int&gt;
 *  Set type of solver (default: 1)
 *    0 = L2-regularized logistic regression
 *    1 = L2-loss support vector machines (dual)
 *    2 = L2-loss support vector machines (primal)
 *    3 = L1-loss support vector machines (dual)
 *    4 = multi-class support vector machines by Crammer and Singer</pre>
 *
 * <pre> -C &lt;double&gt;
 *  Set the cost parameter C
 *   (default: 1)</pre>
 *
 * <pre> -Z
 *  Turn on normalization of input data (default: off)</pre>
 *
 * <pre> -N
 *  Turn on nominal to binary conversion.</pre>
 *
 * <pre> -M
 *  Turn off missing value replacement.
 *  WARNING: use only if your data has no missing values.</pre>
 *
 * <pre> -P
 *  Use probability estimation (default: off)
 * currently for L2-regularized logistic regression only! </pre>
 *
 * <pre> -E &lt;double&gt;
 *  Set tolerance of termination criterion (default: 0.01)</pre>
 *
 * <pre> -W &lt;double&gt;
 *  Set the parameters C of class i to weight[i]*C
 *   (default: 1)</pre>
 *
 * <pre> -B &lt;double&gt;
 *  Add Bias term with the given value if &gt;= 0; if &lt; 0, no bias term added (default: 1)</pre>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 <!-- options-end -->
 *
 * @author  Benedikt Waldvogel (mail at bwaldvogel.de)
 * @version 1.8
 */
public class LibLINEAR extends AbstractClassifier implements TechnicalInformationHandler {

    public static final String  REVISION         = "1.8";

    /** serial UID */
    protected static final long serialVersionUID = 230504711;

    /** LibLINEAR Model */
    protected Model             m_Model;


    public Model getModel() {
        return m_Model;
    }

    /** for normalizing the data */
    protected Filter               m_Filter               = null;

    /** normalize input data */
    protected boolean              m_Normalize            = false;

    /** SVM solver types */
    public static final Tag[]      TAGS_SVMTYPE           = {new Tag(SolverType.L2R_LR.ordinal(), "L2-regularized logistic regression"),
            new Tag(SolverType.L2R_L2LOSS_SVC_DUAL.ordinal(), "L2-loss support vector machines (dual)"),
            new Tag(SolverType.L2R_L2LOSS_SVC.ordinal(), "L2-loss support vector machines (primal)"),
            new Tag(SolverType.L2R_L1LOSS_SVC_DUAL.ordinal(), "L1-loss support vector machines (dual)"),
            new Tag(SolverType.MCSVM_CS.ordinal(), "multi-class support vector machines by Crammer and Singer"),
            new Tag(SolverType.L1R_L2LOSS_SVC.ordinal(), "L1-regularized L2-loss support vector classification"),
            new Tag(SolverType.L1R_LR.ordinal(), "L1-regularized logistic regression"),
            new Tag(SolverType.L2R_LR_DUAL.ordinal(), "L2-regularized logistic regression (dual)")};

    protected final SolverType     DEFAULT_SOLVER         = SolverType.L2R_L2LOSS_SVC_DUAL;

    /** the SVM solver type */
    protected SolverType           m_SolverType           = DEFAULT_SOLVER;

    /** stopping criteria */
    protected double               m_eps                  = 0.01;

    /** cost Parameter C */
    protected double               m_Cost                 = 1;

    /** bias term value */
    protected double               m_Bias                 = 1;

    protected int[]                m_WeightLabel          = new int[0];

    protected double[]             m_Weight               = new double[0];

    /** whether to generate probability estimates instead of +1/-1 in case of
     * classification problems */
    protected boolean              m_ProbabilityEstimates = false;

    /** The filter used to get rid of missing values. */
    protected ReplaceMissingValues m_ReplaceMissingValues;

    /** The filter used to make attributes numeric. */
    protected NominalToBinary      m_NominalToBinary;

    /** If true, the nominal to binary filter is applied */
    private boolean                m_nominalToBinary      = false;

    /** If true, the replace missing values filter is not applied */
    private boolean                m_noReplaceMissingValues;

    /**
     * Returns a string describing classifier
     *
     * @return a description suitable for displaying in the
     *         explorer/experimenter gui
     */
    public String globalInfo() {
        return "A wrapper class for the liblinear classifier.\n" + getTechnicalInformation().toString();
    }

    /**
     * Returns an instance of a TechnicalInformation object, containing
     * detailed information about the technical background of this class,
     * e.g., paper reference or book this class is based on.
     *
     * @return the technical information about this class
     */
    @Override
	public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation result;

        result = new TechnicalInformation(Type.MISC);
        result.setValue(TechnicalInformation.Field.AUTHOR, "Rong-En Fan and Kai-Wei Chang and Cho-Jui Hsieh and Xiang-Rui Wang and Chih-Jen Lin");
        result.setValue(TechnicalInformation.Field.TITLE, "LIBLINEAR - A Library for Large Linear Classification");
        result.setValue(TechnicalInformation.Field.YEAR, "2008");
        result.setValue(TechnicalInformation.Field.URL, "http://www.csie.ntu.edu.tw/~cjlin/liblinear/");
        result.setValue(TechnicalInformation.Field.NOTE, "The Weka classifier works with version 1.33 of LIBLINEAR");

        return result;
    }

    /**
     * Returns an enumeration describing the available options.
     *
     * @return an enumeration of all the available options.
     */
    @Override
	@SuppressWarnings("rawtypes")
    public Enumeration listOptions() {

        Vector<Object> result = new Vector<Object>();

        result.addElement(new Option("\tSet type of solver (default: 1)\n" //
            + "\t\t 0 = L2-regularized logistic regression\n" //
            + "\t\t 1 = L2-loss support vector machines (dual)\n" //
            + "\t\t 2 = L2-loss support vector machines (primal)\n" //
            + "\t\t 3 = L1-loss support vector machines (dual)\n" //
            + "\t\t 4 = multi-class support vector machines by Crammer and Singer\n" //
            + "\t\t 5 = L1-regularized L2-loss support vector classification\n" //
            + "\t\t 6 = L1-regularized logistic regression\n" //
            + "\t\t 7 = L2-regularized logistic regression (dual)", //
            "S", 1, "-S <int>"));

        result.addElement(new Option("\tSet the cost parameter C\n" + "\t (default: 1)", "C", 1, "-C <double>"));

        result.addElement(new Option("\tTurn on normalization of input data (default: off)", "Z", 0, "-Z"));

        result.addElement(new Option("\tTurn on nominal to binary conversion.", "N", 0, "-N"));

        result.addElement(new Option("\tTurn off missing value replacement." + "\n\tWARNING: use only if your data has no missing " + "values.", "M", 0, "-M"));

        result.addElement(new Option("\tUse probability estimation (default: off)\n"
            + "currently for L2-regularized logistic regression, L1-regularized logistic regression or L2-regularized logistic regression (dual)! ", "P", 0,
            "-P"));

        result.addElement(new Option("\tSet tolerance of termination criterion (default: 0.01)", "E", 1, "-E <double>"));

        result.addElement(new Option("\tSet the parameters C of class i to weight[i]*C\n" + "\t (default: 1)", "W", 1, "-W <double>"));

        result.addElement(new Option("\tAdd Bias term with the given value if >= 0; if < 0, no bias term added (default: 1)", "B", 1, "-B <double>"));

        Enumeration en = super.listOptions();
        while (en.hasMoreElements())
            result.addElement(en.nextElement());

        return result.elements();
    }

    /**
     * Sets the classifier options <p/>
     *
     <!-- options-start -->
     * Valid options are: <p/>
     *
     * <pre> -S &lt;int&gt;
     *  Set type of solver (default: 1)
     *    0 = L2-regularized logistic regression
     *    1 = L2-loss support vector machines (dual)
     *    2 = L2-loss support vector machines (primal)
     *    3 = L1-loss support vector machines (dual)
     *    4 = multi-class support vector machines by Crammer and Singer
     *    5 = L1-regularized L2-loss support vector classification
     *    6 = L1-regularized logistic regression
     *    7 = L2-regularized logistic regression (dual)</pre>
     *
     * <pre> -C &lt;double&gt;
     *  Set the cost parameter C
     *   (default: 1)</pre>
     *
     * <pre> -Z
     *  Turn on normalization of input data (default: off)</pre>
     *
     * <pre> -N
     *  Turn on nominal to binary conversion.</pre>
     *
     * <pre> -M
     *  Turn off missing value replacement.
     *  WARNING: use only if your data has no missing values.</pre>
     *
     * <pre> -P
     *  Use probability estimation (default: off)
     * currently for L2-regularized logistic regression only! </pre>
     *
     * <pre> -E &lt;double&gt;
     *  Set tolerance of termination criterion (default: 0.01)</pre>
     *
     * <pre> -W &lt;double&gt;
     *  Set the parameters C of class i to weight[i]*C
     *   (default: 1)</pre>
     *
     * <pre> -B &lt;double&gt;
     *  Add Bias term with the given value if &gt;= 0; if &lt; 0, no bias term added (default: 1)</pre>
     *
     * <pre> -D
     *  If set, classifier is run in debug mode and
     *  may output additional info to the console</pre>
     *
     <!-- options-end -->
     *
     * @param options     the options to parse
     * @throws Exception  if parsing fails
     */
    @Override
	public void setOptions(String[] options) throws Exception {
        String tmpStr;

        tmpStr = Utils.getOption('S', options);
        if (tmpStr.length() != 0)
            setSVMType(new SelectedTag(Integer.parseInt(tmpStr), TAGS_SVMTYPE));
        else
            setSVMType(new SelectedTag(DEFAULT_SOLVER.ordinal(), TAGS_SVMTYPE));

        tmpStr = Utils.getOption('C', options);
        if (tmpStr.length() != 0)
            setCost(Double.parseDouble(tmpStr));
        else
            setCost(1);

        tmpStr = Utils.getOption('E', options);
        if (tmpStr.length() != 0)
            setEps(Double.parseDouble(tmpStr));
        else
            setEps(1e-3);

        setNormalize(Utils.getFlag('Z', options));

        setConvertNominalToBinary(Utils.getFlag('N', options));
        setDoNotReplaceMissingValues(Utils.getFlag('M', options));

        tmpStr = Utils.getOption('B', options);
        if (tmpStr.length() != 0)
            setBias(Double.parseDouble(tmpStr));
        else
            setBias(1);

        setWeights(Utils.getOption('W', options));

        setProbabilityEstimates(Utils.getFlag('P', options));

        super.setOptions(options);
    }

    /**
     * Returns the current options
     *
     * @return            the current setup
     */
    @Override
	public String[] getOptions() {

        List<String> options = new ArrayList<String>();

        options.add("-S");
        options.add("" + m_SolverType.ordinal());

        options.add("-C");
        options.add("" + getCost());

        options.add("-E");
        options.add("" + getEps());

        options.add("-B");
        options.add("" + getBias());

        if (getNormalize()) options.add("-Z");

        if (getConvertNominalToBinary()) options.add("-N");

        if (getDoNotReplaceMissingValues()) options.add("-M");

        if (getWeights().length() != 0) {
            options.add("-W");
            options.add("" + getWeights());
        }

        if (getProbabilityEstimates()) options.add("-P");

        return options.toArray(new String[options.size()]);
    }

    /**
     * Sets type of SVM (default SVMTYPE_L2)
     *
     * @param value       the type of the SVM
     */
    public void setSVMType(SelectedTag value) {
        if (value.getTags() == TAGS_SVMTYPE) {
            setSolverType(SolverType.values()[value.getSelectedTag().getID()]);
        }
    }

    protected void setSolverType(SolverType solverType) {
        m_SolverType = solverType;
    }

    protected SolverType getSolverType() {
        return m_SolverType;
    }

    /**
     * Gets type of SVM
     *
     * @return            the type of the SVM
     */
    public SelectedTag getSVMType() {
        return new SelectedTag(m_SolverType.ordinal(), TAGS_SVMTYPE);
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for
     *         displaying in the explorer/experimenter gui
     */
    public String SVMTypeTipText() {
        return "The type of SVM to use.";
    }

    /**
     * Sets the cost parameter C (default 1)
     *
     * @param value       the cost value
     */
    public void setCost(double value) {
        m_Cost = value;
    }

    /**
     * Returns the cost parameter C
     *
     * @return            the cost value
     */
    public double getCost() {
        return m_Cost;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for
     *         displaying in the explorer/experimenter gui
     */
    public String costTipText() {
        return "The cost parameter C.";
    }

    /**
     * Sets tolerance of termination criterion (default 0.001)
     *
     * @param value       the tolerance
     */
    public void setEps(double value) {
        m_eps = value;
    }

    /**
     * Gets tolerance of termination criterion
     *
     * @return            the current tolerance
     */
    public double getEps() {
        return m_eps;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for
     *         displaying in the explorer/experimenter gui
     */
    public String epsTipText() {
        return "The tolerance of the termination criterion.";
    }

    /**
     * Sets bias term value (default 1)
     * No bias term is added if value &lt; 0
     *
     * @param value       the bias term value
     */
    public void setBias(double value) {
        m_Bias = value;
    }

    /**
     * Returns bias term value (default 1)
     * No bias term is added if value &lt; 0
     *
     * @return             the bias term value
     */
    public double getBias() {
        return m_Bias;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for
     *         displaying in the explorer/experimenter gui
     */
    public String biasTipText() {
        return "If >= 0, a bias term with that value is added; " + "otherwise (<0) no bias term is added (default: 1).";
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for
     *         displaying in the explorer/experimenter gui
     */
    public String normalizeTipText() {
        return "Whether to normalize the data.";
    }

    /**
     * whether to normalize input data
     *
     * @param value       whether to normalize the data
     */
    public void setNormalize(boolean value) {
        m_Normalize = value;
    }

    /**
     * whether to normalize input data
     *
     * @return            true, if the data is normalized
     */
    public boolean getNormalize() {
        return m_Normalize;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for
     *         displaying in the explorer/experimenter gui
     */
    public String convertNominalToBinaryTipText() {
        return "Whether to turn on conversion of nominal attributes " + "to binary.";
    }

    /**
     * Whether to turn on conversion of nominal attributes
     * to binary.
     *
     * @param b true if nominal to binary conversion is to be
     * turned on
     */
    public void setConvertNominalToBinary(boolean b) {
        m_nominalToBinary = b;
    }

    /**
     * Gets whether conversion of nominal to binary is
     * turned on.
     *
     * @return true if nominal to binary conversion is turned
     * on.
     */
    public boolean getConvertNominalToBinary() {
        return m_nominalToBinary;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for
     *         displaying in the explorer/experimenter gui
     */
    public String doNotReplaceMissingValuesTipText() {
        return "Whether to turn off automatic replacement of missing " + "values. WARNING: set to true only if the data does not " + "contain missing values.";
    }

    /**
     * Whether to turn off automatic replacement of missing values.
     * Set to true only if the data does not contain missing values.
     *
     * @param b true if automatic missing values replacement is
     * to be disabled.
     */
    public void setDoNotReplaceMissingValues(boolean b) {
        m_noReplaceMissingValues = b;
    }

    /**
     * Gets whether automatic replacement of missing values is
     * disabled.
     *
     * @return true if automatic replacement of missing values
     * is disabled.
     */
    public boolean getDoNotReplaceMissingValues() {
        return m_noReplaceMissingValues;
    }

    /**
     * Sets the parameters C of class i to weight[i]*C (default 1).
     * Blank separated list of doubles.
     *
     * @param weightsStr          the weights (doubles, separated by blanks)
     */
    public void setWeights(String weightsStr) {
        StringTokenizer tok = new StringTokenizer(weightsStr, " ");
        m_Weight = new double[tok.countTokens()];
        m_WeightLabel = new int[tok.countTokens()];

        if (m_Weight.length == 0) System.out.println("Zero Weights processed. Default weights will be used");

        for (int i = 0; i < m_Weight.length; i++) {
            m_Weight[i] = Double.parseDouble(tok.nextToken());
            m_WeightLabel[i] = i;
        }
    }

    /**
     * Gets the parameters C of class i to weight[i]*C (default 1).
     * Blank separated doubles.
     *
     * @return            the weights (doubles separated by blanks)
     */
    public String getWeights() {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m_Weight.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(m_Weight[i]);
        }

        return sb.toString();
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for
     *         displaying in the explorer/experimenter gui
     */
    public String weightsTipText() {
        return "The weights to use for the classes, if empty 1 is used by default.";
    }

    /**
     * Returns whether probability estimates are generated instead of -1/+1 for
     * classification problems.
     *
     * @param value       whether to predict probabilities
     */
    public void setProbabilityEstimates(boolean value) {
        m_ProbabilityEstimates = value;
    }

    /**
     * Sets whether to generate probability estimates instead of -1/+1 for
     * classification problems.
     *
     * @return            true, if probability estimates should be returned
     */
    public boolean getProbabilityEstimates() {
        return m_ProbabilityEstimates;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for
     *         displaying in the explorer/experimenter gui
     */
    public String probabilityEstimatesTipText() {
        return "Whether to generate probability estimates instead of -1/+1 for classification problems "
            + "(currently for L2-regularized logistic regression only!)";
    }

    /**
     * transfers the local variables into a svm_parameter object
     *
     * @return the configured svm_parameter object
     */
    protected Parameter getParameters() {

        Parameter parameter = new Parameter(m_SolverType, m_Cost, m_eps);

        if (m_Weight.length > 0) {
            parameter.setWeights(m_Weight, m_WeightLabel);
        }

        return parameter;
    }

    /**
     * returns the svm_problem
     *
     * @param vx the x values
     * @param vy the y values
     * @param max_index
     * @return the Problem object
     */
    protected Problem getProblem(FeatureNode[][] vx, int[] vy, int max_index) {

        if (vx.length != vy.length) throw new IllegalArgumentException("vx and vy must have same size");

        Problem problem = new Problem();

        problem.l = vy.length;
        problem.n = max_index;
        problem.bias = getBias();
        problem.x = vx;
        problem.y = vy;

        return problem;
    }

    /**
     * returns an instance into a sparse liblinear array
     *
     * @param instance	the instance to work on
     * @return		the liblinear array
     * @throws Exception	if setup of array fails
     */
    protected FeatureNode[] instanceToArray(Instance instance) throws Exception {
        // determine number of non-zero attributes
        int count = 0;

        for (int i = 0; i < instance.numValues(); i++) {
            if (instance.index(i) == instance.classIndex()) continue;
            if (instance.valueSparse(i) != 0) count++;
        }

        if (m_Bias >= 0) {
            count++;
        }

        // fill array
        FeatureNode[] nodes = new FeatureNode[count];
        int index = 0;
        for (int i = 0; i < instance.numValues(); i++) {

            int idx = instance.index(i);
            double val = instance.valueSparse(i);

            if (idx == instance.classIndex()) continue;
            if (val == 0) continue;

            nodes[index] = new FeatureNode(idx + 1, val);
            index++;
        }

        // add bias term
        if (m_Bias >= 0) {
            nodes[index] = new FeatureNode(instance.numAttributes() + 1, m_Bias);
        }

        return nodes;
    }

    /**
     * Computes the distribution for a given instance.
     *
     * @param instance 		the instance for which distribution is computed
     * @return 			the distribution
     * @throws Exception 		if the distribution can't be computed successfully
     */
    @Override
	public double[] distributionForInstance(Instance instance) throws Exception {

        if (!getDoNotReplaceMissingValues()) {
            m_ReplaceMissingValues.input(instance);
            m_ReplaceMissingValues.batchFinished();
            instance = m_ReplaceMissingValues.output();
        }

        if (getConvertNominalToBinary() && m_NominalToBinary != null) {
            m_NominalToBinary.input(instance);
            m_NominalToBinary.batchFinished();
            instance = m_NominalToBinary.output();
        }

        if (m_Filter != null) {
            m_Filter.input(instance);
            m_Filter.batchFinished();
            instance = m_Filter.output();
        }

        FeatureNode[] x = instanceToArray(instance);
        double[] result = new double[instance.numClasses()];
        if (m_ProbabilityEstimates) {
            if (m_SolverType != SolverType.L2R_LR && m_SolverType != SolverType.L2R_LR_DUAL && m_SolverType != SolverType.L1R_LR) {
                throw new WekaException("probability estimation is currently only " + "supported for L2-regularized logistic regression");
            }

            int[] labels = m_Model.getLabels();
            double[] prob_estimates = new double[instance.numClasses()];

            Linear.predictProbability(m_Model, x, prob_estimates);

            // Return order of probabilities to canonical weka attribute order
            for (int k = 0; k < labels.length; k++) {
                result[labels[k]] = prob_estimates[k];
            }
        } else {
            int prediction = Linear.predict(m_Model, x);
            assert (instance.classAttribute().isNominal());
            result[prediction] = 1;
        }

        return result;
    }

    /**
     * Returns default capabilities of the classifier.
     *
     * @return      the capabilities of this classifier
     */
    @Override
	public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.disableAll();

        // attributes
        result.enable(Capability.NOMINAL_ATTRIBUTES);
        result.enable(Capability.NUMERIC_ATTRIBUTES);
        result.enable(Capability.DATE_ATTRIBUTES);
        //    result.enable(Capability.MISSING_VALUES);

        // class
        result.enable(Capability.NOMINAL_CLASS);
        result.enable(Capability.MISSING_CLASS_VALUES);
        return result;
    }

    /**
     * builds the classifier
     *
     * @param insts       the training instances
     * @throws Exception  if liblinear classes not in classpath or liblinear
     *                    encountered a problem
     */
    @Override
	public void buildClassifier(Instances insts) throws Exception {
        m_NominalToBinary = null;
        m_Filter = null;

        // remove instances with missing class
        insts = new Instances(insts);
        insts.deleteWithMissingClass();

        if (!getDoNotReplaceMissingValues()) {
            m_ReplaceMissingValues = new ReplaceMissingValues();
            m_ReplaceMissingValues.setInputFormat(insts);
            insts = Filter.useFilter(insts, m_ReplaceMissingValues);
        }

        // can classifier handle the data?
        // we check this here so that if the user turns off
        // replace missing values filtering, it will fail
        // if the data actually does have missing values
        getCapabilities().testWithFail(insts);

        if (getConvertNominalToBinary()) {
            insts = nominalToBinary(insts);
        }

        if (getNormalize()) {
            m_Filter = new Normalize();
            m_Filter.setInputFormat(insts);
            insts = Filter.useFilter(insts, m_Filter);
        }

        int[] vy = new int[insts.numInstances()];
        FeatureNode[][] vx = new FeatureNode[insts.numInstances()][];
        int max_index = 0;

        for (int d = 0; d < insts.numInstances(); d++) {
            Instance inst = insts.instance(d);
            FeatureNode[] x = instanceToArray(inst);
            if (x.length > 0) {
                max_index = Math.max(max_index, x[x.length - 1].index);
            }
            vx[d] = x;
            double classValue = inst.classValue();
            int classValueInt = (int)classValue;
            if (classValueInt != classValue) throw new RuntimeException("unsupported class value: " + classValue);
            vy[d] = classValueInt;
        }

        if (!m_Debug) {
            Linear.disableDebugOutput();
        } else {
            Linear.enableDebugOutput();
        }

        // reset the PRNG for regression-stable results
        Linear.resetRandom();

        // train model
        m_Model = Linear.train(getProblem(vx, vy, max_index), getParameters());
    }


    private boolean isOnlyNumeric(Instances insts) {
        for (int i = 0; i < insts.numAttributes(); i++) {
            if (i != insts.classIndex()) {
                if (!insts.attribute(i).isNumeric()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * turns on nominal to binary filtering
     * if there are not only numeric attributes
     */
    private Instances nominalToBinary(Instances insts) throws Exception {
        if (!isOnlyNumeric(insts)) {
            m_NominalToBinary = new NominalToBinary();
            m_NominalToBinary.setInputFormat(insts);
            insts = Filter.useFilter(insts, m_NominalToBinary);
        }
        return insts;
    }

    /**
     * returns a string representation
     *
     * @return a string representation
     */
    @Override
	public String toString() {
        return "LibLINEAR wrapper";
    }

    /**
     * Returns the revision string.
     *
     * @return the revision
     */
    @Override
	public String getRevision() {
        return REVISION;
    }

    /**
     * Main method for testing this class.
     *
     * @param args the options
     */
    public static void main(String[] args) {
        runClassifier(new LibLINEAR(), args);
    }
}
