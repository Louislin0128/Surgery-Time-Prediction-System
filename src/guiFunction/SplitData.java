package guiFunction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**�����V�m���δ��ն�*/
public class SplitData {
	/**
	 * �H���w�����Φʤ���A�p���������
	 * @param inFile ��J�ɮ�
	 * @param rangeStart ��J�_�I
	 * @param rangeFinal ��X���I
	 * @param dataSize ����`����(���]�t���D)
	 * @param trainOutFile �V�m����X���|
	 * @param testOutFile ���ն���X���|
	 * @throws Exception
	 */
	public static void byPercent(String inFile, double rangeStart, double rangeFinal, double dataSize, String trainOutFile, String testOutFile) throws Exception {
		int dataStart = (int) Math.round(dataSize * (rangeStart / 100)); // ��Ƥ��ΰ_�l��}
		int dataFinal = (int) Math.round(dataSize * (rangeFinal / 100)); // ��Ƥ��γ̫��}
		split(inFile, dataStart, dataFinal, trainOutFile, testOutFile);
	}
	
	/**
	 * �������w��������
	 * @param inFile ��J�ɮ�
	 * @param rangeStart ��J�_�I
	 * @param rangeFinal ��X���I
	 * @param trainOutFile �V�m����X���|
	 * @param testOutFile ���ն���X���|
	 * @throws Exception
	 */
	public static void byQuantity(String inFile, int rangeStart, int rangeFinal, String trainOutFile, String testOutFile) throws Exception {
		split(inFile, rangeStart, rangeFinal, trainOutFile, testOutFile);
	}
	
	/**
	 * �ھڵ��w����Ƥ��θ��
	 * @param inFile ��J�ɮ�
	 * @param dataStart �}�l����
	 * @param dataFinal ��������
	 * @param trainOutFile �V�m����X���|
	 * @param testOutFile ���ն���X���|
	 * @throws Exception
	 */
	private static void split(String inFile, int dataStart, int dataFinal, String trainOutFile, String testOutFile) throws Exception {
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){ 			// ����
		try(BufferedWriter train = new BufferedWriter(new FileWriter(trainOutFile))){ 	// train�ɮ� �л\�ɮ�
		try(BufferedWriter test = new BufferedWriter(new FileWriter(testOutFile))){ 	// test�ɮ� �л\�ɮ�
			String input = br.readLine(); // ���D | �����Ϊ������
			train.write(input);
			test.write(input);
			
			int num = 1;
			while (((input = br.readLine()) != null)) {
				if ((num >= dataStart) && (num <= dataFinal)) { // �ھڰ_�l�P�̲צ�m�N��Ƽg�J
					test.newLine();
					test.write(input);
				} else {
					train.newLine();
					train.write(input);
				}
				num++;
			}	
		}
		}
		}
	}
}
