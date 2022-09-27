package preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
//import java.util.TreeSet;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;

public class SummarizeData {
	/**����{@link SummarizeData#SummarizeData SummarizeData}*/
	public static void exec(String inFile, String seniorityPath, String outPath) throws Exception {
		new SummarizeData(inFile, seniorityPath, outPath);
	}
	
	private Collator collator = Collator.getInstance(Locale.TRADITIONAL_CHINESE);
	private Comparator<String> chComparator = (String s1, String s2) -> {
		return collator.compare(s1, s2);	//�����嵧�e�Ƨ�
	};
	private Table4_1 table4_1 = new Table4_1();
	private Table4_2 table4_2 = new Table4_2();
	private Table4_3 table4_3 = new Table4_3();
	private Table4_4 table4_4 = new Table4_4();
	private Table4_5 table4_5 = new Table4_5();
	private Table4_6 table4_6 = new Table4_6();
//	private Table4_7 table4_7 = new Table4_7();
	private TreeMap<String, TreeMap<String, ArrayList<Integer>>> deptTM = new TreeMap<String, TreeMap<String, ArrayList<Integer>>>(String.CASE_INSENSITIVE_ORDER);
	//���E�W�� | ��v�m�W | ��N�ɶ� | 4-2 5 6 �@�θ�� ���i����
	private TreeMap<String, ArrayList<Integer>> drTM = new TreeMap<String, ArrayList<Integer>>(chComparator);
	//��v�m�W | �U��v��O��N�ɶ�
	private double total = 0.0; //����`���� ���i����
	
