package preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class RemoveRecord {
	/**����{@link RemoveRecord#RemoveRecord RemoveRecord}*/
	public static void exec(String inFile, String title, String content, String outFile) throws Exception {
		new RemoveRecord(inFile, title, content, outFile);
	}
	
	/**
	 * ��������
	 * @param inFile ��J�ɮ�
	 * @param target �ؼ����
	 * @param content �ؼЭ�
	 * @param outFile ��X�ɮ�
	 * @throws Exception
	 */
	private RemoveRecord(String inFile, String target, String content, String outFile) throws Exception {
		String specify = content; 	// ������Ʀr
		int mode = -1; 			// ���Ҧ�
		if (content.equalsIgnoreCase("NOTNULL")) { // �Y����r���D�ŭ�
			mode = 0;
		} else if (content.equalsIgnoreCase("NULL") || content.isEmpty()) { // �Y����r���ŭ�
			mode = 1;
		} else if (content.startsWith(">=")) {
			specify = content.substring(2);
			mode = 2;
		} else if (content.startsWith("<=")) {
			specify = content.substring(2);
			mode = 3;
		} else if (content.charAt(0) == '=') {
			specify = content.substring(1);
			mode = 4;
		} else if (content.charAt(0) == '>') {
			specify = content.substring(1);
			mode = 5;
		} else if (content.charAt(0) == '<') {
			specify = content.substring(1);
			mode = 6;
		}
		
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){ 	//������
			String input = br.readLine();	//���D | �����Ϊ������
			ArrayList<String> inputSplit = Split.withQuotes(input);	//���D���� | �C����Ƥ�����Ϋ᪺���G
			int index = inputSplit.indexOf(target);	//�ؼ�������(�C����ޭȱq0�}�l)
			
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){	//�ɮ� �л\�ɮ�
			bw.write(input);	//�g�J�Ĥ@�C���D�C
			while((input = br.readLine()) != null) {	//�P�_�O�_�w���ɮ׵���
				inputSplit = Split.withQuotes(input);
				if(compare(mode, inputSplit.get(index), specify)) {
					bw.newLine();
					bw.write(input);
				}
			}
		}
		}
	}
	
	private boolean compare(int mode, String content, String specify) {	//�j�M�Ҧ� | �ɮ׫��w��줺�e | ���w�r��
		switch (mode) {
		case 0: // NOTNULL
			if (content.isEmpty()) {
				return true;
			}
			break;
		case 1: // NULL
			if (!content.isEmpty()) {
				return true;
			}
			break;
		case 2: // >=
			if (content.isEmpty() || !(Double.parseDouble(content) >= Double.parseDouble(specify))) {
				return true;
			}
			break;
		case 3: // <=
			if (content.isEmpty() || !(Double.parseDouble(content) <= Double.parseDouble(specify))) {
				return true;
			}
			break;
		case 4: // =
			if (content.isEmpty() || !(Double.parseDouble(content) == Double.parseDouble(specify))) {
				return true;
			}
			break;
		case 5: // >
			if (content.isEmpty() || !(Double.parseDouble(content) > Double.parseDouble(specify))) {
				return true;
			}
			break;
		case 6: // <
			if (content.isEmpty() || !(Double.parseDouble(content) < Double.parseDouble(specify))) {
				return true;
			}
			break;
		default:
			if (content.isEmpty() || !content.equals(specify)) {
				return true;
			}
			break;
		}
		return false;
	}
}