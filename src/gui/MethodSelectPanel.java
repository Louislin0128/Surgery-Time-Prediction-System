package gui;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import guiComponent.AbstractPreview;
import guiComponent.ShowProgress;
import guiFunction.CreateFile;
import guiFunction.Step;
import predict.Predictors;
import predict.PredictorsException;

class MethodSelectPanel extends JPanel implements ActionListener, ItemListener, Panel {
	private static final long serialVersionUID = -8033801386610992572L;
	private Logger logger = Logger.getLogger("MethodSelectPanel");
	private String wekaTip = "Weka�O�ѯæ����h�d���j�Ǩϥ�Java�}�o����Ʊ��ɳn��A�Q�s�������C";
	private String bpnnTip = "�ۥD��o���˶ǻ����g�����C�Ъ`�N�I�Ҧ���줺�e�������ƭȡC";	
	private JFileChooser chooser = new JFileChooser(Info.getDesktop());
	private JComboBox<String> combo = new JComboBox<>(Predictors.getClassifierSimpleName());
	private JTextArea result = new JTextArea();
	private JTextField info = new JTextField(wekaTip);
	private JButton confirm = new JButton("�T�w");
	private TrainPreview trainPreview = new TrainPreview();
	private TestPreview testPreview = new TestPreview();
	private ShowProgress showProgress = new ShowProgress();
	private Predictors predictors;
	
	public MethodSelectPanel() {
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
		setLayout(new BorderLayout(10, 10));
		
		JLabel label = new JLabel("�п�ܱ��ϥΪ��w���t�ΡG");
		combo.addItemListener(this);
		combo.setSelectedIndex(0);
		combo.setEnabled(false);
		Box chooseBox = Box.createHorizontalBox();
		chooseBox.add(label);
		chooseBox.add(combo);
		
		result.setEditable(false);
		result.setLineWrap(true);
		JScrollPane resultScroll = new JScrollPane(result);
		resultScroll.setViewportBorder(new TitledBorder(null, "�V�m�ҫ��ɪ��ԲӸ�T", TitledBorder.CENTER, TitledBorder.BELOW_TOP));
		
		//==========�������W�b==========
		JPanel propertyPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 0, 10, 0);
		propertyPanel.add(chooseBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		JSplitPane classifierSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, Predictors.getClassifierPanel(), resultScroll);
		propertyPanel.add(classifierSplit, gbc);
		classifierSplit.setDividerLocation(classifierSplit.getPreferredSize().width / 2);
		
