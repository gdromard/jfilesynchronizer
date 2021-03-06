package net.dromard.filesynchronizer;

import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_CREATE_DESTINATION;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_CREATE_SOURCE;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_DELETE_DESTINATION;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_DELETE_SOURCE;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_ERROR;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_NOTHING;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_RESET;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_TASKS;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_UPDATE_DESTINATION;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_UPDATE_SOURCE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dromard.common.io.FileHelper;
import net.dromard.common.util.ReflectHelper;
import net.dromard.common.visitable.Visitable;
import net.dromard.common.visitable.Visitor;
import net.dromard.filesynchronizer.modules.IModule;
import net.dromard.filesynchronizer.modules.ModuleManager;
import net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode;

/**
 * 
 * @author Pingus
 */
public class FileSynchronizerVisitor implements Visitor {
    /** Internal process mode. */
    private int mode;
    protected boolean displayOnly = false;
    /** If a read only file is detect, do we set it as writable ? */
    protected boolean forceWrite = true;
    /** */
    private boolean abort = false;

    private final Map<Integer, Integer> todo = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> done = new HashMap<Integer, Integer>();

    /**
     * Constructor with mode. It display all or simply a set of file comparaisons
     * @param mode the internal process mode.
     */
    public FileSynchronizerVisitor() {
        this.mode = TODO_TASKS.length - 1;
    }

    /**
     * Default empty constructor.
     */
    public FileSynchronizerVisitor(final boolean displayOnly) {
        this();
        this.displayOnly = displayOnly;
    }

    /**
     * Constructor with mode. 
     * It display all or simply a set of file comparisons.
     * @param mode the internal process mode.
     */
    public FileSynchronizerVisitor(final int mode) {
        this();
        this.mode = mode;
    }

    /**
     * Constructor with mode. 
     * It display all or simply a set of file comparisons.
     * @param mode the internal process mode.
     */
    public FileSynchronizerVisitor(final int mode, final boolean displayOnly) {
        this(displayOnly);
        this.mode = mode;
    }

    /**
     * Stop visiting loop.
     */
    public void abort() {
        abort = true;
    }

    public void printSummary() {
        String sTodo = "";
        String sDone = "";
        for (int i = 0; i < TODO_TASKS.length; ++i) {
            sTodo += "[" + TODO_TASKS[i] + "] x " + todo.get(i) + "  ";
            if (!displayOnly) {
                sDone += "[" + TODO_TASKS[i] + "] x " + done.get(i) + "  ";
            }
        }
        System.out.println(sTodo);
        System.out.println(sDone);
    }

    /**
     * Vist implementation.
     * 
     * @param node The element object of the tree.
     * @throws Exception Any exception can occurred during visit.
     */
    public final void visit(final Visitable visitable) throws Exception {
        // Stop visiting if running has been flagged to false.
        if (abort) {
            System.out.println("[FileSynchronizerVisitor]�Aborting visit ...");
            return;
        }
        FileSynchronizerTodoTaskTreeNode node = (FileSynchronizerTodoTaskTreeNode) visitable;

        int todoTask = node.getTodoTask();
        int taskDone = TODO_NOTHING;
        incrementTodo(todoTask);

        if (!displayOnly) {
            taskDone = processBackup(node, todoTask);
            incrementDone(taskDone);
        }
        processDisplay(node, todoTask, taskDone);
    }

    private void incrementDone(final int taskDone) {
        Integer integer = done.get(taskDone);
        if (integer == null) {
            integer = 0;
        }
        done.put(taskDone, integer + 1);
    }

    private void incrementTodo(final int todoTask) {
        Integer integer = todo.get(todoTask);
        if (integer == null) {
            integer = 0;
        }
        todo.put(todoTask, integer + 1);
    }

    /**
     * Process the comparaison of the node, display informattions and return the
     * compare status.
     * 
     * @param fileBackup The Node to be backedup
     * @return The file compare status
     */
    protected void processDisplay(final FileSynchronizerTodoTaskTreeNode fileBackup, final int todoTask, final int doneTask) {
        String prefix = "";
        if (mode >= TODO_TASKS.length - 1) {
            for (int i = 0; i < fileBackup.getRank(); i++) {
                prefix += "  ";
            }
        }
        if (todoTask <= mode || (!displayOnly && (doneTask <= mode || doneTask == TODO_NOTHING))) {
            System.out.println("[" + TODO_TASKS[todoTask] + "] (" + TODO_TASKS[doneTask] + ") " + prefix + fileBackup.getRelativePath());
        }
    }

