package net.dromard.filesynchronizer.gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.dromard.common.swing.InfiniteProgressPanel;
import net.dromard.common.swing.SwingPropertiesHelper;

public final class ProgressBarHandler {
    private final MyInfinityPanel progress = new MyInfinityPanel();
    private JFrame application;
    private boolean running = false;
    private int size = 0;

    public ProgressBarHandler(final JFrame application) {
        this.application = application;
        progress.setPrimitiveWidth(application.getWidth() / 5);
        progress.setFont(SwingPropertiesHelper.asFont("Century Gothic-BOLD-15"));
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void progress(final String action) {
        if (!running) {
            running = true;
            progress.setText(action);
            application.setGlassPane(progress);
            SwingUtilities.updateComponentTreeUI(application);
            progress.start();
        } else {
            progress.setText(action);
        }
    }

    /*
     * public void progress(final String action, int pos) {
     * progress((pos/size100) + "% " + action); }
     */
    public void stop() {
        progress.interrupt();
        application.remove(progress);
        application.repaint();
        running = false;
    }

    public InfiniteProgressPanel getInfiniteProgressPanel() {
        return progress;
    }

    public void setInfo(String info) {
        progress.setInfo(info);
    }

    class MyInfinityPanel extends InfiniteProgressPanel {
        private static final long serialVersionUID = -5428623618234767415L;
        private String info = null;

        public void setInfo(String info) {
            this.info = info;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            if (info != null && info.length() > 0) {
                Font font = getFont().deriveFont(12f);
                g2d.setFont(font);
                FontRenderContext context = g2d.getFontRenderContext();
                TextLayout layout = new TextLayout(info, font, context);
                Rectangle2D bounds = layout.getBounds();
                g2d.setColor(getForeground());
                layout.draw(g2d, 0f, (float) (getHeight() - bounds.getHeight()));
            }
        }
    }
}
