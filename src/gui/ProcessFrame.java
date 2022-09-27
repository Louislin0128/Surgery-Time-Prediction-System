package gui;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import guiFunction.LoadFile;

class ProcessFrame extends JFrame implements PropertyChangeListener {
	private static final long serialVersionUID = -8412388088390642503L;
	private static Logger logger = Logger.getLogger("ProcessFrame");
	enum Mode{SHOW_PAGE, CLEAR_PROCESS_AND_TEMP, CLEAR_ALL, SHOWMESSAGE, SHOWERROR}
	private Border emptyBorder = new EmptyBorder(10, 10, 10, 10);
	private Font font38 = new Font("�L�n������", Font.PLAIN, 38);
	private Font font32 = new Font("�L�n������", Font.PLAIN, 32);
	private Font font30 = new Font("�L�n������", Font.PLAIN, 30);
	private Font font26 = new Font("�L�n������", Font.PLAIN, 26);
	private Font font24 = new Font("�L�n������", Font.PLAIN, 24);
	private Font font22 = new Font("�L�n������", Font.PLAIN, 22);
	private Font font20 = new Font("�L�n������", Font.PLAIN, 20);
	private Color sloganColor = Color.decode("#25119e");
	private Page nowPage, prePage; 	// �ثe�� | ���e��
	private Page[] pages = Page.values();
	private int procNum = pages.length - 2;	// �����ƶq�A���]�t��ܭ����ΥD����
	private JTextField[] rectangle = new JTextField[procNum]; 	// �ΰ}�C���覡�Ө��N(�]�����C�Ӱʧ@�[�@�ӥD����)
	private JLabel tip = new JLabel("�w��ϥΥ��t��"),
			slogan = new JLabel("�u��v��d�� �P �ơu�N�v�p��"),
			systemName = new JLabel("��N�ɶ��w���t��");
	private JLabel[] pageText = new JLabel[procNum]; 	// �ҥH�C�@�ӳ��ί��ޭȨӤ޾ɰʧ@
	private String[] iconName = {"\\surgery.png", "\\manual.png", "\\chart.png"};
	private String[] imageName = {"\\Background.png", "\\TitleBackground.png"};
	private ImageIcon[] icon = LoadFile.fromIcons(Info.getIconPath(), iconName, 80, 80);
	private BufferedImage[] image = LoadFile.fromImages(Info.getIconPath(), imageName);
	// �����ŧi
	private HashMap<Page, Panel> panelMap = new HashMap<>();
	private OperatingManual operatingManual = new OperatingManual();
	private View view = new View();
	private Title title = new Title();
	private Process process = new Process();
	
