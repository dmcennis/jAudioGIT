package jAudioFeatureExtractor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import jAudioFeatureExtractor.OuterFrame;

/**
 * Displays author information about jAudio.
 * @author Daniel McEnnis
 *
 */
public class AboutAction extends AbstractAction {
	static final long serialVersionUID = 1;

	/**
	 * Basic constructor that supplies the menu item with a name.
	 *
	 */
	public AboutAction(){
		super("About...");
	}
	
	/**
	 * Pops up a message giving author information.
	 */
	public void actionPerformed(ActionEvent e) {
		String data = OuterFrame.resourceBundle.getString("created.by.daniel.mcennis.and.cory.mckay");
JOptionPane.showMessageDialog(null, data, OuterFrame.resourceBundle.getString("jaudio.feature.extractor"),
		JOptionPane.INFORMATION_MESSAGE, new javax.swing.ImageIcon("jAudioLogo3-128.jpg"));

	}

}
