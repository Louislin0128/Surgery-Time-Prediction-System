package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import guiComponent.AbstractPreview;
import guiFunction.CreateFile;
import guiFunction.LSH;
import guiFunction.SplitData;
import guiFunction.Step;

class DataSplitPanel extends JPanel implements Panel {
	private static final long serialVersionUID = -835851681402783283L;
	private Logger logger = Logger.getLogger("DataSplitPanel");
	private JFileChooser chooser = new JFileChooser(Info.getDesktop());
	private OptionsPanel optionsPanel = new OptionsPanel();
	private AbstractPreview trainPreview = AbstractPreview.newSheetWithoutImport(),
			testPreview = AbstractPreview.newSheetWithoutImport(),
			popTable = AbstractPreview.newNothingSheet();// �u���ݸ�ƶ�
	private PopUp popUp = new PopUp(popTable);
	private Border normalBorder = UIManager.getBorder("TextField.border");
	private Border errorBorder = new LineBorder(Color.RED, 3);

	public DataSplitPanel() {
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
		popTable.setTableTitle("�˵���ƶ�");
		
		setLayout(new BorderLayout(10, 10));
		add(optionsPanel, BorderLayout.NORTH);
		
		trainPreview.setTableTitle("������G(�V�m��)");
		testPreview.setTableTitle("������G(���ն�)");
		JSplitPane previewPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, trainPreview, testPreview);
		previewPanel.setBorder(null);
		previewPanel.setDividerLocation(previewPanel.getPreferredSize().width / 2);
		add(previewPanel, BorderLayout.CENTER);
	}
	
	@Override
	public void setFile() {
		if(Files.notExists(Paths.get(Info.getFeatureSelect()))) {
			System.err.println("�S���S�x������ɮ�");
			optionsPanel.importData.setEnabled(true);
			return;
		}
		optionsPanel.importData.setEnabled(false);
		
		try {
			popTable.update(Info.getFeatureSelect());
			optionsPanel.dataCount.setText(popTable.getRowsCount() + "");
			optionsPanel.setButtonEnabled(true);
			Info.showMessage("��ƶפJ���\");
		} catch (Exception e) {
			optionsPanel.dataCount.setText("�|�L���");
			optionsPanel.setButtonEnabled(false);
			optionsPanel.clearTable();
			logger.info(e.getMessage());
			Info.showError("��ƶפJ����");
		}
	}
	
	@Override
	public void reset() {
		trainPreview.clear();
		testPreview.clear();
		popTable.clear();
		optionsPanel.importData.setEnabled(true);
		optionsPanel.randomCheck.setSelected(false);
		optionsPanel.splitText[0].setText("");
		optionsPanel.splitText[1].setText("");
		optionsPanel.dataCount.setText("�|�L���");
	}
	
	private class OptionsPanel extends JPanel implements ActionListener, ItemListener {
		private static final long serialVersionUID = -1626768545221257752L;
		private JCheckBox randomCheck = new JCheckBox("���ø�ƶ�");
		private JRadioButton percentRadio = new JRadioButton("�ʤ���", true),
				numberRadio = new JRadioButton("����", false);
		private JTextField[] splitText = new JTextField[2];
		private JTextField randomText = new JTextField();
		private JLabel dataCount = new JLabel("�|�L���");
		private JLabel[] splitLabel = new JLabel[5];
		private JButton importData = new JButton("�פJ��ƶ�"),
				lookup = new JButton("�˵���ƶ�"),
				confirm = new JButton("�T�w����");
		
		public OptionsPanel() {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			
			importData.addActionListener(this);
			lookup.setEnabled(false);
			lookup.addActionListener(this);
			
			Box buttonUpper = Box.createHorizontalBox();
			buttonUpper.add(importData);
			buttonUpper.add(Box.createHorizontalStrut(5));
			buttonUpper.add(lookup);
			
			Box buttonLower = Box.createHorizontalBox();
			buttonLower.add(new JLabel("����`���ơG"));
			buttonLower.add(dataCount);
			buttonLower.add(Box.createHorizontalGlue());
			
			Box buttonBox = Box.createVerticalBox();
			buttonBox.add(buttonUpper);
			buttonBox.add(buttonLower);
			add(buttonBox);
			add(Box.createHorizontalStrut(30));
			//
			randomCheck.addItemListener(this);
			randomText.setEnabled(false);

			Box randomUpper = Box.createHorizontalBox();
			randomUpper.add(randomCheck);
			randomUpper.add(Box.createHorizontalGlue());

			Box randomLower = Box.createHorizontalBox();
			randomLower.add(new JLabel("�ؤl�X�G", JLabel.CENTER));
			randomLower.add(randomText);
			Box randomBox = Box.createVerticalBox();
			randomBox.add(randomUpper);
			randomBox.add(randomLower);
			add(randomBox);
			add(Box.createHorizontalStrut(30));
			//
			splitLabel[0] = new JLabel("�ϥγ��G", JLabel.CENTER);
			splitLabel[1] = new JLabel("�]�w���ն����G", JLabel.CENTER);
			splitLabel[2] = new JLabel("%", JLabel.CENTER);
			splitLabel[3] = new JLabel("~", JLabel.CENTER);
			splitLabel[4] = new JLabel("%�����", JLabel.CENTER);
			
			percentRadio.addItemListener(this);

			ButtonGroup radioGroup = new ButtonGroup();
			radioGroup.add(percentRadio);
			radioGroup.add(numberRadio);

			Box splitUpper = Box.createHorizontalBox();
			splitUpper.add(splitLabel[0]); // JLabel �ϥγ��:
			splitUpper.add(percentRadio);
			splitUpper.add(numberRadio);
			//
			splitText[0] = new JTextField(); // ���Ʃ���ﶵ��J�Ʀr
			splitText[0].setColumns(15);
			splitText[1] = new JTextField(); // ���Ʃ���ﶵ��J�Ʀr
			splitText[1].setColumns(15);

			Box splitLower = Box.createHorizontalBox();
			splitLower.add(splitLabel[1]); 	// JLabel �]�w���ն����G
			splitLower.add(splitText[0]); 	// ��J�Ʀr ��
			splitLower.add(splitLabel[2]); 	// JLabel % | ��
			splitLower.add(splitLabel[3]);	// JLabel ~
			splitLower.add(splitText[1]); 	// ��J�Ʀr �k
			splitLower.add(splitLabel[4]); 	// JLabel %����� | �������
			
			JPanel splitPanel = new JPanel(new GridLayout(2, 1));
			splitPanel.add(splitUpper);
			splitPanel.add(splitLower);
			add(splitPanel);
			add(Box.createHorizontalStrut(30));
			//
			confirm.setEnabled(false);
			confirm.addActionListener(this);
			add(confirm);
		}
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getSource() == randomCheck) {	//���ø�ƶ��A��J�ؤl�X
				if(ItemEvent.SELECTED == e.getStateChange()) {
					randomText.setEnabled(true);
				}else {
					randomText.setEnabled(false);
					randomText.setText("");
				}
			}else if(e.getSource() == percentRadio) {	// percentRadio	| �w��ʤ���RadioButton
				if (ItemEvent.SELECTED == e.getStateChange()) {
					splitLabel[2].setText("%");
					splitLabel[4].setText("%�����");
				} else { 	// ����
					splitLabel[2].setText("��");
					splitLabel[4].setText("�������");
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (importData == e.getSource()) {//�פJ��ƶ����s
				int choose = chooser.showOpenDialog(this);
				if (choose == JFileChooser.APPROVE_OPTION) {
					try {
						CreateFile.copy(chooser.getSelectedFile(), Info.getFeatureSelect());
						popTable.update(Info.getFeatureSelect());
						setFile();
						
						choose = JOptionPane.showConfirmDialog(this, "�аݱz�n�d�ݶפJ����ƶ���?", "���ܵ���", JOptionPane.YES_NO_OPTION);
						if (choose == JOptionPane.YES_OPTION) {
							popUp.setVisible(true);
						}
						confirm.setEnabled(true); 		// �T�w���ͫ��s
						randomCheck.setSelected(false);	// �����Ŀ若��checkBox
						randomText.setText("");
						splitText[0].setText("");
						splitText[1].setText("");
						
					} catch (IOException ee) {
						logger.info(ee.getMessage());
						Info.showError("��ƶפJ����"); 
					}
					trainPreview.clear();
					testPreview.clear();
				}

			} else if (lookup == e.getSource()) {//�d�ݸ�ƶ����s
				popUp.setVisible(true);
				
			} else if(confirm == e.getSource()) {//�T�w���s
				confirm();
			}
		}
		
		/**�T�{�ϥΪ̿�J���ƭȬO�_�X�z*/
		private void confirm() {
			String randSeed = randomText.getText();
			if (!randSeed.isEmpty()) {
				try { // �ˬd��J�Ʀr�O�_�X�k
					Long.parseLong(randSeed);
				} catch (NumberFormatException error) {
					remindError(randomText, "�п�J����ơC");
					return;
				}
			}

			String num1 = splitText[0].getText();
			String num2 = splitText[1].getText();
			int num1Int = 0;
			int num2Int = 0;
			try { // �ˬd��J�Ʀr�O�_�X�k
				num1Int = Integer.parseInt(num1);
			} catch (NumberFormatException error) {
				remindError(splitText[0], "�п�J����ơC");
				return;
			}
			try { // �ˬd��J�Ʀr�O�_�X�k
				num2Int = Integer.parseInt(num2);
			} catch (NumberFormatException error) {
				remindError(splitText[1], "�п�J����ơC");
				return;
			}
			
			if (num1Int < 0) {
				remindError(splitText[0], "�п�J�j�󵥩�0���ȡC");
				return;
			}else if (num2Int <= 0) {
				remindError(splitText[1], "�п�J�j��0���ȡC");
				return;
			}else if (num1Int > num2Int) {
				remindError(splitText[0], "�����J�涷�p�󵥩�k���J�檺�ƭȡC");
				return;
			}
			if (percentRadio.isSelected()) {
				if (num1Int > 100) {
					remindError(splitText[0], "�п�J0~100���ȡC");
					return;
				}else if (num2Int > 100) {
					remindError(splitText[1], "�п�J0~100���ȡC");
					return;
				}
			}
			
			setButtonEnabled(false);
			try {
				execute(num1Int, num2Int);
			}catch (Exception ee) {
				logger.info(ee.getMessage());
				Info.showError("��Ʃ������");
				Page.METHOD_SELECT.setEnabled(false);
				clearTable();
			}
			setButtonEnabled(true);
		}
		
		/**����y�����ɮ� | �B�J�ɵ{��*/
		private void execute(int num1, int num2) throws Exception {
			boolean stepExist = Files.exists(Paths.get(Info.getStep()));//�O�_���B�J��
			if (stepExist) { // ���B�J�A�s�@�쫬��ƶ�
				System.out.println("���B�J�� �����y�����ίS�x������������");
				// �s�@��Ӭy�����ɮ׮ɡA�@�ֲ����y�����ίS�x������������
				LSH.build(Info.getFeatureSelect(), Info.getDataTransform(), Info.getLSH());
			} else {
				// �S���B�J�A�O�{�r�� | ���׵{�r���O�q�S�x����άO��Ʃ���i�ӡA�����Ӱ���o��
				System.out.println("�L�B�J�� �����y����");
				LSH.remove(Info.getFeatureSelect(), Info.getLSH());// �����y����
			}
			
			String splitInFile;
			if (randomCheck.isSelected()) { // ���ø�ƶ�
				System.out.println("���ø�ƶ�");
				CreateFile.disOrganize(Info.getLSH(), randomText.getText(), Info.getDisOrganize());
				splitInFile = Info.getDisOrganize();
			}else {
				System.out.println("�����ø�ƶ�");
				splitInFile = Info.getLSH();
			}
			if(splitLabel[2].getText().equals("%")) {	// �ϥΪ̿�ܦʤ���
				System.out.println("�H�ʤ������");
				SplitData.byPercent(splitInFile, num1, num2, popTable.getRowsCount(), Info.getOriginalTrain(), Info.getOriginalTest());
			}else {
				System.out.println("�H���Ƥ���");
				SplitData.byQuantity(splitInFile, num1, num2, Info.getOriginalTrain(), Info.getOriginalTest());
			}
			
			if(stepExist) {// ����B�J��
				System.out.println("����B�J��");
				Step.executeBitches(Info.getOriginalTrain(), Info.getOriginalTest(), Info.getDoStepPath(),
									Info.getStep(), Info.getModelTrain(), Info.getModelTest());
			}else {//��l�V�m�����ɵ����ҫ��V�m������
				System.out.println("��l�V�m�����ɵ����ҫ��V�m������");
				CreateFile.copy(Info.getOriginalTrain(), Info.getModelTrain());
				CreateFile.copy(Info.getOriginalTest(), Info.getModelTest());
			}
			
			trainPreview.update(Info.getModelTrain());
			testPreview.update(Info.getModelTest());
			Page.METHOD_SELECT.setEnabled(true);
			Info.showMessage("��Ʃ�����\");
		}
		
		/**�]�w���s�O�_�ҥ�*/
		private void setButtonEnabled(boolean b) {
			lookup.setEnabled(b);
			confirm.setEnabled(b);
		}
		
		/**�M���V�m�δ��ժ��*/
		private void clearTable() {
			trainPreview.clear();
			testPreview.clear();
		}
		
		/**
		 * ���ܿ��~�T��
		 * @param comp ����
		 * @param errorMessage ���~�T��
		 */
		private void remindError(JComponent comp, String errorMessage) {
			comp.setBorder(errorBorder);
			JOptionPane.showMessageDialog(DataSplitPanel.this, errorMessage, "���ܰT��", JOptionPane.WARNING_MESSAGE);
			comp.setBorder(normalBorder);
		}
	}
	
	private class PopUp extends JFrame {
		private static final long serialVersionUID = -4929202953380274007L;
		public PopUp(AbstractPreview sheet) {
			setTitle("��ƹw��");
			setSize(sheet.getPreferredSize().width, sheet.getPreferredSize().height);
			setContentPane(sheet);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setLocationRelativeTo(DataSplitPanel.this);
		}
	}
}
