package predict;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;

import guiFunction.CreateFile;
import guiFunction.Step;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CorrelationAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.CSVLoader;

public class FeatureSelect {
	/**
	 * �S�x����غc�l
	 * @param inFile ��J�ɮ�
	 * @param readFile ��wekaŪ�����ɮ�
	 * @param tempStep �ȮɨB�J��
	 * @return �S�x�W�� | �S�x���n��
	 * @throws FeatureSelectException ��ƶ����ŦX�n�D
	 * @throws Exception ��L�ҥ~
	 */
	public static LinkedHashMap<String, String> startRank(String inFile, String readFile, String tempStep)
												throws FeatureSelectException, Exception {
		return new FeatureSelect(inFile, readFile, tempStep).getAttrsRank();
	}
	
	private Instances insts;
	private FeatureSelect(String inFile, String readFile, String tempStep)
			throws FeatureSelectException, FileNotFoundException, IOException {
		// ���͵�Weka���ɮץH����String�ݩʪ����
		CreateFile.forWekaLoader(inFile, readFile);
		//==============================
		CSVLoader csv = new CSVLoader();
		csv.setSource(new File(readFile));
		insts = csv.getDataSet();
		if(insts.attribute(0).name().equals("�y����")) {
			insts.deleteAttributeAt(0);	//�����y����
		}
		insts.setClassIndex(insts.numAttributes() - 1);		//�]�w����(��N�ɶ��γ¾K�ɶ�)
		
		//�}�l�ˬd | ����X�{String�ݩʪ����
		if(insts.checkForStringAttributes()) {	//true�N�����OString�ݩ�
			System.err.println("��ƶ����X�G�W�w");
			ArrayList<String> stringAttr = listStringAttr(insts);
			Step.buildAutoRemove(stringAttr, tempStep);
			throw new FeatureSelectException(getErrorMessage(stringAttr));
		}
		//�Y�L�����A�N���ƥi�Qweka����
	}
	
	/**���o����string attribute���~�T��*/
	private String getErrorMessage(ArrayList<String> remove) {
		StringBuilder tempString = new StringBuilder("�]���o�����ŭȤӦh�A�L�k�i��S�x����A�����ƳB�z�����R���G\n");
		remove.forEach(s -> tempString.append("�u").append(s).append("�v\n"));
		tempString.append("�O�G�۰ʲ����L�k�ϥΪ��S�x�F\n").append("�_�G�C�X�Ҧ��S�x�ѱz�ۦ����C");
		return tempString.toString();
	}
	
	/**�C�|string attribute*/
	private ArrayList<String> listStringAttr(Instances insts) {	//�T�O�S��String type���ݩ�
		Enumeration<Attribute> attrs = insts.enumerateAttributes();
		ArrayList<String> stringAttr = new ArrayList<String>(insts.numAttributes());
		Attribute attr;
		while(attrs.hasMoreElements()) {
			attr = attrs.nextElement();
			if (attr.isString()) {	//�Y����쬰String Type
				stringAttr.add(attr.name());	//�[�J�}�C
			}
		}
		return stringAttr;
	}
	
	/**���o�S�x�Ƨǵ��G*/
	private LinkedHashMap<String, String> getAttrsRank() throws Exception {
		AttributeSelection attrSelection = new AttributeSelection();
		CorrelationAttributeEval evaluator = new CorrelationAttributeEval();
		Ranker ranker = new Ranker();
		attrSelection.setEvaluator(evaluator);
		attrSelection.setSearch(ranker);
		attrSelection.SelectAttributes(insts);
		System.out.println(attrSelection.toResultsString());
		
		LinkedHashMap<String, String> rankAttr = new LinkedHashMap<String, String>(
		attrSelection.numberAttributesSelected());
		for (double[] eachAttrRank : attrSelection.rankedAttributes()) {
			rankAttr.put(insts.attribute((int) eachAttrRank[0]).name(), Utils.doubleToString(eachAttrRank[1], 4));
			
		}
		return rankAttr;
	}
}
