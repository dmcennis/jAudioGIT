/**
 * HelpWindow
 *
 * Created Sep 15, 2010-12:20:16 PM by Daniel McEnnis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.multihelp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/**
 * HelpWindow
 * 
 * Base window for providing a help system in a Java application or applet.  It utilizes the provided language
 * and provided icons to produce the correct subdirectory of the 'help' directory.
 * If no localization is present, it utilizes the base 'help' directory instead with the MultiHelp icon.
 * 
 * @author Daniel McEnnis
 */
public class HelpWindow extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FileTreeReader leftPane;

	private HelpViewer rightPane;
	
	/**
	 * Generic no argument constructor for creating a default help window
	 */
	public HelpWindow() {
		super();
		createWindow((new ImageIcon("icons/MultiHelp16.png")).getImage(),Locale.getDefault());
	}
	
	/**
	 * Creates a help window system with default localization (if any) and the given image for the
	 * window.  If no image is present, the default image is used.
	 * 
	 * @param image
	 */
	public HelpWindow(Image image){
		super();
		createWindow(image,Locale.getDefault());
	}
	
	/**
	 * Create a help window with the given locale, potentially different from the default.
	 * If the locale is not present, the contents of the 'help' directory are used directly.
	 * 
	 * 
	 * @param locale
	 */
	public HelpWindow(Locale locale){
		super();
		createWindow((new ImageIcon("icons/MultiHelp16.png")).getImage(),locale);
	}

	/**
	 * Creates a window with the given window.  If the locale is null, the default is used.  If the image
	 * is null, the MultiHelp icon is used.
	 * 
	 * @param image
	 * @param locale
	 */
	public HelpWindow(Image image, Locale locale){
		super();
		createWindow(image,locale);
	}
	
	/**
	 * Helper function containing code for creating a new Window with both image and locale using defaults
	 * if the given parameters are null.
	 * 
	 * 
	 * @param image
	 * @param locale
	 */
	protected void createWindow(Image image,Locale locale){
		if(locale == null){
			System.out.println("Null locale- 'help'");
			leftPane= new FileTreeReader(new File("help"));
		}else{
			File base = new File("help"+File.separator+locale);
			System.out.println("'"+base.getAbsolutePath()+"'");
			if(base.exists()){
				leftPane = new FileTreeReader(base);
			}else{
				System.out.println("base did not exists. using help");
				leftPane = new FileTreeReader(new File("help"));
			}
		}
		rightPane = new HelpViewer(leftPane);
		
		JScrollPane rightScroll = new JScrollPane();
		rightScroll.setPreferredSize(new Dimension(600, 400));
		rightScroll.getViewport().add(rightPane);
		JSplitPane core = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		Container root = getContentPane();
		root.add(core, BorderLayout.CENTER);
		JScrollPane leftScroll = new JScrollPane();
		leftScroll.setPreferredSize(new Dimension(200, 400));
		leftScroll.getViewport().add(leftPane);
		core.setLeftComponent(leftScroll);
		core.setRightComponent(rightScroll);
		pack();
		setVisible(true);
	}
	
}
