package preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MergeFeature {
	/**����{@link MergeFeature#MergeFeature MergeFeature}*/
	public static void exec(String inFile, String monthPath, String doctorPath, String outFile) throws Exception {
		new MergeFeature(inFile, monthPath, doctorPath, outFile);
	}
	
	/**
	 * �X�֦~�����
	 * @param inFile ��J�ɦW
	 * @param monthPath �����ؿ�
	 * @param doctorPath ��v�ؿ�
	 * @param outFile ��X�ɦW
	 * @throws Exception
	 */
	private MergeFeature(String inFile, String monthPath, String doctorPath, String outFile) throws Exception {
		try(BufferedReader readYearFile = new BufferedReader(new FileReader(inFile))){	//�~�����ɦW | Ū���~����
			Year Year = new Year(monthPath, doctorPath, Split.withQuotes(readYearFile.readLine()));//�~������| | ��v�~����| | �������|
		try(BufferedWriter writeFile = new BufferedWriter(new FileWriter(outFile))){//�g�J�~����
			writeFile.write(Year.wholeTitle);
			String yearInput;		//�~���� �����Ϊ������
			while((yearInput = readYearFile.readLine()) != null) {		//Ū�~���� �P�_�O�_�w���ɮ׵���
				writeFile.newLine();
				writeFile.write(Year.createWholeLine(yearInput));			
			}
		}
		}
	}
	
	private class Year{
		final String wholeTitle;	//�X�ֳ��� ���D�C
		final int divisionIndex;	//�~������O�W�� ���ޭ�
		final int dateIndex;		//�~������N��� ���ޭ�
		final int numberIndex;		//�~�����f�����X ���ޭ�
		final int operationIndex;	//�~������N�W�� ���ޭ�
		final int doctorIndex;		//�~������v�m�W ���ޭ�
		String division;			//�~���� ��O�W��
		String date;				//�~���� ��N���
		String number;				//�~���� �f�����X
		String operation;			//�~���� ��N�W��
		String doctorName;			//�~���� ��v�m�W
		StringBuilder line = new StringBuilder();
		ArrayList<String> inputSplit;
		
		final Doctor doctor;
		final Month month;
		
		public Year(String monthPath, String doctorPath, ArrayList<String> yearTitle) throws Exception {
			doctor = new Doctor(doctorPath);
			month = new Month(monthPath);
			
			divisionIndex = yearTitle.indexOf("��O");		//�~������O�W�� ���ޭ�
			dateIndex = yearTitle.indexOf("��N��");			//�~������N��� ���ޭ�
			numberIndex = yearTitle.indexOf("�f����");		//�~�����f�����X ���ޭ�
			operationIndex = yearTitle.indexOf("��N�W��");	//�~������N�W�� ���ޭ�
			doctorIndex = yearTitle.indexOf("�D�v��v");		//�~������v�m�W ���ޭ�
			for(int i = 0; i < yearTitle.size(); i++) {
				line.append(yearTitle.get(i));
				if(i == doctorIndex) {		//�b��v�m�W���ǥ[�W�u��v�~��(��)�v��
					line.append(",��v�~��]��^");
				}
				line.append(",");
			}
			line.append(month.title);		//��X�������D
			wholeTitle = line.toString();
		}
		
		private String createWholeLine(String yearLine) throws Exception {
			inputSplit = Split.withQuotes(yearLine);			//�~���� �C����Ƥ�����Ϋ᪺���G �s�JyearInputSplit
			division = inputSplit.get(divisionIndex);	//�~���� ��O
			division = (division.equals("OBS")) ? "GYN" : division;	//��OBS����ƴN�hGYN��Ƨ���
			date = inputSplit.get(dateIndex);			//�~���� ��N���
			number = inputSplit.get(numberIndex);		//�~���� �f�����X
			operation = inputSplit.get(operationIndex);	//�~���� ��N�W��
			doctorName = inputSplit.get(doctorIndex);		//�~���� ��v�m�W
			
			line.setLength(0);	//�M�Ŧr��
			for(int i = 0; i < inputSplit.size(); i++) {
				line.append(inputSplit.get(i));	//��X�~����
				if(i == doctorIndex) {	//���X�D�v��v
					line.append(",").append(doctor.countSeniority(doctorName, date));	//�s�a��X��v���~��(���G��)
				}
				line.append(",");
			}
			line.append(month.findLine(division, date, number, operation));
			
			return line.toString();
		}
	}
	
	private class Doctor{
		final File[] files;		//��v�~��
		int nameIndex = 0;				//��v�~�ꪺ��v�m�W ���ޭ�
		int onBoardDateIndex = 0;		//��v�~�ꪺ��|��� ���ޭ�
		String input;					//��v�~�� �����Ϊ������
		String seniority;				//��v���~��
		String onBoardDate;				//��v�~�ꪺ��|���
		LocalDate oldDate;				//��v�~�ꪺ��|���
		LocalDate newDate;				//�~������N���
		Period period;					//�~��϶�
		ArrayList<String> inputSplit;	//��v�~�� �C����Ƥ�����Ϋ᪺���G
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		
		public Doctor(String doctorPath) throws Exception {
			files = new File(doctorPath).listFiles();	//�إ���v�~�ꪺ�Ҧ���ƲM�� ��K�M��			
		}
		
		private String countSeniority(String yearDoctor, String yearDate) throws Exception {
			if(yearDoctor.isEmpty() || yearDate.isEmpty()) {//��v�m�W�M��N������n���Ȥ~�p����v�~��
				return "";
			}
			
			for(File eachFile: files) {				//�M����v�~���ɮ�
				try(BufferedReader readDoctorFile = new BufferedReader(new FileReader(eachFile))){ //Ū����v�~��
					inputSplit = Split.withQuotes(readDoctorFile.readLine());	//������v�~����D�C
					nameIndex = inputSplit.indexOf("�m�W");					//��v�~�ꪺ��v�m�W ���ޭ�
					onBoardDateIndex = inputSplit.indexOf("��|��");			//��v�~�ꪺ��v��|��� ���ޭ�
					
					while((input = readDoctorFile.readLine()) != null) {		//Ū��v�~�� �P�_�O�_�w���ɮ׵���
						inputSplit = Split.withQuotes(input);					//����doctorInput �s�JdoctorInputSplit
						if(inputSplit.get(nameIndex).equals(yearDoctor)) {		//�Y��v�~�ꪺ��v�m�W�P�~�����ǰt
							onBoardDate = inputSplit.get(onBoardDateIndex);		//��v�~�� ��|���(�~-��-��)			
							oldDate = LocalDate.parse(onBoardDate, formatter);
					        newDate = LocalDate.parse(yearDate, formatter);
					        period = Period.between(oldDate, newDate);			//�p��}�l���~�������(���]�A�������)�C
				        	seniority = String.valueOf(period.toTotalMonths());
				        	readDoctorFile.close(); 	//�N�ɮ׸�Ƭy����
							return seniority;	//�Y���\�t�� ���X��v�~��j�M�j�� ���Χ�F
						}
					}	
				}
			}
			return "";	//�䤣����v�����~��h��X�ŭ�
		}
	}
	
	private class Month {
		final String title;	//�������D		
		final String path;
		final String titleDelimiter;	//�������D���j��
		final ArrayList<File> csvFiles;
		int dateIndex;		//�~������N��� ���ޭ�
		int numberIndex;	//�~�����f�����X ���ޭ�
		int operationIndex;	//�~������N�W�� ���ޭ�
		String date;		//�~������N���
		String number;		//�~�����f�����X
		String operation;	//�~������N�W��
		int[] doctorIndexs = new int[6];			//�������v����
		int[] operationIndexs = new int[4];	//������N�N�X����
				
		ArrayList<String> inputSplit;		//�C����Ƥ�����Ϋ᪺���G
		String input;						//����� �����Ϊ������
		String[] yDateSplit;
		StringBuilder mInputFile = new StringBuilder();
		
		public Month(String monthPath) throws Exception {
			this.path = monthPath;
			FileFilter monthFilter = pathname -> {
				String name = pathname.getName();
				return pathname.isDirectory() &&
					   !name.equals("Seniority") &&
					   !name.equals("Yearly Report") &&
					   !name.equals("Drg");
			};
			File[] monthFolder = new File(monthPath).listFiles(monthFilter);	//�u�`���]�t������ɮת���Ƨ�
			csvFiles = listAllFiles(monthFolder);
			
			String[] titles;
			try(BufferedReader readMonthFile = new BufferedReader(new FileReader(csvFiles.get(0)))){//Ū���Ĥ@�Ӥ����
				//�������D�|���إߡA��Ū���������D
				titles = readMonthFile.readLine().split(",");
			}
			
			int length = titles.length;
			for(int i = 0, d = 0, o = 0; i < length; i++) {
				if(titles[i].equals("��N��")) {
					dateIndex = i;
				}
				if(titles[i].equals("�f����")) {
					numberIndex = i;
				}
				if(titles[i].equals("��N�W��")) {
					operationIndex = i;
				}
				if(titles[i].contains("��v")) {
					doctorIndexs[d++] = i;
				}
				if(titles[i].startsWith("��N�N�X")) {
					operationIndexs[o++] = i;
				}
			}
			title = String.join(",", titles) + ",��v�H��,��N�ƶq";
			titleDelimiter = ",".repeat(length + 1);	//�������D
		}
		
		private String findLine(String yearDivision, String yearDate, String yearRecordNumber, String yearOperation) throws Exception {
			if(yearDate.isEmpty() || yearRecordNumber.isEmpty() || yearOperation.isEmpty()) {	
				//�u�n���@�~�����N����B�f�����X�Τ�N�W�٬��ŭȫh�����䤣��
				return titleDelimiter;
			}
			if(yearDivision.isEmpty()) {	//�Y�~�����O�O�ŭ� ��Ҧ�������L�@�M
				for(File eachCSVfile: csvFiles) {
					try(BufferedReader readMonthFile = new BufferedReader(new FileReader(eachCSVfile))){ //Ū�������
						input = readMonthFile.readLine();	//���L�������D
						while((input = readMonthFile.readLine()) != null) {	//Ū����� �P�_�O�_�w���ɮ׵���
							inputSplit = Split.withQuotes(input);
							date = inputSplit.get(dateIndex);
							number = inputSplit.get(numberIndex);
							operation = inputSplit.get(operationIndex);
							
							if(date.equals(yearDate) &&					//�Y�����Ӧ�]�t�~�����N���
							   number.equals(yearRecordNumber) &&		//�Y�����Ӧ�]�t�~����f�����X
							   operation.equals(yearOperation)) {		//�Y�����Ӧ�]�t�~�����N�W��
								return count(inputSplit);	//�Y���\�t�� ���X�����j�M�j�� ���Χ�F
							}
						}	
					}
				}
				//�p�G�䤣������������ ��^�u�A�v
				return titleDelimiter;
			}
			
			//�p�G�~�����O�B��N����B�f�����X�M��N�W�ٳ����ȡA�h�i��@�몺�j�M
			yDateSplit = yearDate.split("/");	//�~���� �������
			mInputFile.setLength(0);
			mInputFile.append(path)
						  .append("\\")
						  .append(yearDivision)
						  .append("\\WFServlet_")
						  .append(yDateSplit[0])
						  .append(yDateSplit[1])
						  .append(".csv");
			//�إߤ������|�HŪ�������
			try(BufferedReader readMonthFile = new BufferedReader(new FileReader(mInputFile.toString()))){ 	//Ū�������
				input = readMonthFile.readLine();		//���L�������D�C
				while((input = readMonthFile.readLine()) != null) {	//Ū����� �P�_�O�_�w���ɮ׵���
					inputSplit = Split.withQuotes(input);
					date = inputSplit.get(dateIndex);
					number = inputSplit.get(numberIndex);
					operation = inputSplit.get(operationIndex);
					
					if(date.equals(yearDate) &&					//�Y�����Ӧ�]�t�~�����N���
					   number.equals(yearRecordNumber) &&			//�Y�����Ӧ�]�t�~����f�����X
					   operation.equals(yearOperation)) {			//�Y�����Ӧ�]�t�~�����N�W��
						readMonthFile.close();		//�N�ɮ׸�Ƭy����
						return count(inputSplit);	//�Y���\�t�� ���X�����j�M�j�� ���Χ�F
					}
				}	
			}
			//�p�G�䤣������������ ��^�u�A�v
			return titleDelimiter;
		}
		
		/**�p����v�H�ƤΤ�N�ƶq*/
		private String count(ArrayList<String> inputSplit) {
			int doctorCount = 0, operationCount = 0;
			String str;
			for(int i: doctorIndexs) {
				str = inputSplit.get(i);
				if(!str.isEmpty()) {
					doctorCount++;
				}
			}
			
			for(int i: operationIndexs) {
				str = inputSplit.get(i);
				if(!str.isEmpty()) {
					operationCount++;
				}
			}
			return String.join(",", inputSplit) + "," + doctorCount + "," + operationCount;
		}
		
		/**�C�|�ɮ�*/
		private ArrayList<File> listAllFiles(File[] folder) {
			ArrayList<File> files = new ArrayList<>();
			for(File eachfile: folder) {
				if(eachfile.isDirectory()) {
					files.addAll(listAllFiles(eachfile.listFiles()));
				}else {
					files.add(eachfile);
				}
			}
			return files;
		}
	}
}