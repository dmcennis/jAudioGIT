/**
 * SoloLaunch
 *
 * Created Sep 15, 2010-12:54:34 PM by Daniel McEnnis
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

/**
 * SoloLaunch
 *
 * Creates MultiHelp without an embedded application (primarily for demonstration/testing purposes
 *
 * @author Daniel McEnnis
 */
public class SoloLaunch {

	/**
	 * Main - launch with HelpWindow
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		HelpWindow help = new HelpWindow((new javax.swing.ImageIcon("MultiHelp16.png")).getImage(),java.util.Locale.getDefault());
		
	}

}
