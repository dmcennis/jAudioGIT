package org.jaudio.gui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Copy object that is included for completeness only.
 * @author Daniel McEnnis
 *
 */
public class CopyAction extends AbstractAction {
	
	static final long serialVersionUID = 1;

	/**
	 * Constructor that sets the menu item text.
	 *
	 */
	public CopyAction(){
		super("Copy");
		setEnabled(false);
	}
	
	/**
	 * Performs the copy action.
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
