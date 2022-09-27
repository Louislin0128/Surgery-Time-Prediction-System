package gui;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

public class Info {
	private static Logger logger = Logger.getLogger("Info");
	
	private static PropertyChangeSupport p = new PropertyChangeSupport(ProcessFrame.Mode.class);
	/**�[�J��ť��*/
	static void addListener(PropertyChangeListener listener) {
		p.addPropertyChangeListener(listener);
	}
	/**��ܭ���*/
	static void showPage(Page page) {
		p.firePropertyChange(new PropertyChangeEvent(ProcessFrame.Mode.SHOW_PAGE, "SHOWPAGE", null, page));
	}
	/**
	 * �M���Ҧ��L�{�μȦs�ɮ�<br>
	 * ���]�tModel�ؿ���Predict�ؿ�<br>
	 * �q�`�Ω�i�J�w���D�������ɭ�<br>
	 * {@link #deleteTemp()}
	 */
	static void clearProcessAndTemp() {
		p.firePropertyChange(new PropertyChangeEvent(ProcessFrame.Mode.CLEAR_PROCESS_AND_TEMP, "CLEAR_PROCESS_AND_TEMP", null, null));
		deleteTemp();
	}
	/**
	 * �M���Ҧ��L�{�β��ͪ��ɮ�<br>
	 * {@link #deleteAll()}
	 */
	static void clearAll() {
		p.firePropertyChange(new PropertyChangeEvent(ProcessFrame.Mode.CLEAR_ALL, "CLEAR_ALL", null, null));
		deleteAll();
	}
	/**�]�w�t�ΰT��*/
	static void showMessage(String message) {
		p.firePropertyChange(new PropertyChangeEvent(ProcessFrame.Mode.SHOWMESSAGE, "SHOWMESSAGE", null, message));
	}
	/**�]�w�t�ο��~�T��*/
	static void showError(String message) {
		p.firePropertyChange(new PropertyChangeEvent(ProcessFrame.Mode.SHOWERROR, "SHOWERROR", null, message));
	}
	
	/**�ϥλ�����U���|*/
	private static String manualPath ="src\\Manual";
	static String getManualPath() {
		return manualPath;
	}
	/**icon���|*/
	private static String iconPath = "src\\icon";
	public static String getIconPath() {
		return iconPath;
	}
	
	// ============================================
	private static String desktop = "D:\\AA\\Surgery Time Prediction System";	//�ୱ���|
	/**�u�@�ؿ�*/
	private static Path workingData;
	
	/** ��l���|*/
	private static Path rawPath;
	
	/**�ഫ����|*/
	private static Path dataPath;
	
	/**�¦~������|*/
	private static String oldYearPath;
	
	/**�s�~������|*/
	private static String newYearPath;
	
	/**��͸��|*/
	private static String doctorPath;
	
	/**DRG�ɮ׸��| */
	private static String drgPath;
	
	/**�X�֧����|*/
	private static String appendRecord;
	
	/**merge�~����������|*/
	private static String mergeFeature;
	
	/**mergeDRG���|*/
	private static String mergeDRG;
	
	/**��N�ɶ� | �¾K�ɶ����|*/
	private static String createTime;
	
	/**�N���D���s�s��*/
	private static String rearrangeTitle;
	
	/**������ɭ������ͪ��ɮ׸��|*/
	private static String dataTransform;
	
	/**�S�x����������ͪ��ɮ׸��|*/
	private static String featureSelect;
	
	/**������ɸ�Ƨ����|*/
	private static String dataTransformPath;
	
	/**�J�����Ƨ����|*/
	private static String summarizeDataPath;
	
	/**�S�x�����Ƨ����|*/
	private static String featureSelectPath;
	
	/**�N��Ʃ�iweka���e�ݭn����B�z*/
	private static String transFeature;
	
	/**
	 * �S�x�������T<br>
	 * ������S�x�αƦW
	 */
	private static String featureInfo;

