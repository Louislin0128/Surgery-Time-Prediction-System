package guiFunction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * �إߦU�ؤ��P���ɮ�<br><br>
 * toText�G��X�r��<br>
 * forWekaLoader�G���͵�CSVLoader���ɮ�<br>
 * buildOptions�G���U�D�����w�����s�ɡA�ݭn�N�Ҧ��ﶵ�g���@���ɮרѫ����ഫ<br>
 * forLSH�G�إ߬y�����ɮ�<br>
 * disOrganize�G���ø�ƶ�
 */
public class CreateFile {
	/**
	 * �ƻs�è��N
	 * @param inFile ��J�ɮ�
	 * @param outFile ��X�ɮ�
	 * @throws IOException
	 */
	public static void copy(String inFile, String outFile) throws IOException {
		Files.copy(Paths.get(inFile), Paths.get(outFile), StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * �ƻs�è��N
	 * @param inFile ��J�ɮ�
	 * @param outFile ��X�ɮ�
	 * @throws IOException
	 */
	public static void copy(Path inFile, String outFile) throws IOException {
		Files.copy(inFile, Paths.get(outFile), StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * �ƻs�è��N
	 * @param inFile ��J�ɮ�
	 * @param outFile ��X�ɮ�
	 * @throws IOException
	 */
	public static void copy(File inFile, String outFile) throws IOException {
		Files.copy(inFile.toPath(), Paths.get(outFile), StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * ��X�r��
	 * @param outFile ��X�ɮ�
	 * @param text ��r
	 * @throws IOException
	 */
	public static void toCSV(String outFile, String text) throws IOException {
		Files.writeString(Paths.get(outFile), text, Charset.defaultCharset(), StandardOpenOption.CREATE);
	}
	
	/**
	 * ���͵�CSVLoader���ɮ�
	 * @param inFile ��J�ɮ�
	 * @param outFile ��X�ɮ�
	 * @throws Exception
	 */
	public static void forWekaLoader(String inFile, String outFile) throws FileNotFoundException, IOException {
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){
			bw.write(br.readLine()); // �L�X���D
			String input;
			while ((input = br.readLine()) != null) {
				bw.write(addDoubleQuotes(input));
			}
		}
		}
	}
	/**�b��Ӧr��e��[�W"*/
	private static String addDoubleQuotes(String input) { 	// �b�C��Ҧ��ȫe��[�W" | �����Ϊ������
		boolean inQuotes = false; 					// �P�_�Y�r���O�_�b���޸���
		StringBuilder tempString = new StringBuilder("\n\""); // �s��v�r���P�_�Ҧ걵���r�� | ����ɦ^����Ť�"
		for (char c : input.toCharArray()) { 		// �Ninput�ഫ���r���}�C�ȩ̀�Ū�X
			if (c == ',' && !inQuotes)
				tempString.append("\",\"");
			else if (c == '\"')						// ���޸�
				inQuotes = !inQuotes;
			else
				tempString.append(c);				// ��L�r���[��tempString��
		}
		tempString.append('\"'); 					// �ɦ^"
		return tempString.toString();
	}
	
	/**
	 * ���U�D�����w�����s�ɡA�ݭn�N�Ҧ��ﶵ�g���@���ɮרѫ����ഫ
	 * @param title
	 * @param options
	 * @param outFile
	 * @throws IOException
	 */
	public static void buildOptions(String[] title, String[] options, String outFile) throws IOException {
		StringBuilder tempStr = new StringBuilder(String.join(",", title));
		tempStr.append(",�����ɶ�\n");
		for(String s: options) {
			if(s.contains(",")) {
				tempStr.append("\"").append(s).append("\""); // �g�J���e | �H"�]�Чt��,������
			}else {
				tempStr.append(s);
			}
			tempStr.append(",");
		}
		tempStr.append("0.0");
		toCSV(outFile, tempStr.toString());
	}
	
	private static SecureRandom random = new SecureRandom();
	/**
	 * ���ø�ƶ�
	 * @param inFile ���ƶ�
	 * @param number �ؤl�X
	 * @param outfile ���ø�ƶ����ɮ׸��|
	 * @throws Exception
	 */
	public static void disOrganize(String inFile, String number, String outfile) throws Exception {
		if (number != null && !number.isEmpty()) {
			random.setSeed(Long.parseLong(number)); // �]�w�ؤl�X
		}
		List<String> content;
		try(Stream<String> stream = Files.lines(Paths.get(inFile), Charset.defaultCharset())){
			content = stream.skip(1).toList();
		}
		String title;
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))) {
			title = br.readLine();
		}
		Collections.shuffle(content, random);
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile))) { // �g�J�ɮ�
			bw.write(title); // ��X���D
			for (String s: content) {
				bw.newLine();
				bw.write(s); // ��X���ë᪺���e
			}
		}
	}
}
