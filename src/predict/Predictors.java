package predict;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import guiFunction.CreateFile;
import guiFunction.Step;
import guiFunction.TransIfNeeded;
import preprocess.Split;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.Utils;
import weka.core.converters.CSVLoader;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertySheetPanel;

public class Predictors {
	private static String[] classifierName = {	// �̷Ӥ������W�ٱƧ�
			"weka.classifiers.meta.AdditiveRegression",
			"weka.classifiers.meta.AttributeSelectedClassifier",
			"weka.classifiers.meta.Bagging",
			"predict.BPNN",
			"weka.classifiers.meta.CVParameterSelection",
			"weka.classifiers.trees.DecisionStump",
			"weka.classifiers.rules.DecisionTable",
			"weka.classifiers.meta.FilteredClassifier",
			"weka.classifiers.functions.GaussianProcesses",
			"weka.classifiers.lazy.IBk",
			"weka.classifiers.meta.IterativeClassifierOptimizer",
			"weka.classifiers.lazy.KStar",
			"weka.classifiers.functions.LinearRegression",
			"weka.classifiers.lazy.LWL",
			"weka.classifiers.trees.M5P",
			"weka.classifiers.rules.M5Rules",
			"weka.classifiers.functions.MultilayerPerceptron",
			"weka.classifiers.meta.MultiScheme",
			"weka.classifiers.meta.RandomCommittee",
			"weka.classifiers.trees.RandomForest",
			"weka.classifiers.meta.RandomizableFilteredClassifier",
			"weka.classifiers.meta.RandomSubSpace",			
			"weka.classifiers.trees.RandomTree",
			"weka.classifiers.meta.RegressionByDiscretization",			
			"weka.classifiers.trees.REPTree",
			"weka.classifiers.functions.SMOreg",
			"weka.classifiers.meta.Stacking",
			"weka.classifiers.meta.Vote",
			"weka.classifiers.meta.WeightedInstancesHandlerWrapper",
			"weka.classifiers.rules.ZeroR"};
	private static String[] classifierSimpleName;
	/**���o�i�ѨϥΪ��������}�C*/
	public static String[] getClassifierSimpleName() {
		return classifierSimpleName;
	}
	static {
		int length = classifierName.length;
		classifierSimpleName = new String[length];
		for(int i = 0; i < length; i++) {
			classifierSimpleName[i] = classifierName[i].substring(classifierName[i].lastIndexOf(".") + 1);
		}
		GenericObjectEditor.determineClasses();
	}
	
	private static PropertySheetPanel propertyPanel = new PropertySheetPanel(false);
	/**�o����Ѽƪ�����*/
	public static JPanel getClassifierPanel() {
		return propertyPanel;
	}
	/**
	 * ���ϥΪ�������
	 * @param index �������}�C���ޭ�
	 * @throws Exception
	 */
	public static void changeClassifier(int index) throws Exception {
		clsr = AbstractClassifier.forName(classifierName[index], null);
		propertyPanel.setTarget(clsr);
	}
	
	private static Classifier clsr;
	private Instances trainInsts, testInsts;
	private String origTrain, step, tempStep;
	/**
	 * Ū���w�إߨðV�m�������ҫ�
	 * @param loadModel ��J�ҫ����|
	 * @throws Exception
	 */
	public Predictors(String loadModel) throws Exception {
		Object[] load = SerializationHelper.readAll(loadModel);
		clsr = (Classifier) load[0];
		trainInsts = (Instances) load[1];
	}
	
	/**
	 * ���L����ˬd���q�A�����إ�
	 * @param origTrain ��l���ഫ�V�m�� (�����g�L�T�_)
	 * @param transTrain �g�L�ഫ�V�m�� (�]�����ŦXBPNN�n�D�A�۰��ഫ���ɮ�)
	 * @param transTest �g�L�ഫ���ն� (�]�����ŦXBPNN�n�D�A�۰��ഫ���ɮ�)
	 * @param step �B�J��
	 * @throws IOException
	 */
	public Predictors(String origTrain, String transTrain, String transTest, String step) throws IOException {
		this.origTrain = origTrain;
		this.step = step;
		trainInsts = readFile(transTrain);
		testInsts = readFile(transTest);
	}
	