	/**��ƳB�z��Ƨ����|*/
	private static String dataHandlePath;
	
	/**��ƳB�z���|*/
	private static String dataHandle;
	
	/**��Ʃ����Ƨ����|*/
	private static String dataSplitPath;
	
	/**�y�������|*/
	private static String lsh;
	
	/**���ø�ƶ����|*/
	private static String disOrganize;
	
	/**
	 * TrainTestSplit�I�s��s�񪺰V�m��<br>
	 * �P�ɤ]�O�w���D������ܿﶵ���ӷ��ɮ�<br>
	 * (�@�w���]�t�T�_)
	 */
	private static String originalTrain;
	
	/**
	 * TrainTestSplit�I�s��s�񪺴��ն�<br>
	 * (�@�w���]�t�T�_)
	 */
	private static String originalTest;
	
	// �ҫ�����
	/**�ҫ����|*/
	private static String modelPath;
	
	/**�V�m�ҫ��Ϊ��V�m��(�i��]�t�T�_)*/
	private static String modelTrain;
	
	/**�V�m�ҫ��Ϊ����ն�(�i��]�t�T�_)*/
	private static String modelTest;
	
	/**�������ҫ�*/
	private static String classifier;
	
	/**DoStepFile�{�� �s�񪺸�Ƨ����|*/
	private static String doStepPath;
	
	/**�U�ӭ����i���ഫ�ɡA���͹L���ɮת��s����|*/
	private static String tempPath;
	
	/**�B�J�ɦW�� | �����s�b*/
	private static String step;
	
	/**
	 * �S�x����Τ�k���<br>
	 * weka��������ƶ��ɡA�n�۰ʲ��X���B�J��
	 */
	private static String tempStep;
	
	/**��ƳB�z�������B�J��*/
	private static String dataHandleStep;
	
	/**PreviewSheet�i�沾���y�������Ȧs���|*/
	private static String impAndExpTemp;
	
	/**�V�m�ҫ����ͪ��T��*/
	private static String trainResult;
	
	/**��l�w���P��ڭȪ���� ��PreviewSheetŪ��*/
	private static String pAnda;
	
	/**��Predictors���OŪ���V�m���A�O�@�ӹL���ɮ�*/
	private static String transTrain;
	
	/**��Predictors���OŪ�����ն��A�O�@�ӹL���ɮ�*/
	private static String transTest;
	
	/**
	 * �D�������w�����s�|���ͪ��ɮ�<br>
	 * ���s���<br>
	 */
	private static String predictPath;
	
	/**
	 * �g�D�����B�J���ഫ�᪺�V�m��<br>
	 * �Y��N�γ¾K�ɶ������W�ơA�i�ഫ�٭�
	 */
	private static String predictTrain;

	/**��i�ҫ��w�����浧���ն�*/
	private static String predictTest;
	
	/**�w���ﶵ*/
	private static String predictOptions;
	
	/**�h����ƹw�� �ɮ׿�X*/
	private static String multiplePredict;
	
