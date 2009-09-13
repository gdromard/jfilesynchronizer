package net.dromard.filesynchronizer.treenode;

import java.io.File;
import java.util.List;
import java.util.Vector;

import net.dromard.common.visitable.Visitable;
import net.dromard.common.visitable.Visitor;

public abstract class FileSynchronizerTreeNode implements Visitable {
    /** Parent TreeNode. */
    private FileSynchronizerTreeNode parent = null;
    /** Name. */
    private String name = null;
    /** Relative TreeNode path. */
    private String relativePath = null;
    /** Absolute destination path. */
    private String absoluteDestinationPath = null;
    /** Absolute source path. */
    private String absoluteSourcePath = null;
    /** Node childs. */
    private List<FileSynchronizerTreeNode> childs = new Vector<FileSynchronizerTreeNode>();
    /** Node attached object. */
    private File source = null;
    /** Node attached object. */
    private File destination = null;

    /**
     * Construct the node with source and destination objects.
     * @param source The source file.
     * @param destintation The destintation file.
     */
    public FileSynchronizerTreeNode(final File source, final File destination) {
        this.source = source;
        this.destination = destination;
    }

    /**
     * Return node rank.
     * @return the rank
     */
    public final int getRank() {
        // Initialize rank;
        int rank = 0;
        // Retreive parent object
        FileSynchronizerTreeNode father = this;
        // Iter until root node
        while ((father = father.getParent()) != null) {
            ++rank;
        }
        return rank;
    }

    /*
    public final int countChild() {
        if (isLeaf()) {
            return 1;
        } else {
            int childs = 0;
            for (FileSynchronizerTreeNode node : getChilds()) {
                childs += node.countChild();
            }
            return childs;
        }
    }
     */

    /**
     * Is this node a leaf ?
     * @return If this node is a leaf (if it does not contain any child)
     */
    public final boolean isLeaf() {
        return getChilds().size() == 0 && ((getSource() != null && getSource().isFile()) || (getDestination() != null && getDestination().isFile()));
    }

    /**
     * Add a child. Construct the node (Used by childs class)
     * @param source The source file.
     * @param destination The destination file.
     */
    public final FileSynchronizerTreeNode addChild(File source, File destination) {
        return addChild(createNode(source, destination));
    }

    /**
     * Construct a node.
     * @param source The source file.
     * @param destination The destination file.
     */
    public abstract FileSynchronizerTreeNode createNode(File source, File destination);

    /**
     * Retreive the childs.
     * @return the childs
     */
    public final List<FileSynchronizerTreeNode> getChilds() {
        return childs;
    }

    /**
     * @return the name
     */
    public final String getName() {
        if (name == null) {
            if (getSource() != null) {
                name = getSource().getName();
            } else {
                name = getDestination().getName();
            }
        }
        return name;
    }

    /**
     * @return the relativePath
     */
    public final String getRelativePath() {
        if (relativePath == null) {
            if (getParent() != null) {
                File parent = getParent().getSource();
                if (parent != null && source != null) {
                    relativePath = source.getAbsolutePath().substring(parent.getAbsolutePath().length());
                } else {
                    parent = getParent().getDestination();
                    relativePath = destination.getAbsolutePath().substring(parent.getAbsolutePath().length());
                }
            } else {
                relativePath = "";
            }
        }
        return relativePath;
    }

    /**
     * @return the source absolute Path
     */
    protected final String getSourceAbsolutePath() {
        if (absoluteSourcePath == null) {
            if (getParent() == null || getSource() != null) {
                absoluteSourcePath = getSource().getAbsolutePath();
            } else {
                absoluteSourcePath = getParent().getSourceAbsolutePath() + File.separator + getRelativePath();
            }
        }
        return absoluteSourcePath;
    }

    /**
     * @return the destination absolute Path
     */
    protected final String getDestinationAbsolutePath() {
        if (absoluteDestinationPath == null) {
            if (getParent() == null || getDestination() != null) {
                absoluteDestinationPath = getDestination().getAbsolutePath();
            } else {
                absoluteDestinationPath = getParent().getDestinationAbsolutePath() + File.separator + getRelativePath();
            }
        }
        return absoluteDestinationPath;
    }

    /**
     * @return the source file
     */
    public final File getSource() {
        return source;
    }

    /**
     * @param source the source file to set
     */
    public final void setSource(File source) {
        this.source = source;
    }

    /**
     * @return the destination file
     */
    public final File getDestination() {
        return destination;
    }

    /**
     * @return the new destination file
     */
    public final File createDestination() {
        if (destination == null) {
            String destinationPath = getDestinationAbsolutePath();
            destination = new File(destinationPath);
        }
        return destination;
    }

    /**
     * @return the new source file
     */
    public final File createSource() {
        if (source == null) {
            String sourcePath = getSourceAbsolutePath();
            source = new File(sourcePath);
            System.err.println("[DEBUG] Creating source file to " + source.getAbsolutePath());
        }
        return source;
    }

    /**
     * @param source the source file to set
     */
    public final void setDestination(File destination) {
        this.destination = destination;
    }

    /**
     * @return the parent
     */
    public final FileSynchronizerTreeNode getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    protected final void setParent(final FileSynchronizerTreeNode parent) {
        this.parent = parent;
    }

    /**
     * Add a child to node.
     * @param child The node child element to be added.
     */
    public final FileSynchronizerTreeNode addChild(final FileSynchronizerTreeNode child) {
        if (child != null) {
            child.setParent(this);
            this.childs.add(child);
        }
        return child;
    }

    /**
     * Accept method implementation of Visitor pattern.
     * @param visitor the visitor.
     * @throws Exception Any exception can occured during visit.
     */
    public final void accept(final Visitor visitor) throws Exception {
        visitor.visit(this);
    }
}
