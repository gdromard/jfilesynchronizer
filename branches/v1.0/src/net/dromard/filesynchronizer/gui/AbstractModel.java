package net.dromard.filesynchronizer.gui;

import net.dromard.filesynchronizer.gui.tree.JFileSynchronizerTreeNode;

public abstract class AbstractModel {
	private JFileSynchronizerTreeNode root = null;
	
	public AbstractModel(final JFileSynchronizerTreeNode root) {
		setRootNode(root);
	}
	
	public void setRootNode(final JFileSynchronizerTreeNode root) {
		this.root = root;
	}

	public JFileSynchronizerTreeNode getRootNode() {
		return root;
	}
}
