package preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MergeDRG {
	/**����{@link MergeDRG#MergeDRG MergeDRG}*/
	public static void exec(String yearFile, String drgPath, String outFile) throws Exception {
		new MergeDRG(yearFile, drgPath, outFile);
	}
	
	/**
	 * �X��DRG�ɮ�
	 * @param yearFile �~(��)�����ɦW
	 * @param drgPath DRG�ɦW�����|
	 * @param outFile ��X�ɦW
	 * @throws Exception
	 */
	private MergeDRG(String yearFile, String drgPath, String outFile) throws Exception {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		ArrayList<Object[]> drgList = new ArrayList<>();
		ArrayList<String> drgSplit;
		String drgInput;
		Object[] drg;
		int inDateIndex, outDateIndex, drgIndex, drgNumberIndex, divisionIndex;	// �J�|��� | �X�|��� | DRG�s�� | �f���� | ��O
		for(File file: new File(drgPath).listFiles()) {
			try(BufferedReader readDRG = new BufferedReader(new FileReader(file))) {
				drgInput = readDRG.readLine();
				drgSplit = Split.withQuotes(drgInput);
				inDateIndex = drgSplit.indexOf("�J�|���");	//DRG�ɮת��J�|��� ���ޭ�
				outDateIndex = drgSplit.indexOf("�X�|���");	//DRG�ɮת��X�|��� ���ޭ�
				drgIndex = drgSplit.indexOf("DRG�s��");		//DRG�ɮת����X ���ޭ�
				drgNumberIndex = drgSplit.indexOf("�f����");	//DRG�ɮת��f�����X ���ޭ�
				divisionIndex = drgSplit.indexOf("��O");	//DRG�ɮת���O ���ޭ�
				
				while((drgInput = readDRG.readLine()) != null) {
					drgSplit = Split.withQuotes(drgInput);
					drg = new Object[5];
					drg[0] = LocalDate.parse(drgSplit.get(inDateIndex), formatter);
					drg[1] = LocalDate.parse(drgSplit.get(outDateIndex), formatter);		
					drg[2] = drgSplit.get(drgNumberIndex);
					drg[3] = drgSplit.get(divisionIndex);
					drg[4] = drgSplit.get(drgIndex);
					drgList.add(drg);
				}	
			}
		}
		
		try(BufferedReader readYear = new BufferedReader(new FileReader(yearFile))){
			String yearInput = readYear.readLine();
			ArrayList<String> yearSplit = Split.withQuotes(yearInput);
			int dateIndex = yearSplit.indexOf("��N��");
			int yearNumberIndex = yearSplit.indexOf("�f����");	//�~�����f�����X ���ޭ�
			int yeardivisionIndex = yearSplit.indexOf("��O");	//�~������O ���ޭ�
			
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){
			bw.write(yearInput + ",DRG�s��");
			LocalDate inDate, outDate, date;	// �J�|���(drg) | �X�|���(drg) | ��N��(year)
			String drgStr = "";
			while((yearInput = readYear.readLine()) != null) {
				yearSplit = Split.withQuotes(yearInput);
				date = LocalDate.parse(yearSplit.get(dateIndex), formatter);
				
				for(Object[] o: drgList) {
					inDate = (LocalDate) o[0];
					outDate = (LocalDate) o[1];
					if(((String) o[2]).equals(yearSplit.get(yearNumberIndex)) &&
					   ((String) o[3]).equals(yearSplit.get(yeardivisionIndex)) &&
					  ((date.isAfter(inDate) && date.isBefore(outDate)) || date.isEqual(inDate) || date.isEqual(outDate))) {				
					// DRG���f�����X����~�����f�����X &&
					// DRG����O����~������O &&
					// ((��N���J�|��� && ��N���X�|��e) || ��N�鵥��J�|�� || ��N�鵥��X�|��)
						drgStr = (String) o[4];
						break;
					}
				}
				bw.newLine();
				bw.write(yearInput + "," + drgStr);
				drgStr = "";
			}	
		}
		}
	}
}
