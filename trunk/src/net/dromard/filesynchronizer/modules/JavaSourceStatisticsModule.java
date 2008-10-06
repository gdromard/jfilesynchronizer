package net.dromard.filesynchronizer.modules;

import static net.dromard.filesynchronizer.treenode.FileSynchronizationStatusTreeNode.SYNCHRONIZATION_FILES_EQUALS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.dromard.filesynchronizer.gui.AbstractManager;
import net.dromard.filesynchronizer.gui.ManagerListener;

// Uncomment IModule to reactivate this module.
public class JavaSourceStatisticsModule implements IModule, ManagerListener {
	public int nbFiles = 0;
	public int nbLines = 0;
	public int nbClasses = 0;
	public int nbInterfaces = 0;
	
	public JavaSourceStatisticsModule() {
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
		if (currentStatus == SYNCHRONIZATION_FILES_EQUALS) {
			getStats(source);
		}
		return currentStatus;
	}

	public int doTask(int todoTask, File source, File destination) {
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

	private void getStats(File source) {
		if (source.isFile() && source.getName().endsWith(".java")) {
			BufferedReader reader = null;
			try {
				reader = (new BufferedReader(new FileReader(source)));
				String line = null;
				while ((line = reader.readLine()) != null) {
					++nbLines;
					if (line.indexOf(" class ") > 0) {
						++nbClasses;
					}
					if (line.indexOf(" interface ") > 0) {
						++nbInterfaces;
					}
				}
				++nbFiles;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (reader != null) reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void processFinished(AbstractManager initiator) {
	}

	public void processStarted(AbstractManager initiator) {
	}

	public void synchronizeFinished(AbstractManager initiator) {
		System.out.println("Number of files: " + nbFiles);
		System.out.println("Number of lines: " + nbLines);
		System.out.println("Number of classes: " + nbClasses);
		System.out.println("Number of interfaces: " + nbInterfaces);
		System.out.println("Mean lines per file: " + nbLines / nbFiles);
		System.out.println("Mean classes per file: " + 1f * nbClasses / nbFiles);
	}

	public void synchronizeStarted(AbstractManager initiator) {
	}
}