	/**
	 * �w�����������غc�l
	 * @param origTrain ��l���ഫ�V�m�� (�����g�L�T�_)
	 * @param modelTrain �V�m�� (�i��g�L�T�_)
	 * @param modelTest ���ն� (�i��g�L�T�_)
	 * @param transTrain �Ѥ����ഫ���V�m��
	 * @param transTest �Ѥ����ഫ�����ն�
	 * @param step �B�J��
	 * @param tempStep �ȮɨB�J��
	 * 
	 * @throws PredictorsException ��ƶ����ŦX�W�w
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Predictors(String origTrain,
					  String modelTrain, String modelTest,
					  String transTrain, String transTest,
					  String step, String tempStep)
					  throws PredictorsException, FileNotFoundException, IOException {
		try {
			changeClassifier(0);
		} catch (Exception e) {
			// �Y�o�ͨҥ~�A�Э��s�Ұʨt��
			JOptionPane.showMessageDialog(null, "���������ɵo�Ͱ��D�A�Э��s�Ұʨt�ΡC");
			System.exit(1);
		}
		
		this.origTrain = origTrain;
		this.step = step;
		this.tempStep = tempStep;
		
		// ���͵�CSVLoader���ɮץH����String�ݩʪ����
		CreateFile.forWekaLoader(modelTrain, transTrain);
		CreateFile.forWekaLoader(modelTest, transTest);
		// �V�m���}�l�ˬd | ����X�{String�ݩʪ����
		trainInsts = readFile(transTrain);
		// ���ն��}�l�ˬd | ����X�{String�ݩʪ����
		testInsts = readFile(transTest);
		
		//��ƶ��}�l�ˬd | ����X�{String�ݩʪ����
		if(trainInsts.checkForStringAttributes()) {
			System.err.println("�V�m�����X�G�W�w");
			ArrayList<String> stringAttr = listStringAttr(trainInsts);
			Step.buildAutoRemove(stringAttr, tempStep);
			throw new PredictorsException(getErrorMessage(stringAttr));
		}
	}
	
	/**
	 * Ū���ɮרçP�_���L����ϥΪ��S�x
	 * @param inFile ��J�ɮ�
	 * @return Instances
	 * @throws PredictorsException ��ƶ����ŦX�W�w
	 * @throws IOException
	 */
	private Instances readFile(String inFile) throws IOException {
		CSVLoader csv = new CSVLoader();
		csv.setSource(new File(inFile));
		Instances insts = csv.getDataSet();
		
		if(insts.attribute(0).name().equals("�y����")) {
			insts.deleteAttributeAt(0);	// �Y�Ĥ@�欰�y�����A�N�R���̫�@��
		}
		insts.setClassIndex(insts.numAttributes() - 1);
		//�Y�L�����A�N���ƥi�Qweka����
		return insts;
	}
	
	/**�C�XString���A��weka�S�x*/
	private ArrayList<String> listStringAttr(Instances insts) {
		Enumeration<Attribute> attrs = insts.enumerateAttributes();
		ArrayList<String> stringAttr = new ArrayList<String>(insts.numAttributes());
		Attribute attr;
		while(attrs.hasMoreElements()) {
			attr = attrs.nextElement();
			if (attr.isString()) {	//�Y����쬰String Type
				stringAttr.add(attr.name());//�[�J�}�C
			}
		}
		return stringAttr;
	}
	
	/**���o����String�����~�T��*/
	private String getErrorMessage(ArrayList<String> stringAttr) {
		StringBuilder tempString = new StringBuilder("�]���o�����ŭȤӦh�A�L�k�i��S�x����A�N�۰ʧR���G\n");
		stringAttr.forEach(s -> {
			tempString.append("�u")
					  .append(s)
					  .append("�v ");	
		});
		tempString.append("\n�O�G�۰ʧR���W�C�S�x�F")
				  .append("\n�_�G�L�k�ϥΥ������\��C");
		return tempString.toString();
	}
	
	/**
	 * �x�s�V�m�ҫ�
	 * @param fileName ��X���|
	 * @throws Exception
	 */
	public void saveClassifier(String fileName) throws Exception {
		SerializationHelper.writeAll(fileName, new Object[] {clsr, trainInsts});
	}
	
