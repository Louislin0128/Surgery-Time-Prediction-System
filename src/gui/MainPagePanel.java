package gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import guiComponent.AbstractPreview;
import guiComponent.JAutoCompleteComboBox;
import guiComponent.PanelTable;
import guiComponent.PanelTableModel;
import guiComponent.ShowProgress;
import guiFunction.CreateFile;
import guiFunction.Detect;
import guiFunction.Enum;
import guiFunction.LoadFile;
import guiFunction.Step;
import guiFunction.TransIfNeeded;
import guiFunction.ZipOrUnzip;
import predict.Predictors;

class MainPagePanel extends JPanel implements ActionListener, Panel {
	private static final long serialVersionUID = 1180611674900194495L;
	private Logger logger = Logger.getLogger("MainPagePanel");
	private String[] imageName = {"\\single.png", "\\multiple.png", "\\folder.png"};
	private ImageIcon[] icon = LoadFile.fromIcons(Info.getIconPath(), imageName, 30, 30);
	private JFileChooser chooser = new JFileChooser(Info.getDesktop());
	private JTextArea info = new JTextArea();
	private JButton startPredict = new JButton("�}�l�w��"),
			loadModel = new JButton("���J�ҫ�");
	private JTabbedPane optionPane = new JTabbedPane();	//�S�x�ﶵ��������
	private SingleModel singleModel = new SingleModel();
	private ShowProgress showProgress = new ShowProgress();
	private SingleResult singleResult = new SingleResult();
	private MultiplePreview multiplePreview = new MultiplePreview();
	private String[] title;
	private TransIfNeeded trans;
	private Predictors predictors;
	private String predictType;		//��N�ɶ��γ¾K�ɶ�
	
