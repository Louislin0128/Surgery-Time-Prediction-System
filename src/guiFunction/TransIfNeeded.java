package guiFunction;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class TransIfNeeded {	
	/**
	 * �Y��N�ɶ��γ¾K�ɶ��������W�ƩμзǤ�<br>
	 * �N�w���ƭȵ�����C<br>
	 * <br>
	 * ����G�Y��P�ɰ����W�ƤμзǤƩΦh����<br>
	 * �ഫ�᪺�ƭȱN�|���ǽT<br>
	 * �ϥΪ����קK��<br>
	 * 
	 * @param stepFile �B�J��
	 * @param trainFile ��l�V�m�����
	 * @throws Exception
	 */
	public static TransIfNeeded transValue(String stepFile, String trainFile) throws Exception {
		try(BufferedReader br = new BufferedReader(new FileReader(stepFile))){ // �B�J�ɡAŪ��
			String input; 	// �����Ϊ������
			String[] step; 	// �B�J�C����
			while ((input = br.readLine()) != null) { // �P�_�O�_�w���ɮ׵���
				step = input.split(",");
				if ((step[0].startsWith("��N�ɶ�") || step[0].startsWith("�¾K�ɶ�"))) {
					switch(step[2]) {
					case "NormalizeFeature":
						System.err.println("���g�L���W�ơA���ഫ");
						return new TransNormalize(trainFile);
					case "StandardizeFeature":
						System.err.println("���g�L�зǤơA���ഫ");
						return new TransStandardize(trainFile);
					}
					// �N��᭱���N|�¾K�ɶ������W�ƩμзǤơA�]�L����
					// �]���]�L�k���`�ഫ�ƭ�
				}
			}
		}
		System.out.println("���g�L���W�ƩμзǤơA�����ഫ");
		return new TransIfNeeded();
	}
	
	/**��l��k �Y���ݭn�ഫ�h��X��l��*/
	public double get(double value) {
		return value;
	}
	
	/**�ഫ���A*/
	public String getType() {
		return "���ݸg�L�ഫ";
	}
}

class TransNormalize extends TransIfNeeded {
	private double max = Double.MIN_VALUE, min = Double.MAX_VALUE;
	/**
	 * �Y��N�ɶ��γ¾K�ɶ������W��<br>
	 * �N�w���ƭȵ��ϥ��W�ơC
	 * 
	 * @param trainFile
	 * @throws Exception
	 */
	protected TransNormalize(String trainFile) throws Exception {
		try(BufferedReader br = new BufferedReader(new FileReader(trainFile))){ //�����ɡAŪ��
			String str, input = br.readLine();	//���L���D�C
			double value;
			while((input = br.readLine()) != null) {	//�P�_�O�_�w���ɮ׵���
				str = input.substring(input.lastIndexOf(",") + 1);	//�̫�@�ӳr������m | ���o�ƭ�
				if(!str.isEmpty()) {
					value = Double.parseDouble(str);
					max = (max < value) ? value : max;	//�̤j��
					min = (min > value) ? value : min;	//�̤p��
				}
			}
		}
	}
	
	@Override
	public double get(double inValue) {
		return inValue * (max - min) + min;	//���ݭn�N�ƭȤϥ��W��
	}
	
	@Override
	public String getType() {
		return "�ϥ��W��";
	}
}

class TransStandardize extends TransIfNeeded {
	private double average, sd;
	protected TransStandardize(String trainFile) throws Exception {
		ArrayList<Double> numAL = new ArrayList<>(); // �ؼ����ƭ� �}�C
		
		try(BufferedReader br = new BufferedReader(new FileReader(trainFile))){ // �����ɡAŪ��
			String str, input = br.readLine();	//���L���D�C
			while ((input = br.readLine()) != null) { // �P�_�O�_�w���ɮ׵���
				str = input.substring(input.lastIndexOf(",") + 1); // �̫�@�ӳr������m | ���o�ƭ�
				if (!str.isEmpty()) {
					numAL.add(Double.valueOf(str));
				}
			}	
		}

		average = average(numAL); 	// ��������
		sd = sd(numAL, average); 	// ���зǮt
	}
	
