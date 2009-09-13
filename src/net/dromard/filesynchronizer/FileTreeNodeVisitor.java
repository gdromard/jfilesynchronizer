package net.dromard.filesynchronizer;

import java.io.File;
import java.util.HashMap;

import net.dromard.common.visitable.Visitable;
import net.dromard.common.visitable.Visitor;
import net.dromard.filesynchronizer.gui.table.FileTableModel;
import net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode;
import net.dromard.filesynchronizer.treenode.FileSynchronizerTreeNode;

/*
 * 
 * @author Pingus
 */
public class FileTreeNodeVisitor implements Visitor {
    private boolean running = true;
    private final FileTableModel model;

    public FileTreeNodeVisitor(FileTableModel model) {
        this.model = model;
    }

    /**
     * Visit implementation.
     * 
     * @param node The element object of the tree.
     * @throws Exception Any exception can occurred during visit.
     */
    public final void visit(final Visitable visitable) throws Exception {
        visit((FileSynchronizerTodoTaskTreeNode) visitable);
    }

    public void abort() {
        running = false;
    }

    private final void visit(FileSynchronizerTodoTaskTreeNode node) throws Exception {
        if (running) {
            if (node.isLeaf() && node.getTodoTask() != FileSynchronizerTodoTaskTreeNode.TODO_NOTHING) {
                model.add(node);
            }

            HashMap<String, File> destinationFiles = new HashMap<String, File>();
            if (node.getDestination() != null) {
                File[] tmp = node.getDestination().listFiles();
                if (tmp != null) {
                    for (int i = 0; i < tmp.length; ++i) {
                        destinationFiles.put(tmp[i].getName(), tmp[i]);
                    }
                }
            }
            FileSynchronizerTreeNode child;
            if (node.getSource() != null) {
                File[] tmp = node.getSource().listFiles();
                if (tmp != null && tmp.length > 0) {
                    for (int i = 0; i < tmp.length; ++i) {
                        File dest = destinationFiles.get(tmp[i].getName());
                        child = node.createNode(tmp[i], dest);
                        if (dest != null) {
                            destinationFiles.remove(tmp[i].getName());
                        }
                        if (child != null) {
                            child.accept(this);
                        }
                    }
                    for (String key : destinationFiles.keySet()) {
                        child = node.createNode(null, destinationFiles.get(key));
                        if (child != null) {
                            child.accept(this);
                        }
                    }
                }
            } else {
                for (String key : destinationFiles.keySet()) {
                    child = node.createNode(null, destinationFiles.get(key));
                    if (child != null) {
                        child.accept(this);
                    }
                }
            }
        }
    }
}
