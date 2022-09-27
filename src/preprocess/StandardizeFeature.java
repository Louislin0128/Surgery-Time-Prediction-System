package preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class StandardizeFeature {
	/**����{@link StandardizeFeature#StandardizeFeature StandardizeFeature}*/
	public static void exec(String inTrain, String target, String outTrain, String... test) throws Exception {
		new StandardizeFeature(inTrain, target, outTrain, test);
	}
	
	/**
	 * �зǤ�
	 * @param inTrain ��J�V�m��
	 * @param target �ؼ����
	 * @param outTrain ��X�V�m�� 
	 * @param test ���ն��}�C(�i��)
	 * @throws Exception
	 */
	private StandardizeFeature(String inTrain, String target, String outTrain, String... test) throws Exception{
		ArrayList<Double> numAL = new ArrayList<Double>();	//�ؼ����ƭ� �}�C
		ArrayList<String> inputSplit;
		String input, str;	//�����Ϊ������
		int index;
		try(BufferedReader br = new BufferedReader(new FileReader(inTrain))){//Ū��
			inputSplit = Split.withQuotes(br.readLine());	//���μ��D�C | �C����Ƥ�����Ϋ᪺���G
			index = inputSplit.indexOf(target);	//���ഫ����ഫ�����ޭ�
			if(index == -1) {	// �Y�䤣����ഫ���
				throw new Exception("�䤣��z��J�����ഫ���s���u" + target + "�v�A�Э��s�T�{");
			}
			
			while((input = br.readLine()) != null) {	//�P�_�O�_�w���ɮ׵���
				inputSplit = Split.withQuotes(input);
				str = inputSplit.get(index);
				if(!str.isEmpty()){
					numAL.add(Double.valueOf(str));
				}
			}
		}
		
		HashMap<Double, String> zScoreHM = standardize(numAL);	//�NoriginalHashMap�зǤƫ᪺HashMap
		// �V�m�� | ���ն� ��X�J�ɦW
		Queue<String> file = new LinkedList<>();
		file.offer(inTrain);
		file.offer(outTrain);
		for(String s: test) {	//�[�J��J���ն�
			file.offer(s);
		}
		
		StringBuilder outStr = new StringBuilder();
		while(!file.isEmpty()) {
			try(BufferedReader br = new BufferedReader(new FileReader(file.poll()))){ 	//������
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(file.poll()))){	//�ɮ� �л\�ɮ�
				bw.write(br.readLine());//�Ĥ@�C���D�C
				while((input = br.readLine()) != null) {	//�P�_�O�_�w���ɮ׵���
					inputSplit = Split.withQuotes(input);
					for(int j = 0, size = inputSplit.size(); j < size; j++) {
						str = inputSplit.get(j);
						if(j == index && !str.isEmpty()) {	//�O���ഫ���B�D�ŴӤ~��X�ƭ�
							outStr.append(zScoreHM.getOrDefault(Double.valueOf(str), "0.0"));	//��X�Ƽƭ�
						}else {		//�_�h������X
							outStr.append(str);
						}
						if(j != size - 1) {
							outStr.append(",");
						}
					}
					bw.newLine();
					bw.write(outStr.toString());
					outStr.setLength(0);
				}	
			}
			}
		}
	}
	
	/**�p��зǮt*/
	private HashMap<Double, String> standardize(ArrayList<Double> numAL) {
		double average = numAL.stream().mapToDouble(Double::doubleValue).average().getAsDouble();	//������
		double sum = numAL.stream().mapToDouble(eachNumber -> Math.pow((eachNumber - average), 2)).sum();	//(�C�ӭ�-������)������M
		double sd = Math.sqrt(sum / numAL.size());	//�зǮt
		
		HashMap<Double, String> zScoreHM = new HashMap<Double, String>();
		Double zScore;
		BigDecimal bd;
		for(Double eachNumber: numAL) {
			zScore = Math.abs((eachNumber - average) / sd);
			bd = new BigDecimal(zScore.toString());
			zScoreHM.putIfAbsent(eachNumber, bd.toPlainString());
		}
		return zScoreHM;
	}
}