package net.dromard.filesynchronizer.gui.table;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JTable;

import net.dromard.common.swing.TableSorter;
import net.dromard.filesynchronizer.gui.AbstractManager;
import net.dromard.filesynchronizer.gui.AbstractModel;
import net.dromard.filesynchronizer.gui.tree.JFileSynchronizerTreeNode;

public class FileTableManager extends AbstractManager {
	private JTable table;
	private FileTableModel model = new FileTableModel(null);
	private TableSorter sorterModel;
	
	public FileTableManager(JTable table) {
		super(table);
		this.table = table;
		manage();
	}
	
	private void manage() {
		table.addMouseListener(this);
		setModelToComponent(null);
		table.setOpaque(false);
	}
	
	public FileTableModel getModel() {
		return model;
	}

	@Override
	protected void setModelToComponent(final JFileSynchronizerTreeNode root) {
		model.setRootNode(root);
		sorterModel = new TableSorter(model);
		table.setModel(sorterModel);
		sorterModel.setTableHeader(table.getTableHeader());
		// Set the first visible column to 100 pixels wide
		table.getColumnModel().getColumn(0).setMinWidth(40);
		table.getColumnModel().getColumn(0).setMaxWidth(70);
		table.getColumnModel().getColumn(2).setMinWidth(80);
		table.getColumnModel().getColumn(2).setMaxWidth(120);
		table.getColumnModel().getColumn(3).setMinWidth(80);
		table.getColumnModel().getColumn(3).setMaxWidth(120);
	}
    
	@Override
    protected ArrayList<JFileSynchronizerTreeNode> getSelectedElements() {
		ArrayList<JFileSynchronizerTreeNode> selectedItems = new ArrayList<JFileSynchronizerTreeNode>();
		int[] selectedRows = table.getSelectedRows();
		for (int i : selectedRows) {
			i = sorterModel.modelIndex(i);
			selectedItems.add(getModel().getNode(i));
		}
        return selectedItems;
    }

    /**
     * Used for popup management.
     * @param event Event.
     */
    @Override
    public final void mouseReleased(final MouseEvent event) {
    	super.mouseReleased(event);
        int row = table.rowAtPoint(event.getPoint());
        if (event.getButton() == MouseEvent.BUTTON3 && !table.isRowSelected(row)) {
        	table.clearSelection();
        	table.addRowSelectionInterval(row, row);
        }
    }

	@Override
	protected AbstractModel getAbstractModel() {
		return getModel();
	}
}