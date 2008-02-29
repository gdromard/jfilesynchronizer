package net.dromard.filesynchronizer.treenode;

import java.io.File;

/**
 * File Synchronization Status Tree Node object.
 * This is a node that add the capability to compare file source with destination.
 * @author Pingus
 */
public class FileSynchronizationStatusTreeNode extends FileSynchronizerTreeNode {
	/**
	 * Human representation of file compare result.
	 */
	public static final char[] SYNCHRONIZATION_STATUS = new char[] { '!', '-', '+', '<', '>', '=' };
	
	/**
	 * Force recalculation of todo.
	 */
	public static final int SYNCHRONIZATION_RESET = -1;
	
	/**
	 * [!] Can occured if source and destination exist but have not the same name. You did not pass the correct object to the method.
	 */
	public static final int SYNCHRONIZATION_ERROR = 0;
	
	/**
	 * [-] Used when a destination file has to be deleted (it no more exists in source).
	 */
	public static final int SYNCHRONIZATION_SOURCE_DELETED = 1;
	
	/**
	 * [+] Used when the destination file has to be created (it does not exist).
	 */
	public static final int SYNCHRONIZATION_SOURCE_ADDED = 2;
	
	/**
	 * [<] Used when the destination file has been modified outside. (is newer than the master)
	 */
	public static final int SYNCHRONIZATION_DESTINATION_CHANGED = 3;
	
	/**
	 * [>] Used when the destination file has to be updated.
	 */
	public static final int SYNCHRONIZATION_SOURCE_CHANGED = 4;
	
	/**
	 * [=] Used when the source destination file are equals (same date, same name, same length).
	 */
	public static final int SYNCHRONIZATION_FILES_EQUALS = 5;

	/* -------------------------------------------------------------------------------- */

	/**
	 * Synchronization status (result of comparaison). 
	 */
	private int synchronizeStatus = SYNCHRONIZATION_RESET;
	
	/**
	 * Synchronization status (result of comparaison). 
	 */
	private String synchronizeError = null;
	
	/**
     * Construct the node with source and destination objects.
     * @param source       The source file.
     * @param destintation The destintation file.
     */
    public FileSynchronizationStatusTreeNode(final File source, final File destination) {
        super(source, destination);
    }

    /**
     * Add a child. Construct the node (Used by childs class)
     * @param source      The source file.
     * @param destination The destination file.
     */
	protected void addChild(final File source, final File destination) {
		addChild(new FileSynchronizationStatusTreeNode(source, destination));
	}

	/**
	 * Set Synchronization error message.
	 * @param error The error message to be set.
	 */
	protected final void setSynchronizationErrorMessage(final String error) {
		this.synchronizeError = error;
	}
	
	/**
	 * Get Synchronization error message.
	 * @return The error message.
	 */
	public final String getSynchronizationErrorMessage() {
		return this.synchronizeError;
	}

	/**
	 * Compare two files
	 * @param source      The source one (considered as master)
	 * @param destination The destination
	 * @return A character giving the compartion result (see class field for details)
	 */
	private final int synchronize() {
		if ((getSource() == null || !getSource().exists()) && (getDestination() == null || !getDestination().exists())) {
			return SYNCHRONIZATION_FILES_EQUALS;
		} else if (getSource() == null && getDestination() != null && getDestination().exists()) {
			return SYNCHRONIZATION_SOURCE_DELETED;
		} else if (getSource() != null && (getDestination() == null || !getDestination().exists())) {
			return SYNCHRONIZATION_SOURCE_ADDED;
		} else if (!getSource().isDirectory() && !getSource().isFile()) {
			setSynchronizationErrorMessage("Source is neither a directory neither a file !!");
			return SYNCHRONIZATION_ERROR;
		} else if (getSource().isDirectory() != getDestination().isDirectory() || getSource().isFile() != getDestination().isFile()) {
			setSynchronizationErrorMessage("Files types are differents !!");
			return SYNCHRONIZATION_ERROR;
		} else if (getSource().getName().equals(getDestination().getName()) || getParent() == null) {
			if ((getSource().isDirectory() && getDestination().isDirectory()) || (getSource().length() == getDestination().length() && getSource().lastModified() <= getDestination().lastModified())) {
				return SYNCHRONIZATION_FILES_EQUALS;
			} else if (getSource().lastModified() > getDestination().lastModified()) {
				return SYNCHRONIZATION_SOURCE_CHANGED;
			} else if (getSource().lastModified() < getDestination().lastModified()) {
				if (getSource().length() == getDestination().length()) {
					return SYNCHRONIZATION_FILES_EQUALS;
				}
				return SYNCHRONIZATION_DESTINATION_CHANGED;
			}
			setSynchronizationErrorMessage("Oopps I migth have missed something !!");
			return SYNCHRONIZATION_ERROR;
		}
		setSynchronizationErrorMessage("Oopps I did not manage to compare files !!");
		return SYNCHRONIZATION_ERROR;
	}

	protected final void resetSynchronization() {
		this.synchronizeStatus = SYNCHRONIZATION_RESET;
	}

	public final int getSynchronizationStatus() {
		if (synchronizeStatus == SYNCHRONIZATION_RESET) {
			synchronizeStatus = synchronize();
		}
		return synchronizeStatus;
	}
}