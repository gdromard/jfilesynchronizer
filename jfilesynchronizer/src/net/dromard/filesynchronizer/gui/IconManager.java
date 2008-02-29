package net.dromard.filesynchronizer.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.dromard.filesynchronizer.gui.icons.Icons;
import net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_TASKS_NAMES;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_ERROR;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_DELETE_DESTINATION;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_DELETE_SOURCE;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_CREATE_DESTINATION;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_CREATE_SOURCE;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_UPDATE_DESTINATION;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_UPDATE_SOURCE;
import static net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode.TODO_NOTHING;

public class IconManager {
	public static boolean synchronize = false;
	public static final String ICON_EXTENSION = ".gif";
	private static final Hashtable<String, ImageIcon> folders  = new Hashtable<String, ImageIcon>();
	private static final Hashtable<String, ImageIcon> leafs    = new Hashtable<String, ImageIcon>();
	private static final Hashtable<String, ImageIcon> overlays = new Hashtable<String, ImageIcon>();
	private static final String[] IMAGE_TYPES = new String[TODO_TASKS_NAMES.length];
	
	static {
		IMAGE_TYPES[TODO_ERROR] = "ERROR";
		IMAGE_TYPES[TODO_DELETE_SOURCE] = "DELETE_SRC";
		IMAGE_TYPES[TODO_DELETE_DESTINATION] = "DELETE_DST";
		IMAGE_TYPES[TODO_CREATE_SOURCE] = "CREATE_SRC";
		IMAGE_TYPES[TODO_CREATE_DESTINATION] = "CREATE_DST";
		IMAGE_TYPES[TODO_UPDATE_SOURCE] = "UPDATE_SRC";
		IMAGE_TYPES[TODO_UPDATE_DESTINATION] = "UPDATE_DST";
		IMAGE_TYPES[TODO_NOTHING] = "NONE";
		IMAGE_TYPES[TODO_TASKS_NAMES.length - 1] = "NONE";
	}
	
	public static String getImageType(int type) {
		if (type == -1) return IMAGE_TYPES[TODO_NOTHING];
		return IMAGE_TYPES[type];
	}
	
	public static Icon getIcon(Icon icon, int todoTask, FileSynchronizerTodoTaskTreeNode node) {
		String iconType = getImageType(todoTask);
		ImageIcon toReturn = null;
		boolean isLeaf = node.isLeaf() && node.getParent() != null;
		
		// Get image from cache
		if (isLeaf) {
			toReturn = leafs.get(iconType);
		} else {
			toReturn = folders.get(iconType);
		}
		
		if (icon != null) {
			if (toReturn == null) {
				ImageIcon overlay = getOverlayIcon(iconType);
				
				int width = icon.getIconWidth() + 4;
				int height = icon.getIconHeight() + 2;
				BufferedImage tmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				tmp.getGraphics().setColor(new Color(255, 255, 255, 0));
				tmp.getGraphics().fillRect(0, 0, width, height);
				icon.paintIcon(null, tmp.getGraphics(), 0, 0);
				tmp.getGraphics().drawImage(overlay.getImage(), width-overlay.getIconWidth(), height-overlay.getIconHeight(), overlay.getIconWidth(), overlay.getIconHeight(), overlay.getImageObserver());
				toReturn = new ImageIcon(tmp);
				if (isLeaf) {
					leafs.put(iconType, toReturn);
				} else {
					folders.put(iconType, toReturn);
				}
			}
		} else {
			toReturn = getOverlayIcon(iconType);
		}

		return toReturn;
	}
	
	private static ImageIcon getOverlayIcon(final String iconType) {
		ImageIcon overlay = overlays.get(iconType);
		if (overlay == null) {
			overlay = Icons.getIcon(iconType + ICON_EXTENSION);
			overlays.put(iconType, overlay);
		}
		return overlay;
	}
}
