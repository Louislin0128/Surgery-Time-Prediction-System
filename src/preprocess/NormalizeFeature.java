package preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class NormalizeFeature {
	/**����{@link NormalizeFeature#NormalizeFeature NormalizeFeature}*/
	public static void exec(String inTrain, String target, String outTrain, String... test) throws Exception {
		new NormalizeFeature(inTrain, target, outTrain, test);
	}
	
	/**
	 * ���W��
	 * @param inTrain ��J�V�m��
	 * @param target �ؼ����
	 * @param outTrain ��X�V�m�� 
	 * @param test ���ն��}�C(�i��)
	 * @throws Exception
	 */
	private NormalizeFeature(String inTrain, String target, String outTrain, String... test) throws Exception {
		ArrayList<String> inputSplit;	//�C����Ƥ�����Ϋ᪺���G
		String input, str;	//�����Ϊ������
		double max = Double.MIN_VALUE, min = Double.MAX_VALUE, value;
		int index;	//���ഫ����ഫ�����ޭ�
		try(BufferedReader br = new BufferedReader(new FileReader(inTrain))){	//Ū�����
			input = br.readLine();	//Ū�����D�C
			inputSplit = Split.withQuotes(input);	//���μ��D�C | �C����Ƥ�����Ϋ᪺���G
			index = inputSplit.indexOf(target);	
			if(index == -1) {	// �Y�䤣����ഫ���
				throw new Exception("�b��J�ɧ䤣��z��J�����ഫ���s���u" + target + "�v�A�Э��s�T�{");
			}
			
			while((input = br.readLine()) != null) {//�P�_�O�_�w���ɮ׵���
				inputSplit = Split.withQuotes(input);
				str = inputSplit.get(index);
				if(!str.isEmpty()) {
					value = Double.parseDouble(str);
					max = (max < value) ? value : max;
					min = (min > value) ? value : min;	
				}
			}	
		}
		double deviation = max - min;	//�̤j�̤p�Ȫ��t��
		if(deviation == 0.0) {
			throw new Exception(target + "��쪺�t�Ȭ�0�A�L�k���͵��G�C");
		}
		
		// �V�m�� | ���ն� ��X�J�ɦW
		Queue<String> file = new LinkedList<>();
		file.offer(inTrain);
		file.offer(outTrain);
		for(String s: test) {	//�[�J��J���ն�
			file.offer(s);
		}
		
		StringBuilder outStr = new StringBuilder();
		BigDecimal bd;
		Double normNumber;
		while(!file.isEmpty()) {
			try(BufferedReader br = new BufferedReader(new FileReader(file.poll()))){ 	//������
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(file.poll()))){	//�ɮ� �л\�ɮ�
				bw.write(br.readLine());					//�Ĥ@�C���D�C
				while((input = br.readLine()) != null) {	//�P�_�O�_�w���ɮ׵���
					inputSplit = Split.withQuotes(input);
					for(int j = 0, size = inputSplit.size(); j < size; j++) {
						str = inputSplit.get(j);
						if(j == index && !str.isEmpty()) {	//�O���ഫ���B�D�ŴӤ~��X�ƭ�
							value = Double.parseDouble(str);
							normNumber = (value - min) / deviation;
							bd = new BigDecimal(normNumber.toString());
							outStr.append(bd.toPlainString());	//��X�Ƽƭ�
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
}