package net.dromard.filesynchronizer.gui.tree;

import java.io.File;

import net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode;

/**
 * File tree node object.
 * @author Pingus
 */
public class JFileSynchronizerTreeNode extends FileSynchronizerTodoTaskTreeNode {
	
	public JFileSynchronizerTreeNode(File source, File destination) {
		super(source, destination);
	}

	protected void addChild(File source, File destination) {
		JFileSynchronizerTreeNode child = new JFileSynchronizerTreeNode(source, destination);
		if (!child.isLeaf() || child.getTodoTask() != FileSynchronizerTodoTaskTreeNode.TODO_NOTHING) {  
			addChild(child);
		}
	}

}
