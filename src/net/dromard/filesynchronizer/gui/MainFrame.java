package net.dromard.filesynchronizer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import sun.management.snmp.jvminstr.JvmRuntimeMetaImpl;

import net.dromard.common.swing.InfiniteProgressPanel;
import net.dromard.filesynchronizer.gui.table.FileTableManager;
import net.dromard.filesynchronizer.gui.tree.FileTreeManager;
import net.dromard.filesynchronizer.modules.ModuleManager;


public class MainFrame extends JFrame implements ActionListener, ManagerListener {
	private static final long serialVersionUID = 317260848003010016L;
	private static final String MENU_ITEM_SELECT_SOURCE = "Select Source/Destination";
	private static final String MENU_ITEM_SYNCHRONIZE = "Synchronize";
	private static final String MENU_ITEM_PROCESS = "Process";
	private static final String MENU_ITEM_DISPLAY_LOGS = "Display logs";
	private static final String MENU_ITEM_QUIT = "Quit";
	private InfiniteProgressPanel progress;
	private AbstractManager tableManager;
	private AbstractManager treeManager;
	
	private File source = null;
	private File destination = null;
	private JTextArea control;
	private JSplitPane bottomSplitPanel;
	private TextAreaOutputStream textareaOutputStream;
	private JScrollPane scrollpane;


	public MainFrame() {
		System.setProperty("com.apple.macos.useScreenMenuBar", "true");
		UIManager.put("SplitPaneUI", net.dromard.common.swing.ui.MySplitPaneUI.class.getName());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		init();
		List<ManagerListener> listeners = ModuleManager.getInstance().registerModules();
		for (ManagerListener listener : listeners) {
			getTableManager().addListener(listener);
		}
	}
	
	private AbstractManager getTableManager() {
		return tableManager;
	}
	
	private AbstractManager getTreeManager() {
		return treeManager;
	}
	
