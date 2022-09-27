package gui;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import guiComponent.AbstractPreview;
import guiFunction.Enum;
import guiFunction.FeatureInfo;
import guiFunction.LoadFile;
import guiFunction.ZipOrUnzip;

class TrainResultPanel extends JPanel implements ActionListener, Panel {
	private static final long serialVersionUID = 7225862284829363231L;
	private Logger logger = Logger.getLogger("TrainResultPanel");
	private JFileChooser chooser = new JFileChooser(Info.getDesktop());
	private JButton save = new JButton("�x�s�ҫ�"),
			lookUp = new JButton("�˵����ն�"),
			done = new JButton("�����إ�");
	private JTextArea resultText = new JTextArea();
	private AbstractPreview featureSheet = AbstractPreview.newNothingSheet(),
			showPandA = AbstractPreview.newNothingSheet(),
			popTable = AbstractPreview.newNothingSheet();
	private PopUp popUpFrame = new PopUp(popTable);
	
	public TrainResultPanel() {
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("Model File", "model"));
		
		setLayout(new GridLayout(1, 2, 10, 10));
		resultText.setEditable(false);
		
		featureSheet.setTableTitle("�S�x�Ψ䭫�n��");
		showPandA.setTableTitle("��ڭȻP�w���Ȫ����");
		popTable.setTableTitle("�˵����ն�");
		
		JScrollPane resultScroll = new JScrollPane(resultText);
		resultScroll.setViewportBorder(new TitledBorder(null, "�V�m�ҫ����Ӹ`", TitledBorder.CENTER, TitledBorder.BELOW_TOP));
		
		JSplitPane infoSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resultScroll, featureSheet);
		infoSplit.setBorder(null);
		infoSplit.setDividerLocation(infoSplit.getPreferredSize().height / 2);
		add(infoSplit);
		
		lookUp.addActionListener(this);
		save.addActionListener(this);
		done.addActionListener(this);
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(lookUp);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(save);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(done);
		
		JPanel viewPanel = new JPanel(new BorderLayout(10, 10));
		viewPanel.add(showPandA, BorderLayout.CENTER);
		viewPanel.add(buttonBox, BorderLayout.SOUTH);
		add(viewPanel);
	}
	
	private class PopUp extends JFrame {
		private static final long serialVersionUID = -4929202953380274007L;
		public PopUp(AbstractPreview sheet) {
			setTitle("��ƹw��");
			setSize(sheet.getPreferredSize().width, sheet.getPreferredSize().height);
			setContentPane(sheet);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setLocationRelativeTo(TrainResultPanel.this);
		}
	}
	
	@Override
	public void setFile() {
		try {
			// �b��ܯS�x�ƦW�e���ˬd�A�Y�L�h�إߤ�
			if(Files.notExists(Paths.get(Info.getFeatureInfo()))) {
				String[] title = Enum.title(Info.getModelTrain());
				// ����ܤ�N�ɶ� | �¾K�ɶ�
				FeatureInfo.buildWithoutScore(Arrays.copyOf(title, title.length - 1), Info.getFeatureInfo());
			}
			featureSheet.update(Info.getFeatureInfo()); // ��s�S�x�ƦW
			
			resultText.setText(LoadFile.fromText(Info.getTrainResult()));
			showPandA.update(Info.getPandA());	//��ܹw���Ȥι�ӭ�
			popTable.update(Info.getModelTest());
			
		} catch (Exception e) {
			logger.info(e.getMessage());
			Info.showError("�L�k�V�m���G");
			JOptionPane.showMessageDialog(this, "�L�k�V�m���G");
		}
	}
	
	@Override
	public void reset() {
		resultText.setText("");
		featureSheet.clear();
		showPandA.clear();
		popTable.clear();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == lookUp) {
			popUpFrame.setVisible(true);	//�˵����ն�
			
		}else if(e.getSource() == save) {	//�x�s�ҫ�
			int choose = chooser.showSaveDialog(this);
			if (choose == JFileChooser.APPROVE_OPTION) {
				try {
					//���Y
					//XXX Paths.get(Info.getModelPath())
					Path source = Info.getWorkingData();
					String name = chooser.getSelectedFile().getAbsolutePath();
					if(!name.endsWith(".model")) {
						name += ".model";
					}
					Path target = Paths.get(name);
					ZipOrUnzip.zip(source, target);
					System.out.println("���Y���\");
				    Info.showMessage("�x�s�ҫ��ɦ��\");
				} catch (IOException ee) {
					System.err.println("���Y����");
				    Info.showError("�x�s�ҫ��ɥ���");
				}
			}
		}else if(e.getSource() == done) {	//�����إ�
			int choose = JOptionPane.showConfirmDialog(this, "�T�w�n�ϥΦ��ҫ��ܡH\n(�Y�n��Φ��ҫ��A�Х��x�s)", "���U�T�w��L�k��^", JOptionPane.YES_NO_OPTION);
			if(choose == JOptionPane.YES_OPTION) {	// �Y�_�h���X
				//�Y�ȮɨB�J�ɦs�b�A�N�ȮɨB�J�ɴ��N���B�J��
//				if(Files.exists(Paths.get(Info.getTempStep()))) {
//					try {
//						//�Y�ȮɨB�J�ɦs�b�A�q�`�O��k��ܭ����ɡA�ϥ�BPNN�t��k�A�t�Φ۰��ഫ���ҰO�����ȮɨB�J��
//						CreateFile.copy(Info.getTempStep(), Info.getStep());
//					} catch (IOException ee) {
//						JOptionPane.showMessageDialog(this, "�N�ȮɨB�J�ɴ��N���B�J�ɮɥ���");
//					}
//				}
				Info.showPage(Page.MAIN_PAGE);
				Info.showMessage("�����إ�");
			}
		}
	}
}
