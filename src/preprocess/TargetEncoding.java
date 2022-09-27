package preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;

public class TargetEncoding {
	/**����{@link TargetEncoding#TargetEncoding TargetEncoding}*/
	public static void exec(String inTrain, String target, String refer, String outTrain, String... test) throws Exception {
		new TargetEncoding(inTrain, target, refer, outTrain, test);
	}
	
	/**
	 * �ƭȤ�
	 * @param inTrain ��J�V�m��
	 * @param target �ؼ����
	 * @param refer �ѷ����
	 * @param outTrain ��X�V�m�� 
	 * @param test ���ն��}�C(�i��)
	 * @throws Exception
	 */
	private TargetEncoding(String inTrain, String target, String refer, String outTrain, String... test) throws Exception {
		//refer = reference | numer = numeralization
		HashMap<String, String> numerHM;
		ArrayList<String> inputSplit;
		String input, referStr, str;	//�����Ϊ������ | �ѷӤ�r | ���X��r
		int index, referIndex;
		try(BufferedReader br = new BufferedReader(new FileReader(inTrain))) {//Ū�� ������
			inputSplit = Split.withQuotes(br.readLine());	//���μ��D�C | �C����Ƥ�����Ϋ᪺���G
			index = inputSplit.indexOf(target);		//�N�ഫ����ഫ�����ޭ�
			if(index == -1) {
				throw new Exception("�䤣��z��J�����ഫ���s���u" + target + "�v�A�Э��s�T�{");
			}
			referIndex = inputSplit.indexOf(refer);	//�ѷ���쪺����
			if(referIndex == -1) {
				throw new Exception("�䤣��z��J���ѷ����s���u" + refer + "�v�A�Э��s�T�{");
			}
			
			HashMap<String, ArrayList<Double>> referNumHM = new HashMap<String, ArrayList<Double>>();
			//�ؼ��ഫ���<�W��, �����ƭȰ}�C(����׼ƭ�)>
			double referNum = 0.0;	//�ѷ���쪺�Ʀr
			while((input = br.readLine()) != null) {//�P�_�O�_�w���ɮ׵���
				inputSplit = Split.withQuotes(input);
				referStr = inputSplit.get(referIndex);
				if(!referStr.isEmpty()) {	//�p�G�ѷ���줣����... �_�h������C���
					referNum = Double.parseDouble(referStr);	//�ഫ
					str = inputSplit.get(index);
					if(!str.isEmpty()) {	//�Y���ഫ��쬰�ūh����
						referNumHM.putIfAbsent(str, new ArrayList<Double>());	//���ؼ����W�� �Y�S���ӦW�� �[�J�s���}�C
						referNumHM.get(str).add(referNum);		//�N�ѷ����Ʀr�[�J
					}
				}
			}
			numerHM = numeralize(referNumHM);	//�NoriginalHashMap�ƭȤƫ᪺HashMap
		}
		
		// �V�m�� | ���ն� ��X�J�ɦW
		Queue<String> file = new LinkedList<>();
		file.offer(inTrain);
		file.offer(outTrain);
		for(String s: test) {	//�[�J��J���ն�
			file.offer(s);
		}
		
		// �V�m�� | ���ն� ��X�ɦW
		StringBuilder outStr = new StringBuilder();
		while(!file.isEmpty()) {
			try(BufferedReader br = new BufferedReader(new FileReader(file.poll()))) { 	//������
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(file.poll()))) {	//�ɮ� �л\�ɮ�
				bw.write(br.readLine());					//�Ĥ@�C���D�C
				while((input = br.readLine()) != null) {	//�P�_�O�_�w���ɮ׵���
					inputSplit = Split.withQuotes(input);
					
					for(int i = 0, size = inputSplit.size(); i < size; i++) {
						str = inputSplit.get(i);
						if(i == index) {		//�p�G�O���ഫ���
							if(str.isEmpty()) {	//�Y���Ů�
								outStr.append("0.0");	//��X0
							}else {	//�Y�D�Ů�
								outStr.append(numerHM.getOrDefault(str, "0.0"));	//��X�ƭȤƼƭ�
							}
						}else {	//�p�G���O���ഫ���
							outStr.append(str);	//������X
						}
						if(i != size - 1) {
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
	
	private HashMap<String, String> numeralize(HashMap<String, ArrayList<Double>> referNumHM) { //�}�C�Ҧs�Ʀr ����/�W�v
		double average = 0.0;	//������
		Double numerNumber;
		BigDecimal bd;
		HashMap<String, String> numerHM = new HashMap<String, String>(referNumHM.size());
		//�NoriginalHashMap�ƭȤƫ᪺HashMap
		
		for(Entry<String, ArrayList<Double>> e: referNumHM.entrySet()) {
			average = e.getValue().stream().mapToDouble(Double::doubleValue).average().getAsDouble();
			numerNumber = average / e.getValue().size();//������/�}�C�j�p
			bd = new BigDecimal(numerNumber.toString());
			numerHM.put(e.getKey(), bd.toPlainString());
		}
		return numerHM;
	}
}
