package net.dromard.filesynchronizer.gui;

import static net.dromard.filesynchronizer.treenode.FileSynchronizationStatusTreeNode.SYNCHRONIZATION_DESTINATION_CHANGED;
import static net.dromard.filesynchronizer.treenode.FileSynchronizationStatusTreeNode.SYNCHRONIZATION_SOURCE_ADDED;
import static net.dromard.filesynchronizer.treenode.FileSynchronizationStatusTreeNode.SYNCHRONIZATION_SOURCE_CHANGED;
import static net.dromard.filesynchronizer.treenode.FileSynchronizationStatusTreeNode.SYNCHRONIZATION_SOURCE_DELETED;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_CREATE_DESTINATION;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_CREATE_SOURCE;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_DELETE_DESTINATION;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_DELETE_SOURCE;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_NOTHING;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_RESET;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_TASKS_NAMES;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_UPDATE_DESTINATION;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_UPDATE_SOURCE;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.dromard.filesynchronizer.FileSynchronizerVisitor;
import net.dromard.filesynchronizer.gui.icons.Icons;
import net.dromard.filesynchronizer.gui.table.FileTableModel;
import net.dromard.filesynchronizer.gui.tree.JFileSynchronizerTreeNode;
import net.dromard.filesynchronizer.modules.IModule;
import net.dromard.filesynchronizer.modules.ModuleManager;

public abstract class AbstractManager extends MouseAdapter implements ActionListener {
	private Thread synchronizer;
	private Thread processor;
	private JComponent component;
	private ArrayList<ManagerListener> listeners = new ArrayList<ManagerListener>();
	private ArrayList<GuiManagerListener> guiManagerListeners = new ArrayList<GuiManagerListener>();
	
	protected AbstractManager(final JComponent component) {
		this.component = component;
	}
    
    protected abstract ArrayList<JFileSynchronizerTreeNode> getSelectedElements();
	
    protected abstract AbstractModel getAbstractModel();
    
    protected abstract void setModelToComponent(final JFileSynchronizerTreeNode root);
    
    public void stopProcesses() {
    	processor.stop();
    }

    public void addListener(final ManagerListener managerListener) {
    	listeners.add(managerListener);
    }
    
    public void addListener(final GuiManagerListener guiManagerListener) {
    	guiManagerListeners.add(guiManagerListener);
    }
    
    /**
     * Create the popup for this tree.
     * This is a default implementation that add Delete menu and Insert Menu
     * @return A DDMPopup comp.
     */
    protected final JPopupMenu createPopupMenu() {
    	JPopupMenu popup = new JPopupMenu();
    	JMenuItem processMenu = new JMenuItem();
    	processMenu.setActionCommand("PROCESS");
		processMenu.setText("Process");
		processMenu.addActionListener(this);
    	popup.add(processMenu);
    	JMenu overlayIconsMenu = addOverlayIconsToMenu();
    	if (overlayIconsMenu != null) {
    		popup.add(overlayIconsMenu);
    	}
        return popup;
    }
	
