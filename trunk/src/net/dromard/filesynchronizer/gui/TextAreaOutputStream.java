package net.dromard.filesynchronizer.gui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

public class TextAreaOutputStream extends OutputStream {
    private JTextArea textControl;
    
    /**
     * Creates a new instance of TextAreaOutputStream which writes
     * to the specified instance of javax.swing.JTextArea control.
     * @param control A reference to the JTextArea control to which the output 
     *                must be redirected to.
     */
    public TextAreaOutputStream(JTextArea control) {
    	textControl = control;
		PrintStream printer = new PrintStream(this);
		System.setOut(printer);
    }
    
    /**
     * Writes the specified byte as a character to the javax.swing.JTextArea.
     * @param b The byte to be written as character to the JTextArea.
     */
    public void write( int b ) throws IOException {
        // append the data as characters to the JTextArea control
        textControl.append( String.valueOf( ( char )b ) );
        textControl.setCaretPosition(textControl.getText().length() - 1);
    }
    
    @Override
	public void close() throws IOException {
    	textControl.setText("");
    	System.setOut(System.out);
    	System.out.println("Logs has been closed");
		super.close();
	}
}
