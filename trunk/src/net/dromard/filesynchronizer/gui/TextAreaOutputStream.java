package net.dromard.filesynchronizer.gui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

public class TextAreaOutputStream extends OutputStream {
    private JTextArea textControl;
    private boolean enabled = true;
    
    /**
     * Creates a new instance of TextAreaOutputStream which writes
     * to the specified instance of javax.swing.JTextArea control.
     * @param control A reference to the JTextArea control to which the output 
     *                must be redirected to.
     */
    public TextAreaOutputStream(JTextArea control) {
    	setControl(control);
    }
    
    public void setControl(JTextArea control) {
    	textControl = control;
    	enable(true);
	}

	/**
     * Writes the specified byte as a character to the javax.swing.JTextArea.
     * @param b The byte to be written as character to the JTextArea.
     */
    public void write( int b ) throws IOException {
    	if (enabled) {
	        // append the data as characters to the JTextArea control
	        textControl.append( String.valueOf( ( char )b ) );
	        textControl.setCaretPosition(textControl.getText().length() - 1);
    	}
    }
    
	public void enable(boolean enable) {
    	this.enabled = enable;
    	if (enable) {
    		PrintStream printer = new PrintStream(this);
    		System.setOut(printer);
    	} else {
	    	textControl.setText("");
	    	System.setOut(System.out);
    	}
	}
}
