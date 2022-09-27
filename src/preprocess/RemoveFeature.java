package preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class RemoveFeature {
	/**
	 * ����{@link RemoveFeature#RemoveFeature RemoveFeature}
	 * @param remove ��������D�i�ܪ��װ}�C
	 */
	public static void exec(String inFile, String outFile, ArrayList<String> remove) throws Exception {
		new RemoveFeature(inFile, remove.toArray(new String[remove.size()]), outFile);
	}
	
	/**
	 * ����{@link RemoveFeature#RemoveFeature RemoveFeature}
	 * @param remove ��������D�i�ܪ��װ}�C
	 */
	public static void exec(String inFile, String outFile, String... remove) throws Exception {
		new RemoveFeature(inFile, remove, outFile);
	}
	
	/**
	 * �������
	 * @param inFile ��J�ɮ�
	 * @param remove ��������D�}�C
	 * @param outFile ��X�ɮ�
	 * @throws Exception
	 */
	private RemoveFeature(String inFile, String[] remove, String outFile) throws Exception {
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){ 	//������
			String input = br.readLine();	//�Ĥ@�C���D�C | �����Ϊ������
			ArrayList<String> inputSplit = Split.withQuotes(input);	//���μ��D�C | �C����Ƥ�����Ϋ᪺���G
			int length = remove.length;
			int[] index = new int[length];
			for(int i = 0; i < length; i++) {
				index[i] = inputSplit.indexOf(remove[i]);//�N���������̧��ഫ�����ޭ�
			}
			Arrays.sort(index);	// �ɱƧ�
			
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){	//��X�ɮ� �л\�ɮ�
			bw.write(outputLine(inputSplit, index));	//��X���D
			while((input = br.readLine()) != null) {	//�P�_�O�_�w���ɮ׵���
				inputSplit = Split.withQuotes(input);
				bw.newLine();
				bw.write(outputLine(inputSplit, index));
			}
		}	
		}
	}
	
	private String outputLine(ArrayList<String> inputSplit, int[] index) {
		StringBuilder tempString = new StringBuilder();
		int inputSize = inputSplit.size();
		int dynamic = inputSize - 1;	//�w�]���S�������̫᪺��� | �C��˼ƲĤ@�Ӥ��[","
		int indexLength = index.length;
		if(index[indexLength -1] == inputSize - 1) {	//�Y�ϥΪ̱������̫᪺���
			dynamic -= 1;	//���վ�� | �C��˼ƲĤG�Ӥ��[","
		}
		int n = 0;
		for(int i = 0; i < inputSize; i++) {
			if(indexLength > n && i == index[n]) {
				n += 1;
			}else {		//�p�G���D���w������� ��X
				tempString.append(inputSplit.get(i));
				if(i != dynamic) {
					tempString.append(",");
				}
			}
		}
		return tempString.toString();
	}
}