	/**
	 * �p��έp��T�ÿ�X
	 * @param inFile ��J�ɮ�
	 * @param seniorityPath �~����|
	 * @param outPath ��X���|
	 * @throws Exception
	 */
	private SummarizeData(String inFile, String seniorityPath, String outPath) throws Exception {
		//surg = surgery ��N | dr = doctor ��v | en = English �^�� | ch = Chinese ����
		//dept = department ��O(����) | anaes = anaesthetization �¾K | seni = seniority �~��
		long startTime = System.currentTimeMillis();
		ArrayList<String> inputSplit;
		String input;			//�����Ϊ������
		int[] spendTimeSplit;	//��N�ɶ� �ɻP��
		Integer spendTime;		//�N��N�ɶ��ഫ����
		
		long processingTime = System.currentTimeMillis();	//�{�����椤�~ ������U�ɶ�
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){//������
			inputSplit = Split.withQuotes(br.readLine());		//�C����Ƥ�����Ϋ᪺���G | �Ĥ@�C���D�C
			int surgIndex = inputSplit.indexOf("��N�W��");		//��N�W�ٯ���
			int deptIndex = inputSplit.indexOf("��O");			//���E�W�ٯ���
			int drIndex = inputSplit.indexOf("�D�v��v");			//��v�m�W����
	//		int anaesIndex = inputSplit.indexOf("�¾K");			//�¾K��������
			int spendTimeIndex = inputSplit.indexOf("��N�ɶ��]��:���^");	//��N�ɶ�����
			
			String spendTimeStr, surgStr, deptStr, drStr;
			while((input = br.readLine()) != null) {	//�P�_�O�_�w���ɮ׵���
				inputSplit = Split.withQuotes(input);
				spendTimeStr = inputSplit.get(spendTimeIndex);
				if(spendTimeStr.contains(":")) {
					spendTimeSplit = Arrays.stream(spendTimeStr.split(":")).mapToInt(Integer::parseInt).toArray();	//�H:�����j�Ÿ� �N��N�ɶ����ɻP�� ���}
					spendTime = spendTimeSplit[0] * 60 + spendTimeSplit[1];
					
					surgStr = inputSplit.get(surgIndex);
					if(!surgStr.isEmpty()) {
						table4_1.buildData(surgStr, spendTime); 		//�إ߰򥻸�� ��N�W�� ��N�ɶ��ഫ
					}
					
					deptStr = inputSplit.get(deptIndex);
					drStr = inputSplit.get(drIndex);
					if(!deptStr.isEmpty() && !drStr.isEmpty()) {
						table4_2.buildData(deptStr, drStr, spendTime);	//�إ߰򥻸�� ���E�W�� ��v�m�W ��N�ɶ��ഫ | 4-2 5 6 �@�θ��
					}
					
					if(!drStr.isEmpty()) {
						table4_3.buildData(drStr, spendTime); 	//�إ߰򥻸�� ��v�m�W ��N�ɶ��ഫ | 4-3 4 �@�θ��
					}	
				}
	//			if(!inputSplit.get(deptIndex).isEmpty() && !inputSplit.get(anaesIndex).isEmpty()) {
	//				table4_7.buildData(inputSplit.get(deptIndex), inputSplit.get(anaesIndex));  		//�إ߰򥻸�� ���E�W�� �¾K����
	//			}
				
				inputSplit.clear(); //�C����j�X���r��}�C �M��
				total++; 		//����`���ƭp��			
			}	
		}
		
		
		Files.walkFileTree(Paths.get(seniorityPath), new SimpleFileVisitor<Path>() {	//�إ���v�~�ꪺ�Ҧ���ƲM�� ��K�M��
			private ArrayList<String> doctorTitle, inputSplit;	//��v�~����D�C | ���ΰ}�C
			private String input;
			private int drIndex, yearSeniIndex;
			
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				try(BufferedReader br = Files.newBufferedReader(file, Charset.defaultCharset())){//������
					doctorTitle = Split.withQuotes(br.readLine());		//������v�~����D�C
					drIndex = doctorTitle.indexOf("�m�W");			//��v�~�ꪺ��v�m�W ���ޭ� (�ܼƪu��)
					yearSeniIndex = doctorTitle.indexOf("�~��_�~");	//��v�~�ꪺ��v��|��� ���ޭ�
					
					while((input = br.readLine()) != null) {			//�P�_�O�_�w���ɮ׵���
						inputSplit = Split.withQuotes(input);
						table4_4.buildData(inputSplit.get(drIndex), Integer.valueOf(inputSplit.get(yearSeniIndex)));	//�إ߰򥻸�� ��v�m�W ��v�~��(�~)
					}	
				}
				return FileVisitResult.CONTINUE;
			}
		});
		System.out.printf("�B�z�P�x�s��� ��O�ɶ��G%d (ms)\n\n", System.currentTimeMillis() - processingTime);
		
		processingTime = System.currentTimeMillis();	//�{�����椤�~ ������U�ɶ�
		table4_1.create(outPath + "\\�U�e�f�Ħ檺��N������N�����ɶ�.csv");
		System.out.printf("��O�ɶ��G%d (ms)\n\n", System.currentTimeMillis() - processingTime);
		
		processingTime = System.currentTimeMillis();	//�{�����椤�~ ������U�ɶ�
		table4_2.create(outPath + "\\�U�줧��N�����ɶ�.csv");
		System.out.printf("��O�ɶ��G%d (ms)\n\n", System.currentTimeMillis() - processingTime);
		
		processingTime = System.currentTimeMillis();	//�{�����椤�~ ������U�ɶ�
		table4_3.create(outPath + "\\�U��v����N�����ɶ�.csv");
		System.out.printf("��O�ɶ��G%d (ms)\n\n", System.currentTimeMillis() - processingTime);
		
		processingTime = System.currentTimeMillis();	//�{�����椤�~ ������U�ɶ�
		table4_4.create(outPath + "\\�P��v�~�꦳������N�����ɶ�.csv");
		System.out.printf("��O�ɶ��G%d (ms)\n\n", System.currentTimeMillis() - processingTime);
		
		processingTime = System.currentTimeMillis();	//�{�����椤�~ ������U�ɶ�
		table4_5.create(outPath + "\\�U����v�O����N�����ɶ�.csv");
		System.out.printf("��O�ɶ��G%d (ms)\n\n", System.currentTimeMillis() - processingTime);
		
		processingTime = System.currentTimeMillis();	//�{�����椤�~ ������U�ɶ�
		table4_6.create(outPath + "\\�U��O��N�H����.csv");
		System.out.printf("��O�ɶ��G%d (ms)\n\n", System.currentTimeMillis() - processingTime);
		
