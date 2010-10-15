/**
 * FileNodeRenderer
 *
 * Created Sep 17, 2010-3:35:09 PM by Daniel McEnnis
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
package org.multihelp.file;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.JLabel;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * FileNodeRenderer
 * 
 * Controller for rendering FileNodes on the HelpViewer class.
 * 
 * @author Daniel McEnnis
 */
public class FileNodeRenderer extends DefaultTreeCellRenderer {

	Icon defaultLeaf = null;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor 
	 */
	public FileNodeRenderer() {
		super();
		defaultLeaf = this.getLeafIcon();
	}

	/**
	 * Get the component used to display the current row. Used for both directories and leaf nodes.
	 *
	 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree,
	 *      java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
//		this.setLeafIcon(defaultLeaf);
		try {
			Component ret = ((FileNode) value).render(tree, value, sel,
					expanded, leaf, row, hasFocus);
			if (ret != null) {
				return ret;
			}
			
			if (!leaf) {
//				System.out.println("Default rendering used ");
				return super.getTreeCellRendererComponent(tree,((FileNode)value).getText(),sel,expanded,leaf,row,hasFocus);
			} else {
				Icon icon = ((FileNode) value).getIcon();
				String text = ((FileNode) value).getText();
				JLabel label = new JLabel();
				
				if (icon != null) {
					label.setIcon(icon);
				}else{
					label.setIcon(super.getLeafIcon());
				}
				label.setText(text);
				ret = label;
				return ret;
			}
		} catch (ClassCastException e) {
			return super.getTreeCellRendererComponent(tree, value, sel, expanded,
							leaf, row, hasFocus);
		}
	}
}