    /**
     * Process the backup of the node.
     * 
     * @param fileBackup The Node to be backedup
     * @param todoTask The task to be done
     * @return The processed status
     * @throws IOException If something wrong occured
     * @throws FileNotFoundException Can be thrown while creating a new file
     */
    protected int processBackup(final FileSynchronizerTodoTaskTreeNode fileBackup, final int todoTask) {
        int doneTask = TODO_NOTHING;
        if (!displayOnly) {
            List<IModule> modules = ModuleManager.getInstance().getAvailableModules();
            switch (todoTask) {
            // Source modifications
            case TODO_DELETE_SOURCE:
                if (FileHelper.delete(fileBackup.getSource())) {
                    doneTask = todoTask;
                } else {
                    doneTask = TODO_ERROR;
                }
                break;
            case TODO_CREATE_SOURCE:
                if (create(fileBackup.getDestination(), fileBackup.createSource())) {
                    doneTask = todoTask;
                } else {
                    doneTask = TODO_ERROR;
                }
                break;
            case TODO_UPDATE_SOURCE:
                if (update(fileBackup.getDestination(), fileBackup.getSource())) {
                    doneTask = todoTask;
                } else {
                    doneTask = TODO_ERROR;
                }
                break;
            // Destination modifications
            case TODO_DELETE_DESTINATION:
                if (FileHelper.delete(fileBackup.getDestination())) {
                    doneTask = todoTask;
                } else {
                    doneTask = TODO_ERROR;
                }
                break;
            case TODO_CREATE_DESTINATION:
                if (create(fileBackup.getSource(), fileBackup.createDestination())) {
                    doneTask = todoTask;
                } else {
                    doneTask = TODO_ERROR;
                }
                break;
            case TODO_UPDATE_DESTINATION:
                if (update(fileBackup.getSource(), fileBackup.getDestination())) {
                    doneTask = todoTask;
                } else {
                    doneTask = TODO_ERROR;
                }
                break;
            // No modifications
            case TODO_ERROR:
            case TODO_NOTHING:
                doneTask = TODO_NOTHING;
            default:
                break;
            }
            for (IModule module : modules) {
                if (module.knowsTodoTask(todoTask)) {
                    module.doneTask(doneTask, todoTask, fileBackup.getSource(), fileBackup.getDestination());
                }
            }
            // If all goes well reset todo task
            if (doneTask != TODO_ERROR && doneTask == todoTask) {
                fileBackup.setTodoTask(TODO_RESET);
            } else {
                fileBackup.setTodoTask(doneTask);
            }
        }
        return doneTask;
    }

    protected final boolean update(final File source, final File destination) {
        if (source.isDirectory()) {
            return setLastModified(source, destination);
        }
        if (source.isFile()) {
            if (!destination.canWrite()) {
                try {
                    if (!forceWrite || !((Boolean) ReflectHelper.invokeMethod(destination, "setWritable", new Object[]{Boolean.TRUE})).booleanValue()) {
                        System.out.println("[WARNING] Source file '" + destination.getPath() + "' is read only.");
                        return false;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("[WARNING] Source file '" + destination.getPath() + "' is read only.");
                    return false;
                }
            }
            try {
                FileHelper.copy(source, destination);
                setLastModified(source, destination);
                return true;
            } catch (IOException e) {
                System.out.println("[WARNING] Unable to copy '" + source.getPath() + "' to '" + destination.getPath() + "'.");
                FileHelper.delete(destination);
                // e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    protected final boolean create(final File source, final File destination) {
        if (source.isDirectory()) {
            return destination.mkdir();
        }
        if (source.isFile()) {
            try {
                if (!destination.getParentFile().exists()) {
                    if (!destination.getParentFile().mkdirs()) {
                        return false;
                    }
                }
                if (destination.createNewFile()) {
                    FileHelper.copy(source, destination);
                    setLastModified(source, destination);
                    return true;
                }
            } catch (IOException e) {
                System.out.println("[WARNING] Unable to copy '" + source.getPath() + "' to '" + destination.getPath() + "'.");
                FileHelper.delete(destination);
                // e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    protected final boolean setLastModified(final File source, final File destination) {
        if (!destination.setLastModified(source.lastModified()) || destination.lastModified() != source.lastModified()) {
            if (!destination.setLastModified(new Date().getTime())) {
                System.out.println("[WARNING] Unable to set last modification time on '" + destination.getPath() + "'.");
                return false;
            }
        }
        return true;
    }
}