	public void init() {
		this.setTitle("File Synchronizer");
		getContentPane().setLayout(new BorderLayout());
		getContentPane().setBackground(Color.WHITE);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setOpaque(false);
		// Table Manager
		JTable table = new JTable();
		tableManager = new FileTableManager(table);
		tableManager.addListener(this);
		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.getViewport().setOpaque(false);
		tableScrollPane.setOpaque(false);
		tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
		tableScrollPane.setOpaque(false);
		tabbedPane.addTab("Table View", tableScrollPane);
		// Tree Manager
		JTree tree = new JTree();
		treeManager = new FileTreeManager(tree);
		treeManager.addListener(this);
		JScrollPane treeScrollPane = new JScrollPane(tree);
		treeScrollPane.getViewport().setOpaque(false);
		treeScrollPane.setOpaque(false);
		treeScrollPane.setBorder(BorderFactory.createEmptyBorder());
		treeScrollPane.setOpaque(false);
		JSplitPane leftSplitPanel = new JSplitPane();
        leftSplitPanel.setLeftComponent(treeScrollPane);
        FileDiffDetailsPanel filesDetails = new FileDiffDetailsPanel();
		treeManager.addListener(filesDetails);
        leftSplitPanel.setRightComponent(filesDetails);
        leftSplitPanel.setOpaque(false);
        leftSplitPanel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        leftSplitPanel.setBorder(null);
        leftSplitPanel.setBackground(Color.WHITE);
        leftSplitPanel.setDividerLocation(240);
		tabbedPane.addTab("Tree View", leftSplitPanel);
		
		bottomSplitPanel = new JSplitPane();
		bottomSplitPanel.setTopComponent(tabbedPane);
        control = new JTextArea();
        scrollpane = new JScrollPane(control);
		bottomSplitPanel.setBottomComponent(scrollpane);
        bottomSplitPanel.setOpaque(false);
        bottomSplitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
        bottomSplitPanel.setBorder(null);
        bottomSplitPanel.setBackground(Color.WHITE);
        control.setVisible(false);
        scrollpane.setVisible(false);
        bottomSplitPanel.setDividerLocation(1d);
        getContentPane().add(bottomSplitPanel, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menuBar.add(menu);
		
		JMenuItem selectSourceMenuItem = new JMenuItem(MENU_ITEM_SELECT_SOURCE);
		selectSourceMenuItem.addActionListener(this);
		menu.add(selectSourceMenuItem);
		
		JMenuItem synchronizeMenuItem = new JMenuItem(MENU_ITEM_SYNCHRONIZE);
		synchronizeMenuItem.addActionListener(this);
		menu.add(synchronizeMenuItem);
		
		JMenuItem processMenuItem = new JMenuItem(MENU_ITEM_PROCESS);
		processMenuItem.addActionListener(this);
		menu.add(processMenuItem);
		
		menu.addSeparator();
		
		JRadioButtonMenuItem displayLogMenuItem = new JRadioButtonMenuItem(MENU_ITEM_DISPLAY_LOGS);
		displayLogMenuItem.addActionListener(this);
		menu.add(displayLogMenuItem);
		
		menu.addSeparator();
		
		JMenuItem quitMenuItem = new JMenuItem(MENU_ITEM_QUIT);
		quitMenuItem.addActionListener(this);
		menu.add(quitMenuItem);
		
		setJMenuBar(menuBar);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	private void enableLog() {
		scrollpane.setVisible(true);
        textareaOutputStream = new TextAreaOutputStream(control);
		this.bottomSplitPanel.setDividerLocation(0.75);
	}
	
	private void disableLog() throws IOException {
		if (textareaOutputStream != null) {
			textareaOutputStream.close();
			textareaOutputStream = null;
		}
		this.bottomSplitPanel.setDividerLocation(1d);
		scrollpane.setVisible(false);
	}

	private void selectSourceDestination() {
		File tmp = showChooserOpenDialog(this, "Select source ...", source);
		if (tmp != null) source = tmp;
		tmp = showChooserOpenDialog(this, "Select destination ...", destination);
		if (tmp != null) destination = tmp;
		synchronize();
	}

	private void synchronize() {
		if (source == null || destination == null) {
			JOptionPane.showMessageDialog(this, "You must select files (or folder) to synchronized !", "Ooops ...", JOptionPane.ERROR_MESSAGE);
		} else {
			getTableManager().synchronize(source, destination);
		}
	}
	
	private void process() {
		if (source == null || destination == null) {
			JOptionPane.showMessageDialog(this, "You must select files (or folder) to synchronized !", "Ooops ...", JOptionPane.ERROR_MESSAGE);
		} else {
			getTableManager().processSynchronization();
		}
	}
	
	/**
	 * @return the destination
	 */
	public File getDestination() {
		return destination;
	}
	
	/**
	 * @param destination the destination to set
	 */
	public void setDestination(File destination) {
		this.destination = destination;
	}
	
	/* ------------    Manager Listener     ------------ */

	/**
	 * @return the source
	 */
	public File getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(File source) {
		this.source = source;
	}

	public void synchronizeStarted(final AbstractManager initiator) {
		System.out.println("Synchronization started at " + new Date());
		startInfiniteProgress("View differences");
    }

	public void synchronizeStopped(final AbstractManager initiator) {
		System.out.println("Synchronization stopped at " + new Date());
		progress.setText("Stopping synchronization process");
    }

    public void synchronizeFinished(final AbstractManager initiator) {
		System.out.println("Synchronize Finished at " + new Date());
    	if (initiator == getTableManager()) {
    		getTreeManager().setModelToComponent(getTableManager().getAbstractModel().getRootNode());
    	}
    	endInfiniteProgress();
    }

    public void processStarted(final AbstractManager initiator) {
		System.out.println("Processing started at " + new Date());
		startInfiniteProgress("Applying changes");
    }
    
    public void processStopped(final AbstractManager initiator) {
    	System.out.println("Processing stopped at " + new Date());
		progress.setText("Stopping process");
    }

	public void processFinished(final AbstractManager initiator) {
		System.out.println("Processing finished at " + new Date());
    	endInfiniteProgress();
    }

	public void startInfiniteProgress(final String text) {
		//this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		progress = new InfiniteProgressPanel("");
		progress.setPrimitiveWidth(getWidth()/5);
		progress.setFont(Font.decode("Century Gothic-BOLD-15"));
		progress.setText(text);
		setGlassPane(progress);
		//SwingUtilities.updateComponentTreeUI(this);
		progress.start();
		progress.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int option = JOptionPane.showConfirmDialog(progress, "Do you want to stop process ?", "Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (option == JOptionPane.OK_OPTION) {
						// TODO stop process
						getTableManager().stopCurrentProcess();
					}
				}
			}
		});
	}
	
	public void endInfiniteProgress() {
		progress.interrupt();
		remove(progress);
		repaint();
		//this.setCursor(Cursor.getDefaultCursor());
	}
	
	/* ------------     Action Listener     ------------ */
	
	public final void actionPerformed(ActionEvent e) {
		try {
			if (e.getActionCommand().equals(MENU_ITEM_PROCESS)) {
				process();
			} else if (e.getActionCommand().equals(MENU_ITEM_SYNCHRONIZE)) {
				synchronize();
			} else if (e.getActionCommand().equals(MENU_ITEM_SELECT_SOURCE)) {
				selectSourceDestination();
			} else if (e.getActionCommand().equals(MENU_ITEM_DISPLAY_LOGS)) {
				if (((JRadioButtonMenuItem) e.getSource()).isSelected()) {
					enableLog();
				} else {
					disableLog();
				}
			} else if (e.getActionCommand().equals(MENU_ITEM_QUIT)) {
				System.exit(0);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/* ------------      Static methods     ------------ */
    
    public File showChooserOpenDialog(Component parent, String title, File currentDirectory) {
    	JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(currentDirectory);
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        
        // Retreive selected file
        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(parent)) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

	public static void main(final String[] args) {
		final MainFrame frame = new MainFrame();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.setSize(600, 600);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                if (args.length == 2) {
                	frame.setSource(new File(args[0]));
                	frame.setDestination(new File(args[1]));
                	frame.synchronize();
                }
            }
        });
	}
}