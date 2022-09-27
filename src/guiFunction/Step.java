package guiFunction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import guiComponent.AbstractTools;
import guiComponent.AbstractTools.Tool;
import preprocess.NormalizeFeature;
import preprocess.RemoveFeature;
import preprocess.RemoveRecord;
import preprocess.StandardizeFeature;
import preprocess.TargetEncoding;

/**
 * �w�q�B�J�ɬ������ާ@
 */
public class Step {
	/**
	 * ����B�J�ɤ����T�_
	 * @param preTrain ��l�V�m���ɮ�
	 * @param preTest ��l���ն��ɮ�
	 * @param doStepPath ����L�{�����ɮש�m�ؿ�
	 * @param stepFile �B�J��
	 * @param trainOut �V�m����X�ɮ�
	 * @param testOut ���ն���X�ɮ�
	 * @throws Exception
	 */
	public static void executeBitches(String preTrain, String preTest, String doStepPath, String stepFile, String trainOut, String testOut) throws Exception {
		String[] split;
		try(BufferedReader br = new BufferedReader(new FileReader(preTrain))){
			split = br.readLine().split(",");	// �ؼ���� | ���w���e | �{���W��
		}
		String ref = split[split.length - 1];	// �ѷ���� | �s�@��Ӭy�����ɮ׮ɡA�w�����y����
		
		int index = 1;
		String train = doStepPath + "\\1_train.csv", test = doStepPath + "\\1_test.csv";
		try(BufferedReader br = new BufferedReader(new FileReader(stepFile))){// Ū�J�B�J��
			String input;
			while ((input = br.readLine()) != null) {
				if(input.isEmpty()) {//�Y�B�J�ɬ��šA���X�j��
					break;
				}
				split = input.split(",");
				switch (split[2]) {
					case "TargetEncoding":
						TargetEncoding.exec(preTrain, split[0], ref, train, preTest, test);
						break;
					case "NormalizeFeature":
						NormalizeFeature.exec(preTrain, split[0], train, preTest, test);
						break;
					case "StandardizeFeature":
						StandardizeFeature.exec(preTrain, split[0], train, preTest, test);
						break;
				}
				switch (split[2]) {
					case "TargetEncoding":
					case "NormalizeFeature":
					case "StandardizeFeature":
						preTrain = train;
						preTest = test;
						train = doStepPath + "\\" + ++index + "_train.csv";
						test = doStepPath + "\\" + ++index + "_test.csv";		
						break;
				}
			}	
		}
		CreateFile.copy(preTrain, trainOut);
		CreateFile.copy(preTest, testOut);
	}
	
	/**
	 * ����B�J�ɤ����T�_
	 * @param preTrain ��l�V�m���ɮ�
	 * @param preTest ��l���ն��ɮ�
	 * @param doStepPath ����L�{�����ɮש�m�ؿ�
	 * @param stepFile �B�J��
	 * @param trainOut �V�m����X�ɮ�
	 * @param testOut ���ն���X�ɮ�
	 * @throws Exception
	 */
	public static void executePredictorsStep(String preTrain, String preTest, String doStepPath, String stepFile, String trainOut, String testOut) throws Exception {
		String[] split;
		try(BufferedReader br = new BufferedReader(new FileReader(preTrain))){
			split = br.readLine().split(",");	// �ؼ���� | ���w���e | �{���W��
		}
		String ref = split[split.length - 1];	// �ѷ���� | �s�@��Ӭy�����ɮ׮ɡA�w�����y����
		
		int index = 1;
		String train = doStepPath + "\\1_train.csv", test = doStepPath + "\\1_test.csv";
		try(BufferedReader br = new BufferedReader(new FileReader(stepFile))){// Ū�J�B�J��
			String input;
			while ((input = br.readLine()) != null) {
				if(input.isEmpty()) {//�Y�B�J�ɬ��šA���X�j��
					break;
				}
				split = input.split(",");
				switch (split[2]) {
					case "TargetEncoding":
						TargetEncoding.exec(preTrain, split[0], ref, train, preTest, test);
						break;
					case "NormalizeFeature":
						NormalizeFeature.exec(preTrain, split[0], train, preTest, test);
						break;
					case "RemoveRecord"://�u�������ն����ŭ�
						RemoveRecord.exec(preTest, split[0], split[1], test);
						break;
				}
				switch (split[2]) {
					case "TargetEncoding":	//�V�m���ո��|���n��s
					case "NormalizeFeature":
						preTrain = train;
						train = doStepPath + "\\" + ++index + "_train.csv";
					case "RemoveRecord":	//��s���ո��|�N�n
						preTest = test;
						test = doStepPath + "\\" + ++index + "_test.csv";		
						break;
				}
			}	
		}
		CreateFile.copy(preTrain, trainOut);
		CreateFile.copy(preTest, testOut);
	}
	