//		processingTime = System.currentTimeMillis();	//�{�����椤�~ ������U�ɶ�
//		table4_7.create(outPath + "\\�U��O��N�Ħ�¾K��k�Τ�v.csv");
//		System.out.printf("��O�ɶ��G%d (ms)\n\n", System.currentTimeMillis() - processingTime);
		
		System.out.printf("�`��O�ɶ��G%d (ms)\n\n", System.currentTimeMillis() - startTime);
	}
	
	/**
	 * �Y��Ƶ��ƥe����`���ƪ��X%
	 * @param number �Y��Ƶ���
	 * @return %��
	 */
	private double percent(double number) {
		return number / total * 100;
	}
	
	/**
	 * �p�⥭��
	 * @param numberList �ƭȰ}�C
	 * @return ������
	 */
	private double average(ArrayList<Integer> numberList) {
		return numberList.stream().mapToInt(Integer::intValue).average().getAsDouble();
	}
	
	/**
	 * �p��зǮt
	 * @param numberList �ƭȰ}�C
	 * @param average ���ƭȰ}�C��������
	 * @return �зǮt
	 */
	private double sd(ArrayList<Integer> numberList, double average) {
		double sum = numberList.stream().mapToDouble(eachNumber -> Math.pow((eachNumber - average), 2)).sum();	
		return Math.sqrt(sum / numberList.size());
	}
	
	private class Table4_1 {
		private TreeMap<String, ArrayList<Integer>> surgTM = new TreeMap<String, ArrayList<Integer>>(String.CASE_INSENSITIVE_ORDER);
		//��N�W�� | ��N�ɶ� | ���Ҽ{�^��j�p�g�Ƨ�
		
		private void buildData(String surg, Integer time) {
			surgTM.putIfAbsent(surg, new ArrayList<Integer>());		//����N�W�� �Y�S���ӦW�� �[�J�s���}�C
			surgTM.get(surg).add(time);
		}
		
		private void create(String fileName) throws Exception {
			StringBuilder outStr = new StringBuilder("��N�W��,�̤p��,�̤j��,�����ɶ�,�зǮt,����");	
			ArrayList<Integer> surgSpendTime;
			double average;
			
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))){
				for(Entry<String, ArrayList<Integer>> surgTMEntry: surgTM.entrySet()) {
			    	surgSpendTime = surgTMEntry.getValue();
			    	//����Entry<String, ArrayList<Integer>>	�A����䤤��ArrayList<Integer> ��N�ɶ�
			    	average = average(surgSpendTime);
			    	
			    	outStr.append("\n")//�s�W�@����
			    				.append(surgTMEntry.getKey())
			    				.append(",")
			    				.append(Collections.min(surgSpendTime))
			    				.append(",")
			    				.append(Collections.max(surgSpendTime))
			    				.append(",")
			    				.append(Math.round(average * 100.0) / 100.0)
			    				.append(",")
			    				.append(Math.round(sd(surgSpendTime, average) * 100.0) / 100.0)
			    				.append(",")
			    				.append(surgSpendTime.size());
			    	bw.write(outStr.toString());
			    	outStr.setLength(0);
			    }
			}
			System.out.println("���\��X4-1���C");
		}
	}
	
	private class Table4_2 {
		private void buildData(String dept, String dr, Integer time) {	//�إߥD�n��� | ���E�W�� | ��v�m�W | ��N��O�ɶ�		
			deptTM.putIfAbsent(dept, new TreeMap<String, ArrayList<Integer>>(chComparator));	//�����E�W�� �L�Ӫ��E �إ���vTreeMap
			deptTM.get(dept).putIfAbsent(dr, new ArrayList<Integer>());	//�����ͩm�W �L����v �إߤ�N��O�ɶ��}�C		
			deptTM.get(dept).get(dr).add(time); 						//�N��N�ɶ��[�J
		}
		
		private void create(String fileName) throws Exception {
			StringBuilder outStr = new StringBuilder("���E�W��,�̤p��,�̤j��,�����ɶ�,�зǮt,����");
			ArrayList<Integer> deptSpendTime = new ArrayList<Integer>(); 		//�C�ӳ����Ҫ᪺��N�ɶ�
			double average;
			
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))){	//�ɮ� �л\�ɮ�
				for(Entry<String, TreeMap<String, ArrayList<Integer>>> deptTMEntry: deptTM.entrySet()) {	//departmentTreeMap�����N��
					deptTMEntry.getValue().values().forEach(deptSpendTime::addAll);
					//�N�U��O�U�C����v����N�ɶ��[�J�}�C
					average = average(deptSpendTime);
					
					outStr.append("\n")	//�s�W�@����
								.append(deptTMEntry.getKey())			//���E�W��
								.append(",")
								.append(Collections.min(deptSpendTime))	//�̤p��
								.append(",")
								.append(Collections.max(deptSpendTime))	//�̤j��
								.append(",")
								.append(Math.round(average * 100.0) / 100.0)	//������
								.append(",")
								.append(Math.round(sd(deptSpendTime, average) * 100.0) / 100.0)		//�зǮt
								.append(",")
								.append(deptSpendTime.size());			//����
					deptSpendTime.clear(); //�N���E�Ҫ᪺��N�ɶ��}�C�M��
					bw.write(outStr.toString());
			    	outStr.setLength(0);
				}
			}
			System.out.println("���\��X4-2���C");
		}
	}
	
	private class Table4_3 {
		private void buildData(String dr, Integer time) {
			drTM.putIfAbsent(dr, new ArrayList<Integer>());	//�����v�m�W �L�өm�W �إ���vTreeMap
			drTM.get(dr).add(time);
		}
		
		private void create(String fileName) throws Exception {
			StringBuilder outStr = new StringBuilder("��v�m�W,�̤p��,�̤j��,�����ɶ�,�зǮt,����");
			ArrayList<Integer> drSpendTime;
			double average;
			
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))){	//�ɮ� �л\�ɮ�
				for(Entry<String, ArrayList<Integer>> drTMEntry: drTM.entrySet()) {
					drSpendTime = drTMEntry.getValue();
					average = average(drSpendTime);
					
					outStr.append("\n")	//�s�W�@����
								.append(drTMEntry.getKey())				//��v�W��
								.append(",")
								.append(Collections.min(drSpendTime))	//�̤p��
								.append(",")
								.append(Collections.max(drSpendTime))	//�̤j��
								.append(",")
								.append(Math.round(average * 100.0) / 100.0)	//������
								.append(",")
								.append(Math.round(sd(drSpendTime, average) * 100.0) / 100.0)		//�зǮt
								.append(",")
								.append(drSpendTime.size());			//����
					bw.write(outStr.toString());
					outStr.setLength(0);
				}
			}
			System.out.println("���\��X4-3���C");
		}
	}
	
	private class Table4_4 {	
		private HashMap<String, Integer> drSeniHM = new HashMap<String, Integer>();	//��v�m�W | ��v�~��(�~)
		
		private void buildData(String dr, Integer yearSeni) {
			drSeniHM.putIfAbsent(dr, yearSeni);
		}
		
		private void create(String fileName) throws Exception {
			LinkedHashMap<String, ArrayList<Integer>> seniLHM = new LinkedHashMap<String, ArrayList<Integer>>(5);
			for(String eachKey: new String[] {"1-5�~", "6-10�~", "11-15�~", "16-20�~", "20�~�H�W"}) {
				seniLHM.put(eachKey, new ArrayList<Integer>());
			}
			Integer yearSeni;
			ArrayList<Integer> drSpendTime, seniSpendTime;
			for(Entry<String, ArrayList<Integer>> drTMEntry: drTM.entrySet()) {
				yearSeni = drSeniHM.get(drTMEntry.getKey());	//�Y��v���~��(�~)
				if(yearSeni != null) {							//�Y��v�~��HashMap��������ƦA�~��
					drSpendTime = drTMEntry.getValue();			//�Y��v���Ҧ���N�ɶ�
					if(yearSeni <= 5) 		seniLHM.get("1-5�~").addAll(drSpendTime);
					else if(yearSeni <= 10) seniLHM.get("6-10�~").addAll(drSpendTime);
					else if(yearSeni <= 15) seniLHM.get("11-15�~").addAll(drSpendTime);
					else if(yearSeni <= 20) seniLHM.get("16-20�~").addAll(drSpendTime);
					else 					seniLHM.get("20�~�H�W").addAll(drSpendTime);
				}
			}
			
			double average;
			StringBuilder outStr = new StringBuilder("��v�~��,�̤p��,�̤j��,�����ɶ�,�зǮt,��N����");
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))){	//�ɮ� �л\�ɮ�
				for(Entry<String, ArrayList<Integer>> seniLHMEntry: seniLHM.entrySet()) {
					seniSpendTime = seniLHMEntry.getValue();
					average = average(seniSpendTime);
					
					outStr.append("\n")	//�s�W�@����
								.append(seniLHMEntry.getKey())	//��v�~�����
								.append(",")
								.append(Collections.min(seniSpendTime))	//�̤p��
								.append(",")
								.append(Collections.max(seniSpendTime))	//�̤j��
								.append(",")
								.append(Math.round(average * 100.0) / 100.0)	//������
								.append(",")
								.append(Math.round(sd(seniSpendTime, average) * 100.0) / 100.0)		//�зǮt
								.append(",")
								.append(seniSpendTime.size());			//����				
					bw.write(outStr.toString());
					outStr.setLength(0);
				}	
			}
			System.out.println("���\��X4-4���C");
		}
	}
	
	private class Table4_5 {
		private void create(String fileName) throws Exception {
			StringBuilder outStr = new StringBuilder("��O,��v,�����ɶ�,�зǮt,����\n");
			ArrayList<Integer> drSpendTime;	//��N��O�ɶ��}�C
			double average;
			
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))){	//�ɮ� �л\�ɮ�
				for(Entry<String, TreeMap<String, ArrayList<Integer>>> deptTMEntry: deptTM.entrySet()) { //departmentTreeMap�����N��
					outStr.append(deptTMEntry.getKey()); 		//���E�W��
					
					for(Entry<String, ArrayList<Integer>> drTMEntry: deptTMEntry.getValue().entrySet()) {
						//������doctorTreeMap �A���쥦��entry �A���쭡�N��	| doctorTreeMap�����N��
						drSpendTime = drTMEntry.getValue();
						average = average(drSpendTime);
						
						outStr.append(",")
									.append(drTMEntry.getKey())		//��v�W��
									.append(",")
									.append(Math.round(average * 100.0) / 100.0)	//������
									.append(",")
									.append(Math.round(sd(drSpendTime, average) * 100.0) / 100.0)		//�зǮt
									.append(",")
									.append(drSpendTime.size())		//����
									.append("\n");	//�s�W�@����
						bw.write(outStr.toString());
						outStr.setLength(0);
					}
				}
			}
			System.out.println("���\��X4-5���C");
		}
	}
	
	private class Table4_6 {
		private void create(String fileName) throws Exception {
			double surgCount = 0.0;			//��N����
			StringBuilder outStr = new StringBuilder("��O,�H����,�ʤ���v(%)");
			
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))){	//�ɮ� �л\�ɮ�
				for(Entry<String, TreeMap<String, ArrayList<Integer>>> deptTMEntry: deptTM.entrySet()) {	//departmentTreeMap�����N��
					outStr.append("\n")	//�s�W�@����
								.append(deptTMEntry.getKey()); //���E�W��
					surgCount = deptTMEntry.getValue().values().stream().mapToDouble(e -> e.size()).sum();
					outStr.append(",")
								.append(surgCount)	//��v�W��
								.append(",")
								.append(Math.round(percent(surgCount) * 100.0) / 100.0);			//������
					bw.write(outStr.toString());
					outStr.setLength(0);
				}
			}
			System.out.println("���\��X4-6���C");
		}
	}
	
