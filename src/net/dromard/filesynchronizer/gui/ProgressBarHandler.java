package net.dromard.filesynchronizer.gui;

import javax.swing.JFrame;

import net.dromard.common.swing.SwingPropertiesHelper;
import net.dromard.common.widget.infinityprogress.InfiniteProgressGlassPane;

public final class ProgressBarHandler extends InfiniteProgressGlassPane {

    public ProgressBarHandler(final JFrame application) {
        super(application);
        progress.setWidth(100);
        progressLabel.setFont(SwingPropertiesHelper.asFont("Century Gothic-BOLD-15"));
        infoLabel.setFont(SwingPropertiesHelper.asFont("Century Gothic-BOLD-12"));
    }

    public void progress(final String action) {
        if (!progress.isRunning()) {
            setText(action);
            start();
        } else {
            setText(action);
        }
    }
}
