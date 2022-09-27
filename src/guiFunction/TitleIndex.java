package guiFunction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**���D�s������*/
public class TitleIndex {
	/**
	 * �����D�s��<br>
	 * �Y��Ӥw�g�����D�s���A�h�|���s�s��
	 * @param inFile ��J�ɮ�
	 * @param outFile ��X�ɮ�
	 * @throws Exception
	 */
	public static void append(String inFile, String outFile) throws Exception {
		rearrage(new File(inFile), outFile, true);
	}
	
	/**
	 * �����D�s��<br>
	 * �Y��Ӥw�g�����D�s���A�h�|���s�s��
	 * @param inFile ��J�ɮ�
	 * @param outFile ��X�ɮ�
	 * @throws Exception
	 */
	public static void append(File inFile, String outFile) throws Exception {
		rearrage(inFile, outFile, true);
	}
	
	/**
	 * �������D�s��
	 * @param inFile ��J�ɮ�
	 * @param outFile ��X�ɮ�
	 * @throws Exception
	 */
	public static void remove(String inFile, String outFile) throws Exception {
		rearrage(new File(inFile), outFile, false);
	}
	
	/**
	 * �����D�s���β������D�s��
	 * @param inFile ��J�ɮ�
	 * @param outFile ��X�ɮ�
	 * @param add true�G�����D�s���Ffalse�G�������D�s��
	 * @throws Exception
	 */
	private static void rearrage(File inFile, String outFile, boolean add) throws Exception {
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))) {
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))) {
			if(add) {	//�b�C�Ӽ��D��[�W�渹
				bw.write(rearrangeTitle(splitInput(br.readLine()))); 		// ��X���ܫ᪺���D
			}else {		//�u��X���D(���]�t�渹)
				bw.write(String.join(",", splitInput(br.readLine())));
			}
			String input;
			while ((input = br.readLine()) != null) { 	// ��X�Ѿl���e
				bw.newLine();
				bw.write(input);
			}	
		}
		}
	}
	
	/**�����O�M�Ϊ����Φr��禡*/
	private static ArrayList<String> splitInput(String input) {	//�����Ϊ������
		boolean inQuotes = false;								//�P�_�Y�r���O�_�b���޸���
		boolean inBrackets = false;								//�P�_�Y�r��O�_�b�A����
		StringBuilder tempString = new StringBuilder();			//�s��v�r���P�_�Ҧ걵���r��
		ArrayList<String> inputSplit = new ArrayList<String>();	//�C����Ƥ�����Ϋ᪺���G
		for (char c: input.toCharArray()) {	//�Ninput�ഫ���r���}�C�ȩ̀�Ū�X
			if(c == ',' && !inQuotes) {
				inputSplit.add(tempString.toString());
			    tempString.setLength(0);	//�NtempString�M��
			}else if(c == '\"') {			//���޸�
				tempString.append(c);		//�ɦ^"
				inQuotes = !inQuotes;
			}else if(c == '(' || c == ')') {
				inBrackets = !inBrackets;
			}else if(!inBrackets) {
				tempString.append(c);		//��L�r���[��tempString��
			}
		}
		inputSplit.add(tempString.toString());
		return inputSplit;
	}
	
	/**�N���ޭ�(�渹)�ର��W*/
	private static String rearrangeTitle(ArrayList<String> title) {
		String indexName;
		StringBuilder newTitle = new StringBuilder();
		for (int i = 0, size = title.size(); i < size; i++) {
			indexName = "";
			for (int index = i; index >= 0; index = index / 26 - 1) {
				indexName = (char)((char)(index % 26) + 'A') + indexName;
	        }
			
			newTitle.append(title.get(i))
					.append('(')
					.append(indexName)
					.append(')');
			if(i != size - 1) {
				newTitle.append(',');
			}		
		}
		return newTitle.toString();
	}
}