//	private class Table4_7 {
//		private TreeMap<String, HashMap<String, Integer>> deptTM = new TreeMap<String, HashMap<String, Integer>>(String.CASE_INSENSITIVE_ORDER);
//		//���E�W�� | �¾K�W�� | �¾K����
//		private TreeSet<String> anaesSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER); 	//�Ҧ��¾K����
//
//		private void buildData(String dept, String anaes) {				//���E�W�� | �¾K����		
//			deptTM.putIfAbsent(dept, new HashMap<String, Integer>());	//�����E�W�� �L�Ӫ��E �إ߳¾KHashMap
//			deptTM.get(dept).putIfAbsent(anaes, 0);						//�q���E�W�٧P�w���L�ӳ¾K���� | �Y�L���N���Ƴ]��0
//			deptTM.get(dept).put(anaes, deptTM.get(dept).get(anaes) + 1);
//			//�q���E�W�ٮ���S�w�¾K���� / �̾ڳ¾K�������䤤����N���� / �A��쥻�¾K�����䤤����N����+1
//			anaesSet.add(anaes);	//�N�¾K�����[�J������Set
//		}
//		
//		private void create(String fileName) throws Exception {
//			StringBuilder outStr = new StringBuilder();
//			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));	//�ɮ� �л\�ɮ�
//			outStr.append("�¾K��k,");
//			String[] anaes = anaesSet.toArray(new String[anaesSet.size()]);
//			for(int i = 0; i < anaes.length; i++) {
//				outStr.append(anaes[i]);
//				if(i != anaes.length - 1) {
//					outStr.append(",");
//				}		
//			}
//			outStr.append("\n��O,");
//			for(int i = 0; i < anaes.length; i++) {
//				outStr.append("%");
//				if(i != anaes.length - 1) {
//					outStr.append(",");
//				}
//			}
//			
//			for(Entry<String, HashMap<String, Integer>> deptTMEntry: deptTM.entrySet()) {
//				outStr.append("\n")	//�s�W�@����
//							.append(deptTMEntry.getKey())
//							.append(",");	//���E�W��
//				
//				for(int i = 0; i < anaes.length; i++) {
//					if(deptTMEntry.getValue().containsKey(anaes[i])) { //�P�_�Ӫ��E���¾K����hashMap�O�_�]�t�ӳ¾K�W��
//						outStr.append(Math.round(percent(deptTMEntry.getValue().get(anaes[i])) * 100.0) / 100.0);
//					}else {
//						outStr.append(0);
//					}
//					if(i != anaes.length - 1) {
//						outStr.append(",");
//					}
//				}
//				bw.write(outStr.toString());				
//				outStr.setLength(0);
//			}
//			bw.close();
//			System.out.println("���\��X4-7���C");
//		}
//	}
}