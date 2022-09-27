package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import guiComponent.AbstractPreview;
import guiComponent.ShowProgress;
import guiFunction.CreateFile;
import guiFunction.Enum;
import guiFunction.FeatureInfo;
import guiFunction.LSH;
import guiFunction.Step;
import guiFunction.TitleIndex;
import predict.FeatureSelect;
import predict.FeatureSelectException;
import preprocess.RemoveFeature;

class FeatureSelectPanel extends JPanel implements ActionListener, Panel {
	private static final long serialVersionUID = 2449969085578476145L;
	private Logger logger = Logger.getLogger("FeatureSelectPanel");
	private Font font20 = new Font("�L�n������", Font.PLAIN, 20);
	private JFileChooser chooser = new JFileChooser(Info.getDesktop());
	private JButton confirmButton = new JButton("�T�{�ÿ��");
	private RankFeature rankFeature = new RankFeature();
	private BeforeSheet beforeSheet = new BeforeSheet();
	private AbstractPreview afterSheet = AbstractPreview.newSheetWithoutImport();
	private ShowProgress showProgress = new ShowProgress();
	private Border normalBorder = LineBorder.createBlackLineBorder();
	private Border errorBorder = new LineBorder(Color.RED, 3, true);
	private LinkedHashMap<String, String> attrsRank;

	public FeatureSelectPanel() {
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
		setLayout(new BorderLayout(5, 5));
		
		beforeSheet.setTableTitle("��Ƥ��e(����e)");
		afterSheet.setTableTitle("��Ƥ��e(�����)");
		JSplitPane previewPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, beforeSheet, afterSheet);
		previewPanel.setDividerLocation(previewPanel.getPreferredSize().height / 2);
		add(previewPanel, BorderLayout.CENTER);
		
		JLabel tipLabel = new JLabel("�п���n�Ω�w�����S�x�G");
		confirmButton.addActionListener(this);
		rankFeature.setViewportBorder(normalBorder);
		
		JPanel eastPanel = new JPanel(new GridBagLayout());
		add(eastPanel, BorderLayout.EAST);
		Insets inset = new Insets(5, 0, 5, 0);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 0.5;
		gbc.insets = inset;
		gbc.anchor = GridBagConstraints.LINE_START;
		eastPanel.add(tipLabel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 0.5;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = inset;
		eastPanel.add(rankFeature, gbc);

		gbc = new GridBagConstraints();
		gbc.weightx = 0.5;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = inset;
		eastPanel.add(confirmButton, gbc);
	}
	
