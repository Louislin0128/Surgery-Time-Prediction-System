package guiFunction;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**�إ߯S�x���n���ɮ�*/
public class FeatureInfo {
	/**
	 * �ѯS�x���������X���
	 * @param keep �O�d�S�x�}�C
	 * @param attrsRank �S�x���n��
	 * @param outFile ��X�ɮ�
	 * @throws IOException
	 */
	public static void buildWithScore(ArrayList<String> keep, LinkedHashMap<String, String> attrsRank, String outFile) throws IOException {
		try(BufferedWriter br = new BufferedWriter(new FileWriter(outFile))){
			br.write("������S�x,���n��");
			for(Entry<String, String> e: attrsRank.entrySet()) {
				if(keep.contains(e.getKey())) {
					br.newLine();
					br.write(e.getKey() + "," + e.getValue());
				}
			}	
		}
	}
	
	/**
	 * �Y�S���S�x�����ơA�إߤ@�Ӥ����ѱƦW���ɮ�
	 * @param feature �S�x�}�C
	 * @param outFile ��X�ɮ�
	 * @throws IOException
	 */
	public static void buildWithoutScore(String[] feature, String outFile) throws IOException {
		try(BufferedWriter br = new BufferedWriter(new FileWriter(outFile))){
			br.write("������S�x,���n��");
			for(String s: feature) {
				br.newLine();
				br.write(s + ",�L�k����");
			}
		}
	}
	
	/**
	 * �Y�S���S�x�����ơA�إߤ@�Ӥ����ѱƦW���ɮ�
	 * @param feature
	 * @param outFile
	 * @throws IOException
	 */
	public static void buildWithoutScore(ArrayList<String> feature, String outFile) throws IOException {
		buildWithoutScore(feature.toArray(new String[feature.size()]), outFile);
	}
}
