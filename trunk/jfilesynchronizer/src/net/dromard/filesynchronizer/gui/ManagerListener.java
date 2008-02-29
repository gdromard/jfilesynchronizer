package net.dromard.filesynchronizer.gui;

public interface ManagerListener {
	public void synchronizeStarted(AbstractManager initiator);
	public void synchronizeFinished(AbstractManager initiator);
	public void processStarted(AbstractManager initiator);
	public void processFinished(AbstractManager initiator);
}