	/**
	 * ���沾���S�x���
	 * @param inFile ��J�ɮ�
	 * @param stepFile �B�J�ɮ�
	 * @param outFile ��X�ɮ�
	 * @throws Exception
	 */
	public static void executeRemoveFeature(String inFile, String stepFile, String outFile) throws Exception {
		String input;
		String[] split;
		ArrayList<String> remove = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader(stepFile))) {// Ū�J�B�J��
			while((input = br.readLine()) != null) {
				if(input.isEmpty()) {//�Y�B�J�ɬ��šA���X�j��
					break;
				}
				split = input.split(",");
				remove.add(split[0]);
			}
		}
		RemoveFeature.exec(inFile, outFile, remove);
	}
	
	/**
	 * ���禡��FeatureSelect��MethodSelect�ϥΡC<br>
	 * �ǤJ�Ѽƶ��u�]�t�������S�x�����W�١C<br>
	 * �إߤ@�ӼȮɪ��B�J�ɨ�DataHandleŪ�J�A�H�K�����L�k�ϥΪ��S�x
	 *
	 * @param remove ���������}�C
	 * @param outFile ��X�B�J�ɮ�
	 * @throws IOException
	 */
	public static void buildAutoRemove(ArrayList<String> remove, String outFile) throws IOException {
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){
			for(String s: remove) {
				bw.write(s + ",,RemoveFeature");
				bw.newLine();
			}
		}
	}
	
	/**
	 * �ഫ�D�ƭ����
	 * @param step ���ഫ���}�C
	 * @param outFile ��X�B�J�ɮ�
	 * @throws Exception
	 */
	public static void buildAutoStep(ArrayList<ArrayList<String>> step, String outFile) throws Exception { // �O�_�n��3�_
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))) {
			for(String s: step.get(0)) {
				bw.write(s + ",null,TargetEncoding");
				bw.newLine();
			}
			for(String s: step.get(1)) {
				bw.write(s + ",null,NormalizeFeature");
				bw.newLine();
			}
			for(String s: step.get(2)) {
				bw.write(s + ",null,RemoveRecord");
				bw.newLine();
			}
		}
	}
	
	/**
	 * ���禡��FeatureSelect�ϥΡC<br>
	 * �ǤJ�Ѽƶ��u�]�t�������S�x�����W�١C<br>
	 * �]�S�x����i��|�������A�ҥH�ݭn���s�վ�B�J�C<br>
	 * ��w�g�������S�x�������B�J�����C
	 * @param inFile ��J�ɮרB�J
	 * @param remove ���������}�C
	 * @param outFile ��X�B�J�ɮ�
	 * @throws IOException
	 */
	public static void rearrange(String inFile, ArrayList<String> remove, String outFile) throws IOException {
		List<String> steps = Files.readAllLines(Paths.get(inFile), Charset.defaultCharset());
		if(steps.isEmpty()) {
			return;//�Y��J�B�J�ɨS������B�J�A���ק�B�J��
		}
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){
			String[] split;
			for(String step: steps) {
				split = step.split(",");
				if(!remove.contains(split[0])) {// �p�G�����S�x�}�C���]�t�ӦW��
					bw.write(step);				// �h��X�ӨB�J
					bw.newLine();
				}
			}	
		}
	}
	
	private JFileChooser chooser;
	/**
	 * 
	 * @param desktopPath �ୱ���|
	 */
	public Step(String desktopPath) {
		chooser = new JFileChooser(desktopPath);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("Step Files", "step"));
	}
	
	/**
	 * �}�ҹ�ܮإH�x�s�B�J�ܥت��a
	 * @param steps
	 * @throws IOException
	 */
	public void saveWithDialog(Vector<Tool> steps) throws IOException {
		int choose = chooser.showSaveDialog(null);
		if (choose == JFileChooser.APPROVE_OPTION) {
			String saveFile = chooser.getSelectedFile().getAbsolutePath();
			if(!saveFile.endsWith(".step")) {
				saveFile += ".step";
			}
			saveAll(steps, saveFile);
		}
	}
	
	/**
	 * �x�s�Ҧ��B�J�ܨB�J��
	 * @param steps
	 * @param saveFile
	 * @throws IOException
	 */
	public void saveAll(Vector<Tool> steps, String saveFile) throws IOException {
		String title, content, funcName;
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile))){
			for(Tool tool: steps) {
				title = tool.getTitle();
				content = tool.getContent();
				funcName = tool.getClass().getSimpleName();
				bw.write(String.join(",", title, content, funcName));
				bw.newLine();
			}
		}
	}
	
	/**
	 * �}�ҹ�ܮإH���J�B�J
	 * @param tool
	 * @return ���J���B�J
	 * @throws IOException
	 */
	public Optional<Vector<Tool>> loadWithDialog(AbstractTools tool) throws IOException {
		int choose = chooser.showOpenDialog(null);
		if (choose == JFileChooser.APPROVE_OPTION) {
			return Optional.ofNullable(loadAll(tool, chooser.getSelectedFile().getAbsolutePath()));
		}
		return Optional.empty();
	}
	
	/**
	 * ���J�Ҧ��B�J
	 * @param tools
	 * @param loadFile
	 * @return ���J���B�J
	 * @throws IOException
	 */
	public Vector<Tool> loadAll(AbstractTools tools, String loadFile) throws IOException {
		Vector<Tool> allStep = new Vector<>();
		String step;
		String[] split;
		try(BufferedReader br = new BufferedReader(new FileReader(loadFile))){
			while((step = br.readLine()) != null) {
		    	split = step.split(",");
				switch(split[2]) {	// funcName
					case "ExtractRecord":
					case "RemoveRecord":
					case "NormalizeFeature":
					case "RemoveFeature":
					case "RemoveOutlier":
					case "RemoveOutlierBySD":
					case "StandardizeFeature":
					case "TargetEncoding":
						allStep.add(tools.newInstance(split[2], split[0], split[1]));
						break;
					default:
						System.err.println("�S���ӨB�J!");
				}
		    }	
		}
		return allStep;
	}
}
