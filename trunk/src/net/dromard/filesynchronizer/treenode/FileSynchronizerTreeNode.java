package net.dromard.filesynchronizer.treenode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.dromard.common.visitable.Visitable;
import net.dromard.common.visitable.Visitor;

public class FileSynchronizerTreeNode implements Visitable {
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
	private ArrayList<FileSynchronizerTreeNode> childs = null;
	/** Node attached object. */
	private File source = null;
	/** Node attached object. */
	private File destination = null;
	
	/**
     * Construct the node with source and destination objects.
     * @param source       The source file.
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

    /**
     * Is this node a leaf ?
     * @return If this node is a leaf (if it does not contain any child)
     */
    public final boolean isLeaf() {
        return (getChilds() == null || getChilds().size() == 0) 
        	&& (getSource() != null && getSource().isFile());
    }

    /**
     * Add a child. Construct the node (Used by childs class)
     * @param source      The source file.
     * @param destination The destination file.
     */
	protected void addChild(File source, File destination) {
		addChild(new FileSynchronizerTreeNode(source, destination));
	}

    /**
     * Retreive the childs.
     * @return the childs
     */
    public final ArrayList<FileSynchronizerTreeNode> getChilds() {
    	if (childs == null) {
    		HashMap<String, File> destinationFiles = new HashMap<String, File>();
    		if (destination != null) {
    			File[] tmp = destination.listFiles();
    			if (tmp != null) {
    				for (int i = 0; i < tmp.length; ++i) {
    					destinationFiles.put(tmp[i].getName(), tmp[i]);
    				}
    			}
    		}
    		childs = new ArrayList<FileSynchronizerTreeNode>();
	    	if (source != null) {
	    		File[] tmp = source.listFiles();
	    		if (tmp != null && tmp.length > 0) {
					for (int i = 0; i < tmp.length; ++i) {
						File dest = destinationFiles.get(tmp[i].getName());
						addChild(tmp[i], dest);
						if (dest != null) {
							destinationFiles.remove(tmp[i].getName());
						}
					}
					for (String key : destinationFiles.keySet()) {
						addChild(null, destinationFiles.get(key));
					}
	    		}
	    	} else if (destination != null) {
	    		File[] tmp = destination.listFiles();
	    		if (tmp != null && tmp.length > 0) {
					for (int i = 0; i < tmp.length; ++i) {
						addChild(null, tmp[i]);
					}
	    		}
	    	}
    	}
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
    		relativePath = (getParent() != null ? getParent().getRelativePath() : ".") + File.separator + getName();
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
    			absoluteSourcePath = getParent().getSourceAbsolutePath() + File.separator + getName();
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
    			absoluteDestinationPath = getParent().getDestinationAbsolutePath() + File.separator + getName();
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
    public final void addChild(final FileSynchronizerTreeNode child) {
        child.setParent(this);
        this.childs.add(child);
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
