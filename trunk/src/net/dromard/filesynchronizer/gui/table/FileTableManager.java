package net.dromard.filesynchronizer.gui.table;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import net.dromard.common.swing.TableSorter;
import net.dromard.filesynchronizer.gui.AbstractManager;
import net.dromard.filesynchronizer.gui.AbstractModel;
import net.dromard.filesynchronizer.gui.MainFrame;
import net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode;

public class FileTableManager extends AbstractManager {
    private final JTable table;
    private final FileTableModel model = new FileTableModel(null);
    private TableSorter sorterModel;

    public FileTableManager(final JTable table) {
        super(table);
        this.table = table;
        manage();
    }

    private void manage() {
        table.addMouseListener(this);
        setModelToComponent(null);
        table.setOpaque(false);
        sorterModel = new TableSorter(model) {
            @Override
            public void fireTableDataChanged() {
                MainFrame.getInstance().getProgressBarHandler().progress("Sorting files");
                super.fireTableDataChanged();
                MainFrame.getInstance().getProgressBarHandler().stop();
            }
        };

        table.setModel(sorterModel);
        // Set the first visible column to 100 pixels wide
        table.getColumnModel().getColumn(0).setMinWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.getColumnModel().getColumn(2).setMinWidth(80);
        table.getColumnModel().getColumn(2).setMaxWidth(120);
        table.getColumnModel().getColumn(3).setMinWidth(80);
        table.getColumnModel().getColumn(3).setMaxWidth(120);

        sorterModel.setTableHeader(table);
        model.addTableModelListener(new TableModelListener() {
            public void tableChanged(final TableModelEvent e) {
                sorterModel.fireTableRowsInserted(sorterModel.getRowCount() - 1, sorterModel.getRowCount() - 1);
            }
        });
    }

    public FileTableModel getModel() {
        return model;
    }

    @Override
    protected void setModelToComponent(final FileSynchronizerTodoTaskTreeNode root) {
        if (root != null) {
            model.setRootNode(root);
        }
    }

    @Override
    protected ArrayList<FileSynchronizerTodoTaskTreeNode> getSelectedElements() {
        ArrayList<FileSynchronizerTodoTaskTreeNode> selectedItems = new ArrayList<FileSynchronizerTodoTaskTreeNode>();
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