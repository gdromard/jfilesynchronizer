package net.dromard.filesynchronizer.gui.tree;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.dromard.filesynchronizer.gui.AbstractManager;
import net.dromard.filesynchronizer.gui.AbstractModel;

public class FileTreeManager extends AbstractManager {
	private JTree tree;
	private FileTreeModel model = new FileTreeModel();
	
	public FileTreeManager(JTree tree) {
		super(tree);
		this.tree = tree;
		manage();
	}
	
	public void manage() {
		if (tree != null) {
	        // Initialisation of tree
	        tree.setCellRenderer(new FileTreeCellRenderer());
	        // Initialize model
	        tree.setModel(null);
	        // Add listeners only if they are not yet added
	        tree.addMouseListener(this);
	        tree.setOpaque(true);
	        //tree.addKeyListener(this);
	        tree.setRowHeight(20);
	        //tree.addTreeWillExpandListener(this);
	        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		}
	}
	
	public FileTreeModel getModel() {
		return model;
	}
	
	public JTree getTree() {
		return tree;
	}
    
	@Override
    protected ArrayList<JFileSynchronizerTreeNode> getSelectedElements() {
		ArrayList<JFileSynchronizerTreeNode> list = new ArrayList<JFileSynchronizerTreeNode>();
        TreePath path = tree.getSelectionPath();
        if (path != null) {
        	list.add((JFileSynchronizerTreeNode) path.getLastPathComponent());
        }
        return list;
    }

	@Override
	protected AbstractModel getAbstractModel() {
		return getModel();
	}

	@Override
	protected void setModelToComponent(JFileSynchronizerTreeNode root) {
		getModel().setRootNode(root);
		tree.setModel(getModel());
		refreshUI();
	}
}