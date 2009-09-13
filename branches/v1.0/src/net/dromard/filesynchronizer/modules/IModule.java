package net.dromard.filesynchronizer.modules;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.dromard.filesynchronizer.treenode.FileSynchronizationStatusTreeNode;

public interface IModule {
	
	public char[] retrieveTodoTasks();
	
	public String[] retrieveTodoTaskNames();
	
	public void addImageTypes(final Map<Integer, String> imageTypeByTodoTask);
	
	public int calculateTodoTask(int synchronizationStatus);

	public boolean knowsSynchronizationStatus(int synchronizationStatus);
	
	/**
	 * Synchronize the two files.
	 * If the module is not able to do anything depending on synchronization status,
	 * It has to return the same synchronizationStatus. Else it has to return a own 
	 * synchronization status starting at {@link FileSynchronizationStatusTreeNode#SYNCHRONIZATION_STATUS}.length
	 * @param synchronizationStatus The current status.
	 * @param source      The source file
	 * @param destination The destination file
	 * @return The synchronization status for this module.
	 */
	public int synchronize(final int synchronizationStatus, final File source, final File destination);

	public boolean knowsTodoTask(int todoTask);

	/**
	 * Do the task.
	 * The {@link #knowsTodoTask(int)} method must be call just before.
	 * @param todoTask    The task to be done
	 * @param source      The source file
	 * @param destination The destination file
	 * @return The to do task, if all goes well it must be FileSynchronizerTodoTaskTreeNode#TODO_NOTHING
	 */
	public int doTask(int todoTask, File source, File destination);

	/**
	 * Called when right clicking on a line for adding dynamic possible tasks on popup menu.
	 * @param synchronizationStatus The actual synchronization status
	 * @return The possible task for a given synchronization status for this module.
	 */
	public List<Integer> getPossibleTasks(int synchronizationStatus);
}