	/**�T�{��ƬO�_�ŦX�n�D*/
	public String checkBPNNdata() throws Exception {
		String clsrName = clsr.getClass().getSimpleName();
		if(clsrName.equals("BPNN")) {	// �p�G�ϥ�BPNN�A�ˬd��ƶ��U��쪺���A
			System.out.println("�ˬd��ƶ���");
			if(trainInsts.checkForAttributeType(Attribute.NOMINAL)) {
				System.err.println("��ƶ����ŦX�ݨD�A�ݭn�ഫ");
				ArrayList<ArrayList<String>> transAttrs = listTransAttrs(trainInsts);
				Step.buildAutoStep(transAttrs, tempStep);
				return getTransMessage(transAttrs);
			}
		}
		return null;
	}
	
	/**
	 * �V�m������
	 * @param outFile ��X�V�m�ȻP��ӭ��ɮ�
	 * @return �V�m���G�r��
	 * @throws PredictorsException
	 * @throws Exception
	 */
	public String trainClassifier(String outFile) throws Exception {
		String clsrName = clsr.getClass().getSimpleName();
		System.out.println("�إ߼ҫ���");
		StringBuilder results = new StringBuilder();
		results.append("�ϥΪ��������G").append(clsrName).append('\n');
		//�H�V�m���V�m�ҫ�
		long start = System.currentTimeMillis();
		clsr.buildClassifier(trainInsts);
		//�H�V�m�����ռҫ�
		Evaluation eval = new Evaluation(trainInsts);
		eval.evaluateModel(clsr, trainInsts);
		long end = System.currentTimeMillis();
		
		//��X�ҫ����U�������ƭ�
		String cc = Utils.doubleToString(eval.correlationCoefficient(), 4);
		TransIfNeeded trans = TransIfNeeded.transValue(step, origTrain);//�Y�����T�_�A�N�������٭�
		String transMAE = Utils.doubleToString(trans.get(eval.meanAbsoluteError()), 4);
		
//		System.out.println(origTrain);
//		System.out.println("MAE "+eval.meanAbsoluteError());
//		System.out.println("transMAE "+trans.get(eval.meanAbsoluteError()));
		
		String transRMSE = Utils.doubleToString(trans.get(eval.rootMeanSquaredError()), 4);
		String transType = trans.getType();
		results.append("�ϥΰV�m�������ҫ����U�����мƭ�\n");
		results.append("Correlation coefficient(�����Y��)�G").append(cc).append('\n');
		results.append("Mean absolute error(��������~�t)�G").append(transMAE).append(" (").append(transType).append(")\n");
		results.append("Root mean squared error(����ڻ~�t)�G").append(transRMSE).append(" (").append(transType).append(")\n");
		results.append("�V�m�ҫ��ɶ��G").append(end - start).append("(ms)\n\n");
		
		//�H���ն����ռҫ�
		start = System.currentTimeMillis();
		eval = new Evaluation(trainInsts);
		eval.evaluateModel(clsr, testInsts);
		end = System.currentTimeMillis();
		//��X�ҫ����U�������ƭ�
		cc = Utils.doubleToString(eval.correlationCoefficient(), 4);
		transMAE = Utils.doubleToString(trans.get(eval.meanAbsoluteError()), 4);
		transRMSE = Utils.doubleToString(trans.get(eval.rootMeanSquaredError()), 4);
		results.append("�ϥδ��ն������ҫ����U�����мƭ�\n");
		results.append("Correlation coefficient(�����Y��)�G").append(cc).append('\n');
		results.append("Mean absolute error(��������~�t)�G").append(transMAE).append(" (").append(transType).append(")\n");
		results.append("Root mean squared error(����ڻ~�t)�G").append(transRMSE).append(" (").append(transType).append(")\n");
		results.append("���ռҫ��ɶ��G").append(end - start).append("(ms)");
		
		//��X���ն�����ڭȻP�w���Ȫ�����ɮ�
		StringBuilder pAndAstr = new StringBuilder("��ڭ�,�w����,�t��(��ڭ�-�w����)\n");
		eval.predictions().forEach(p -> {
			double actual = trans.get(p.actual()), predict = trans.get(p.predicted()), deviation = actual - predict;
			pAndAstr.append(actual == Double.NaN ? '?' : actual)	//���
					.append(',')
					.append(predict == Double.NaN ? '?' : predict)	//�w��
					.append(',')
					.append(deviation == Double.NaN ? '?' : deviation)//�t��
					.append('\n');
		});
		CreateFile.toCSV(outFile, pAndAstr.toString());
		
		return results.toString();
	}
	