	private class BeforeSheet extends AbstractPreview {
		private static final long serialVersionUID = -7287660204910605281L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int choose = chooser.showOpenDialog(getParent());
			if (choose == JFileChooser.APPROVE_OPTION) {
	        	try {
	        		//�p��S�x���n�ת���ƨӷ�
	        		TitleIndex.append(chooser.getSelectedFile(), Info.getImpAndExpTemp());
	        		LSH.append(Info.getImpAndExpTemp(), Info.getDataHandle());
					//�ϥΪ̿�ܪ��ɮקڭ̵����e�ӨB�J-��ƳB�z���ɮ�
					setFile();	// �]�w�ɮ� �}�l�S�x���
					Info.showMessage("��Ƹ��J���\");
					
				} catch (Exception ee) {
					logger.info(ee.getMessage());
					Info.showError("��ƳB�z����");
					JOptionPane.showMessageDialog(FeatureSelectPanel.this, ee.getMessage(), "���G�X�F�I���D...", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	@Override
	public void setFile() {
		if(Files.notExists(Paths.get(Info.getDataHandle()))) {
			beforeSheet.setImportEnabled(true);
			return;
		}
		beforeSheet.setImportEnabled(false);
		
		Page.FEATURE_SELECT.setEnabled(false);	// �����ϥΪ̦b�{�����槹���e���U���s
		showProgress.update(new SwingWorker<Void, Void>(){
			@Override
			protected Void doInBackground() {
				confirmButton.setEnabled(false);
				rankFeature.clear();
				beforeSheet.clear();
				afterSheet.clear();
				
				try {
					showProgress.setText("���bŪ�����");
					beforeSheet.update(Info.getDataHandle());	//���J��ƳB�z���ɮ�
					Info.showMessage("��Ƹ��J���\");
				} catch (Exception e) {
					logger.info(e.getMessage());
					Info.showError("��Ƹ��J����");
					JOptionPane.showMessageDialog(FeatureSelectPanel.this, e.getMessage(), "���G�X�F�I���D...", JOptionPane.ERROR_MESSAGE);
					return null;
				}
				
				try {
					showProgress.setText("���b�i��S�x���n�׵���");			
					attrsRank = FeatureSelect.startRank(Info.getDataHandle(), Info.getTransFeature(), Info.getTempStep());
					rankFeature.update(attrsRank);		//��s�S�x�ƧǦC��
					Info.showMessage("�S�x���n�׵��񦨥\");
					
				} catch (FeatureSelectException e) {	//��ƶ����ŦX�n�D
					Info.showError("��ƶ����ŦX�n�D");
					
					int choose = JOptionPane.showConfirmDialog(FeatureSelectPanel.this, e.getMessage(), "���G�X�F�I���D...", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
					if(choose == JOptionPane.YES_OPTION) {
						try {
							//�q�`���XPredictorsException�ҥ~�ɡA�N���ƶ����ŦX�n�D�A�B�ȮɨB�J�ɤw�g�s�b
							Step.executeRemoveFeature(Info.getDataHandle(), Info.getTempStep(), Info.getTransFeature());
							CreateFile.copy(Info.getTransFeature(), Info.getDataHandle());
							Files.delete(Paths.get(Info.getTempStep()));
							setFile();//�A����Ū��
							
						} catch (Exception ee) {
							logger.info(ee.getMessage());
							Info.showError("�����L�ίS�x�ɵo�Ϳ��~");
							JOptionPane.showMessageDialog(FeatureSelectPanel.this, "�����L�ίS�x�ɵo�Ϳ��~");
						}
						
					}else {	// JOptionPane.NO_OPTION
						//�YWeka�S�x����L�k�ϥΡA�h�������ѩҦ��S�x�ѨϥΪ̿��
						setNoRankOptions();
					}
					
				} catch (Exception e) {
					setNoRankOptions();
				}
				
				confirmButton.setEnabled(true);
				Page.FEATURE_SELECT.setEnabled(true);
				return null;
			}
		});
	}
	
	@Override
	public void reset() {
		rankFeature.clear();
		beforeSheet.clear();
		afterSheet.clear();
		beforeSheet.setImportEnabled(true);
	}
	
	/**��ܥ����ѱƦW���S�x�ﶵ*/
	private void setNoRankOptions() {
		Info.showError("�S�x���n�׵��񥢱�");
		try {
			String[] title = Enum.title(Info.getDataHandle());
			rankFeature.update(Arrays.copyOf(title, title.length - 1));	// ����̫ܳ�@�檺��N�ɶ��γ¾K�ɶ�
			JOptionPane.showMessageDialog(this, "�L�k�p��S�x���n��");
		} catch(Exception e) {
			logger.info(e.getMessage());
			JOptionPane.showMessageDialog(this, "�L�k���ѥ���S�x�ѱz��ܡA�ШϥΨ�L�ɮ�");
		}
	}

	/**���U�u�T�{�ô����v���s�ɭn�����ʧ@*/
	@Override
	public void actionPerformed(ActionEvent e) {
		// �ˬd�ϥΪ̬O�_�Q�R���������ݩʡA�Y�O�h����
		ArrayList<String> keep;
		if((keep = rankFeature.listRemove(false)).isEmpty()) {
			// �Y�O�d�S�x�}�C���šA�N��ϥΪ̷Q�R���������ݩʡC�o���Q���\�I
			afterSheet.clear();		//�M���S�x�����w�����
			remindError(rankFeature, "�Цܤֿ�ܤ@���ݩʡC");
			return;
		}
		
		// �إ߯S�x�������T�ɮ�
		try {
			if(attrsRank == null) {	//�Y�L�k�i��S�x�ƧǡA��X�u�L�k���ѡv���S�x�ɮ�
				FeatureInfo.buildWithoutScore(keep, Info.getFeatureInfo());
			}else{
				FeatureInfo.buildWithScore(keep, attrsRank, Info.getFeatureInfo());
			}
		} catch (Exception ee) {
			logger.info(ee.getMessage());
			Page.DATA_SPLIT.setEnabled(false);
			Info.showError("�إ߯S�x���n���ɮץ���");
			return;
		}
		
		// �p�G�ϥΪ̤������S�x�A�N��l�ɮת�����X
		ArrayList<String> remove = rankFeature.listRemove(true);
		if(remove.isEmpty()) {
			try {
				CreateFile.copy(Info.getDataHandle(), Info.getFeatureSelect());
				afterSheet.update(Info.getFeatureSelect());
				Page.DATA_SPLIT.setEnabled(true);
				Info.showMessage("�������S�x�A�S�x������\");
			} catch (Exception ee) {
				logger.info(ee.getMessage());
				Page.DATA_SPLIT.setEnabled(false);
				Info.showError("�إ߯S�x����ɮץ���");
				JOptionPane.showMessageDialog(this, "�إ߯S�x����ɮץ���");
			}
			return;
		}
		
		//�ˬd��ƳB�z�������B�J�ɬO�_�s�b
		//�s�b�G�ݭn���s�վ�B�J�ɪ���ơA��w�g�������S�x����
		//���s�b�G���L�B�J�վ�A��B�J�ɧR���C
		if(Files.exists(Paths.get(Info.getDataHandleStep()))) {
			try {
				//���s�վ�B�J�ɡA���������Ψ쪺�S�x�B�J
				Step.rearrange(Info.getDataHandleStep(), remove, Info.getStep());
			} catch (IOException ee) {
				logger.info(ee.getMessage());
				Page.DATA_SPLIT.setEnabled(false);
				Info.showError("�إ߽վ�᪺�B�J�ɥ���");
				JOptionPane.showMessageDialog(this, "�إ߽վ�᪺�B�J�ɥ���");
				return;
			}
		}else {
			try {
				Files.deleteIfExists(Paths.get(Info.getStep()));
			} catch (IOException ee) {
				logger.info(ee.getMessage());
				Info.showError("�����y�{���楢��");
				JOptionPane.showMessageDialog(this, "�����y�{���楢�ѡA�ЦA�դ@��");
			}
		}
		
		// ���o�ϥΪ̱����������W�٤ΫغcRemoveFeature���ǤJ�Ѽ�
		try {
			RemoveFeature.exec(Info.getDataHandle(), Info.getFeatureSelect(), remove);
			afterSheet.update(Info.getFeatureSelect());
			Page.DATA_SPLIT.setEnabled(true);
			Info.showMessage("�����S�x���\");
		} catch (Exception ee) {
			logger.info(ee.getMessage());
			Page.DATA_SPLIT.setEnabled(false);
			Info.showError("�����S�x����");
			JOptionPane.showMessageDialog(this, "�����S�x����");
		}
	}
	
	private void remindError(JComponent comp, String errorMessage) {
		comp.setBorder(errorBorder);
		JOptionPane.showMessageDialog(this, errorMessage, "���ܰT��", JOptionPane.WARNING_MESSAGE);
		comp.setBorder(normalBorder);
	}

	public class RankFeature extends JScrollPane {
		private static final long serialVersionUID = 3838537531891727458L;
		private int defaultRowHeight = 30;
		private JTable table = new JTable() {
			private static final long serialVersionUID = -1340347628284121524L;
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0) {
					return Boolean.class;
				}
				return String.class;
			}
		};
		private String[] all = {"����", "���n��", "���W��"};
		private String[] none = {"������", "���n��", "���W��"};

		public RankFeature() {
			table.setRowHeight(defaultRowHeight);
			table.setDefaultEditor(Object.class, null);
			table.setFillsViewportHeight(true);
			table.setFont(font20);
			table.setDragEnabled(false);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					int index = table.rowAtPoint(e.getPoint());
					if (index != -1) {
						boolean check = (boolean) table.getValueAt(index, 0);
						table.setValueAt(!check, index, 0);
					}
				}
			});
			setViewportView(table);
			
			DefaultTableCellRenderer r = (DefaultTableCellRenderer) table.getDefaultRenderer(Object.class);
			r.setHorizontalAlignment(SwingConstants.CENTER);
			table.setDefaultRenderer(Object.class, r);
			
			JTableHeader columnHeader = table.getTableHeader();
			columnHeader.setReorderingAllowed(false);
			columnHeader.setFont(font20);
			columnHeader.setToolTipText("�I������i����/��������");
			((DefaultTableCellRenderer) columnHeader.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
			columnHeader.addMouseListener(firstColumnSelect);
		}
		
		private MouseAdapter firstColumnSelect = new MouseAdapter() {
			private boolean flag = true;	//true ���� | false ��������
			public void mousePressed(MouseEvent e) {
				int index = table.columnAtPoint(e.getPoint());
				if (index == 0) {	//��ܭ��檺���D
					DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
					flag = !flag;
					if(flag) {
						tableModel.setColumnIdentifiers(all);
					}else {
						tableModel.setColumnIdentifiers(none);
					}
					for(int i = 0, count = table.getRowCount(); i < count; i++) {
						table.setValueAt(flag, i, 0);
					}
				}
			}			
		};
		
		/**�إߦ����n�ױƧǪ����*/
		private void update(LinkedHashMap<String, String> rank) {
			Object[][] content = new Object[rank.size()][3];
			int index = 0;
			double score;
			for(Entry<String, String> rankEntry: rank.entrySet()) {
				score = Double.parseDouble(rankEntry.getValue());
				content[index++] = new Object[] {score >= 0, rankEntry.getValue(), rankEntry.getKey()};
				// �Y�Ȭ����A�w�����ġF�Y���t�A�w����������
			}
			((DefaultTableModel) table.getModel()).setDataVector(content, all);
		}
		
		/**�إߨS�����n�ױƧǪ����*/
		private void update(String[] title) {
			Object[][] content = new Object[title.length][3];
			int index = 0;
			for(String s: title) {
				content[index++] = new Object[] {true, "�L�k����", s};
			}
			((DefaultTableModel) table.getModel()).setDataVector(content, all);
		}
		
		/**�M�����*/
		private void clear() {
			((DefaultTableModel) table.getModel()).setColumnCount(0);
		}
		
		/**
		 * �C�|���O�d�β������S�x
		 * @Param remove �O�_�C�|���������S�x�C�Y��false�A�C�|���O�d���S�x
		 * @return �S�x�}�C
		 * @throws IllegalStateException �Y�ϥΪ̱������Ҧ����ɡA�|�ߥX�ҥ~
		 */
		private ArrayList<String> listRemove(boolean listRemove) {
			int count = table.getRowCount();	// �S�x�Ӽ�
			ArrayList<String> list = new ArrayList<>(count);
			for (int i = 0; i < count; i++) {
				if ((boolean) table.getValueAt(i, 0) ^ listRemove)	{// ������
					// ��쬰true�A�S�x�O�d�F��쬰false�A�S�x����
					list.add(table.getValueAt(i, 2).toString());
				}
			}
			return list;
		}
	}
}