    private final List<Integer> getPossibleTask() {
    	ArrayList<JFileSynchronizerTreeNode> nodes = getSelectedElements();
    	List<Integer> possibleTask = new ArrayList<Integer>();
    	possibleTask.add(possibleTask.size(), TODO_NOTHING);
    	possibleTask.add(possibleTask.size(), TODO_RESET);
    	if (nodes.size() == 0) return null;
    	
    	int synchronizationStatus = nodes.get(0).getSynchronizationStatus(); 
    	for (JFileSynchronizerTreeNode node : nodes) {
    		if (synchronizationStatus != node.getSynchronizationStatus()) {
    			return possibleTask;
    		}
    	}
    	/*
    	if (nodes.size() == 1) {
	    	JFileSynchronizerTreeNode node = nodes.get(0);
	    	int todoTask = node.getTodoTask();
			if (todoTask == TODO_NOTHING) {
				node.setTodoTask(TODO_RESET);
				todoTask = node.getTodoTask();
				node.setTodoTask(TODO_NOTHING);
				if (todoTask != TODO_NOTHING) {
					possibleTask[0] = todoTask;
				}
			}
    	}
    	*/
        if (synchronizationStatus == SYNCHRONIZATION_DESTINATION_CHANGED) {
        	possibleTask.add(possibleTask.size(), TODO_UPDATE_SOURCE);
        	possibleTask.add(possibleTask.size(), TODO_UPDATE_DESTINATION);
        } else if (synchronizationStatus == SYNCHRONIZATION_SOURCE_CHANGED) {
        	possibleTask.add(possibleTask.size(), TODO_UPDATE_DESTINATION);
        	possibleTask.add(possibleTask.size(), TODO_UPDATE_SOURCE);
        } else if (synchronizationStatus == SYNCHRONIZATION_SOURCE_DELETED) {
        	possibleTask.add(possibleTask.size(), TODO_CREATE_SOURCE);
        	possibleTask.add(possibleTask.size(), TODO_DELETE_DESTINATION);
        } else if (synchronizationStatus == SYNCHRONIZATION_SOURCE_ADDED) {
        	possibleTask.add(possibleTask.size(), TODO_DELETE_SOURCE);
        	possibleTask.add(possibleTask.size(), TODO_CREATE_DESTINATION);
        }
        List<IModule> modules = ModuleManager.getInstance().getAvailableModules();
        for (IModule module : modules) {
	        if (module.knowsSynchronizationStatus(synchronizationStatus)) {
	        	List<Integer> modulePossibleTasks = module.getPossibleTasks(synchronizationStatus);
	        	if (modulePossibleTasks != null) {
	        		possibleTask.addAll(possibleTask.size(), modulePossibleTasks);
	        	}
		    }
        }
    	return possibleTask;
    }
    
	protected final JMenu addOverlayIconsToMenu() {
		List<Integer> possibleTasks = getPossibleTask();
		if (possibleTasks != null && possibleTasks.size() > 0) {
			JMenu menu = new JMenu();
			menu.setText("Override todo task to ...");
			for (int possibleTask : possibleTasks) {
				JMenuItem todoTask = new JMenuItem();
				todoTask.setActionCommand("TASK_" + possibleTask);
				todoTask.setIcon(Icons.getIcon(IconManager.getImageType(possibleTask) + IconManager.ICON_EXTENSION));
				todoTask.setText(TODO_TASKS_NAMES.get(possibleTask));
				todoTask.addActionListener(this);
				menu.add(todoTask);
			}
			return menu;
		}
		return null;
	}

    /**
     * Show the popup if requested.
     * @param event The original mouse event.
     * @param event Event.
     */
    protected final void maybeShowPopup(final MouseEvent event) {
    	if (getSelectedElements().size() > 0) {
    		fireElementsSelected();
    	}
        if (event.getButton() == MouseEvent.BUTTON3) {
        	JPopupMenu popup = createPopupMenu();
            popup.setInvoker(event.getComponent());
            Point invokerOrigin = event.getComponent().getLocationOnScreen();
            popup.setLocation(invokerOrigin.x + event.getX(), invokerOrigin.y + event.getY());
            fireShowPopup(popup);
            popup.setVisible(true);
        }
    }

	protected void refreshUI() {
		SwingUtilities.updateComponentTreeUI(component);
	}

	public void stopCurrentProcess() {
		if (synchronizer != null && synchronizer.isAlive()) {
			synchronizer.interrupt();
			fireSynchronizeStopped();
		}
		if (processor != null && processor.isAlive()) {
			processor.interrupt();
			fireProcessStopped();
		}
	}
    
	public final void synchronize() {
		JFileSynchronizerTreeNode root = getAbstractModel().getRootNode();
		synchronize(root.getSource(), root.getDestination());
	}
	
	public final void synchronize(final File source, final File destination) {
		synchronizer = new Thread(new Synchronizer(source, destination));
		synchronizer.start();
    }

	public final void processSynchronization() {
		processSynchronization(getAbstractModel().getRootNode());
	}
	
	public final void processSynchronization(final JFileSynchronizerTreeNode node) {
		processor = new Processor(node);
		processor.start();
	}
	
	public final void processSynchronization(final ArrayList<JFileSynchronizerTreeNode> nodes) {
		processor = new Processor(nodes);
		processor.start();
	}

