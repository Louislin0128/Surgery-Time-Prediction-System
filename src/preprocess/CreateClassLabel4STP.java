package preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateClassLabel4STP {
	/**����{@link CreateClassLabel4STP#CreateClassLabel4STP CreateClassLabel4STP}*/
	public static void exec(String inFile, String outFile) throws Exception {
		new CreateClassLabel4STP(inFile, outFile);
	}
	
	/**
	 * �p���N�ɶ�
	 * @param inFile ��J�ɮ�
	 * @param outFile ��X�ɮ�
	 * @throws Exception
	 */
	private CreateClassLabel4STP(String inFile, String outFile) throws Exception {
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){//Ū��
			ArrayList<String> inputSplit;	//�C����Ƥ�����Ϋ᪺���G
			StringBuilder outStr = new StringBuilder();
			int[] spendTimeSplit;	//��N�ɶ� �ɻP��
			int spendTime = 0;		//��N�ɶ�(��)
			
			String input = br.readLine();	//�����Ϊ������
			int spendTimeIndex = Split.withQuotes(input).indexOf("��N�ɶ��]��:���^");	//���N�ɶ�����
			outStr.append(input).append(",��N�ɶ��]���^");	//�ɤW���D
			
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){	//�g��
			String spendTimeStr;
			while((input = br.readLine()) != null) {	//�P�_�O�_�w���ɮ׵���
				inputSplit = Split.withQuotes(input);	//�ɦ^����Ũä���
				
				spendTimeStr = inputSplit.get(spendTimeIndex);
				if(spendTimeStr.contains(":")) {	//�Y��N�ɶ���]�t�u:�v�h�ഫ
					spendTimeSplit = Arrays.stream(spendTimeStr.split(":")).mapToInt(Integer::parseInt).toArray();
					//�H:�����j�Ÿ� �N��N�ɶ����ɻP�� ���}
					spendTime = spendTimeSplit[0] * 60 + spendTimeSplit[1];
				}else spendTime = 0;	//�_�h������X0
				
				outStr.append('\n').append(input).append(",").append(spendTime);
				bw.write(outStr.toString());
				outStr.setLength(0);
			}	
		}
		}
	}
}