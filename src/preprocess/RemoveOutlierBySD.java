package preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class RemoveOutlierBySD {
	/**����{@link RemoveOutlierBySD#RemoveOutlierBySD RemoveOutlierBySD}*/
	public static void exec(String inFile, String remove, String multiple, String outFile) throws Exception {
		new RemoveOutlierBySD(inFile, remove, multiple, outFile);
	}
	
	/**
	 * @param inFile ��ƿ�J�ɦW
	 * @param target ����������
	 * @param multiple �зǮt����
	 * @param outFile ��ƿ�X�ɦW
	 * @throws Exception
	 */
	private RemoveOutlierBySD(String inFile, String target, String multiple, String outFile) throws Exception {
		ArrayList<Double> numAL = new ArrayList<Double>(); // �ؼ����ƭ� �}�C
		ArrayList<String> inputSplit;
		String input, str; // �����Ϊ������ | �������
		int index;
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){ // Ū��
			inputSplit = Split.withQuotes(br.readLine());	// ���μ��D�C | �C����Ƥ�����Ϋ᪺���G
			index = inputSplit.indexOf(target);	//�ѷ�������
			if(index == -1) {
				throw new Exception("�䤣��z��J�����ഫ���s���u" + target + "�v�A�Э��s�T�{");
			}
			
			while ((input = br.readLine()) != null) { // �P�_�O�_�w���ɮ׵���
				inputSplit = Split.withoutQuotes(input);
				str = inputSplit.get(index);
				if (!str.isEmpty()) {
					numAL.add(Double.valueOf(str));
				}
			}	
		}
		double[] outlier = countOutlier(numAL, Double.parseDouble(multiple));
		System.out.printf("| �p�⵲�G | �̤p���s�ȡG%f | �̤j���s�ȡG%f |\n", outlier[0], outlier[1]);
		
		double number;
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){	// ������
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){	// �ɮ� �л\�ɮ�
			bw.write(br.readLine());// �Ĥ@�C���D�C
			while ((input = br.readLine()) != null) { // �P�_�O�_�w���ɮ׵���
				inputSplit = Split.withoutQuotes(input);
				str = inputSplit.get(index);
				if(!str.isEmpty()) {
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
	
	/**
	 * �p�����s��
	 * @param numAL �ƭȰ}�C
	 * @param multiple �X�����зǮt
	 * @return �U�����s�� | �W�����s��
	 */
	private double[] countOutlier(ArrayList<Double> numAL, double multiple) {
		double average = numAL.stream().mapToDouble(Double::doubleValue).average().getAsDouble();	//������
		double sum = numAL.stream().mapToDouble(eachNumber -> Math.pow((eachNumber - average), 2)).sum();
		double sd = Math.sqrt(sum / numAL.size());	//�зǮt
		double adjustedSD = multiple * sd;
		return new double[] {average - adjustedSD, average + adjustedSD};
	}
}