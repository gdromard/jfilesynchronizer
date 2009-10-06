package net.dromard.filesynchronizer.gui.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.dromard.common.io.FileHelper;
import net.dromard.filesynchronizer.gui.AbstractModel;
import net.dromard.filesynchronizer.gui.IconManager;
import net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode;
import net.dromard.filesynchronizer.treenode.FileSynchronizerTreeNode;

public class FileTableModel extends AbstractModel implements TableModel {
    private final List<TableModelListener> tableModelListeners = new ArrayList<TableModelListener>();
    private final Class[] columnsClass = new Class[]{ImageIcon.class, String.class, String.class, ImageIcon.class};
    private final String[] columnsNames = new String[]{"Status", "File Path", "File Extension", "File Date Status"};

    // private Class[] columnsClass = new Class[] { ImageIcon.class, String.class, String.class, ImageIcon.class, Double.class, Double.class, Double.class, Double.class*/ };
    // private String[] columnsNames = new String[] { "Status", "File Path", "File Extension", "File Date Status", "Source length (ko)", "Destination length (ko)", "Source timestamp",
    // "Destination timestamp" };

    public FileTableModel(final FileSynchronizerTodoTaskTreeNode root) {
        super(root);
        setRootNode(root);
    }

    public void addTableModelListener(final TableModelListener tableModelListener) {
        tableModelListeners.add(tableModelListener);
    }

    public void removeTableModelListener(final TableModelListener tableModelListener) {
        tableModelListeners.remove(tableModelListener);
    }

    public Class<?> getColumnClass(final int columnIndex) {
        return columnsClass[columnIndex];
    }

    public int getColumnCount() {
        return columnsClass.length;
    }

    public String getColumnName(final int columnIndex) {
        return columnsNames[columnIndex];
    }

    public int getRowCount() {
        return (getRootNode() != null) ? getRootNode().getChilds().size() : 0;
    }

    public FileSynchronizerTodoTaskTreeNode getNode(final int rowIndex) {
        if (getRootNode().getChilds().size() > rowIndex) {
            return (FileSynchronizerTodoTaskTreeNode) getRootNode().getChilds().get(rowIndex);
        }
        return null;
    }

    public Object getValueAt(final int rowIndex, final int columnIndex) {
        FileSynchronizerTodoTaskTreeNode node = getNode(rowIndex);
        if (node == null) {
            return null;
        }
        if (columnIndex == 0) {
            return IconManager.getIcon(null, node.getTodoTask(), node);
        } else if (columnIndex == 1) {
            return node.getRelativePath();
        } else if (columnIndex == 2) {
            if (node.isLeaf()) {
                return FileHelper.getExtension(node.getName());
            }
            return "";
        } else if (columnIndex == 3) {
            if (node.isLeaf()) {
                int todoTask = node.getTodoTask();
                if (node.getSource() != null && node.getDestination() != null && node.getSource().exists() && node.getDestination().exists()) {
                    long diff = node.getSource().lastModified() - node.getDestination().lastModified();
                    if (diff < 0) {
                        todoTask = FileSynchronizerTodoTaskTreeNode.TODO_UPDATE_SOURCE;
                    } else if (diff > 0) {
                        todoTask = FileSynchronizerTodoTaskTreeNode.TODO_UPDATE_DESTINATION;
                    } else if (diff == 0) {
                        todoTask = FileSynchronizerTodoTaskTreeNode.TODO_NOTHING;
                    }
                } else if ((node.getSource() == null || !node.getSource().exists()) && (node.getDestination() != null && node.getDestination().exists())) {
                    todoTask = FileSynchronizerTodoTaskTreeNode.TODO_CREATE_SOURCE;
                } else if ((node.getSource() != null && node.getSource().exists()) && (node.getDestination() == null || !node.getDestination().exists())) {
                    todoTask = FileSynchronizerTodoTaskTreeNode.TODO_CREATE_DESTINATION;
                }
                if (todoTask == node.getTodoTask()) {
                    todoTask = FileSynchronizerTodoTaskTreeNode.TODO_NOTHING;
                }
                return IconManager.getIcon(null, todoTask, node);
            }
            return null;
        } else if (columnIndex == 4) {
            if (node.isLeaf() && node.getSource() != null) {
                return new Double(node.getSource().length() / 1024);
            }
            return null;
        } else if (columnIndex == 5) {
            if (node.isLeaf() && node.getDestination() != null) {
                return new Double(node.getDestination().length() / 1024);
            }
            return null;
        } else if (columnIndex == 6) {
            if (node.isLeaf() && node.getSource() != null) {
                return new Double(node.getSource().lastModified() / 1000);
            }
            return null;
        } else if (columnIndex == 7) {
            if (node.isLeaf() && node.getDestination() != null) {
                return new Double(node.getDestination().lastModified() / 1000);
            }
            return null;
        } else {
            throw new ArrayIndexOutOfBoundsException(" Column index " + columnIndex + " is unknown");
        }
    }

    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return false;
    }

    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        // Table is not editable
    }

    public void add(final FileSynchronizerTreeNode node) {
        FileSynchronizerTodoTaskTreeNode j = (FileSynchronizerTodoTaskTreeNode) node;
        if (j.isLeaf() && j.getTodoTask() != FileSynchronizerTodoTaskTreeNode.TODO_NOTHING) {
            getRootNode().addChild(j);
        }
    }

    private void fireTableChanged() {
        for (TableModelListener listener : tableModelListeners) {
            listener.tableChanged(null);
        }
    }
}
