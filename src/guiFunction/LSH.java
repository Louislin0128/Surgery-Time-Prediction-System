package guiFunction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedList;

import preprocess.Split;

/**�P�y�����������ާ@*/
public class LSH {
	/**
	 * �b�C���[�W�y����
	 * @param inFile ��J�ɦW
	 * @param outFile ��X�ɦW
	 * @throws Exception
	 */
	public static void append(String inFile, String outFile) throws Exception {
		append(new File(inFile), outFile);
	}
	
	/**
	 * �b�C���[�W�y����
	 * @param inFile ��J�ɦW
	 * @param outFile ��X�ɦW
	 * @throws Exception
	 */
	public static void append(File inFile, String outFile) throws Exception {
		try(LineNumberReader lnr = new LineNumberReader(new FileReader(inFile))){
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){
			String input = lnr.readLine();	// Ū�����D�C
			if(input.startsWith("�y����")) {	// �즳�y�������s�[�y����
				bw.write(input);	// ��X����D�C(�]�t�y����)
				while((input = lnr.readLine()) != null) {
					bw.newLine();	// ����
					bw.write((lnr.getLineNumber() - 1) + "," + removeFirstCol(input));// ��X�[�y����������
				}
			}else {
				bw.write("�y����," + input);	//��X���D�C
				while((input = lnr.readLine()) != null) {
					bw.newLine();	// ����
					bw.write((lnr.getLineNumber() - 1) + "," + input);	// ��X����+�y����
				}
			}	
		}
		}
	}
	/**
	 * �b�C�������y����
	 * @param inFile ��J�ɦW
	 * @param outFile ��X�ɦW
	 * @throws Exception
	 */
	public static void remove(String inFile, String outFile) throws Exception {
		remove(new File(inFile), outFile);
	}
	
	/**
	 * �b�C�������y����
	 * @param inFile ��J�ɦW
	 * @param outFile ��X�ɦW
	 * @throws Exception
	 */
	public static void remove(File inFile, String outFile) throws Exception {
		try(LineNumberReader lnr = new LineNumberReader(new FileReader(inFile))){
			String input = lnr.readLine();	// Ū�����D�C
			if(input.startsWith("�y����")) {
				try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){
					bw.write(removeFirstCol(input));	// ��X����D�C(���]�t�y����)
					while((input = lnr.readLine()) != null) {
						bw.newLine();	// ����
						bw.write(removeFirstCol(input));// ��X���[�y���������
					}	
				}
			}else {	//�S���y�����S���n�[�y�����A�N��J�ɪ����ƻs��ت��a
				Files.copy(inFile.toPath(), Paths.get(outFile), StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}
	
	/**
	 * �������檺�r��
	 * @param input ���B�z���r��
	 * @return �^�ǲ������檺�r��
	 */
	private static String removeFirstCol(String input) {
		return input.substring(input.indexOf(",") + 1);
	}
	
	/**
	 * �إ߬y�����ɮ�
	 * @param featureSelect �S�x���
	 * @param dataTransform �������
	 * @param outFile ��X�ɮ�
	 * @throws Exception
	 */
	public static void build(String featureSelect, String dataTransform, String outFile) throws Exception {	
		String fsLSH, dtLSH;				// �S�x��� �y���� | ������� �y����
		String fsLine, dtLine;				// �S�x��� ��C | ������� ��C
		ArrayList<String> fsSplit, dtSplit;	// �S�x��� ���� | ������� ����
		LinkedList<String> outLine = new LinkedList<>();
		ArrayList<Integer> keep = new ArrayList<>();
		
		try(BufferedReader fsReader = new BufferedReader(new FileReader(featureSelect))){// �S�x���
			fsLine = fsReader.readLine(); 		// Ū���S�x������D�C
			fsSplit = Split.withQuotes(fsLine);	// ���ίS�x������D�C	
		try(BufferedReader dtReader = new BufferedReader(new FileReader(dataTransform))){// �������
			dtLine = dtReader.readLine();		// Ū��������ɼ��D�C
			dtSplit = Split.withQuotes(dtLine);	// ���θ�����ɼ��D�C
			
			// �M��������ɼ��D�C�A�j�M�S�x������L�����C���h��X�Ӽ��D��A�å[�Jkeep(ArrayList)
			for(int i = 1, size = dtSplit.size(); i < size; i++) {	// �M�����]�t�y����
				if(fsSplit.contains(dtSplit.get(i))) {
					outLine.add(dtSplit.get(i));
					keep.add(i);
				}
			}
			fsLine = fsReader.readLine();		// Ū���S�x����Ĥ@�C
			fsSplit = Split.withQuotes(fsLine);	// ���ίS�x����Ĥ@�C
			fsLSH = fsSplit.get(0);				// ���o�S�x��� ����y����
			
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){ // ��X�ɮ� �л\�ɮ�
			bw.write(String.join(",", outLine));// ��X������ɼ��D�C
			outLine.clear();	// �M�ſ�X�Ȧs��
			
			while ((dtLine = dtReader.readLine()) != null) {	// Ū�������
				dtSplit = Split.withQuotes(dtLine);				// ����
				dtLSH = dtSplit.get(0);	// ���o������� ����y����
				
				if (dtLSH.equals(fsLSH)) {
					bw.newLine();
					for(int i = 1, size = dtSplit.size(); i < size; i++) {	// �M�����]�t�y����
						if(keep.contains(i)) {			// �Y�����b�O�d�W��h��X
							outLine.add(dtSplit.get(i));
						}
					}
					bw.write(String.join(",", outLine));// ��X�������
					outLine.clear();	// �M�ſ�X�Ȧs��
					
					if((fsLine = fsReader.readLine()) == null) break;	//�Y�S�x���Ū�������A���j�M�F
					fsSplit = Split.withQuotes(fsLine);
					fsLSH = fsSplit.get(0);	// ���o�S�x��� ����y����	
				}
			}	
		}
		}
		}
	}
}