	/**
	 * �C�|���ഫ�S�x
	 * @param train �V�m��
	 * @return ���ഫ�S�x�}�C
	 */
	public ArrayList<ArrayList<String>> listTransAttrs(Instances train) {
		ArrayList<ArrayList<String>> transAttrs = new ArrayList<>(3);
		transAttrs.add(new ArrayList<>());//TargetEncoding
		transAttrs.add(new ArrayList<>());//NormalizeFeature
		transAttrs.add(new ArrayList<>());//RemoveRecord
		Enumeration<Attribute> trainAttrs = train.enumerateAttributes();
		Attribute trainAttr;
		String attrName;
		while(trainAttrs.hasMoreElements()) {
			trainAttr = trainAttrs.nextElement();
			attrName = trainAttr.name();
			if(trainAttr.isNominal()) {
				transAttrs.get(0).add(attrName);
			}
			transAttrs.get(1).add(attrName);
			transAttrs.get(2).add(attrName);
		}
		transAttrs.get(1).add(train.classAttribute().name());
		return transAttrs;
	}
	
	/**���o����N�۰��ഫ��쪺�T��*/
	private String getTransMessage(ArrayList<ArrayList<String>> trans) {
		StringBuilder tempStr = new StringBuilder("�H�U�ҦC��줣�ŦX�t��k(BPNN)�A�L�k����t��k�A�N�۰��ഫ�G");
		tempStr.append("\n���ƭȤơG");
		trans.get(0).forEach(s -> tempStr.append(s).append(' '));
		
		tempStr.append("\n�����W�ơG");
		trans.get(1).forEach(s -> tempStr.append(s).append(' '));
		
		tempStr.append("\n�N�����ŭȡG");
		trans.get(2).forEach(s -> tempStr.append(s).append(' '));
		return tempStr.toString();
	}
	
	/**
	 * �o��浧�w����
	 * @param fileName �ݹw���ɮ�
	 * @param trans �ǤJTransIfNeeded�H�ഫ�ƭ�
	 * @return �g�ഫ�᪺�w����
	 * @throws Exception
	 */
	public double getValue(String fileName, TransIfNeeded trans) throws Exception {	//�@���ݹw�����
		System.out.println("�浧�w����");
		ArrayList<String> predict;
		String input;
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			input = br.readLine();//���L���D�C
			input = br.readLine();
			predict = Split.withoutQuotes(input);
		}
		return trans.get(getPredict(predict));
	}
	
	/**
	 * ���X�h���w�����ɮ�
	 * @param inFile ���տ�J�ɮ�
	 * @param outFile ���տ�X�ɮ�
	 * @param trans �ǤJTransIfNeeded�H�ഫ�ƭ�
	 * @throws Exception
	 */
	public void buildPredictFile(String inFile, String outFile, TransIfNeeded trans) throws Exception {	//�@���ݹw�����
		System.out.println("�h���w����");
		ArrayList<String> predict;
		String input;
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))) {
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))) {
			bw.write(br.readLine());//�g�J���D�C
			while((input = br.readLine()) != null) {
				predict = Split.withoutQuotes(input);
				bw.newLine();
				input = input.substring(0, input.lastIndexOf(","));
				bw.write(input + "," + trans.get(getPredict(predict)));
			}
		}
		}
	}
	
	/**
	 * �o��浧�w����
	 * @param predict ���ո�ư}�C
	 * @return �w����
	 * @throws Exception
	 */
	private double getPredict(ArrayList<String> predict) throws Exception {
		double[] values = new double[predict.size()];
		Enumeration<Attribute> attrs = trainInsts.enumerateAttributes();
		Attribute attr;
		int i = 0;
		while(attrs.hasMoreElements()) {
			attr = attrs.nextElement();
			if(attr.isNominal()) {
				values[i] = trainInsts.attribute(i).indexOfValue(predict.get(i));
			}else if(attr.isNumeric()) {
				values[i] = Double.parseDouble(predict.get(i));
			}else {
				System.err.println(attr.name() + "�L�k�w��");
			}
			i++;
		}
		DenseInstance predictInst = new DenseInstance(1.0, values);
		predictInst.setDataset(trainInsts);
		predictInst.setClassMissing();
		return clsr.classifyInstance(predictInst);
	}
}