	public MainPagePanel() {// �}�l�إ߼ҫ����s����ť��
		// XXX chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("Model File", "model"));
		setLayout(new BorderLayout(10, 10));
		
		JScrollPane singleScroll = new JScrollPane(new PanelTable(singleModel));
		singleScroll.setViewportBorder(new TitledBorder(null, "��ܱ��w�����ﶵ", TitledBorder.CENTER, TitledBorder.BELOW_TOP));
		optionPane.addTab("�浧��ƹw��", icon[0], singleScroll, "��ܦU�S�x���ﶵ�H�w���浧��N���");
		optionPane.addTab("�h����ƹw��", icon[1], multiplePreview, "�פJ�ɮץH�w���h����N���");
		
		info.setLineWrap(true);
		info.setEditable(false);
		JScrollPane infoScroll = new JScrollPane(info);
		infoScroll.setViewportBorder(new TitledBorder(null, "�ϥμҫ����ԲӸ�T", TitledBorder.CENTER, TitledBorder.BELOW_TOP));
		JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
		centerPanel.add(optionPane);
		centerPanel.add(infoScroll);
		add(centerPanel, BorderLayout.CENTER);
		
		startPredict.setEnabled(false);
		startPredict.addActionListener(this);
		loadModel.addActionListener(this);
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(startPredict);
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(loadModel);
		add(buttonBox, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startPredict) { // �}�l�w��			
			switch(optionPane.getSelectedIndex()) {
			case 0://�浧��ƹw��
				try {
					CreateFile.buildOptions(title, singleModel.getOptions(), Info.getPredictOptions());
					Step.executeBitches(Info.getOriginalTrain(), Info.getPredictOptions(), Info.getPredictPath(),
								Info.getStep(), Info.getPredictTrain(), Info.getPredictTest());
					singleResult.update(singleModel.getOptionsText(), predictType,
							predictors.getValue(Info.getPredictTest(), trans));	// �ˬd���L�ݭn�ϥ��W�ƩΤϼзǤ�
					JOptionPane.showMessageDialog(this, singleResult, "�w�����G", JOptionPane.INFORMATION_MESSAGE);
					
				} catch (Exception ee) {
					logger.info(ee.getMessage());
					Info.showError("�L�k�w��");
					JOptionPane.showMessageDialog(this, "�o�Ͱ��D�A�L�k�w���I");
				}
				break;
				
			case 1://�h����ƹw��
				try {
					Step.executeBitches(Info.getOriginalTrain(), Info.getMultiplePredict(), Info.getPredictPath(),
								Info.getStep(), Info.getPredictTrain(), Info.getPredictTest());
					predictors.buildPredictFile(Info.getPredictTest(), Info.getMultiplePredict(), trans);
					multiplePreview.update(Info.getMultiplePredict());
					JOptionPane.showMessageDialog(this, "�w���\�ഫ�h����ƹw����");
					
				} catch (Exception ee) {
					logger.info(ee.getMessage());
					Info.showError("�L�k�w��");
					JOptionPane.showMessageDialog(this, "�o�Ͱ��D�A�L�k�w���I");
					return;
				}
				break;				
			}

		} else if (e.getSource() == loadModel) { // ���J�ҫ�
			int choose = chooser.showOpenDialog(this);
			if (choose == JFileChooser.APPROVE_OPTION) {
				//�����Y
				Path source = chooser.getSelectedFile().toPath();
				Info.setRawPath(source);
				Path target = Paths.get(Info.getModelPath());
				try {
					ZipOrUnzip.unzip(source, target);
					Info.showMessage("���J�ҫ��ɦ��\");
				} catch (IOException ee) {
					logger.info(ee.getMessage());
					Info.showError("���J�ҫ��ɥ���");
				}
				
				showProgress.update(new SwingWorker<Void, Void>(){
					@Override
					protected Void doInBackground() {
						showProgress.setText("���b�إ߿�ܦC��");
						try {
							buildOptions(source.toString());
							Info.showMessage("�إ߿�ܦC���\");
						} catch (Exception e) {
							logger.info(e.getMessage());
							Info.showError("�إ߿�ܦC����");
						}
						return null;
					}
				});
			}
		}
	}

	@Override
	public void setFile() {
		if(Info.getStep() == null || Files.notExists(Paths.get(Info.getStep()))) {
			System.err.println("�|���]�w");
			reset();
			return;
		}
		
		showProgress.update(new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {
				// �����ϥΪ̦b�{�����槹���e���U���s
				Page.MAIN_PAGE.setEnabled(false);
				showProgress.setText("���b�M�����e�u�@���q");
				Info.clearProcessAndTemp();
				showProgress.setText("���b�إ߿�ܲM��");
				try {
					buildOptions("�ھڬy�{�إ�");
				} catch (Exception e) {
					startPredict.setEnabled(false);
					logger.info(e.getMessage());
					Info.showError("�L�k�إ߿�ܦC��");
					JOptionPane.showMessageDialog(MainPagePanel.this, "�L�k�إ߿�ܦC��");
				}
				Page.MAIN_PAGE.setEnabled(true);
				return null;
			}
		});
	}
	
	@Override
	public void reset() {
		predictors = null;
		info.setText("");
		singleModel.clear();
		multiplePreview.clear();
	}
	
	/**
	 * �浧��ƹw�� | �إ߭�������ܿﶵ
	 * @param modelPath ���J�ҫ����|
	 * @throws Exception
	 */
	private void buildOptions(String modelPath) throws Exception {
		info.setText("�ҫ����|�G" + modelPath + "\n" + LoadFile.fromText(Info.getTrainResult()));
		
		trans = TransIfNeeded.transValue(Info.getStep(), Info.getOriginalTrain());
		predictors = new Predictors(Info.getClassifier());
		String[] rawTitle = Enum.title(Info.getOriginalTrain());
		int last = rawTitle.length - 1;
		predictType = rawTitle[last].startsWith("��N�ɶ�") ? "��N�ɶ�" : "�¾K�ɶ�";		// ��N�ɶ� | �¾K�ɶ�
		title = Arrays.copyOf(rawTitle, last);
		singleModel.showFeature(title, Detect.digits(Info.getOriginalTrain()), Enum.content(Info.getOriginalTrain()));
		startPredict.setEnabled(true);
	}
	
	/**�浧�w�� | �ﶵ����*/
	private class OptionPanel extends JPanel {
		private static final long serialVersionUID = -4294554042900750386L;
		private JLabel label;
		private JTextField text;
		private JAutoCompleteComboBox combo;
		
		/**�إ߼ƭȿﶵ������*/
		public OptionPanel(String title) {
			setLabel(title);
			text = new JTextField();
			text.setPreferredSize(new Dimension(150, 10));
			add(text);
		}
		
		/**�إߥi�ѿ�ܿﶵ������*/
		public OptionPanel(String title, String[] items) {
			setLabel(title);
			combo = new JAutoCompleteComboBox(items);
			combo.setPreferredSize(new Dimension(150, 10));
			add(combo);
		}
		
		private void setLabel(String title) {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setBorder(new EmptyBorder(5, 5, 5, 5));
			label = new JLabel(title, JLabel.RIGHT);
			label.setPreferredSize(new Dimension(120, 10));
			add(label);
			add(Box.createHorizontalStrut(10));
		}
		
		public String getDigit() {
			return label.getText();
		}
		
		public String getOption() {
			if(text != null) {
				return text.getText();
			}else {
				return combo.getSelectedItem().toString();
			}
		}
	}
	
	/**�浧�w���� | ��ܨѨϥΪ̿�ܹw���ﶵ�����Model*/
	private class SingleModel extends PanelTableModel<OptionPanel> {
		private static final long serialVersionUID = -4299382561130529685L;
		/**
		 * �^�ǨϥΪ̿�ܪ��ﶵ(�P���D�X��)<br>
		 * ����ܩ�w���ƭȵ��G������
		 * @return �ϥΪ̿�ܪ��ﶵ
		 */
		private String getOptionsText() {
			StringBuilder str = new StringBuilder();
			forEach((OptionPanel o) -> {
				String digit = o.getDigit();
				str.append(digit.isEmpty() ? "0.0" : digit).append("�G").append(o.getOption()).append("\n");
			});
			return str.toString();
		}
		
		/**
		 * �^�ǨϥΪ̿�ܪ��ﶵ(���]�t���D)<br>
		 * �ѹw���ҫ��e�A��X���ɮ�
		 * @return �ϥΪ̿�ܪ��ﶵ
		 */
		private String[] getOptions() {
			int size = getRowCount();
			String[] value = new String[size];
			String option;
			for(int i = 0; i < size; i++) {
				option = get(i).getOption();
				option = option.isEmpty() ? "0.0" : option;
				value[i] = option;
			}
			return value;
		}
		
		/**
		 * �ǤJ���e�����]�t��N�ɶ��γ¾K�ɶ�
		 * @param title
		 * @param isDigit
		 * @param content
		 */
		private void showFeature(String[] title, boolean[] isDigit, String[][] content) {
			clear();
			for (int i = 0, length = title.length; i < length; i++) {
				if (isDigit[i]) {	// �ƭȡGJTextField
					add(new OptionPanel(title[i]));
				} else { 			// ��r�GJComboBox
					add(new OptionPanel(title[i], content[i]));
				}
			}
		}
	}
	
	/**�浧�w�� | ���ѹw����T���ϥΪ�*/
	private class SingleResult extends JPanel {
		private static final long serialVersionUID = 6779114897192596182L;
		private JTextArea featureInfo, predictInfo;
		
		public SingleResult() {
			setLayout(new BorderLayout(10, 10));
			setPreferredSize(new Dimension(500, 500));
			
			featureInfo = new JTextArea();
			featureInfo.setEditable(false);
			JScrollPane featureScroll = new JScrollPane(featureInfo);
			featureScroll.setViewportBorder(new TitledBorder(null, "�z��ܪ��w���ﶵ", TitledBorder.CENTER, TitledBorder.BELOW_TOP));
			add(featureScroll, BorderLayout.CENTER);
			
			predictInfo = new JTextArea();
			predictInfo.setBorder(new TitledBorder(null, "�w�����G�G", TitledBorder.LEFT, TitledBorder.BELOW_TOP));
			predictInfo.setEditable(false);
			add(predictInfo, BorderLayout.SOUTH);
		}
		
		/**
		 * ��s�w�����G
		 * @param optionsText �ϥΪ̿�ܪ��S�x��T
		 * @param predictType ��N�ɶ� | �¾K�ɶ�
		 * @param predictValue �w���ɶ�
		 */
		private void update(String optionsText, String predictType, double predictValue) {
			featureInfo.setText(optionsText);
			predictInfo.setText(predictType + "���w���Ȭ��u" + predictValue + "�v����");
		}
	}
	
	/**�h����ƹw�� �פJ��ƹw��*/
	private class MultiplePreview extends AbstractPreview {
		private static final long serialVersionUID = 1705315329772510531L;
		private JFileChooser chooser = new JFileChooser(Info.getDesktop());
		private MultiplePreview() {
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
			setTableTitle("�h����ƹw�����e");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int choose = chooser.showOpenDialog(this);
			if(choose == JFileChooser.APPROVE_OPTION) {
				try {
					File file = chooser.getSelectedFile();
					CreateFile.copy(file, Info.getMultiplePredict());
					update(file);
				} catch (Exception ee) {
					logger.info(ee.getMessage());
					JOptionPane.showMessageDialog(this, "�פJ��Ʈɵo�Ϳ��~�I");
				}
			}
		}
	}
}
