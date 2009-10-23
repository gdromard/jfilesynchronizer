package net.dromard.filesynchronizer.modules;

import static net.dromard.filesynchronizer.treenode.FileSynchronizationStatusTreeNode.SYNCHRONIZATION_FILES_EQUALS;

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
        return new char[]{};
    }

    public String[] retrieveTodoTaskNames() {
        return new String[]{};
    }

    public void addImageTypes(final Map<Integer, String> imageTypeByTodoTask) {
    }

    public int synchronize(final int currentStatus, final File source, final File destination) {
        if (currentStatus != SYNCHRONIZATION_FILES_EQUALS) {
            ++nbFilesToBackup;
        }
        ++nbFiles;
        MainFrame.getInstance().getProgressBarHandler().setInfo(nbFilesToBackup + "/" + nbFiles + " " + (source != null ? source.getName() : destination.getName()));
        return currentStatus;
    }

    public void doneTask(final int alreadyDoneTask, final int todoTask, final File source, final File destination) {
        if (todoTask == alreadyDoneTask) {
            ++nbBackedupFiles;
        }
        MainFrame.getInstance().getProgressBarHandler().setInfo(nbBackedupFiles + "/" + nbFilesToBackup + " " + (source != null ? source.getName() : destination.getName()));
    }

    public int calculateTodoTask(final int synchronizationStatus) {
        throw new RuntimeException("I do not know this synchronisation status: " + synchronizationStatus + ", please use knows() methods before.");
    }

    public boolean knowsSynchronizationStatus(final int synchronizationStatus) {
        return false;
    }

    public boolean knowsTodoTask(final int todoTask) {
        return true;
    }

    public List<Integer> getPossibleTasks(final int synchronizationStatus) {
        return null;
    }
}