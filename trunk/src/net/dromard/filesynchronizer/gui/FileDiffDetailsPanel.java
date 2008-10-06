package net.dromard.filesynchronizer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import net.dromard.filesynchronizer.gui.tree.JFileSynchronizerTreeNode;
import net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode;

/**
 * This panel is design to display the details of differences between two files.
 * @author st22085
 */
public class FileDiffDetailsPanel extends JPanel implements GuiManagerListener {
	private JEditorPane details = new JEditorPane();
	
	public FileDiffDetailsPanel() {
		this.setLayout(new BorderLayout(10, 10));
		this.setOpaque(false);
		details.setBackground(Color.WHITE);
		details.setEditorKit(new HTMLEditorKit());
		this.add(new JScrollPane(details), BorderLayout.CENTER);
		clear();
	}

	private void clear() {
		details.setText("");
	}

	private void showDetails(JFileSynchronizerTreeNode node) {
		StringBuffer buff = new StringBuffer();
		buff.append("<HTML>");
		buff.append("<STYLE>");
		buff.append("body { font-family: verdana,helvetica,sans-serif; font-size: 10px; }");
		buff.append("table, tr, th, td { border: 1px solid #88888; }");
		buff.append("<STYLE>");
		buff.append("<BODY>");
			buff.append("<H3>");
				buff.append("Files differences details");
			buff.append("</H3>");
			if (node.getTodoTask() == FileSynchronizerTodoTaskTreeNode.TODO_ERROR) {
				buff.append("<FONT color='darkred'>");
					buff.append("<B>");
						buff.append("An error occured during synchrionization." + node.getErrorMessage());
					buff.append("</B>");
				buff.append("</FONT>");
			} else if (node.getTodoTask() == FileSynchronizerTodoTaskTreeNode.TODO_NOTHING) {
				buff.append("<FONT color='darkgreen'>");
					buff.append("<B>");
						buff.append("Files are equals. Nothing will be done.");
					buff.append("</B>");
				buff.append("</FONT>");
			} else if (node.getTodoTask() == FileSynchronizerTodoTaskTreeNode.TODO_RESET) {
				buff.append("<FONT color='darkblue'>");
					buff.append("<B>");
						buff.append("Files status has been reseted it will be recalculated");
					buff.append("</B>");
				buff.append("</FONT>");
			} else if (node.getTodoTask() == FileSynchronizerTodoTaskTreeNode.TODO_CREATE_SOURCE) {
				buff.append("<FONT color='darkblue'>");
					buff.append("<B>");
						buff.append("Source file does not exist and will be copied from destination.");
					buff.append("</B>");
				buff.append("</FONT>");
			} else if (node.getTodoTask() == FileSynchronizerTodoTaskTreeNode.TODO_CREATE_DESTINATION) {
				buff.append("<FONT color='darkblue'>");
					buff.append("<B>");
						buff.append("Destination file does not exist and will be copied from source.");
					buff.append("</B>");
				buff.append("</FONT>");
			} else if (node.getTodoTask() == FileSynchronizerTodoTaskTreeNode.TODO_DELETE_SOURCE) {
				buff.append("<FONT color='darkblue'>");
					buff.append("<B>");
						buff.append("Source file will be deleted.");
					buff.append("</B>");
				buff.append("</FONT>");
			} else if (node.getTodoTask() == FileSynchronizerTodoTaskTreeNode.TODO_DELETE_DESTINATION) {
				buff.append("<FONT color='darkblue'>");
					buff.append("<B>");
						buff.append("Destination file will be deleted.");
					buff.append("</B>");
				buff.append("</FONT>");
			} else if (node.getTodoTask() == FileSynchronizerTodoTaskTreeNode.TODO_UPDATE_DESTINATION) {
				buff.append("<FONT color='darkblue'>");
					buff.append("<B>");
						buff.append("Destination file will be overriden by the source file.");
					buff.append("</B>");
				buff.append("</FONT>");
			} else if (node.getTodoTask() == FileSynchronizerTodoTaskTreeNode.TODO_UPDATE_SOURCE) {
				buff.append("<FONT color='darkblue'>");
					buff.append("<B>");
						buff.append("Source file will be overriden by the destination file.");
					buff.append("</B>");
				buff.append("</FONT>");
			}
			buff.append("<BR>");
			buff.append("<BR>");
			
			buff.append("<BR>");
			buff.append("<FONT color='darkblue'><B><U>");
			buff.append("Files details.");
			buff.append("</U></B></FONT>");
			buff.append("<BR>");
			appendFileDetails(buff, node.getSource(), node.getDestination());
		buff.append("</BODY>");
		buff.append("</HTML>");
		details.setText(buff.toString());
	}
	
