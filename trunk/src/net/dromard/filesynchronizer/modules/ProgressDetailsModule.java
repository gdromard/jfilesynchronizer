package net.dromard.filesynchronizer.modules;

import static net.dromard.filesynchronizer.treenode.FileSynchronizationStatusTreeNode.SYNCHRONIZATION_FILES_EQUALS;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_NOTHING;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_ERROR;
import java.io.File;
import java.util.List;
import java.util.Map;

import net.dromard.filesynchronizer.gui.MainFrame;

// Uncomment IModule to reactivate this module.
public class ProgressDetailsModule implements IModule {
	public int nbFiles = 0;
	public int nbFilesToBackup = 0;
	private int nbBackedupFiles = 0;
	
	public ProgressDetailsModule() {
		System.out.println("Loading module " + this.getClass().getSimpleName());
	}
	
	public char[] retrieveTodoTasks() {
		return new char[] { };
	}

	public String[] retrieveTodoTaskNames() {
		return new String[] { };
	}

	public void addImageTypes(final Map<Integer, String> imageTypeByTodoTask) {
	}

	public int synchronize(final int currentStatus, final File source, final File destination) {
		if (currentStatus != SYNCHRONIZATION_FILES_EQUALS) {
			++nbFilesToBackup;
		}
		++nbFiles;
		MainFrame.getInstance().getProgressBarHandler().setInfo(nbFilesToBackup + "/" +nbFiles + " " + (source!=null?source.getName():destination.getName()));
		return currentStatus;
	}

	public int doTask(int todoTask, File source, File destination) {
		if (todoTask != TODO_NOTHING && todoTask != TODO_ERROR) {
			++nbBackedupFiles ;
		}
		MainFrame.getInstance().getProgressBarHandler().setInfo(nbBackedupFiles + "/" +nbFilesToBackup + " " + (source!=null?source.getName():destination.getName()));
		return todoTask;
	}

	public int calculateTodoTask(int synchronizationStatus) {
		throw new RuntimeException("I do not know this synchronisation status: " + synchronizationStatus + ", please use knows() methods before.");
	}

	public boolean knowsSynchronizationStatus(int synchronizationStatus) {
		return false;
	}

	public boolean knowsTodoTask(int todoTask) {
		return false;
	}

	public List<Integer> getPossibleTasks(int synchronizationStatus) {
		return null;
	}
}