	class Processor extends Thread {
		ArrayList<JFileSynchronizerTreeNode> nodes;
		private FileSynchronizerVisitor visitor;
		
		public Processor(final JFileSynchronizerTreeNode node) {
			nodes = new ArrayList<JFileSynchronizerTreeNode>();
			nodes.add(node);
		}
		public Processor(final ArrayList<JFileSynchronizerTreeNode> nodes) {
			this.nodes = nodes;
		}
		@Override
		public void interrupt() {
			if (visitor != null) visitor.abort();
		}

        public void run() {
    		try {
    			fireProcessStarted();
    			visitor = new FileSynchronizerVisitor();
    			for (JFileSynchronizerTreeNode node : nodes) {
    				visitor.visit(node);
				}
    			fireProcessFinished();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        }
	}
	
	class Synchronizer extends Thread {
		private final File source;
		private final File destination;
		private FileTableModel model;
		public Synchronizer(final File source, final File destination) {
			setModelToComponent(null);
			this.source = source;
			this.destination = destination;
		}
		@Override
		public void interrupt() {
			if (model != null)	model.abort();
		}

		public void run() {
			fireSynchronizeStarted();
			component.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			model = (FileTableModel) getAbstractModel();
			model.setRootNode(new JFileSynchronizerTreeNode(source, destination));
			setModelToComponent(getAbstractModel().getRootNode());
			component.getParent().getParent().setCursor(Cursor.getDefaultCursor());
			fireSynchronizeFinished();
		}
	}
	
	private final AbstractManager getManager() {
		return this;
	}
	
    private final void setTodoTask(final int todoTask, final ArrayList<JFileSynchronizerTreeNode> nodes) {
    	for (JFileSynchronizerTreeNode node : nodes) {
			node.setTodoTask(todoTask);
		}
    }
	
	/* ------------    Manager Listener     ------------ */
	
	public void fireSynchronizeStarted() {
    	for (ManagerListener listener : listeners) {
			listener.synchronizeStarted(this);
		}
    }
	
	public void fireSynchronizeStopped() {
    	for (ManagerListener listener : listeners) {
			listener.synchronizeStopped(this);
		}
    }

    public void fireSynchronizeFinished() {
    	for (ManagerListener listener : listeners) {
			listener.synchronizeFinished(this);
		}
    	refreshUI();
    }

    public void fireProcessStarted() {
    	for (ManagerListener listener : listeners) {
			listener.processStarted(this);
		}
    }

    public void fireProcessStopped() {
    	for (ManagerListener listener : listeners) {
			listener.processStopped(this);
		}
    }
    
	public void fireProcessFinished() {
    	for (ManagerListener listener : listeners) {
			listener.processFinished(this);
		}
    	refreshUI();
    }
	
	/* ------------    Manager Listener     ------------ */
	
	public void fireShowPopup(final JPopupMenu popup) {
    	for (GuiManagerListener listener : guiManagerListeners) {
			listener.showPopup(this, getSelectedElements(), popup);
		}
    }
	
	public void fireElementsSelected() {
    	for (GuiManagerListener listener : guiManagerListeners) {
			listener.elementsSelected(this, getSelectedElements());
		}
	}
	
	/* ------------     Action Listener     ------------ */
	
	public final void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("PROCESS")) {
			getManager().processSynchronization(getManager().getSelectedElements());
		} else if (e.getActionCommand().equals("REFRESH")) {
			refreshUI();
		} else if (e.getActionCommand().startsWith("TASK_")) {
			setTodoTask(Integer.parseInt(e.getActionCommand().substring("TASK_".length())), getManager().getSelectedElements());
			refreshUI();
			fireElementsSelected();
		}
	}

    /* ------------ Tree Selection Listener ------------ */

    /**
     * Used when mouse pressed.
     * @param event Event.
     */
    @Override
    public void mousePressed(final MouseEvent event) {
        if (event.getClickCount() > 1) {
        	System.err.println("[DEBUG] Mouse pressed");
        }
    }

    /**
     * Used for popup management.
     * @param event Event.
     */
    @Override
    public void mouseReleased(final MouseEvent event) {
        maybeShowPopup(event);
    }
}