	private void appendFileDetails(final StringBuffer buff, final File source, final File destination) {
		buff.append("<TABLE>");
			buff.append("<TR>");
				buff.append("<TH>");
				buff.append("</TH>");
				buff.append("<TH>");
				buff.append("Source");
				buff.append("</TH>");
				buff.append("<TH>");
				buff.append("Destination");
				buff.append("</TH>");
			buff.append("</TR>");
			buff.append("<TR>");
				buff.append("<TH align='left'>Path</TH>");
				buff.append("<TD>");
				if (source.exists()) buff.append(source.getAbsolutePath());
				buff.append("</TD>");
				buff.append("<TD>");
				if (destination.exists()) buff.append(destination.getAbsolutePath());
				buff.append("</TD>");
			buff.append("</TR>");
			buff.append("<TR>");
				buff.append("<TH align='left'>Length</TH>");
				buff.append("<TD>");
				if (source.exists()) buff.append((float)(source.length() / 1024) + " ko");
				buff.append("</TD>");
				buff.append("<TD>");
				if (destination.exists()) buff.append((float)(destination.length() / 1024) + " ko");
				buff.append("</TD>");
			buff.append("</TR>");
			buff.append("<TR>");
				buff.append("<TH align='left' nowrap>Modification Date</TH>");
				buff.append("<TD>");
				DateFormat timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
				DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);
				if (source.exists()) {
					Date date = new Date(source.lastModified());
					buff.append(dateFormat.format(date) + " " + timeFormat.format(date));
				}
				buff.append("</TD>");
				buff.append("<TD>");
				if (destination.exists()) { 
					Date date = new Date(destination.lastModified());
					buff.append(dateFormat.format(date) + " " + timeFormat.format(date));
				}
				buff.append("</TD>");
			buff.append("</TR>");
		buff.append("</TABLE>");
	}

	public Icon getFileDateStatus(JFileSynchronizerTreeNode node) {
		int todoTask = node.getTodoTask();
		if (node.getSource() != null && node.getDestination() != null && node.getSource().exists() && node.getDestination().exists()) {
			long diff = node.getSource().lastModified() - node.getDestination().lastModified();
			if (diff < 0) {
				todoTask = FileSynchronizerTodoTaskTreeNode.TODO_UPDATE_SOURCE;
			} else if (diff > 0) {
				todoTask = FileSynchronizerTodoTaskTreeNode.TODO_UPDATE_DESTINATION;
			} else if (diff == 0) {
				todoTask = FileSynchronizerTodoTaskTreeNode.TODO_NOTHING;
			}
		} else if ((node.getSource() == null || !node.getSource().exists()) && (node.getDestination() != null && node.getDestination().exists())) {
			todoTask = FileSynchronizerTodoTaskTreeNode.TODO_CREATE_SOURCE;
		} else if ((node.getSource() != null && node.getSource().exists()) && (node.getDestination() == null || !node.getDestination().exists())) {
			todoTask = FileSynchronizerTodoTaskTreeNode.TODO_CREATE_DESTINATION;
		}
		if (todoTask == node.getTodoTask()) {
			todoTask = FileSynchronizerTodoTaskTreeNode.TODO_NOTHING;
		}
		return IconManager.getIcon(null, todoTask, node);
	}
	
	/* --------------------- GuiManagerListener --------------------- */

	public void elementsSelected(AbstractManager initiator, ArrayList<JFileSynchronizerTreeNode> nodes) {
		if (nodes.size() == 1) {
			showDetails(nodes.get(0));
		} else {
			clear();
		}
	}

	public void showPopup(AbstractManager initiator, ArrayList<JFileSynchronizerTreeNode> nodes, JPopupMenu popup) {
		elementsSelected(initiator, nodes);
	}
}
