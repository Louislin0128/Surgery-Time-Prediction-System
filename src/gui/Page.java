package gui;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * �w�q�����W�٤Ψ�O�_�}��
 */
enum Page {
	/**�]�w��ƥؿ�*/
	SELECT_FOLDER("�]�w��ƥؿ�"),
	/**�������*/
	DATA_TRANSFORM("�������"),
	/**��ƳB�z*/
	DATA_HANDLE("��ƳB�z"),
	/**�S�x���*/
	FEATURE_SELECT("�S�x���"),
	/**��Ʃ��*/
	DATA_SPLIT("��Ʃ��"),
	/**��k�]�w*/
	METHOD_SELECT("��k�]�w"),
	/**�V�m���G*/
	TRAIN_RESULT("�V�m���G"),
	/**�w���D����*/
	MAIN_PAGE("�w���D����"),
	/**�y�{���*/
	CHOOSE("�y�{���");
	
	/**�ӭ����W��*/
	private String name;
	/**�O�_�ҥθӭ���*/
	private boolean enabled;
	/**�����y�{�����i�J���X��*/
	private boolean firstEnter;
	/**��ť��*/
	private PropertyChangeSupport p;
	private Page(String name) {
		this.name = name;
		this.enabled = false;
		this.firstEnter = true;
		this.p = new PropertyChangeSupport(this);
	}
	
	/**���o�����W��*/
	public String getName() {
		return name;
	}
	
	/**���o�����O�_�ҥ�*/
	public boolean isEnabled() {
		return enabled;
	}
	
	/**�]�w��ť��*/
	public void addListener(PropertyChangeListener listener) {
		p.addPropertyChangeListener(listener);
	}
	
	/**�ϥΪ̤w�i�J�ӭ����A�]�w�X��*/
	public void enteredPage() {
		this.firstEnter = false;
	}
	
	/**�]�w�����O�_�ҥ�*/
	public void setEnabled(boolean b) {
		//�p�G�ӭ����������i�J�A�Ұʺ�ť��
		System.out.println("�N" + name + "�����]���G" + b);
		if(firstEnter || !b) {	//�Y�����i�J�γ]���T�ΡA�Ұʺ�ť��
			p.firePropertyChange(name, enabled, b);
		}
		this.enabled = b;
	}
	
	/**���]�����X��*/
	public void reset() {
		setEnabled(false);
		this.firstEnter = true;
	}
}
