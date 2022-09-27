package preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FillFeature {
	/**����{@link FillFeature#FillFeature FillFeature}*/
	public static void exec(String in, String out) throws Exception {
		new FillFeature(in, out);
	}
	
	/**
	 * ��ɯʥ���
	 * @param in ��J���|
	 * @param out ��X���|
	 * @throws Exception
	 */
	private FillFeature(String in, String out) throws Exception {
		Path outPath = Paths.get(out);
		Files.walkFileTree(Paths.get(in), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				try(BufferedReader br = Files.newBufferedReader(file, Charset.defaultCharset())){
					String[] inputSplit;//�C����Ƥ�����Ϋ᪺���G
					String input, date = "", room = "";		//�����Ϊ������ | ��� | �ǧO
					StringBuilder outStr = new StringBuilder();
					
				try(BufferedWriter bw = Files.newBufferedWriter(outPath.resolve(file.getFileName()), Charset.defaultCharset())){
					bw.write(br.readLine());	//�Ĥ@�C���D�C
					while((input = br.readLine()) != null) {	//�P�_�O�_�w���ɮ׵���
						inputSplit = input.split(",", 3);		//�N�C�C�����T��
						if(!inputSplit[0].isEmpty()) {	//�p�G���榳���
							date = inputSplit[0];		//�N��������Jdate
						}
						if(!inputSplit[1].isEmpty()) {	//�p�G���榳�ǧO
							room = inputSplit[1];		//�N����ǧO��Jroom
						}
						
						outStr.append(date).append(",").append(room).append(",").append(inputSplit[2]);
						bw.newLine();
						bw.write(outStr.toString());			
						outStr.setLength(0);
					}		
				}
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}
}