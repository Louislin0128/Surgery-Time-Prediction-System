package guiFunction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.TreeSet;

import preprocess.Split;

public class Enum {
	private static Collator collator = Collator.getInstance(Locale.TRADITIONAL_CHINESE);
	private static Comparator<Object> sort = (Object s1, Object s2) -> {
		return collator.compare(s1, s2);
	};
	
	/**
	 * �N���D�s�J�@���}�C��
	 * @param path �r����|
	 * @return ���D�@���}�C
	 * @throws Exception
	 */
	public static String[] title(String path) throws Exception {
		return title(new File(path));
	}
	
	/**
	 * �N���D�s�J�@���}�C��
	 * @param file �ɮ׸��|
	 * @return ���D�@���}�C
	 * @throws Exception
	 */
	public static String[] title(File file) throws Exception {
		String[] title;
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			title = Split.withoutQuotesOfArray(br.readLine());//�Y�Ĥ@����D���u�y�����v�h�����y����
		}
		if(title[0].equals("�y����")) {
			return Arrays.copyOfRange(title, 1, title.length);
		}
		return title;
	}
	
	/**
	 * �N���e�s�J�G���}�C��
	 * @param path �r����|
	 * @return ���e�G���}�C
	 * @throws Exception
	 */
	public static String[][] content(String path) throws Exception {
		return content(new File(path));
	}
	
	/**
	 * �N���e�s�J�G���}�C��
	 * @param file �ɮ׸��|
	 * @return ���e�G���}�C
	 * @throws Exception
	 */
	public static String[][] content(File file) throws Exception {
		ArrayList<TreeSet<String>> contentList;
		int length;
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			ArrayList<String> title = Split.withoutQuotes(br.readLine());
			//�Y�Ĥ@����D���u�y�����v�h�����y����
			int start;
			if(title.get(0).equals("�y����")) {
				start = 1;
				length = title.size() - 1;
			}else {
				start = 0;
				length = title.size();
			}
			
			contentList = new ArrayList<>(length);
			for (int i = 0; i < length; i++) {
				contentList.add(new TreeSet<String>(sort));
			}
			
			String input;
			ArrayList<String> inputSplit;
			while ((input = br.readLine()) != null) {
				inputSplit = Split.withoutQuotes(input);
				for (int i = 0; i < length; i++) {
					contentList.get(i).add(inputSplit.get(i + start));
				}
			}
		}
		
		String[][] content = new String[length][];
		TreeSet<String> set;
		for (int i = 0; i < length; i++) {
			set = contentList.get(i);
			content[i] = set.toArray(new String[set.size()]);
		}
		return content;
	}
}
