package net.dromard.filesynchronizer.gui.tree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.dromard.filesynchronizer.gui.IconManager;
import net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode;

public class FileTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -3409980899784496717L;

    public FileTreeCellRenderer() {
        super();
    }
	
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		this.setOpaque(false);
		if (value instanceof FileSynchronizerTodoTaskTreeNode) {
	        // The node to draw
			FileSynchronizerTodoTaskTreeNode node  = (FileSynchronizerTodoTaskTreeNode) value;
	        super.getTreeCellRendererComponent(tree, node.getName(), selected, expanded, leaf, row, hasFocus);
	        
			// Change the icon depending of the node's type
	       	setIcon(IconManager.getIcon(getIcon(), node.getTodoTask(), node));
		}
		return this;
	}
}