	/**���o�ϥΪ̪��ୱ���|*/
	static String getDesktop() {
		return desktop;
	}
	//��������ɭ����A�����ˬd
	static String getAppendRecord() {
		return appendRecord;
	}
	static String getMergeDRG() {
		return mergeDRG;
	}
	static String getMergeFeature() {
		return mergeFeature;
	}
	static String getCreateTime() {
		return createTime;
	}
	static String getSummarizeData() {	
		return summarizeDataPath;
	}
	//====================
	static String getRearrangeTitle() {
		setDefaultRawPath();
		return rearrangeTitle;
	}
	static String getFeatureSelect() {
		setDefaultRawPath();
		return featureSelect;
	}
	static String getTransFeature() {
		setDefaultRawPath();
		return transFeature;
	}
	static String getFeatureInfo() {
		return featureInfo;
	}
	static String getDataHandlePath() {
		return dataHandlePath;
	}
	static String getDataHandle() {
		setDefaultRawPath();
		return dataHandle;
	}
	static String getOriginalTrain() {
		return originalTrain;
	}
	static String getOriginalTest() {
		return originalTest;
	}
	static String getModelPath() {
		return modelPath;
	}
	static String getSummarizeDataPath() {
		setDefaultRawPath();
		return summarizeDataPath;
	}
	static String getDataPath() {
		setDefaultRawPath();
		return dataPath.toString();
	}
	static String getNewYearPath() {
		setDefaultRawPath();
		return newYearPath;
	}
	static String getOldYearPath() {
		setDefaultRawPath();
		return oldYearPath;
	}
	static String getDoctorPath() {
		setDefaultRawPath();
		return doctorPath;
	}
	static String getDRGPath() {
		return drgPath;
	}
	static String getDataTransform() {
		setDefaultRawPath();
		return dataTransform;
	}
	static String getDisOrganize() {
		setDefaultRawPath();
		return disOrganize;
	}
	static String getLSH() {
		setDefaultRawPath();
		return lsh;
	}
	static String getDoStepPath() {
		setDefaultRawPath();
		return doStepPath;
	}
	public static String getTempPath() {
		return tempPath;
	}
	public static String getImpAndExpTemp() {	//PreviewSheet
		return impAndExpTemp;
	}
	static String getModelTrain() {
		setDefaultRawPath();
		return modelTrain;
	}
	static String getModelTest() {
		setDefaultRawPath();
		return modelTest;
	}
	static String getTransTrain() {
		return transTrain;
	}
	static String getTransTest() {
		return transTest;
	}
	static String getTrainResult() {
		return trainResult;
	}
	static String getPandA() {
		return pAnda;
	}
	static String getClassifier() {
		return classifier;
	}
	static String getDataHandleStep() {
		return dataHandleStep;
	}
	public static String getStep() {
		return step;
	}
	public static String getTempStep() {
		setDefaultRawPath();
		return tempStep;
	}
	static String getPredictPath() {
		return predictPath;
	}
	static String getPredictTrain() {
		return predictTrain;
	}
	static String getPredictTest() {
		return predictTest;
	}
	static String getPredictOptions() {
		return predictOptions;
	}
	static String getMultiplePredict() {
		return multiplePredict;
	}
	
