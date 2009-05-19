package net.dromard.filesynchronizer.treenode;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.dromard.filesynchronizer.modules.IModule;
import net.dromard.filesynchronizer.modules.ModuleManager;

/**
 * File Synchronizer Todo Status Tree Node object.
 * This is a node that add the capability to flag a todo task depending on synchronization status.
 * This implementation act by default as a backuper. 
 * (their is - by default - no modification of sources files and no deletion of destination files)
 * @author Pingus
 */
public class FileSynchronizerTodoTaskTreeNode extends FileSynchronizationStatusTreeNode {
	/**
	 * Human representation of file todo status.
	 */
	public static char[] TODO_TASKS = new char[] { '!', '<', '-', '+', '>', '-', '+', ' ' };

	/**
	 * Human representation of file todo status.
	 */
	public static Map<Integer, String> TODO_TASKS_NAMES = new HashMap<Integer, String>();

	/**
	 * Force recalculation of synchronization (comparaison).
	 */
	public static final int TODO_RESET = -1;

	/**
	 * [?] Can occured if the files modification dates are equals but the files length are different.
	 */
	public static final int TODO_ERROR = 0;

	/**
	 * [<] Used when the source file has been modified outside. (is older than the backup)
	 */
	public static final int TODO_UPDATE_SOURCE = 1;

	/**
	 * [-] Used when a source file has to be deleted (it no more exists in destination).
	 */
	public static final int TODO_DELETE_SOURCE = 2;

	/**
	 * [+] Used when the source file has to be created (it does not exist in destination - must never happen).
	 */
	public static final int TODO_CREATE_SOURCE = 3;

	/**
	 * [>] Used when the destination file has to be updated.
	 */
	public static final int TODO_UPDATE_DESTINATION = 4;

	/**
	 * [-] Used when a destination file has to be deleted (it no more exists in source).
	 */
	public static final int TODO_DELETE_DESTINATION = 5;

	/**
	 * [+] Used when the destination file has to be created (it does not exist).
	 */
	public static final int TODO_CREATE_DESTINATION = 6;

	/**
	 * [ ] Occurred if we are parsing the root folder to be synchronize.
	 */
	public static final int TODO_NOTHING = 7;

	static { 
		TODO_TASKS_NAMES.put(TODO_RESET, "Reset");
		TODO_TASKS_NAMES.put(TODO_ERROR, "An error occured during task computation"); 
		TODO_TASKS_NAMES.put(TODO_UPDATE_SOURCE, "Update source");
		TODO_TASKS_NAMES.put(TODO_DELETE_SOURCE, "Delete source");
		TODO_TASKS_NAMES.put(TODO_CREATE_SOURCE, "Add source");
		TODO_TASKS_NAMES.put(TODO_UPDATE_DESTINATION, "Update destination"); 
		TODO_TASKS_NAMES.put(TODO_DELETE_DESTINATION, "Delete destination");
		TODO_TASKS_NAMES.put(TODO_CREATE_DESTINATION, "Add destination");
		TODO_TASKS_NAMES.put(TODO_NOTHING, "Do nothing");
	};

	/* -------------------------------------------------------------------------------- */

	/**
	 * Todo task. (the task to do depending on synchronization status) 
	 */
	private int todoTask = TODO_RESET;
	
	/**
	 * Todo Task calculation error. 
	 */
	private String error = "May be an error occured while treating my father ?";
	
	/**
     * Construct the node with source and destination objects.
     * @param source       The source file.
     * @param destintation The destintation file.
     */
    public FileSynchronizerTodoTaskTreeNode(final File source, final File destination) {
        super(source, destination);
    }

    /**
     * Add a child. Construct the node (Used by childs class)
     * @param source      The source file.
     * @param destination The destination file.
     */
	protected void addChild(final File source, final File destination) {
		addChild(new FileSynchronizerTodoTaskTreeNode(source, destination));
	}
	
	/**
	 * Set todo error message.
	 * @param error The error message to be set.
	 */
	protected final void setErrorMessage(final String error) {
		this.error = error;
	}
	
	/**
	 * Get todo error message.
	 * @return The error message.
	 */
	public final String getErrorMessage() {
		return this.error;
	}

	/**
	 * Compare two files
	 * @param source      The source one (considered as master)
	 * @param destination The destination
	 * @return A character giving the compartion result (see class field for details)
	 */
	protected final int calculateTodoTask() {
		if (getSynchronizationStatus() == SYNCHRONIZATION_ERROR || getSynchronizationStatus() == SYNCHRONIZATION_FILES_EQUALS) {
			return TODO_NOTHING;
		} else if(getSynchronizationStatus() == SYNCHRONIZATION_DESTINATION_CHANGED || getSynchronizationStatus() == SYNCHRONIZATION_SOURCE_CHANGED) {
			return TODO_UPDATE_DESTINATION;
		} else if(getSynchronizationStatus() == SYNCHRONIZATION_SOURCE_ADDED) {
			return TODO_CREATE_DESTINATION;
		} else if(getSynchronizationStatus() == SYNCHRONIZATION_SOURCE_DELETED) {
			return TODO_CREATE_SOURCE;
		}
		List<IModule> modules = ModuleManager.getInstance().getAvailableModules();
		for (IModule module : modules) {
			if (module.knowsSynchronizationStatus(getSynchronizationStatus())) {
				return module.calculateTodoTask(getSynchronizationStatus());
			}
		}
		setErrorMessage("Ooops its seams that I missed a case !!");
		return TODO_ERROR;
	}
	
	/**
	 * Retreive todo task.
	 * @return The todo task
	 */
	public final int getTodoTask() {
		if (todoTask == TODO_RESET) {
			todoTask = calculateTodoTask();
		}
		return todoTask;
	}

	/**
	 * Set the todo task.
	 * @param todoTask The todo task to be set.
	 */
	public final void setTodoTask(final int todoTask) {
		if (todoTask == TODO_RESET) {
			resetSynchronization();
		}
		if (getSource() != null && getSource().isDirectory()) {
			if (todoTask == TODO_NOTHING || todoTask == TODO_ERROR) {
				for (Iterator<FileSynchronizerTreeNode> childs = getChilds().iterator(); childs.hasNext();) {
					((FileSynchronizerTodoTaskTreeNode) childs.next()).setTodoTask(todoTask);
				}
			} else {
				for (Iterator<FileSynchronizerTreeNode> childs = getChilds().iterator(); childs.hasNext();) {
					((FileSynchronizerTodoTaskTreeNode) childs.next()).setTodoTask(TODO_RESET);
				}
			}
		}
		this.todoTask = todoTask;
	}

	public static void registerTodoTasks(char[] todoTasks, String[] todoTaskNames) {
		// Add todo task names
		for (String taskName : todoTaskNames) {
			// -1 for reset !!
			TODO_TASKS_NAMES.put(TODO_TASKS_NAMES.size()-1, taskName);
		}
		char[] tmp1 = new char[TODO_TASKS.length + todoTasks.length];
		for (int i = 0; i < TODO_TASKS.length; ++i) {
			tmp1[i] = TODO_TASKS[i];
		}
		for (int i = 0; i < todoTasks.length; ++i) {
			tmp1[TODO_TASKS.length + i] = todoTasks[i];
		}
		TODO_TASKS = tmp1;
	}
}