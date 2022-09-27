package guiComponent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import gui.Info;
import guiFunction.LSH;
import guiFunction.LoadFile;
import guiFunction.TitleIndex;
import preprocess.Split;

/**�����ܪ��*/
public abstract class AbstractPreview extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1998540816189543580L;
	private Logger logger = Logger.getLogger("AbstractPreview");
	private enum Mode {WITHOUT_IMPORT, NOTHING, ALL};
	private Mode mode;	//�ĥμҦ�
	private File file;
	private JFileChooser chooser;
	private JLabel title;
	private JButton importButton, popUpButton, storeButton;
	private JFrame popUp;
	private ViewTable popUpTable, viewTable;
	private static Font font16 = new Font("�L�n������", Font.PLAIN, 16);
	private static Font font12 = new Font("�L�n������", Font.PLAIN, 12);
	private static String[] imageName = {"\\import.png", "\\popUp.png", "\\store.png"};
	private static ImageIcon[] icon = LoadFile.fromIcons(Info.getIconPath(), imageName, 30, 30);
	
	/**�إߵL������s���w�����*/
	public static AbstractPreview newNothingSheet() {
		return new AbstractPreview(Mode.NOTHING) {
			private static final long serialVersionUID = 2668494334011211477L;
			@Override
			public void actionPerformed(ActionEvent e) {}//���U�פJ���s�n�����ʧ@�A���ݹ�@
		};
	}
	
	/**�إߥ]�t�ץX�μu�����w�����*/
	public static AbstractPreview newSheetWithoutImport() {
		return new AbstractPreview(Mode.WITHOUT_IMPORT) {
			private static final long serialVersionUID = -7704785758861677448L;
			@Override
			public void actionPerformed(ActionEvent e) {}//���U�פJ���s�n�����ʧ@�A���ݹ�@
		};
	}
	
	/**�إߥ]�t�פJ�B�ץX�μu�����w�����*/
	protected AbstractPreview() {
		this(Mode.ALL);
	}
	
	private AbstractPreview(Mode mode) {
		this.mode = mode;
		viewTable = new ViewTable();
		title = new JLabel("�L��ƥi�w��", JLabel.CENTER);
		
		// �u�����
		switch(mode) {
		case NOTHING:
			setLayout(new BorderLayout());
			add(title, BorderLayout.NORTH);
			add(viewTable, BorderLayout.CENTER);
			break;
		case ALL:
			// �]�t�פJ��ƶ��u���B�x�s���s�Ϊ��
			importButton = new JButton(icon[0]);
			importButton.setToolTipText("�פJ�ɮ�");
			importButton.addActionListener(this);
		case WITHOUT_IMPORT:
			setLayout(new GridBagLayout());
			chooser = new JFileChooser();
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
			
			popUpButton = new JButton(icon[1]);
			popUpButton.setEnabled(false);
			popUpButton.setToolTipText("�u�X�s�����A�H�����˵��Ӫ��");
			popUpButton.addActionListener(listener);
			
			storeButton = new JButton(icon[2]);
			storeButton.setEnabled(false);
			storeButton.setToolTipText("�x�s�Ӫ��");
			storeButton.addActionListener(listener);

			popUpTable = new ViewTable();
			popUp = new PopUp(popUpTable);

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.weightx = 1.0;
			gbc.anchor = GridBagConstraints.CENTER;
			add(title, gbc);
			
			if(importButton != null) {
				gbc = new GridBagConstraints();
				add(importButton, gbc);
			}

			gbc = new GridBagConstraints();
			add(popUpButton, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			add(storeButton, gbc);

			gbc = new GridBagConstraints();
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.fill = GridBagConstraints.BOTH;
			add(viewTable, gbc);
			break;
		}
	}
	
	/**��s���*/
	public void update(String fileName) throws FileNotFoundException, IOException {
		update(new File(fileName));
	}

	/**��s���*/
	public void update(File file) throws FileNotFoundException, IOException {
		this.file = file; 	// �]����Ʃ���ݭn�S�O���B�z�覡
		viewTable.update(file.toPath());
		if (mode != Mode.NOTHING) { // �C�C�̫�|���y���s���A�ݭn����(�����)
			popUpTable.update(viewTable.table.getModel());
			popUpButton.setEnabled(true);
			storeButton.setEnabled(true);
		}
	}

	/**�M�����*/
	public void clear() {
		viewTable.clear();
		if (mode != Mode.NOTHING) {
			popUpTable.clear();
			popUpButton.setEnabled(false);
			storeButton.setEnabled(false);
		}
	}
	
	/**
	 * �^�Ǹ�Ƶ���
	 * @return ��Ƶ���
	 */
	public int getRowsCount() {
		return viewTable.getRowCount();
	}
	
	/**
	 * �^������
	 * @return ����
	 */
	public int getColumnCount() {
		return viewTable.getColumnCount();
	}

	/**�]�w�����D*/
	public void setTableTitle(String text) {
		title.setText(text);
		if (mode != Mode.NOTHING) {
			popUp.setTitle(text);
		}
	}
	
	/**�ҥΩθT�ζפJ���s*/
	public void setImportEnabled(boolean b) {
		if(mode != Mode.ALL) {
			throw new IllegalArgumentException("�L�k�ҥΩθT�ζפJ���s");
		}
		importButton.setEnabled(b);
	}
	
	/**���U�u�����s | ���U�x�s���s*/
	private ActionListener listener = e -> {
		if (e.getSource() == popUpButton) {			// ���U�u�����s
			popUp.setVisible(true);
			
		} else if (e.getSource() == storeButton) {	// ���U�x�s���s
			int choose = chooser.showSaveDialog(this);
			if (choose == JFileChooser.APPROVE_OPTION) {
				try {
					// �ץX�ɮ׮ɥ���������y����
					LSH.remove(file, Info.getImpAndExpTemp());
					String path = chooser.getSelectedFile().toString();
					if(!path.endsWith(".csv")) {
						path += ".csv";
					}
					// �A���s�����D�s�X
					TitleIndex.append(Info.getImpAndExpTemp(), path);
				} catch (Exception ee) {
					logger.info(ee.getMessage());
				}
			}
		}
	};
	
	private class PopUp extends JFrame {
		private static final long serialVersionUID = 3312840970342114873L;
		public PopUp(ViewTable viewTable) {
			setTitle("��ƹw��");
			setContentPane(viewTable);
			setSize(800, 500);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setLocationRelativeTo(AbstractPreview.this);
		}
	}

	private class ViewTable extends JScrollPane { // ���ۮe��java�Ҧ������A���������禡�ŧi���R�A�A�������O�]���P���R�A
		private static final long serialVersionUID = 129864934755437301L;
		private int defaultRowHeight = font16.getSize() + 10;
		private JTable table;
		private JLabel cornerLabel;
		private JTableHeader columnHeader;
		private JList<Integer> rowHeader;
		private TableColumnModel columnModel;

		public ViewTable() {
			table = new JTable();
			table.setFont(font16);
			table.setToolTipText("�����i�۰ʽվ����j�p");
			table.setFillsViewportHeight(true);
			table.setDefaultEditor(Object.class, null);
			table.setRowHeight(defaultRowHeight);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						resizeColumn();
					}
				}
			});
			setViewportView(table);
			
			cornerLabel = new JLabel();
			cornerLabel.setFont(font12);
			cornerLabel.setHorizontalAlignment(JLabel.CENTER);
			setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, cornerLabel);
			
			columnHeader = table.getTableHeader();
			columnHeader.setReorderingAllowed(false);
			columnHeader.setFont(font16);
			((DefaultTableCellRenderer) columnHeader.getDefaultRenderer())
					.setHorizontalAlignment(SwingConstants.CENTER);

			rowHeader = new JList<Integer>(new AbstractListModel<Integer>() {
				private static final long serialVersionUID = 6777791480803556423L;
				@Override
				public int getSize() {
					return table.getRowCount();
				}
				@Override
				public Integer getElementAt(int index) {
					return index + 1;
				}
			});
			rowHeader.setOpaque(false);
			rowHeader.setFixedCellWidth(50);

			MouseInputAdapter mouseAdapter = new MouseInputAdapter() {
				Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
				Cursor oldCursor;
				int index = -1, oldY = -1;
				int previ, nexti;
				int y, inc, oldRowHeight, rowHeight;

				@Override
				public void mouseMoved(MouseEvent e) {
					previ = rowHeader.locationToIndex(new Point(e.getX(), e.getY() - 3));
					nexti = rowHeader.locationToIndex(new Point(e.getX(), e.getY() + 3));
					if (previ != -1 && previ != nexti) {
						if (!isResizeCursor()) {
							oldCursor = rowHeader.getCursor();
							rowHeader.setCursor(resizeCursor);
							index = previ;
						}
					} else if (isResizeCursor()) {
						rowHeader.setCursor(oldCursor);
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (isResizeCursor()) {
						rowHeader.setCursor(oldCursor);
						index = -1;
						oldY = -1;
					}
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					if (isResizeCursor() && index != -1) {
						y = e.getY();
						if (oldY != -1) {
							inc = y - oldY;
							oldRowHeight = table.getRowHeight(index);
							if (oldRowHeight > defaultRowHeight || inc > 0) {
								rowHeight = Math.max(defaultRowHeight, oldRowHeight + inc);
								table.setRowHeight(index, rowHeight);
							}
						}
						oldY = y;
					}
				}

				private boolean isResizeCursor() {
					return rowHeader.getCursor() == resizeCursor;
				}
			};
			rowHeader.addMouseListener(mouseAdapter);
			rowHeader.addMouseMotionListener(mouseAdapter);
			rowHeader.setCellRenderer(new RowHeaderRenderer(table));
		}

		private void update(Path file) throws FileNotFoundException, IOException {
			Vector<Vector<String>> content;
			try(Stream<String> stream = Files.lines(file, Charset.defaultCharset())){
				content = stream.skip(1).map(Split::withoutQuotesOfVector).collect(Collectors.toCollection(Vector::new));
			}
			Vector<String> title;
			try(BufferedReader br = Files.newBufferedReader(file, Charset.defaultCharset())) {
				title = Split.withoutQuotesOfVector(br.readLine());
			}
			DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
			tableModel.setDataVector(content, title);
			update(tableModel);
		}

		private void update(TableModel tableModel) {
			table.setModel(tableModel);
			columnModel = table.getColumnModel();
			
			TableColumn firstColumn = columnModel.getColumn(0);
			if (firstColumn.getIdentifier().equals("�y����")) {
				table.removeColumn(firstColumn);
				// �]����Ʃ���ݭn�S�O���B�z�覡
				// �C�C�Ĥ@��|���y���s���A�ݭn����(�����)
			}
			setRowHeaderView(rowHeader);
			int rowCount = table.getRowCount();
			cornerLabel.setText(rowCount + "");
			cornerLabel.setToolTipText("�@" + rowCount + "��(���]�t���D)");
			fillColumn();	//�P�_�����P�_
		}

		private void clear() {
			DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
			if (tableModel != null) {
				tableModel.setColumnCount(0);
			}
			setRowHeader(null);
		}
		
		private int getRowCount() {
			return table.getRowCount();
		}
		
		private int getColumnCount() {
			return table.getColumnCount();
		}

		private void fillColumn() {
			if (getWidth() >= columnModel.getTotalColumnWidth()) {
				// �YScrollPane Width >= Table Width�h�����
				table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
			}else {
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			}
		}

		private void resizeColumn() { // �վ����ܦ��A���j�p
			table.setRowHeight(defaultRowHeight);
			TableCellRenderer renderer;
			Component comp;
			for (int column = 0; column < table.getColumnCount(); column++) { // �Y�_�A�h�̤��e�e�׽վ����
				int width = 25; // Min width
				for (int row = 0; row < table.getRowCount(); row++) {
					renderer = table.getCellRenderer(row, column);
					comp = table.prepareRenderer(renderer, row, column);
					width = Math.max(comp.getPreferredSize().width + 5, width);
				}
				if (width > 300)
					width = 300;
				columnModel.getColumn(column).setPreferredWidth(width);
			}
		}

		private class RowHeaderRenderer extends JLabel implements ListCellRenderer<Object> {
			private static final long serialVersionUID = -1703527603213216336L;
			private JTable table;
			private JTableHeader columnHeader;

			public RowHeaderRenderer(JTable table) {
				this.table = table;
				columnHeader = table.getTableHeader();
				setOpaque(true);
				setBorder(UIManager.getBorder("TableHeader.cellBorder"));
				setHorizontalAlignment(CENTER);
				setForeground(columnHeader.getForeground());
				setBackground(columnHeader.getBackground());
				setFont(columnHeader.getFont());
				setDoubleBuffered(true);
			}

			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				setText((value == null) ? "" : value.toString());
				setPreferredSize(new Dimension((int) getPreferredSize().getWidth(), table.getRowHeight(index)));
				// trick to force repaint on JList (set updateLayoutStateNeeded = true) on
				// BasicListUI
				list.firePropertyChange("cellRenderer", 0, 1);
				return this;
			}
		}
	}
}
