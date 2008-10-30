package net.dromard.filesynchronizer.gui.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.dromard.common.io.FileHelper;
import net.dromard.filesynchronizer.gui.AbstractModel;
import net.dromard.filesynchronizer.gui.IconManager;
import net.dromard.filesynchronizer.gui.tree.JFileSynchronizerTreeNode;
import net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode;


public class FileTableModel extends AbstractModel implements TableModel {
	private List<TableModelListener> tableModelListeners = new ArrayList<TableModelListener>();
	private Vector<JFileSynchronizerTreeNode> rows = new Vector<JFileSynchronizerTreeNode>();
	private Class[]  columnsClass = new Class[] { ImageIcon.class, String.class, String.class, ImageIcon.class };
	private String[] columnsNames = new String[] { "Status", "File Path", "File Extension", "File Date Status" };
	//private Class[]  columnsClass = new Class[] { ImageIcon.class, String.class, String.class, ImageIcon.class, Double.class, Double.class, Double.class, Double.class*/ };
	//private String[] columnsNames = new String[] { "Status", "File Path", "File Extension", "File Date Status", "Source length (ko)", "Destination length (ko)", "Source timestamp", "Destination timestamp" };
	private boolean abortVisit;
	
	public FileTableModel(final JFileSynchronizerTreeNode root) {
		super(root);
		setRootNode(root);
	}
	
	public void abort() {
		abortVisit = true;
	}
	
	public void setRootNode(final JFileSynchronizerTreeNode root) {
		if (root != null && root != getRootNode()) {
			rows = new Vector<JFileSynchronizerTreeNode>();
			visit(root);
			abortVisit = false;
		}
		super.setRootNode(root);
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
		return rows.size();
	}

	public JFileSynchronizerTreeNode getNode(final int rowIndex) {
		return rows.get(rowIndex);
	}
	
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		JFileSynchronizerTreeNode node = rows.get(rowIndex);
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

	protected void visit(Object node) {
		if (abortVisit) return;
        if (!(node instanceof JFileSynchronizerTreeNode)) {
            throw new ClassCastException("JFileSynchronizerTreeNode expected");
        }
        JFileSynchronizerTreeNode fileBackup  = (JFileSynchronizerTreeNode) node;
    	if (fileBackup.getTodoTask() != FileSynchronizerTodoTaskTreeNode.TODO_NOTHING) {
    		if (fileBackup.isLeaf()) {
    			rows.add(fileBackup);
    		}
    	}
    	JFileSynchronizerTreeNode child;
        for (Iterator i = fileBackup.getChilds().iterator(); i.hasNext();) {
        	child = (JFileSynchronizerTreeNode) (i.next());
        	visit(child);
        }
	}
}
