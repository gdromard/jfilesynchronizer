package net.dromard.filesynchronizer.gui;

import net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode;

public abstract class AbstractModel {
    private FileSynchronizerTodoTaskTreeNode root = null;

    public AbstractModel(final FileSynchronizerTodoTaskTreeNode root) {
        setRootNode(root);
    }

    public void setRootNode(final FileSynchronizerTodoTaskTreeNode root) {
        this.root = root;
    }

    public FileSynchronizerTodoTaskTreeNode getRootNode() {
        return root;
    }
}
