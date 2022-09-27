package preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

public class RemoveOutlier {//�¾K anes
	/**����{@link RemoveOutlier#RemoveOutlier RemoveOutlier}*/
	public static void exec(String inFile, String remove, String outFile) throws Exception {
		new RemoveOutlier(inFile, remove, outFile);
	}
	
	/**
	 * �������s��
	 * @param inFile ��J�ɮ�
	 * @param target �����ؼ����
	 * @param outFile ��X�ɮ�
	 * @throws Exception
	 */
	private RemoveOutlier(String inFile, String target, String outFile) throws Exception {
		ArrayList<String> inputSplit;
		int index;
		double number = 0.0;	//�ƭ�
		String input, str;			//�����Ϊ������
		ArrayList<Double> numberAL = new ArrayList<Double>();	//�ƭȰ}�C �p��|�����
		
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){	//Ū�� ������
			inputSplit = Split.withQuotes(br.readLine());	//���μ��D�C | �C����Ƥ�����Ϋ᪺���G
			index = inputSplit.indexOf(target);		//�ѷ�������
			if(index == -1) {
				throw new Exception("�䤣��z��J�����ഫ���s���u" + target + "�v�A�Э��s�T�{");
			}
			
			while((input = br.readLine()) != null) {	//�P�_�O�_�w���ɮ׵���
				inputSplit = Split.withQuotes(input);
				str = inputSplit.get(index);
				if(!str.isEmpty()) {
					number = Double.parseDouble(str);	//�ƭ�
					numberAL.add(number);
				}
			}	
		}
		
		double[] outlier = countOutlier(numberAL);	//�s��p&�j���s��
		System.out.printf("| �p�⵲�G | �̤p���s�ȡG%f | �̤j���s�ȡG%f |\n", outlier[0], outlier[1]);
		
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){ //������
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){//�g�� �л\�ɮ�
			bw.write(br.readLine());					//�Ĥ@�C���D�C		
			while((input = br.readLine()) != null) {	//�P�_�O�_�w���ɮ׵���
				inputSplit = Split.withQuotes(input);
				str = inputSplit.get(index);
				if(!str.isEmpty()) {	//�Y�ؼ����D�Ť~�i��P�_
					number = Double.parseDouble(str);
					if(number < outlier[0] || number > outlier[1]) { //outlier[0](minOutlier) <= number <= outlier[1](maxOutlier) �n��X
						continue;	//�Y���b�d�򤺤���X
					}
				}
				bw.newLine();
				bw.write(input);
			}	
		}
		}
	}
	
	private double[] countOutlier(ArrayList<Double> numberAL){
		double size = numberAL.size();
		double Q1Index = size * 0.25;	//firstQuartileIndex
		double Q3Index = size * 0.75;	//thirdQuartileIndex
		double Q1 = 0.0;				//firstQuartile
		double Q3 = 0.0;				//thirdQuartile
		double IQR = 0.0;
		double maxOutlier = 0.0;
		double minOutlier = 0.0;
		
		Collections.sort(numberAL);	//�p��j�ƦC
		if(Double.toString(Q1Index).contains(".0")) {	//�YfirstQuartileIndex�����
			Q1 = (numberAL.get((int) Q1Index - 1) + numberAL.get((int) Q1Index)) / 2;
		}else {	//�YfirstQuartileIndex�������
			Q1 = numberAL.get((int) (Math.ceil(Q1Index)) - 1);	//�L����i��
		}
		if(Double.toString(Q3Index).contains(".0")) {	//�YthirdQuartileIndex�����
			Q3 = (numberAL.get((int) Q3Index - 1) + numberAL.get((int) Q3Index)) / 2;
		}else {	//�YthirdQuartileIndex�������
			Q3 = numberAL.get((int) (Math.ceil(Q3Index)) - 1);	//�L����i��
		}
		IQR = Q3 - Q1;
		minOutlier = Q1 - (IQR * 1.5);
		maxOutlier = Q3 + (IQR * 1.5);
		
		return new double[] {minOutlier, maxOutlier};
	}
}