	/**�{�������I*/
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				UIManager.put("OptionPane.messageDialogTitle", "���G�X�F�I���D");
				Font mainFont = new Font("�L�n������", Font.PLAIN, 20);
				UIManager.put("Label.font", mainFont);
				UIManager.put("Button.font", mainFont);
				UIManager.put("RadioButton.font", mainFont);
				UIManager.put("ComboBox.font", mainFont);
				UIManager.put("CheckBox.font", mainFont);
				UIManager.put("TextField.font", mainFont);
				UIManager.put("TextArea.font", mainFont);
				UIManager.put("ProgressBar.font", mainFont);
				UIManager.put("TitledBorder.font", mainFont);
				UIManager.put("TabbedPane.font", mainFont);
				Color mainColor = Color.decode("#e9ebfe");
				UIManager.put("info", mainColor);
				UIManager.put("control", mainColor);
				UIManager.put("Panel.background", mainColor);
				UIManager.put("RadioButton.background", mainColor);
				UIManager.put("CheckBox.background", mainColor);
				UIManager.put("TextField.background", mainColor);
				UIManager.put("TextArea.background", mainColor);
				UIManager.put("OptionPane.background", mainColor);
				UIManager.put("ComboBox.background", mainColor);
				UIManager.put("TabbedPane.background", mainColor);
				UIManager.put("Button.background", Color.decode("#e8f4ff"));
				UIManager.put("ProgressBar.background", Color.WHITE);
				UIManager.put("ProgressBar.foreground", Color.decode("#5649a5"));
//				UIManager.put("FileChooserUI", "com.sun.java.swing.plaf.windows.WindowsFileChooserUI");
				UIManager.put("SplitPane.dividerSize", 10);
				UIManager.put("FileChooser.readOnly", true);	// �N�ɮ׿�ܾ��]����Ū
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				
				new ProcessFrame();
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
		});
	}
	
	/**�D�ج[*/
	public ProcessFrame() {
		Info.addListener(this);	//�s�W��ť��
		
		JRootPane rootPane = new JRootPane() {
			private static final long serialVersionUID = -4902174568799098300L;
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
		        g.drawImage(image[0], 0, 0, getWidth(), getHeight(), this);
			}
		};
		rootPane.setBorder(emptyBorder);
		setRootPane(rootPane);
		setIconImage(icon[0].getImage());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH); //�@�}�l�N���e��
		setTitle("��N�ɶ��w���t��");
		setSize(1024, 768); // �Y�p�ɪ��ѪR��
		setLocationRelativeTo(null);
		
		add(view, BorderLayout.CENTER);
		add(title, BorderLayout.NORTH); // �s�@���D
		add(process, BorderLayout.SOUTH);
		addWindowStateListener((WindowEvent e) -> {
			switch(e.getNewState()) {
			case 0:
				System.out.println("�Y�p");
				slogan.setFont(font32);
				systemName.setFont(font22);
				tip.setFont(font26);
				for (int i = 0; i < procNum; i++)
					pageText[i].setFont(font20);
				break;
			case 6:
				System.out.println("���ù�");
				slogan.setFont(font38);
				systemName.setFont(font26);
				tip.setFont(font30);
				for (int i = 0; i < procNum; i++)
					pageText[i].setFont(font24);
				break;
			}
		});
		setVisible(true);
	}
	
	/**��ܫ��w����*/
	private void showPage(Page page) {
		view.showPage(page);
	}
	
	/**�]�w�t�ΰT��*/
	private void showMessage(String message) {
		tip.setForeground(sloganColor);
		tip.setText(message);
	}
	
	/**�]�w�t�ο��~�T��*/
	private void showError(String message) {
		tip.setForeground(Color.RED);
		tip.setText(message);
	}
	
	/**�ھں�ť���ǻ����T���A������w�禡*/
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch((Mode) evt.getSource()) {
		case CLEAR_PROCESS_AND_TEMP://�M���y�{�μȦs�ɮ�
		case CLEAR_ALL:		//�M���y�{��ؤ�Page�C�|�X��
			//�I�s�C�ӭ�����reset()
			panelMap.values().forEach(Panel::reset);
			//�N�����y�{��س]���զ�
			for(int i = 0; i < procNum; i++) {
				rectangle[i].setBackground(Color.WHITE);
			}
			//���]Page�C�|�X��
			for(int i = 0, length = pages.length; i < length; i++) {
				pages[i].reset();
			}
			//�Y�y�{��ؤ��b�{�{�h�����{�{
			if(process.twinkleBorder != null) {
				process.twinkleBorder.cancel(true);//�����{�{
			}
			break;
		case SHOWMESSAGE:	//��ܤ@��T��
			showMessage(evt.getNewValue().toString());
			break;
		case SHOWERROR:		//��ܿ��~�T��
			showError(evt.getNewValue().toString());
			break;
		case SHOW_PAGE:		//��ܭ���
			showPage((Page) evt.getNewValue());
			break;
		}
	}

	/**�ج[����*/
	private class Title extends JPanel implements ActionListener {
		private static final long serialVersionUID = 1745813210715743430L;
		private Color systemNameColor = Color.decode("#5649a5");
		private JButton logo = new JButton(icon[0]),
				help = new JButton(icon[1]);
		
		public Title() {
			setLayout(new BorderLayout(10, 0));
			logo.setFocusPainted(false);
			logo.setBorderPainted(false);
			logo.setContentAreaFilled(false);
			logo.setToolTipText("��^�D����");
			logo.addActionListener(this);
			add(logo, BorderLayout.WEST);
			
			slogan.setFont(font38);
			slogan.setForeground(sloganColor);
			systemName.setFont(font26);
			systemName.setForeground(systemNameColor);
			tip.setFont(font30);
			tip.setForeground(sloganColor);
			tip.setAlignmentX(RIGHT_ALIGNMENT);
			
			Box verticalBox = Box.createVerticalBox();
			verticalBox.add(slogan);
			verticalBox.add(systemName);
			verticalBox.add(Box.createVerticalGlue());
			Box centerBox = Box.createHorizontalBox();
			centerBox.add(verticalBox);
			centerBox.add(Box.createHorizontalGlue());
			centerBox.add(tip);
			add(centerBox, BorderLayout.CENTER);
			
			help.setFocusPainted(false);
			help.setBorderPainted(false);
			help.setContentAreaFilled(false);
			help.addActionListener(this);
			help.setAlignmentX(RIGHT_ALIGNMENT);
			help.setToolTipText("�ϥΤ�U");
			add(help, BorderLayout.EAST);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
	        g.drawImage(image[1], 0, 0, getWidth(), getHeight(), this);
		}

		/**���Ulogo���s*/
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == logo && nowPage != Page.CHOOSE) {
				int choose = JOptionPane.showConfirmDialog(ProcessFrame.this, "�T�w�˱�ثe���u�@�i�׶ܡH", "�O�_�^��D����", JOptionPane.YES_NO_OPTION);
				if(choose == JOptionPane.YES_OPTION) {	// �Y�_�h���X
					Info.clearAll();
					showPage(Page.CHOOSE);
				}
			}else if (e.getSource() == help) {
				operatingManual.setVisible(true);
			}
		}
	}
	
	/**�D����*/
	private class View extends JPanel {
		private static final long serialVersionUID = 2606502395233174342L;
		private Color hasProcessedColor = Color.decode("#a6a9e6");
		private Color isProcessingColor = Color.decode("#3c4074");
		private CardLayout card = new CardLayout(); // �ΨӤ���������
		
		public View() {
			setLayout(card);
			
			String pageName;	// �N�إ�Panel���L�{�]�˰_�ӡA�קK�c��
			Panel panel;		// �����ظm���ǻPPage�C�|���Ǧ���
			for(Page page: pages) {
				panel = newPage(page);
				pageName = page.getName();
				panelMap.put(page, panel);
				add((Component) panel, pageName);
				System.out.println(pageName);
			}
			showPage(Page.CHOOSE);
		}
		
		private Panel newPage(Page page) {
			switch(page) {
			case CHOOSE:
				return new ChoosePanel();
			case DATA_HANDLE:
				return new DataHandlePanel();
			case DATA_SPLIT:
				return new DataSplitPanel();
			case DATA_TRANSFORM:
				return new DataTransformPanel();
			case FEATURE_SELECT:
				return new FeatureSelectPanel();
			case MAIN_PAGE:
				return new MainPagePanel();
			case METHOD_SELECT:
				return new MethodSelectPanel();
			case SELECT_FOLDER:
				return new SelectFolderPanel();
			case TRAIN_RESULT:
				return new TrainResultPanel();
			}
			throw new IllegalArgumentException("�Ѽƥ��w�q");
		}
		
		private void showPage(Page page) {
			card.show(this, page.getName());
			if(page == Page.CHOOSE) {	// �p�G�n�D��ܬy�{��ܭ����A�NprePage��nowPage�]��Page.CHOOSE
				prePage = nowPage = Page.CHOOSE;
			}else {
				prePage = nowPage;
				nowPage = page;
			}
			
			// �]�w�W�ӭ������s�C��
			switch(prePage) {
			case CHOOSE:
			case MAIN_PAGE:
				break;
			default:
				rectangle[prePage.ordinal()].setBackground(hasProcessedColor);
				break;				
			}
			// �]�w���������s�C��
			switch(nowPage) {
			case CHOOSE:
			case MAIN_PAGE:
				break;
			default:
				rectangle[nowPage.ordinal()].setBackground(isProcessingColor);
				break;				
			}
			//�I�s�ӭ�����setFile��k
			panelMap.get(page).setFile();
		}
	}
	
	/**�ج[����*/
	private class Process extends JPanel implements PropertyChangeListener {
		private static final long serialVersionUID = -1881550063813242073L;
		private SwingWorker<Void, Void> twinkleBorder;
		private LineBorder redBorder = new LineBorder(Color.RED, 3);
		private EmptyBorder boxBorder = new EmptyBorder(3, 3, 3, 3);
		private Box[] procBox = new Box[procNum];
		
		public Process() {
			setLayout(new GridLayout(1, procNum, 10, 0));
			setBackground(Color.decode("#b1b5fd"));
			setBorder(emptyBorder);
			
			for(int i = 0; i < procNum; i++) {
				pageText[i] = new JLabel(pages[i].getName());
				pageText[i].setFont(font24);
				pageText[i].setAlignmentX(CENTER_ALIGNMENT);
				rectangle[i] = new JTextField();
				rectangle[i].setEditable(false);
				rectangle[i].setBackground(Color.WHITE);
				
				procBox[i] = Box.createVerticalBox();
				procBox[i].setBorder(boxBorder);
				procBox[i].add(pageText[i]);
				procBox[i].add(rectangle[i]);
				procBox[i].addMouseListener(pageListener);
				add(procBox[i]);
				
				pages[i].addListener(this);//�[�J�C�ӭ�������ť��
			}
		}
		
		private boolean twinkle = true;
		/**�]�w�����}�ҮɡA�|�Ұʺ�ť���A�H��ܬ��ءA�����ϥΪ�*/
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			Page page = (Page) evt.getSource();
//			if((boolean) evt.getNewValue() && nowPage != page) {	// �Y�N�����]���ҥΨåB�]�w�����P�ثe�������ۦP
			if((boolean) evt.getNewValue()) {	// �Y�N�����]���ҥ�
				twinkleBorder = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						twinkle = true;
						while(twinkle) {
							setBoxBorder(page, redBorder);
							Thread.sleep(500);
							setBoxBorder(page, boxBorder);
							Thread.sleep(500);
						}
						return null;
					}
					
					@Override
					protected void done() {
						setBoxBorder(page, boxBorder);	//�{����������ɡA�N����٭�
					}
				};
				twinkleBorder.execute();
			}else {	// �Y�N�����]���T��
				if(twinkleBorder != null) {
					twinkle = false;	//�����{�{ �N��س]���쥻
				}
			}
		}
		
		/**�]�w�y�{����C��*/
		private void setBoxBorder(Page page, Border border) {
			switch(page) {
			case DATA_TRANSFORM:
				procBox[1].setBorder(border);
				break;
			case DATA_HANDLE:
				procBox[2].setBorder(border);
				break;
			case FEATURE_SELECT:
				procBox[3].setBorder(border);
				break;
			case DATA_SPLIT:
				procBox[4].setBorder(border);
				break;
			case METHOD_SELECT:
				procBox[5].setBorder(border);
				break;
			case TRAIN_RESULT:
				procBox[6].setBorder(border);
				break;
			default:
				break;
			}
		}
		
		/**�i�ױ����ʧ@*/
		private MouseAdapter pageListener = new MouseAdapter() {
			private Component comp;
			private Page page;
			@Override
			public void mouseClicked(MouseEvent e) {
				comp = e.getComponent();
				for(int i = 0; i < procNum; i++) {
					if(comp == procBox[i]) {
						// �Y�I�諸�������}��A�h���X
						page = pages[i];
						if(page.isEnabled()) {
							page.enteredPage();
							twinkleBorder.cancel(true);	//�����{�{ �N��س]���쥻
							showPage(page);
							showMessage("�ثe�������u" + page.getName() + "�v");
							break;
						}
					}
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				comp = e.getComponent();
				for(int i = 0; i < procNum; i++) {
					if(comp == procBox[i]) {
						page = pages[i];
						if(page.isEnabled()) {
							comp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						}
						break;
					}
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				comp.setCursor(Cursor.getDefaultCursor());
			}
		};
	}
}