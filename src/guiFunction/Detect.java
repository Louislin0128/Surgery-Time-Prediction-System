package guiFunction;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import preprocess.Split;

/**��������ݩʬ��ƭȩΤ@���r*/
public class Detect {
	/**
	 * ���O�ƭȡAtrue ; �O�ƭȡAfalse
	 * @param inFile ��J�ɦW
	 * @throws Exception
	 */
	public static boolean[] digits(String inFile) throws Exception {
		ArrayList<String> firstLine;
		String input;
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){
			input = br.readLine();	//���L���D�C
			input = br.readLine();
			firstLine = Split.withoutQuotes(input);	//�Ĥ@�C�s��
		}
		int size = firstLine.size();
		boolean[] isDigit = new boolean[size];
		for(int i = 0; i < size; i++) {
			isDigit[i] = isDigit(firstLine.get(i));
		}
		return isDigit;
	}
	
	/**�PŪ���ȬO�_���ƭ�*/
	private static boolean isDigit(String str) {
		if(str.isEmpty()) {
			return false;
		}
		for (char c: str.toCharArray()) {
			//�p�G�����줣�O�ƭ�("�J�줣�O0~9���r��")�A�^��false-->���F��
           	if (!Character.isDigit(c))
               	return false;
        }
        return true;
	}
}
