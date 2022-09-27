package guiComponent;
import java.awt.Color;
import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**�b��椤���JPanel*/
public class PanelTable extends JTable {
	private static final long serialVersionUID = 6406210106332271542L;
	public PanelTable(AbstractTableModel model) {
		super(model);
		setRowHeight(50);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setTableHeader(null);
		TableEditorAndRenderer editAndRender = new TableEditorAndRenderer();
		setDefaultRenderer(JPanel.class, editAndRender);
		setDefaultEditor(JPanel.class, editAndRender);
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		if(isEditing()) {
			getCellEditor().stopCellEditing();
		}
		super.tableChanged(e);
	}
	
	/**
	 * ø�s���O��JPanel(TableCellRenderer)<br>
	 * ��JPanel�i�s��(AbstractCellEditor�BTableCellEditor)
	 */
	private class TableEditorAndRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
		private static final long serialVersionUID = -6788942281921154624L;
		private Color chooseColor = Color.decode("#9fa3ed");
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			// ø�sPanel
			JPanel component = (JPanel) value;
			component.setBackground(isSelected || hasFocus ? chooseColor : Color.WHITE);
			return component;
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			// �إ�Panel������Ϩ�i����
			JPanel component = (JPanel) value;
			component.setBackground(isSelected ? chooseColor : Color.WHITE);
			return component;
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}
	}
}