	static String getRawPath() {
		return rawPath.toString();
	}
	static Path getWorkingData() {
		setDefaultRawPath();
		return workingData;
	}
	static void setRawPath(String rawPath) {
		setRawPath(Paths.get(rawPath));
	}
	public static void setRawPath(Path rawPath) {
		Info.rawPath = rawPath;
		System.out.println("��l��ƥؿ��G" + rawPath);
		try {
			workingData = rawPath.resolveSibling("WorkingData");
			deleteDirectory(workingData);
			Files.createDirectories(workingData);	//�إ߷s��WorkingData
			dataPath = workingData.resolve("Data");
			doctorPath = workingData + "\\Seniority";
			drgPath = workingData + "\\Drg";
			oldYearPath = workingData + "\\YearlyReport";
			newYearPath = Files.createDirectories(dataPath.resolve("FilledYearlyReport")).toString();
			//
			dataTransformPath = Files.createDirectories(dataPath.resolve("DataTransform")).toString();
			appendRecord = dataTransformPath + "\\AppendRecord.csv";
			mergeDRG = dataTransformPath + "\\MergeDRG.csv";
			mergeFeature = dataTransformPath + "\\MergeFeature.csv";
			createTime = dataTransformPath + "\\CreateTime.csv";
			rearrangeTitle = dataTransformPath + "\\RearrangeTitle.csv";
			dataTransform = dataTransformPath + "\\DataTransform.csv";
			summarizeDataPath = Files.createDirectories(dataPath.resolve("SummarizeData")).toString();
			//
			dataHandlePath = Files.createDirectories(dataPath.resolve("DataHandle")).toString();
			dataHandle = dataHandlePath + "\\DataHandle.csv";
			//
			featureSelectPath = Files.createDirectories(dataPath.resolve("FeatureSelect")).toString();
			transFeature = featureSelectPath + "\\TransFeature.csv";
			featureSelect = featureSelectPath + "\\FeatureSelect.csv";
			featureInfo = featureSelectPath + "\\FeatureInfo.csv";
			//
			dataSplitPath = Files.createDirectories(dataPath.resolve("DataSplit")).toString();
			lsh = dataSplitPath + "\\LSH.csv";
			disOrganize = dataSplitPath + "\\DisOrganize.csv";
			//
			doStepPath = Files.createDirectories(dataPath.resolve("DoStepFile")).toString();
			dataHandleStep = doStepPath + "\\DataHandleStep.csv";
			tempStep = doStepPath + "\\TempStepFile.csv";
			//
			tempPath = Files.createDirectories(dataPath.resolve("Temp")).toString();
			pAnda = tempPath + "\\PredictAndActual.csv";
			transTrain = tempPath + "\\TransTrain.csv";
			transTest = tempPath + "\\TransTest.csv";
			modelTrain = tempPath + "\\ModelTrain.csv";
			modelTest = tempPath + "\\ModelTest.csv";
			impAndExpTemp = modelPath + "\\ImpAndExpTemp.csv";
			//
			modelPath = Files.createDirectories(dataPath.resolve("Model")).toString();
			step = modelPath + "\\AllStep.step";
			trainResult = modelPath + "\\TrainResult.csv";
			classifier = modelPath + "\\Predictors.model";
			
			originalTrain = modelPath + "\\OriginalTrain.csv";
			originalTest = modelPath + "\\OriginalTest.csv";
			//
			predictPath = Files.createDirectories(dataPath.resolve("Predict")).toString();
			predictTrain = predictPath + "\\PredictTrain.csv";
			predictTest = predictPath + "\\PredictTest.csv";
			predictOptions = predictPath + "\\PredictOptions.csv";
			multiplePredict = predictPath + "\\MultiplePredict.csv";
			
			System.out.println("�u�@�ؿ��إߧ���");
		} catch (IOException e) {
			logger.info(e.getMessage());
		}
	}
	
	/**�q�`�O�{�r���|Ĳ�o���禡*/
	private static void setDefaultRawPath() {
		if(rawPath == null) {
			setRawPath(desktop);
		}
	}
	
	/**�R������L�{�����ͪ��ɮ�(���]�tModel�BPredict��Temp�ؿ�)*/
	private static void deleteTemp() {
		deleteDirectory(newYearPath);
		deleteDirectory(dataTransformPath);
		deleteDirectory(summarizeDataPath);
		deleteDirectory(dataHandlePath);
		deleteDirectory(featureSelectPath);
		deleteDirectory(dataSplitPath);
		deleteDirectory(doStepPath);
//		deleteDirectory(tempPath);
	}
	
	/**�R���Ҧ��w�إߪ��ɮפΥؿ�*/
	private static void deleteAll() {
		rawPath = null;
		deleteTemp();
		deleteDirectory(tempPath);
		deleteDirectory(modelPath);
		deleteDirectory(predictPath);
	}
	
	/**�R���ؿ�*/
	private static void deleteDirectory(String delete) {
		if(delete != null) {
			deleteDirectory(Paths.get(delete));
		}
	}
	
	/**�R���ؿ�*/
	private static void deleteDirectory(Path delete) {
		if(delete == null) {
			System.err.println("���R���ؿ��|���]�m");
			return;
		}else if(Files.notExists(delete)) {
			System.err.println(delete.getFileName() + "�ؿ����s�b");
			return;
		}
		try {
			Files.walkFileTree(delete, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE; 
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
					if (e == null) {
						Files.delete(dir);
					}else{
						System.err.println(e);
					}
					return FileVisitResult.CONTINUE;
				}
				
				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					System.err.println(exc);
					return FileVisitResult.CONTINUE;
				}
			});
			System.out.println(delete.getFileName() + "�R�����\");
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}