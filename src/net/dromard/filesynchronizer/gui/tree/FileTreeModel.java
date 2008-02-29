package net.dromard.filesynchronizer.gui.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.dromard.filesynchronizer.gui.AbstractModel;


public class FileTreeModel extends AbstractModel implements TreeModel {
	List<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();
	
	public FileTreeModel() {
		super(null);
	}
	
	public FileTreeModel(JFileSynchronizerTreeNode root) {
		super(root);
	}
	
	public Object getRoot() {
		return getRootNode();
	}
	
	public void addTreeModelListener(TreeModelListener treeModelListener) {
		treeModelListeners.add(treeModelListener);
	}

	public Object getChild(Object parent, int index) {
		if (parent != null && parent instanceof JFileSynchronizerTreeNode) {
			return ((JFileSynchronizerTreeNode) parent).getChilds().get(index);
		} 
		return null;
	}

	public int getChildCount(Object parent) {
		if (parent != null && parent instanceof JFileSynchronizerTreeNode) {
			return ((JFileSynchronizerTreeNode) parent).getChilds().size();
		} 
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent != null && parent instanceof JFileSynchronizerTreeNode) {
			return ((JFileSynchronizerTreeNode) parent).getChilds().indexOf(child);
		} 
		return -1;
	}

	public boolean isLeaf(Object element) {
		if (element != null && element instanceof JFileSynchronizerTreeNode) {
			return ((JFileSynchronizerTreeNode) element).isLeaf();
		}
		return false;
	}

	public void removeTreeModelListener(TreeModelListener treeModelListener) {
		treeModelListeners.remove(treeModelListener);
	}

	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// unimplemented function
	}
}
