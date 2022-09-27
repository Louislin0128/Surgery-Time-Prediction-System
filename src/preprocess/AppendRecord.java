package preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AppendRecord {
	/**����{@link AppendRecord#AppendRecord AppendRecord}*/
	public static void exec(String[] inFile, String outFile) throws Exception {
		new AppendRecord(inFile, outFile);
	}
	
	/**
	 * ����AppendRecord
	 * @param inFile ��J�ɮ�
	 * @param outFile ��X�ɮ�
	 * @throws Exception
	 */
	private AppendRecord(String[] inFile, String outFile) throws Exception {
		Files.copy(Paths.get(inFile[0]), Paths.get(outFile), StandardCopyOption.REPLACE_EXISTING);
		int length = inFile.length;
		if(length == 1) {	//�Y�u���@�ӿ�J�ɮ�
			return;			//��������
		}
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile, true))){
			String input;
			for(int i = 1; i < length; i++) {
				try(BufferedReader br = new BufferedReader(new FileReader(inFile[i]))) {
					input = br.readLine();//���L���D�C
					while((input = br.readLine()) != null) {
						bw.newLine();
						bw.write(input);
					}
				}
			}
		}
	}
}