	@Override
	public double get(double value) {
		return value * average + sd;	// ���ݭn�N�ƭȤϼзǤ�
	}
	
	@Override
	public String getType() {
		return "�ϼзǤ�";
	}
	
	/**
	 * �p��зǮt
	 * @param numberList �ƭȰ}�C
	 * @param average ������
	 * @return �зǮt
	 */
	private double sd(ArrayList<Double> numberList, double average) {
		//(�C�ӭ�-������)������M
		double sum = numberList.stream().mapToDouble(eachNumber -> Math.pow((eachNumber - average), 2)).sum();
		return Math.sqrt(sum / numberList.size());	//�зǮt
	}
	
	/**�p�⥭����*/
	private double average(ArrayList<Double> numberList) {
		return numberList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
	}
}

//package guiFunction;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.util.ArrayList;
//
//public class ReverseIfNeeded {
//	private Denormalize denormalize;
//	private Destandardize destandardize;
//	
//	/**
//	 * �ഫ��ڤιw���ɮ�<br>
//	 * <br>
//	 * �Y��N�ɶ��γ¾K�ɶ��������W�ƩμзǤ�<br>
//	 * �N�w���ƭȵ�����C<br>
//	 * ����G�Y��P�ɰ����W�ƤμзǤƩΦh����<br>
//	 * �ഫ�᪺�ƭȱN�|���ǽT<br>
//	 * �ϥΪ����קK��
//	 * 
//	 * @param stepFile �B�J��
//	 * @param trainFile ��l�V�m�����
//	 * @param inFile ��ڹw���ȿ�J�ɮ�
//	 * @param outFile ��ڹw�����ഫ���X�ɮ�
//	 * @throws Exception
//	 */
//	public ReverseIfNeeded(String stepFile, String trainFile, String inFile, String outFile) throws Exception {
//		this(stepFile, trainFile);	// �P�_�O�_�ݭn�i��ϥ��W�ƩΤϼзǤ�
//		BufferedReader br = new BufferedReader(new FileReader(inFile)); 	// ��ڹw���ȿ�J�ɮ�
//		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));	// ��ڹw�����ഫ���X�ɮ�
//		String input; 	// �����Ϊ������
//		String[] split;
//		double actual, predict, deviation;
//		bw.write("��ڭ�,�w����,�t��(��ڭ�-�w����)");
//		while((input = br.readLine()) != null) {
//			split = input.split(",");
//			actual = (split[0].isEmpty()) ? Double.NaN : get(Double.parseDouble(split[0]));
//			predict = (split[1].isEmpty()) ? Double.NaN : get(Double.parseDouble(split[1]));
//			deviation = (actual == Double.NaN || predict == Double.NaN) ? Double.NaN : actual - predict;
//			
//			bw.newLine();
//			bw.write((actual == Double.NaN ? "?" : actual) + "," +
//					 (predict == Double.NaN ? "?" : predict) + "," + 
//					 (deviation == Double.NaN ? "?" : deviation));
//		}
//		br.close();
//		bw.close();
//	}
//	
//	/**
//	 * �Y��N�ɶ��γ¾K�ɶ��������W�ƩμзǤ�<br>
//	 * �N�w���ƭȵ�����C<br>
//	 * <br>
//	 * ����G�Y��P�ɰ����W�ƤμзǤƩΦh����<br>
//	 * �ഫ�᪺�ƭȱN�|���ǽT<br>
//	 * �ϥΪ����קK��<br>
//	 * 
//	 * @param stepFile �B�J��
//	 * @param trainFile ��l�V�m�����
//	 * @throws Exception
//	 */
//	public ReverseIfNeeded(String stepFile, String trainFile) throws Exception {
//		BufferedReader br = new BufferedReader(new FileReader(stepFile)); // �B�J�ɡAŪ��
//		String input; 	// �����Ϊ������
//		String[] step; 	// �B�J�C����
//		while ((input = br.readLine()) != null) { // �P�_�O�_�w���ɮ׵���
//			step = input.split(",");
//			if ((step[0].startsWith("��N�ɶ�") || step[0].startsWith("�¾K�ɶ�"))) {
//				if(step[2].equals("NormalizeFeature")) {
//					System.out.println("�ݭn�ϥ��W��");
//					denormalize = new Denormalize(trainFile);
//					break;
//				}else if(step[2].equals("StandardizeFeature")) {
//					System.out.println("�ݭn�ϼзǤ�");
//					destandardize = new Destandardize(trainFile);
//					break;
//				}
//				// �N��᭱���N|�¾K�ɶ������W�ƩμзǤơA�]�L����
//				// �]���]�L�k���`�ഫ�ƭ�
//			}
//		}
//		br.close();
//	}
//	
//	public double get(double inValue) {
//		if(denormalize != null) {
//			return denormalize.get(inValue);
//		}else if(destandardize != null) {
//			return destandardize.get(inValue);
//		}
//		return inValue;
//	}
//	
//	public class Denormalize {
//		private double max, min;
//		/**
//		 * �Y��N�ɶ��γ¾K�ɶ������W��<br>
//		 * �N�w���ƭȵ��ϥ��W�ơC
//		 * 
//		 * @param stepFile
//		 * @param inFile
//		 * @param inValue
//		 * @throws Exception
//		 */
//		public Denormalize(String inFile) throws Exception{	
//			String input;
//			int last;		//�̫�@�ӳr������m
//			double findMax = Double.MIN_VALUE, findMin = Double.MAX_VALUE, value;
//			String valueStr;
//			BufferedReader br = new BufferedReader(new FileReader(inFile));  //�����ɡAŪ��
//			br.readLine();	//���L���D�C
//			while((input = br.readLine()) != null) {	//�P�_�O�_�w���ɮ׵���
//				last = input.lastIndexOf(",");
//				valueStr = input.substring(last + 1);	//���o�ƭ�
//				if(!valueStr.isEmpty()) {
//					value = Double.parseDouble(valueStr);
//					findMax = (findMax < value) ? value : findMax;
//					findMin = (findMin > value) ? value : findMin;
//				}
//			}
//			br.close(); 		//�N�ɮ׸�Ƭy����
//			max = findMax * 2;	//�N�̤j�ȴ���2��
//			min = findMin;		//�̤p�Ȥ���
//		}
//		
//		private double get(double inValue) {
//			return inValue * (max - min) + min;	//���ݭn�N�ƭȤϥ��W��
//		}
//	}
//	
//	private class Destandardize {
//		private double m, u;
//		public Destandardize(String inFile) throws Exception {
//			BufferedReader br = new BufferedReader(new FileReader(inFile)); // �����ɡAŪ��
//			br.readLine();	//���L���D�C
//			String input;
//			int last; 		// �̫�@�ӳr������m
//			ArrayList<Double> numAL = new ArrayList<>(); // �ؼ����ƭ� �}�C
//			String valueStr;
//			while ((input = br.readLine()) != null) { // �P�_�O�_�w���ɮ׵���
//				last = input.lastIndexOf(",");
//				valueStr = input.substring(last + 1); // ���o�ƭ�
//				if (!valueStr.isEmpty()) {
//					numAL.add(Double.parseDouble(valueStr));
//				}
//			}
//			br.close(); // �N�ɮ׸�Ƭy����
//
//			u = mean(numAL); 	// ��������
//			m = std(numAL, u); 	// ���зǮt
//		}
//		
//		private double get(double inValue) {
//			return inValue * m + u;	// ���ݭn�N�ƭȤϼзǤ�
//		}
//		
//		// �p��зǮt
//		private double std(ArrayList<Double> tot, double u) {
//			int n = tot.size();
//			double value = 0;
//			for (int i = 0; i < n; i++) {
//				value += Math.pow((tot.get(i) - u), 2); // (�C�ӭ�-������)�����誺�`�M
//			}
//			return Math.sqrt(value / n);
//		}
//	
//		// �p�⥭����
//		private double mean(ArrayList<Double> tot) {
//			return tot.stream().mapToDouble(d -> d.doubleValue()).average().getAsDouble();
//		}
//	}
//}