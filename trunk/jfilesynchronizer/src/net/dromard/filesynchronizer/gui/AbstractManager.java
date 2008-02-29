package net.dromard.filesynchronizer.gui;

import static net.dromard.filesynchronizer.treenode.FileSynchronizationStatusTreeNode.SYNCHRONIZATION_DESTINATION_CHANGED;
import static net.dromard.filesynchronizer.treenode.FileSynchronizationStatusTreeNode.SYNCHRONIZATION_SOURCE_ADDED;
import static net.dromard.filesynchronizer.treenode.FileSynchronizationStatusTreeNode.SYNCHRONIZATION_SOURCE_CHANGED;
import static net.dromard.filesynchronizer.treenode.FileSynchronizationStatusTreeNode.SYNCHRONIZATION_SOURCE_DELETED;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_CREATE_DESTINATION;
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

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.dromard.filesynchronizer.FileSynchronizerVisitor;
import net.dromard.filesynchronizer.gui.icons.Icons;
import net.dromard.filesynchronizer.gui.tree.JFileSynchronizerTreeNode;

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
    	popup.add(addOverlayIconsToMenu());
        return popup;
    }
	
    private final int[] getPossibleTask() {
    	ArrayList<JFileSynchronizerTreeNode> nodes = getSelectedElements();
    	int[] possibleTask = new int[2];
    	possibleTask[0] = TODO_NOTHING;
    	possibleTask[1] = TODO_RESET;
    	
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
    		possibleTask = new int[4];
    		possibleTask[0] = TODO_UPDATE_SOURCE;
    		possibleTask[1] = TODO_UPDATE_DESTINATION;
    		possibleTask[2] = TODO_NOTHING;
    		possibleTask[3] = TODO_RESET;
        } else if (synchronizationStatus == SYNCHRONIZATION_SOURCE_CHANGED) {
    		possibleTask = new int[4];
    		possibleTask[0] = TODO_UPDATE_DESTINATION;
    		possibleTask[1] = TODO_UPDATE_SOURCE;
    		possibleTask[2] = TODO_NOTHING;
    		possibleTask[3] = TODO_RESET;
        } else if (synchronizationStatus == SYNCHRONIZATION_SOURCE_DELETED) {
    		possibleTask = new int[4];
    		possibleTask[0] = TODO_DELETE_SOURCE;
    		possibleTask[1] = TODO_DELETE_DESTINATION;
    		possibleTask[2] = TODO_NOTHING;
    		possibleTask[3] = TODO_RESET;
        } else if (synchronizationStatus == SYNCHRONIZATION_SOURCE_ADDED) {
    		possibleTask = new int[4];
    		possibleTask[0] = TODO_DELETE_SOURCE;
    		possibleTask[1] = TODO_CREATE_DESTINATION;
    		possibleTask[2] = TODO_NOTHING;
    		possibleTask[3] = TODO_RESET;
	    }
        return possibleTask;
    }
    
	protected final JMenu addOverlayIconsToMenu() {
		int[] possibleTask = getPossibleTask();
		if (possibleTask != null && possibleTask.length > 0) {
			JMenu menu = new JMenu();
			menu.setText("Override todo task to ...");
			for (int i = 0; i < possibleTask.length; ++i) {
				JMenuItem todoTask = new JMenuItem();
				todoTask.setActionCommand("TASK_" + possibleTask[i]);
				todoTask.setIcon(Icons.getIcon(IconManager.getImageType(possibleTask[i]) + IconManager.ICON_EXTENSION));
				todoTask.setText(TODO_TASKS_NAMES[possibleTask[i]]);
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
        if (event.isPopupTrigger()) {
        	JPopupMenu popup = createPopupMenu();
            popup.setInvoker(event.getComponent());
            Point invokerOrigin = event.getComponent().getLocationOnScreen();
            popup.setLocation(invokerOrigin.x + event.getX(), invokerOrigin.y + event.getY());
            fireShowPopup(popup);
            popup.setVisible(true);
        } else {
        	if (getSelectedElements().size() > 0) {
        		fireElementsSelected();
        	}
        }
    }

	protected void refreshUI() {
		SwingUtilities.updateComponentTreeUI(component);
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
		processor = new Thread(new Processor(node));
		processor.start();
	}
	
	public final void processSynchronization(final ArrayList<JFileSynchronizerTreeNode> nodes) {
		processor = new Thread(new Processor(nodes));
		processor.start();
	}

	class Processor implements Runnable {
		ArrayList<JFileSynchronizerTreeNode> nodes;
		
		public Processor(final JFileSynchronizerTreeNode node) {
			nodes = new ArrayList<JFileSynchronizerTreeNode>();
			nodes.add(node);
		}
		public Processor(final ArrayList<JFileSynchronizerTreeNode> nodes) {
			this.nodes = nodes;
		}

        public void run() {
    		try {
    			fireProcessStarted();
    			FileSynchronizerVisitor visitor = new FileSynchronizerVisitor();
    			for (JFileSynchronizerTreeNode node : nodes) {
    				visitor.visit(node);
				}
    			fireProcessFinished();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        }
	}
	
	class Synchronizer implements Runnable {
		private final File source;
		private final File destination;
		public Synchronizer(final File source, final File destination) {
			setModelToComponent(null);
			this.source = source;
			this.destination = destination;
		}

		public void run() {
			fireSynchronizeStarted();
			component.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			getAbstractModel().setRootNode(new JFileSynchronizerTreeNode(source, destination));
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
     * Used for popup management.
     * @param event Event.
     */
    @Override
    public final void mousePressed(final MouseEvent event) {
        maybeShowPopup(event);
    }

    /**
     * Used for popup management.
     * @param event Event.
     */
    @Override
    public final void mouseReleased(final MouseEvent event) {
        maybeShowPopup(event);
    }
}
