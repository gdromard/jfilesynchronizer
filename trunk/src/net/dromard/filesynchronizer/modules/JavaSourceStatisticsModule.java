package net.dromard.filesynchronizer.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.dromard.filesynchronizer.gui.AbstractManager;
import net.dromard.filesynchronizer.gui.ManagerListener;
import net.dromard.filesynchronizer.treenode.FileSynchronizationStatusTreeNode;

// Uncomment IModule to reactivate this module.
public class JavaSourceStatisticsModule implements /*IModule,*/ManagerListener {
    public int nbFiles = 0;
    public int nbLines = 0;
    public int nbClasses = 0;
    public int nbTest = 0;
    public int nbInterfaces = 0;

    public JavaSourceStatisticsModule() {
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
        if (currentStatus == FileSynchronizationStatusTreeNode.SYNCHRONIZATION_FILES_EQUALS) {
            getStats(source);
        }
        return currentStatus;
    }

    public void doTask(final int alreadyDoneTask, final int todoTask, final File source, final File destination) {
    }

    public int calculateTodoTask(final int synchronizationStatus) {
        throw new RuntimeException("I do not know this synchronisation status: " + synchronizationStatus + ", please use knows() methods before.");
    }

    public boolean knowsSynchronizationStatus(final int synchronizationStatus) {
        return false;
    }

    public boolean knowsTodoTask(final int todoTask) {
        return false;
    }

    public List<Integer> getPossibleTasks(final int synchronizationStatus) {
        return null;
    }

    private void getStats(final File source) {
        if (source.isFile() && source.getName().endsWith(".java")) {
            BufferedReader reader = null;
            try {
                reader = (new BufferedReader(new FileReader(source)));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    ++nbLines;
                    if (line.indexOf("class ") > 0) {
                        ++nbClasses;
                    }
                    if (line.indexOf("interface ") > 0) {
                        ++nbInterfaces;
                    }
                    if (line.indexOf("@Test") > 0) {
                        ++nbTest;
                    }
                }
                ++nbFiles;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void processStopped(final AbstractManager initiator) {
    }

    public void synchronizeStopped(final AbstractManager initiator) {
    }

    public void processFinished(final AbstractManager initiator) {
    }

    public void processStarted(final AbstractManager initiator) {
    }

    public void synchronizeFinished(final AbstractManager initiator) {
        if (nbFiles > 0) {
            System.out.println("Number of files: " + nbFiles);
            System.out.println("Number of lines: " + nbLines);
            System.out.println("Number of classes: " + nbClasses);
            System.out.println("Number of tests: " + nbTest);
            System.out.println("Number of interfaces: " + nbInterfaces);
            System.out.println("Mean lines per file: " + nbLines / nbFiles);
            System.out.println("Mean classes per file: " + 1f * nbClasses / nbFiles);
        }
    }

    public void synchronizeStarted(final AbstractManager initiator) {
    }
}