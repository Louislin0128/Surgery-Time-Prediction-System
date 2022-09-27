package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import guiComponent.AbstractPreview;
import guiComponent.Graph;

class ViewGraph extends JFrame implements ActionListener, ItemListener {
	private static final long serialVersionUID = 890470383572123962L;
	private Logger logger = Logger.getLogger("ViewGraph");
	private JTextField[] rangeText = new JTextField[2];
	private JButton checkButton = new JButton("�T�w");
	private AbstractPreview sheet = AbstractPreview.newNothingSheet();
	private Graph graph = new Graph();
	private File[] listFiles;
	private File chooseFile;
	private Border normalBorder = UIManager.getBorder("TextField.border");
	private Border errorBorder = new LineBorder(Color.RED, 3);
	private String[] items = {"������", "����", "��u��"};
	private String name = items[0];
	private JComboBox<String> fileCombo = new JComboBox<>(),
			graphCombo = new JComboBox<>(items);

	public ViewGraph() {
		setLayout(new BorderLayout(10, 10));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setTitle("��ܲέp�Ϫ�");
		setSize(1024, 768);
		setLocationRelativeTo(null);

		JLabel[] text = new JLabel[2];
		text[0] = new JLabel("��ܪ��ɮ׬��G");
		text[1] = new JLabel("�����ͪ��Ϫ��G");
		
		fileCombo.addItemListener(this);
		graphCombo.addItemListener(this);

		JLabel[] rangeLabel = new JLabel[4];
		rangeLabel[0] = new JLabel("�]�w�d��G��", JLabel.CENTER);
		rangeLabel[1] = new JLabel("��", JLabel.CENTER);
		rangeLabel[2] = new JLabel(" ~ ��", JLabel.CENTER);
		rangeLabel[3] = new JLabel("��", JLabel.CENTER);
		
		rangeText[0] = new JTextField(); // �ﶵ��J�Ʀr
		rangeText[1] = new JTextField();
		
		checkButton.addActionListener(this);
		sheet.setBorder(LineBorder.createBlackLineBorder());
		graph.setPreferredSize(getMaximumSize());
		
		Box chooseBox = Box.createHorizontalBox();
		chooseBox.add(text[0]);
		chooseBox.add(fileCombo);
		chooseBox.add(Box.createHorizontalStrut(15));
		chooseBox.add(text[1]);
		chooseBox.add(graphCombo);
		chooseBox.add(Box.createHorizontalStrut(15));
		
		Box setBox = Box.createHorizontalBox();
		setBox.add(rangeLabel[0]); // JLabel �]�w���ն����G
		setBox.add(rangeText[0]); // ��J�Ʀr ��
		setBox.add(rangeLabel[1]); // JLabel % | ��
		setBox.add(rangeLabel[2]); // JLabel ~
		setBox.add(rangeText[1]); // ��J�Ʀr �k
		setBox.add(rangeLabel[3]); // JLabel %����� | �������
		setBox.add(checkButton);
		
		Box northBox = Box.createVerticalBox();
		northBox.add(chooseBox);
		northBox.add(Box.createVerticalStrut(10));
		northBox.add(setBox);
		add(northBox, BorderLayout.NORTH);

		JSplitPane previewPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sheet, graph);
		previewPanel.setBorder(null);
		previewPanel.setDividerLocation(previewPanel.getPreferredSize().width / 2);
		add(previewPanel, BorderLayout.CENTER);
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			if(e.getSource() == fileCombo) {
				chooseFile = listFiles[fileCombo.getSelectedIndex()];
				try {
					sheet.update(chooseFile);
					sheet.setTableTitle(fileCombo.getSelectedItem().toString());
					rangeText[0].setText("");
					rangeText[1].setText("");
					graph.update(chooseFile, name, 0, 0);
				} catch (Exception ee) {
					logger.info(ee.getMessage());
				}	
			}else if(e.getSource() == graphCombo) {
				name = graphCombo.getSelectedItem().toString(); // graph �W��
				rangeText[0].setText("");
				rangeText[1].setText("");
				graph.update(chooseFile, name, 0, 0);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String num1 = rangeText[0].getText();
		String num2 = rangeText[1].getText();
		int num1Int = 0;
		int num2Int = 0;
		int dataSize = sheet.getRowsCount();	// ����`����(���]�t���D)

		try { // �ˬd��J�Ʀr�O�_�X�k
			num1Int = Integer.parseInt(num1);
		} catch (NumberFormatException error) {
			setErrorBorder(rangeText[0], "�п�J����ơC");
			return;
		}
		try { // �ˬd��J�Ʀr�O�_�X�k
			num2Int = Integer.parseInt(num2);
		} catch (NumberFormatException error) {
			setErrorBorder(rangeText[1], "�п�J����ơC");
			return;
		}
		if (num1Int <= 0 || num1Int > dataSize) {
			setErrorBorder(rangeText[0], "�п�J���`���ƭȡC");
			return;
		} else if (num2Int <= 0 || num2Int > dataSize) {
			setErrorBorder(rangeText[1], "�п�J���`���ƭȡC");
			return;
		} else if (num1Int > num2Int) {
			setErrorBorder(rangeText[0], "�����J�涷�p�󵥩�k���J�檺�ƭȡC");
			return;
		} else if ((num2Int - num1Int) > 40) {
			setErrorBorder(rangeText[0], "�̦h�u�e�{40����Ƴ�~~�C");
			return;
		}
		graph.update(chooseFile, name, Integer.valueOf(rangeText[0].getText()),
				Integer.valueOf(rangeText[1].getText()));
	}

	private void setErrorBorder(JComponent comp, String errorMessage) {
		comp.setBorder(errorBorder);
		JOptionPane.showMessageDialog(null, errorMessage, "���ܰT��", JOptionPane.WARNING_MESSAGE);
		comp.setBorder(normalBorder);
	}

	public void update(String inPath) {
		fileCombo.removeAllItems();
		listFiles = new File(inPath).listFiles();
		String fileName;
		for (int i = 0; i < listFiles.length; i++) {
			fileName = listFiles[i].getName();
			fileCombo.addItem(fileName.substring(0, fileName.length() - 4));
		}
	}
}