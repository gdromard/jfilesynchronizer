/*
 * Created on 14 avr. 2006
 * By Gabriel DROMARD
 */
package net.dromard.filesynchronizer.gui.icons;

import javax.swing.ImageIcon;

/**
 * This class is animage Loader
 * 
 * @author Gabriel Dromard
 */
public class Icons {
    public static ImageIcon getIcon(String resource) {
        return new ImageIcon(Icons.class.getResource(resource));
    }
}
