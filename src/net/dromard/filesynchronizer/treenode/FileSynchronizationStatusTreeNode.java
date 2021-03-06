package net.dromard.filesynchronizer.treenode;

import java.io.File;
import java.util.List;

import net.dromard.filesynchronizer.modules.IModule;
import net.dromard.filesynchronizer.modules.ModuleManager;

/**
 * File Synchronization Status Tree Node object.
 * This is a node that add the capability to compare file source with destination.
 * @author Pingus
 */
public abstract class FileSynchronizationStatusTreeNode extends FileSynchronizerTreeNode {
	/**
	 * Human representation of file compare result.
	 */
	public static char[] SYNCHRONIZATION_STATUS = new char[] { '!', '-', '+', '<', '>', '=' };
	
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
		int synchronizeStatus = SYNCHRONIZATION_ERROR;

		if ((getSource() == null || !getSource().exists()) && (getDestination() == null || !getDestination().exists())) {
			synchronizeStatus = SYNCHRONIZATION_FILES_EQUALS;
		} else if (getSource() == null && getDestination() != null && getDestination().exists()) {
			synchronizeStatus = SYNCHRONIZATION_SOURCE_DELETED;
		} else if (getSource() != null && (getDestination() == null || !getDestination().exists())) {
			synchronizeStatus = SYNCHRONIZATION_SOURCE_ADDED;
		} else if (!getSource().isDirectory() && !getSource().isFile()) {
			setSynchronizationErrorMessage("Source is neither a directory neither a file !!");
			synchronizeStatus = SYNCHRONIZATION_ERROR;
		} else if (getSource().isDirectory() != getDestination().isDirectory() || getSource().isFile() != getDestination().isFile()) {
			setSynchronizationErrorMessage("Files types are differents !!");
			synchronizeStatus = SYNCHRONIZATION_ERROR;
		} else if (getSource().getName().equals(getDestination().getName()) || getParent() == null) {
			if ((getSource().isDirectory() && getDestination().isDirectory()) || (getSource().length() == getDestination().length() && getSource().lastModified() <= getDestination().lastModified())) {
				synchronizeStatus = SYNCHRONIZATION_FILES_EQUALS;
			} else if (getSource().lastModified() > getDestination().lastModified()) {
				synchronizeStatus = SYNCHRONIZATION_SOURCE_CHANGED;
			} else if (getSource().lastModified() < getDestination().lastModified()) {
				if (getSource().length() == getDestination().length()) {
					synchronizeStatus = SYNCHRONIZATION_FILES_EQUALS;
				}
				synchronizeStatus = SYNCHRONIZATION_DESTINATION_CHANGED;
			} else {
				setSynchronizationErrorMessage("Oopps I migth have missed something !!");
			}
		}
		List<IModule> modules = ModuleManager.getInstance().getAvailableModules();
		for (IModule module : modules) {
			synchronizeStatus = module.synchronize(synchronizeStatus, getSource(), getDestination());
		}
		
		if (synchronizeStatus == SYNCHRONIZATION_ERROR) {
			setSynchronizationErrorMessage("Oopps I did not manage to compare files !!");
		}
		return synchronizeStatus;
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

	public static void registerSynchronizationStatus(char[] retrieveSynchronizationStatus) {
		char[] tmp = new char[SYNCHRONIZATION_STATUS.length + retrieveSynchronizationStatus.length];
		for (int i = 0; i < SYNCHRONIZATION_STATUS.length; ++i) {
			tmp[i] = SYNCHRONIZATION_STATUS[i];
		}
		for (int i = 0; i < retrieveSynchronizationStatus.length; ++i) {
			tmp[SYNCHRONIZATION_STATUS.length + i] = retrieveSynchronizationStatus[i];
		}
		SYNCHRONIZATION_STATUS = tmp;
	}
}