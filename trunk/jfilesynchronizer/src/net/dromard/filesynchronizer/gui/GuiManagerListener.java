package net.dromard.filesynchronizer.gui;

import java.util.ArrayList;

import javax.swing.JPopupMenu;

import net.dromard.filesynchronizer.gui.tree.JFileSynchronizerTreeNode;

public interface GuiManagerListener {
	public void showPopup(final AbstractManager initiator, final ArrayList<JFileSynchronizerTreeNode> nodes, final JPopupMenu popup);
	public void elementsSelected(final AbstractManager initiator, final ArrayList<JFileSynchronizerTreeNode> nodes);
}
