package preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateClassLabel4ATP {
	/**����{@link CreateClassLabel4ATP#CreateClassLabel4ATP CreateClassLabel4ATP}*/
	public static void exec(String inFile, String outFile) throws Exception {
		new CreateClassLabel4ATP(inFile, outFile);
	}
	
	/**
	 * �p��¾K�ɶ�
	 * @param inFile ��J�ɮ�
	 * @param outFile ��X�ɮ�
	 * @throws Exception
	 */
	private CreateClassLabel4ATP(String inFile, String outFile) throws Exception {
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){
			ArrayList<String> inputSplit;//�C����Ƥ�����Ϋ᪺���G
			StringBuilder outStr = new StringBuilder();
			int[] anaesStartSplit;		//�¾K�}�l �ɻP��
			int[] anaesEndSplit;		//�¾K���� �ɻP��
			int[] spendTimeSplit;		//��N�ɶ� �ɻP��
			int anaesSpendTime = 0;		//�¾K�ɶ�
			
			String input = br.readLine();	//�����Ϊ������
			inputSplit = Split.withQuotes(input);
			int anaesStartIndex = inputSplit.indexOf("�¾K�}�l");	//��¾K�}�l����
			int anaesEndIndex = inputSplit.indexOf("�¾K����");	//��¾K��������
			int spendTimeIndex = inputSplit.indexOf("��N�ɶ��]��:���^");	//���N�ɶ�����
			outStr.append(input).append(",�¾K�ɶ��]���^");		//�ɤW���D
			
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){
			String anaesStartStr, anaesEndStr, spendTimeStr;
			while((input = br.readLine()) != null) {	//�P�_�O�_�w���ɮ׵���
				inputSplit = Split.withQuotes(input);	//�ɦ^����Ũä���
				
				anaesStartStr = inputSplit.get(anaesStartIndex);
				anaesEndStr = inputSplit.get(anaesEndIndex);
				spendTimeStr = inputSplit.get(spendTimeIndex);
				if(!anaesStartStr.isEmpty() &&	// �Y��N�}�l�ɶ�������
				   !anaesEndStr.isEmpty() &&	// �Y��N�����ɶ�������
				   !spendTimeStr.isEmpty()) {	// �Y��N�ɶ�������
					anaesStartSplit = Arrays.stream(anaesStartStr.split(":")).mapToInt(Integer::parseInt).toArray();	//�N�¾K�}�l���Ψ��ର��ư}�C �ɻP��
					anaesEndSplit = Arrays.stream(anaesEndStr.split(":")).mapToInt(Integer::parseInt).toArray();		//�N�¾K�������Ψ��ର��ư}�C �ɻP��
					spendTimeSplit = Arrays.stream(spendTimeStr.split(":")).mapToInt(Integer::parseInt).toArray();		//�N��N�ɶ����Ψ��ର��ư}�C �ɻP��
					
					if((anaesStartSplit[0] > anaesEndSplit[0]) || ((anaesStartSplit[0] == anaesEndSplit[0]) && (anaesStartSplit[1] > anaesEndSplit[1]))) {
						//�p�G�}�l�ɶ�(��)>�����ɶ�(��) �� (�}�l�ɶ�(��)���󵲧��ɶ�(��) �� �}�l�ɶ�(��)>�����ɶ�(��))
				    	anaesEndSplit[0] += 24;	//�����ɶ�(��)+24
					}
	
					anaesSpendTime = (anaesEndSplit[0] * 60 + anaesEndSplit[1]) - (anaesStartSplit[0] * 60 + anaesStartSplit[1]);
					if((spendTimeSplit[0] * 60 + spendTimeSplit[1]) > anaesSpendTime){
						anaesSpendTime += 1440;		//�Y��N�ɶ��j��¾K�ɶ��A�[1440��
					}
				}else anaesSpendTime = 0;			
				
				outStr.append('\n').append(input).append(",").append(anaesSpendTime);
				bw.write(outStr.toString());
				outStr.setLength(0);
			}	
		}
		}
	}
}