		//==========�����k�W�b==========
		trainPreview.setTableTitle("�V�m��");
		testPreview.setTableTitle("���ն�");
		JSplitPane previewSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, trainPreview, testPreview);
		previewSplit.setBorder(LineBorder.createBlackLineBorder());
		previewSplit.setDividerLocation(previewSplit.getPreferredSize().height / 2);
		
		//==========�D����==========
		JPanel mainPanel = new JPanel(new GridLayout(1, 2, 5, 5));
		mainPanel.add(propertyPanel);
		mainPanel.add(previewSplit);
		add(mainPanel, BorderLayout.CENTER);
		
		//==========�����̤U��==========
		info.setEditable(false);
		confirm.setEnabled(false);
		confirm.addActionListener(this);
		
		Box confirmBox = Box.createHorizontalBox();
		confirmBox.add(info);
		confirmBox.add(Box.createHorizontalStrut(5));
		confirmBox.add(confirm);
		add(confirmBox, BorderLayout.SOUTH);
	}
	
	/**�I��confirmButton*/
	@Override
	public void actionPerformed(ActionEvent e) {
		Page.METHOD_SELECT.setEnabled(false);
		Page.TRAIN_RESULT.setEnabled(false);
		unLockOthers(false);
		
		String checkError;
		try {
			checkError = predictors.checkBPNNdata();//�ˬd��ƶ�
		} catch (Exception ee) {
			Page.METHOD_SELECT.setEnabled(true);
			unLockOthers(true);
			logger.info(ee.getMessage());
			Info.showError("�����y�{�o�Ϳ��~");
			JOptionPane.showMessageDialog(MethodSelectPanel.this, "�����y�{�o�Ϳ��~�A�ЦA�դ@��");
			return;
		}
		
		if(checkError == null) {
			//��ƧY�i�Q�����A�i��V�m�ҫ�
			showProgress.update(new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() {
					if(trainModel()) {
						Page.TRAIN_RESULT.setEnabled(true);
					}
					Page.METHOD_SELECT.setEnabled(true);
					unLockOthers(true);
					return null;
				}
			});
		}else {//result != null
			Info.showError("��ƶ����ŦX�n�D");
			
			int choose = JOptionPane.showConfirmDialog(this,
			checkError + "\n�z�n�۰��ഫ�ܡH\n(�Ъ`�N�I�ഫ��V�m���δ��ն��N�ܧ��ഫ�ᵲ�G�C�Y�n�ϥαz�즳����ƶ��A�Э��s�פJ�C)", 
			"��ƶ����ŦX�n�D", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
			if(choose == JOptionPane.YES_OPTION) {
				showProgress.update(new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() {
						try {
							showProgress.setText("���b�ഫ�S�x");
							//�V�m���M���ն��ܧ��ഫ�ᵲ�G�A�n�έ�Ӫ���ƶ��A�u�୫�s�פJ
							//�q�`���XPredictorsException�ҥ~�ɡA����ƶ����ŦX�n�D�A�B�ȮɨB�J�ɤw�g�s�b
							Step.executePredictorsStep(Info.getModelTrain(), Info.getModelTest(),
													   Info.getTempPath(), Info.getTempStep(),
													   Info.getTransTrain(), Info.getTransTest());
							//XXX �ݴ��աC�Y�즳�B�J�ɳQ�л\�A�O�_�v�T��w���D�������w�����G
							CreateFile.copy(Info.getTempStep(), Info.getStep());
						} catch (Exception eee) {
							Page.METHOD_SELECT.setEnabled(true);
							unLockOthers(true);
							logger.info(eee.getMessage());
							Info.showError("�ഫ�S�x�ɵo�Ϳ��~");
							JOptionPane.showMessageDialog(MethodSelectPanel.this, "�ഫ�S�x�ɵo�Ϳ��~");
							return null;
						}
						
						//���s�إ߹w���������� | ���sŪ��
						try {
							predictors = new Predictors(Info.getModelTrain(), Info.getTransTrain(), Info.getTransTest(), Info.getStep());
						} catch (IOException eee) {
							Page.METHOD_SELECT.setEnabled(true);
							unLockOthers(true);
							logger.info(eee.getMessage());
							Info.showError("��l�Ƥ����w�����ɵo�Ϳ��~");
							JOptionPane.showMessageDialog(MethodSelectPanel.this, "��l�Ƥ����w�����ɵo�Ϳ��~");
							return null;
						}
						
						//�V�m�ҫ�
						boolean success = trainModel();
						
						//��s�V�m���M���ն�
						try {
							showProgress.setText("���b��s�V�m���M���ն�");
							updateSheet(Info.getTransTrain(), Info.getTransTest());
							CreateFile.copy(Info.getTransTrain(), Info.getModelTrain());
							CreateFile.copy(Info.getTransTrain(), Info.getModelTest());
							Info.showMessage("�ഫ��ƶ����\");
						} catch (IOException eee) {
							Page.METHOD_SELECT.setEnabled(true);
							unLockOthers(true);
							logger.info(eee.getMessage());
							Info.showError("��s�V�m��/���ն��o�Ϳ��~");
							JOptionPane.showMessageDialog(MethodSelectPanel.this, "��s�V�m��/���ն��ɵo�Ϳ��~");
							success = false;//�����V�m�ҫ������\
						}
						
						if(success) {//���\�V�m�ҫ�
							Page.TRAIN_RESULT.setEnabled(true);
						}
						Page.METHOD_SELECT.setEnabled(true);
						unLockOthers(true);
						return null;
					}
				});
				
			}else {//JOptionPane.NO_OPTION
				Info.showError("�ШϥΨ�L��ƶ��κt��k");
				JOptionPane.showMessageDialog(this, "�ШϥΨ�L�t��k�ζפJ��L��ƶ�");
				Page.METHOD_SELECT.setEnabled(true);
				unLockOthers(true);
			}
		}
	}
	
	/**
	 * �V�m�ҫ�
	 * @return �V�m�ҫ��O�_���\
	 */
	private boolean trainModel() {
		String results;
		try {
			showProgress.setText("���b�إ߼ҫ�");
			//�o��V�m���G�ÿ�X�w���Ȥι�ڭ�
			results = predictors.trainClassifier(Info.getPandA());
			result.setText(results);
		} catch (Exception ee) {
			logger.info(ee.getMessage());
			Info.showError("�V�m�ҫ�����");
			JOptionPane.showMessageDialog(MethodSelectPanel.this, ee.getMessage());
			return false;
		}
		
		try {
			CreateFile.toCSV(Info.getTrainResult(), results);
			predictors.saveClassifier(Info.getClassifier());
			Info.showMessage("�V�m�ҫ����\");
		} catch (Exception ee) {
			logger.info(ee.getMessage());
			Info.showError("�x�s�V�m�ҫ����G����");
			JOptionPane.showMessageDialog(MethodSelectPanel.this, ee.getMessage());
			return false;
		}
		return true;
	}
	
	/**�I��t��k�C��*/
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (ItemEvent.SELECTED == e.getStateChange()) {
			int index = combo.getSelectedIndex();
			if(index == 3) {	// �Y���BPNN
				info.setText(bpnnTip);
			}else {
				info.setText(wekaTip);
			}
			try {
				Predictors.changeClassifier(index);
			} catch (Exception ee) {
				logger.info(ee.getMessage());
				Info.showError("�Э��s�Ұʨt��");
				JOptionPane.showMessageDialog(this, "���������ɵo�Ͱ��D�A�ЦA�դ@���C");
			}
		}
	}
	
	/**����פJ���s(true -> ����)*/
	private void unLockImport(boolean b) {
		trainPreview.setImportEnabled(b);
		testPreview.setImportEnabled(b);
	}
	
	/**�����L���s(true -> ����)*/
	private void unLockOthers(boolean b) {
		confirm.setEnabled(b);
		combo.setEnabled(b);
	}
	
	@Override
	public void setFile() {
		if(Files.notExists(Paths.get(Info.getModelTrain())) ||
		   Files.notExists(Paths.get(Info.getModelTest()))) {
			unLockImport(true);
			return;
		}
		unLockImport(false);
		Page.METHOD_SELECT.setEnabled(false);
		updateSheet(Info.getModelTrain(), Info.getModelTest());
		
		//�Y�B�J�ɤ��s�b�A�إ�
		Path step = Paths.get(Info.getStep());
		if(Files.notExists(step)) {
			try {
				Files.createFile(step);
			} catch (IOException e) {
				logger.info(e.getMessage());
				Info.showError("�����y�{�X��");
				JOptionPane.showMessageDialog(this, "�����y�{�X���A�ЦA�դ@��");
			}
		}
		
		try {// �إ߹w��������
			predictors = new Predictors(Info.getOriginalTrain(),
										Info.getModelTrain(), Info.getModelTest(),
									 	Info.getTransTrain(), Info.getTransTest(),
									 	Info.getStep(), Info.getTempStep());
			unLockOthers(true);
		} catch (PredictorsException e) {//��ƶ����ŦX�n�D
			Info.showError("��ƶ����ŦX�n�D");
			int choose = JOptionPane.showConfirmDialog(this, e.getMessage(), "��ƶ����ŦX�n�D", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
			if(choose == JOptionPane.YES_OPTION) {
				try {
					//�q�`���XPredictorsException�ҥ~�ɡA�N���ƶ����ŦX�n�D�A�B�ȮɨB�J�ɤw�g�s�b
					Step.executeRemoveFeature(Info.getModelTrain(), Info.getTempStep(), Info.getTransTrain());
					Step.executeRemoveFeature(Info.getModelTest(), Info.getTempStep(), Info.getTransTest());
					CreateFile.copy(Info.getTransTrain(), Info.getModelTrain());
					CreateFile.copy(Info.getTransTest(), Info.getModelTest());
					Files.delete(Paths.get(Info.getTempStep()));
					setFile();//�A����Ū��
					
				} catch (Exception ee) {
					logger.info(ee.getMessage());
					Info.showError("�����L�ίS�x�ɵo�Ϳ��~");
					JOptionPane.showMessageDialog(this, "�����L�ίS�x�ɵo�Ϳ��~");
				}
				
			}else {
				Info.showError("�L�k�ϥΥ������\��");
				JOptionPane.showMessageDialog(this, "�L�k�ϥΥ������\��A�N�M���������");
				reset();
			}
			
		} catch (Exception e) {
			logger.info(e.getMessage());
			Info.showError("��l�Ƥ����w�����ɵo�Ϳ��~");
			JOptionPane.showMessageDialog(this, "��l�Ƥ����w�����ɵo�Ϳ��~");
			return;
		}
		Page.METHOD_SELECT.setEnabled(true);
	}
	
	/**
	 * ��s�w�����
	 * @param train �V�m����ƨӷ�
	 * @param test ���ն���ƨӷ�
	 */
	public void updateSheet(String train, String test) {
		try {
			trainPreview.update(train);
			testPreview.update(test);
		} catch (Exception e) {
			logger.info(e.getMessage());
			Info.showError("�L�k��s�w�����");
			JOptionPane.showMessageDialog(this, "��s�w�����ɵo�Ϳ��~");
		}
	}
	
	@Override
	public void reset() {
		predictors = null;
		result.setText("");
		trainPreview.clear();
		testPreview.clear();
		unLockImport(true);
		unLockOthers(false);
	}
	
	/**��ܰV�m��*/
	private class TrainPreview extends AbstractPreview {
		private static final long serialVersionUID = -3161485467044773055L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int flag = chooser.showOpenDialog(this);
			if (flag == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				try {
					CreateFile.copy(file, Info.getModelTrain());
					CreateFile.copy(file, Info.getOriginalTrain());
					update(file);
					setFile();
					Info.showMessage("�V�m���פJ���\");
				} catch (Exception ee) {
					logger.info(ee.getMessage());
					Info.showError("�V�m���פJ����");
				}
			}
		}
	}
	
	/**��ܴ��ն�*/
	private class TestPreview extends AbstractPreview {
		private static final long serialVersionUID = -8733890928778564081L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int flag = chooser.showOpenDialog(this);
			if (flag == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				try {
					CreateFile.copy(file, Info.getModelTest());
					CreateFile.copy(file, Info.getOriginalTest());
					update(file);
					setFile();
					Info.showMessage("���ն��פJ���\");
				} catch (Exception ee) {
					logger.info(ee.getMessage());
					Info.showError("���ն��פJ����");
				}
			}
		}
	}
}
