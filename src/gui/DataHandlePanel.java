package gui;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import guiComponent.AbstractPreview;
import guiComponent.AbstractTools;
import guiComponent.AbstractTools.Tool;
import guiComponent.PanelTable;
import guiComponent.PanelTableModel;
import guiComponent.ShowProgress;
import guiFunction.CreateFile;
import guiFunction.Enum;
import guiFunction.LSH;
import guiFunction.LoadFile;
import guiFunction.Step;
import guiFunction.TitleIndex;

class DataHandlePanel extends JPanel implements Panel {
	private static final long serialVersionUID = 7437323078040768459L;
	private Logger logger = Logger.getLogger("DataHandlePanel");
	// GUI����
	private BeforeSheet beforeSheet = new BeforeSheet();// ��l���(������ɸ��)
	private AbstractPreview afterSheet = AbstractPreview.newSheetWithoutImport(); 	// �w�����(�u����n��ܪ���ƶ�)
	private AbstractPreview confirmSheet = AbstractPreview.newNothingSheet();	// �P�ϥΪ̽T�{�B�J���浲�G
	private ShowProgress showProgress = new ShowProgress();
	private JFileChooser chooser = new JFileChooser(Info.getDesktop());
	private TableModel model = new TableModel(); 			// ���ҫ�
	private PanelTable stepTable = new PanelTable(model); 	// �B�J��
	private HandlePanel handlePanel = new HandlePanel();
	private ToolPanel toolPanel = new ToolPanel();
	// Class
	private Step step = new Step(Info.getDesktop());// �x�s�B���J�B�J
	private Tools tools; 			// �u��C�A�]�tPanel�P���O(�I�s->�P�W����k)
	// �ܼ�
	private int removeIndex; 		// �R���Y�B�J�ɥΪ�
	private boolean loadStepFlag = false;	// �����B�J��ثe��ܪ��O�_�O���J��
	private boolean bitches = false;		// �����ϥΪ̬O�_�I��T�_
	private Vector<Tool> allStep = new Vector<>();
	
	public DataHandlePanel() {
		stepTable.addMouseListener(clickListener);
		stepTable.addMouseMotionListener(clickListener);
		
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
		setLayout(new BorderLayout());
		
		add(toolPanel, BorderLayout.WEST); 		// �s�@�u����
		JPanel centerPanel = new JPanel(new GridLayout(1, 2));
		centerPanel.add(handlePanel);	// �s�@�B�J��
		centerPanel.add(new PreViewPanel());// �s�@�w����
		add(centerPanel, BorderLayout.CENTER);
		//
		confirmSheet.setTableTitle("�B�J����᪺���G");
	}
	
