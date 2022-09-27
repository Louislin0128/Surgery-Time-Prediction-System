package preprocess;
import java.util.ArrayList;
import java.util.Vector;

public final class Split {	
	public static ArrayList<String> withQuotes(String input) {		//�����Ϊ������
		return split(input, true);
	}
	
	public static ArrayList<String> withoutQuotes(String input) {	//�����Ϊ������
		return split(input, false);
	}
	
	public static String[] withoutQuotesOfArray(String input) {
		ArrayList<String> inputSplit = split(input, false);
		return inputSplit.toArray(new String[inputSplit.size()]);
	}
	
	public static Vector<String> withoutQuotesOfVector(String input) {
		return new Vector<String>(split(input, false));
	}
	
	private static ArrayList<String> split(String input, boolean withQuotes) {
		boolean inQuotes = false;						//�P�_�Y�r���O�_�b���޸���
		StringBuilder tempString = new StringBuilder();	//�s��v�r���P�_�Ҧ걵���r��
		ArrayList<String> inputSplit = new ArrayList<String>();	//�C����Ƥ�����Ϋ᪺���G
		
		for (char c: input.toCharArray()) {	//�Ninput�ഫ���r���}�C�ȩ̀�Ū�X
			if(c == ',' && !inQuotes) {
				inputSplit.add(tempString.toString());
			    tempString.setLength(0);	//�NtempString�M��
			}else if(c == '\"') {			//���޸�
				inQuotes = !inQuotes;
				if(withQuotes) {
					tempString.append('\"');	//�ɦ^"
				}
			}else{
				tempString.append(c);		//��L�r���[��tempString��
			}
		}
		inputSplit.add(tempString.toString());
		return inputSplit;
	}
}