	@Override
	public void setFile() {	// �]�w�����A�N�һݸ�ƷǳƦn
		if (Files.notExists(Paths.get(Info.getDataTransform())) ||
			Files.exists(Paths.get(Info.getDataHandle()))) {
			beforeSheet.setImportEnabled(true);
			return;
		}
		beforeSheet.setImportEnabled(false);
		
		showProgress.update(new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				Page.DATA_HANDLE.setEnabled(false);	// �����ϥΪ̦b�{�����槹���e���U���s
				showProgress.setText("���b���J���");
				unLockOthers(false);
				unLockFuncs(false);
				model.removeAll();
				allStep.removeAllElements();
				try {
					File file = new File(Info.getDataTransform());
					beforeSheet.update(file);
					tools = new Tools(Info.getDataTransform(), Info.getDataHandlePath(), Enum.title(file), Enum.content(file));
					unLockFuncs(true);
					Info.showMessage("��Ƹ��J���\");
				} catch (Exception e) {
					logger.info(e.getMessage());
					Info.showError("��Ƹ��J����");
				}
				
				unLockOthers(true);
				Page.DATA_HANDLE.setEnabled(true);
				return null;
			}
		});
	}
	
	@Override
	public void reset() {
		beforeSheet.clear();	// ��l���(������ɸ��)
		afterSheet.clear(); 	// �w�����(�u����n��ܪ���ƶ�)
		confirmSheet.clear();	// �P�ϥΪ̽T�{�B�J���浲�G
		model.removeAll(); 		// ���ҫ�
		allStep.removeAllElements();
		loadStepFlag = false;	// �����B�J��ثe��ܪ��O�_�O���J��
		bitches = false;		// �����ϥΪ̬O�_�I��T�_
		unLockFuncs(false);		// �N���s�����
		unLockOthers(false);
		beforeSheet.setImportEnabled(true);
	}

	// ��w�u������s(true -> ����)
	private void unLockFuncs(boolean b) {
		toolPanel.rosdButton.setEnabled(b);
		toolPanel.roButton.setEnabled(b);
		toolPanel.rrButton.setEnabled(b);
		toolPanel.rfButton.setEnabled(b);
		toolPanel.erButton.setEnabled(b);
		toolPanel.teButton.setEnabled(b);
		toolPanel.nfButton.setEnabled(b);
		toolPanel.sfButton.setEnabled(b);
	}
	
	// ��w�B�J��U���T�ӫ��s(true -> ����)
	private void unLockOthers(boolean b) {
		handlePanel.confirm.setEnabled(b);
		handlePanel.loadStep.setEnabled(b);
		handlePanel.saveStep.setEnabled(b);
	}
	
	// ��Table�������ʧ@(�즲�洫)	
	private MouseAdapter clickListener = new MouseAdapter() {
		private int first, second; 	// �����洫���O����ӨB�J����
		
		@Override
		public void mousePressed(MouseEvent e) {
			first = removeIndex = stepTable.rowAtPoint(e.getPoint()); // ���o���U�ɪ�����
			Info.showMessage("�w�I��B�J" + (first + 1));
		}
		
		private Tool firstFunc;
		@Override
		public void mouseReleased(MouseEvent e) {
			second = stepTable.rowAtPoint(e.getPoint()); // ���o�P�}�ɪ�����
			
			if (second != -1 &&
				first != second &&
				(firstFunc = model.get(first)).canMove() &&
				model.get(second).canMove()) {
				
				model.remove(firstFunc); 				// ������
				model.insertAt(firstFunc, second);
				Info.showMessage("���J�ܨB�J" + (second + 1));
			}
		}
		
		private int now;
		@Override
		public void mouseDragged(MouseEvent e) {
			now = stepTable.rowAtPoint(e.getPoint());
			stepTable.addRowSelectionInterval(now, now);
		}
	};
	
	private class ToolPanel extends JScrollPane implements ActionListener {
		private static final long serialVersionUID = 4936944520744858424L;
		private String[] imageName = {"\\RemoveOutlier.png", "\\RemoveOutlierBySD.png", "\\RemoveRecord.png", "\\RemoveFeature.png",
				"\\ExtractRecord.png", "\\TargetEncoding.png", "\\NormalizeFeature.png", "\\StandardizeFeature.png"};
		private ImageIcon[] icon = LoadFile.fromIcons(Info.getIconPath(), imageName, 80, 80); 	// Button���Ϯ�
		private JButton erButton, nfButton, sfButton, roButton, rosdButton, rrButton, rfButton, teButton;// �u���檺���s

		public ToolPanel() {
			setViewportBorder(new TitledBorder(null, "�u����", TitledBorder.CENTER, TitledBorder.BELOW_TOP));
			setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			JPanel content = new JPanel(new GridLayout(8, 1, 5, 5));
			setViewportView(content);
			
			roButton = new JButton(icon[0]);
			roButton.setToolTipText("�������w������s��(�ȭ��ƭ����)");
			roButton.addActionListener(this);
			content.add(roButton);
			
			rosdButton = new JButton(icon[1]);
			rosdButton.setToolTipText("�H�зǮt����ǡA�������w������s��(�ȭ��ƭ����)");
			rosdButton.addActionListener(this);
			content.add(rosdButton);

			rrButton = new JButton(icon[2]);
			rrButton.setToolTipText("�������w��줧�S�w���e��");
			rrButton.addActionListener(this);
			content.add(rrButton);

			rfButton = new JButton(icon[3]);
			rfButton.setToolTipText("�������w���");
			rfButton.addActionListener(this);
			content.add(rfButton);

			erButton = new JButton(icon[4]);
			erButton.setToolTipText("�������w��줧�S�w���e��");
			erButton.addActionListener(this);
			content.add(erButton);

			teButton = new JButton(icon[5]);
			teButton.setToolTipText("���w���ƭȤ�(�A�ΫD�ƭ����)");
			teButton.setEnabled(true);
			teButton.addActionListener(this);
			content.add(teButton);

			nfButton = new JButton(icon[6]);
			nfButton.setToolTipText("���w���зǤ�(�A�μƭ����)");
			nfButton.addActionListener(this);
			content.add(nfButton);

			sfButton = new JButton(icon[7]);
			sfButton.setToolTipText("���w���зǤ�(�ȭ��ƭ����)");
			sfButton.addActionListener(this);
			content.add(sfButton);
		}

		/**��Tool�������ʧ@ | �I���u���檺�ʧ@*/
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == erButton)
				model.add(tools.newInstance(Tools.Mode.ExtractRecord));
			else if (e.getSource() == rrButton)
				model.add(tools.newInstance(Tools.Mode.RemoveRecord));
			else if (e.getSource() == rfButton)
				model.add(tools.newInstance(Tools.Mode.RemoveFeature));
			else if (e.getSource() == roButton)
				model.add(tools.newInstance(Tools.Mode.RemoveOutlier));
			else if (e.getSource() == rosdButton)
				model.add(tools.newInstance(Tools.Mode.RemoveOutlierBySD));
			else if (e.getSource() == sfButton) {
				model.add(tools.newInstance(Tools.Mode.StandardizeFeature));
				unLockFuncs(false);
				bitches = true;
			}
			else if (e.getSource() == nfButton) {
				model.add(tools.newInstance(Tools.Mode.NormalizeFeature));
				unLockFuncs(false);
				bitches = true;
			}
			else if (e.getSource() == teButton) {
				model.add(tools.newInstance(Tools.Mode.TargetEncoding));
				unLockFuncs(false);
				bitches = true;
			}
			Info.showMessage("�w�s�W�B�J");
		}
	}

	private class HandlePanel extends JPanel implements ActionListener {
		private static final long serialVersionUID = 6451689933054716323L;
		private JButton loadStep, saveStep, confirm; // �B�J�檺���s�P���J��ƫ��s
		
		public HandlePanel() {
			setLayout(new BorderLayout());
			setBorder(new TitledBorder(null, "��ƳB�z�y�{", TitledBorder.CENTER, TitledBorder.BELOW_TOP));
			add(new JScrollPane(stepTable), BorderLayout.CENTER);

			saveStep = new JButton("�x�s�B�J");
			saveStep.setToolTipText("�x�s���e����ñĥΪ��B�J");
			saveStep.addActionListener(this);

			loadStep = new JButton("���J�B�J");
			loadStep.setToolTipText("���J���e�x�s���B�J");
			loadStep.addActionListener(this);

			confirm = new JButton("�w�����G");
			confirm.addActionListener(this);
			
			Box buttonBox = Box.createHorizontalBox();
			buttonBox.add(Box.createHorizontalGlue());
			buttonBox.add(saveStep);
			buttonBox.add(Box.createHorizontalStrut(5));
			buttonBox.add(loadStep);
			buttonBox.add(Box.createHorizontalStrut(5));
			buttonBox.add(confirm);
			add(buttonBox, BorderLayout.SOUTH);			
		}
		
		/**���J�B�J�B�x�s�B�J�M�w�����G���s*/
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == saveStep) {
				try {
					step.saveWithDialog(allStep);
					Info.showMessage("�x�s�B�J���\");
				} catch (IOException ee) {
					logger.info(ee.getMessage());
					Info.showError("�x�s�B�J����");
				}

			} else if (e.getSource() == loadStep) {
				try {
					step.loadWithDialog(tools).ifPresent(steps -> {	// �Y�����e �~�~��
						model.clear();
						model.addAll(steps);
						if(!steps.isEmpty()) {		// ���T�����J�B�J
							unLockFuncs(false);		// �N�u��C��w
							bitches = false;		// �]�����J�B�J�|���N�즳�Ҧ��B�J�A�ҥH���ɤT�_���A�]��false
							loadStepFlag = true;	// �N���J�B�J�X�г]��true
							Info.showMessage("���J�B�J���\");
						}
					});
				} catch (IOException ee) {
					logger.info(ee.getMessage());
					Info.showError("���J�B�J����");
				}

			} else if (e.getSource() == confirm) {
				if(model.isEmpty()) {
					int choose = JOptionPane.showConfirmDialog(DataHandlePanel.this, "�O�_�n�N��ƳB�z���ɮ׳]�����ơH", "�i�歫�]", JOptionPane.YES_NO_OPTION);
					if (choose == JOptionPane.YES_OPTION) {
						try {
							tools.setInFile(Info.getDataTransform());
							CreateFile.copy(Info.getDataTransform(), Info.getDataHandle());
							afterSheet.update(Info.getDataHandle());
							
							//�N�B�J�ɲM��
							Path step = Paths.get(Info.getDataHandleStep());
							Files.deleteIfExists(step);
							Files.createFile(step);
							
							Page.FEATURE_SELECT.setEnabled(true);
							Info.showMessage("��ƳB�z���\");
						} catch (Exception ee) {
							Page.FEATURE_SELECT.setEnabled(false);
							logger.info(ee.getMessage());
							Info.showError("��ƳB�z����");
						}
						return;
					}
				}
				
				unLockOthers(false);
				unLockFuncs(false);
				try {
					popResult(model);
					Page.FEATURE_SELECT.setEnabled(true);
					Info.showMessage("��ƳB�z���\");
				} catch (Exception ee) {
					logger.info(ee.getMessage());
					Info.showError("��ƳB�z����");
					Page.FEATURE_SELECT.setEnabled(false);
					JOptionPane.showMessageDialog(DataHandlePanel.this, "���ˬd�]�w�B�J", "�B�z�B�J���~", JOptionPane.ERROR_MESSAGE);
				}
				if(!bitches && !loadStepFlag) {	// �Y�D�T�_���A�]�D���J�B�J���A
					unLockFuncs(true);	// ����u��C
				}
				unLockOthers(true);
			}
		}
		
		/**
		 * �u���ᰵ���Ʊ�<br>
		 * 1.���J�B�J�����ұo�쪺�ɮ�<br>
		 * 2.�p�G������ -> ���������(�u������)<br>
		 * �p�G���T�w -> ��sPreviewSheet<br>
		 * �����Ҧ��B�J<br>
		 * ��sTool(�Y�ɤ�)<br>
		 * 
		 * @param stepsModel �B�J�檺�B�J
		 * @throws FileNotFoundException
		 * @throws IOException
		 */
		private void popResult(TableModel stepsModel) throws Exception {
			String funcName;
			for (Tool tool: stepsModel) {	// ���D������쪺�ʧ@
				funcName = tool.getClass().getSimpleName();
				if(!funcName.equals("RemoveFeature")) {
					tool.execute();
				}
			}
			for (Tool tool: stepsModel) {	// ������쪺�ʧ@��b�̫᭱�~��
				funcName = tool.getClass().getSimpleName();
				if (funcName.equals("RemoveFeature")) {
					tool.execute();
				}
			}
			
			//========================================
			Info.showMessage("��Ƨ�s��...");
			File file = tools.getTempOutFile();
			
			confirmSheet.update(file);	// ��s�T�{���
			int choose = JOptionPane.showConfirmDialog(DataHandlePanel.this, confirmSheet, "�O�_�ĥΦ���ƶ��H", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (choose == JOptionPane.YES_OPTION) {
				bitches = false;		// �N�T�_�X�г]��false
				loadStepFlag = false;	// �N���J�B�J�X�г]��false
				showProgress.update(new SwingWorker<Void, Void>(){
					@Override
					protected Void doInBackground() {
						try {
							// �N���浲�G�ƻs��ت��a
							CreateFile.copy(file, Info.getDataHandle());
							afterSheet.update(file);	// ��s�������
							allStep.addAll(stepsModel);	// �N�Ҧ��B�J�[�J
							step.saveAll(allStep, Info.getDataHandleStep());	// �N�o�ǨB�J�x�s�_��
							// ��s
							tools.update(Enum.title(file), Enum.content(file));	// tool�����e�ӷ�
							model.removeAll();	// �����B�J��Ҧ��B�J
							tools.adoptResult();// �]�w�ĥΦ���ƶ�
							Info.showMessage("��Ƨ�s���\�I");
							
						} catch (Exception e) {
							logger.info(e.getMessage());
							Info.showError("��Ƨ�s���ѡI");
						}
						return null;
					}
				});
			} else {
				tools.abandoneResult();
				Info.showMessage("�w������s�I");
			}
			confirmSheet.clear();	// �M���T�{���
		}
	}

	/**�w������*/
	private class PreViewPanel extends JPanel {
		private static final long serialVersionUID = -8552488333943233617L;
		public PreViewPanel() {
			setLayout(new BorderLayout());
			setBorder(new TitledBorder(null, "��ƹw��", TitledBorder.CENTER, TitledBorder.BELOW_TOP));
			
			beforeSheet.setTableTitle("��Ƶ��G(�B�z�e)");
			afterSheet.setTableTitle("��Ƶ��G(�B�z��)");
			JSplitPane previewSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, beforeSheet, afterSheet);
			previewSplit.setBorder(null);
			previewSplit.setDividerLocation(previewSplit.getPreferredSize().height / 2);
			add(previewSplit, BorderLayout.CENTER);
			
			unLockFuncs(false);
			unLockOthers(false);
		}
	}
	
	/**
	 * ��Ƶ��G(�B�z�e) �w�����<br>
	 * �~��{@link AbstractPreview}
	 */
	private class BeforeSheet extends AbstractPreview {
		private static final long serialVersionUID = -7287660204910605281L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Info.showMessage("���J�s��ƶ�...");
			int choose = chooser.showOpenDialog(this);
			
			// �Y��ܤF�ɮסA�h��J��ܤF�����ɮ�(�ɦW)
			if (choose == JFileChooser.APPROVE_OPTION) {
				try {
					TitleIndex.append(chooser.getSelectedFile(), Info.getImpAndExpTemp());
					LSH.append(Info.getImpAndExpTemp(), Info.getDataTransform());
					setFile();
				} catch (Exception ee) {
					logger.info(ee.getMessage());
				}
			} else {
				Info.showMessage("�������J��ƶ�");
			}
		}
	}
	
	/**�~��{@link AbstractTools}*/
	private class Tools extends AbstractTools {
		/**
		 * Tool�غc�l
		 * @param inFile ��J�ɮ�
		 * @param outPath ��X���|
		 * @param title �����D�}�C
		 * @param content ���e�}�C
		 */
		public Tools(String inFile, String outPath, String[] title, String[][] content) {
			super(inFile, outPath, title, content);
		}
		
		/**
		 * Tool�غc�l
		 * @param inFile ��J�ɮ�
		 * @param outPath ��X���|
		 */
		public Tools(String inFile, String outPath) {
			super(inFile, outPath);
		}
		
		/**
		 * ���U�������s�n�����ʧ@
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			model.removeItem(removeIndex);
		}
	}
	
	/**�~��{@link PanelTableModel}*/
	private class TableModel extends PanelTableModel<Tool> {
		private static final long serialVersionUID = -7797314770182057947L;

		public void removeItem(int index) {
			if(loadStepFlag) {	// �Y�B�J��O���J���A�@���U�������s�h�����R��
				int choose = JOptionPane.showConfirmDialog(DataHandlePanel.this, "�N�R���Ҧ��B�J�C�O�_�R���H", "�T�{�R��", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(choose == JOptionPane.YES_OPTION) {
					loadStepFlag = false;
					unLockFuncs(true);	// �Ѱ��u��C
					removeAll();		// �����Ҧ��B�J
				}
				return;
			}
			
			String funcName = get(index).getClass().getSimpleName();
			switch(funcName) {
				case "NormalizeFeature":
				case "StandardizeFeature":
				case "TargetEncoding":
					bitches = false;	 // ���]�T�_���A
					unLockFuncs(true);
					break;
			}
			
			remove(index);
			Info.showMessage("�w�������w�B�J");
		}
		
		public void removeAll() {
			bitches = false;	 // ���]�T�_���A
			super.clear();
			Info.showMessage("�w�����Ҧ��B�J");
		}
	}
}
