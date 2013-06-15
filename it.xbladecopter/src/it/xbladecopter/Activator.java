/*
 XBladeCopter 
 Copyright (C) 2013 RCP & Embedded Solutions - Luca D'Onofrio.

 This file is part of XBladeCopter Project

 XBladeCopter is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 3 of the License, or
 (at your option) any later version.

 XBladeCopter is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.xbladecopter;

import it.xbladecopter.connection.ConnectionManager;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/*
 * TODO WRITE CONTENT: javadoc, I18N, document plugin.xml
 */
/*
 * TODO WRITE CONTENT: help and welcome screen fragments
 */
/*
 * TODO IMPLEMENT: features and update site
 */
/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "it.xbladecopter"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private IOConsole myConsole;
	private IOConsole debugConsole;

	private ConnectionManager connectionManager;

	public IOConsole getMyConsole() {
		return myConsole;
	}

	public IOConsole getDebugConsole() {
		return debugConsole;
	}

	public void setDebugConsole(IOConsole debugConsole) {
		this.debugConsole = debugConsole;
	}

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		myConsole = new IOConsole("System Console", null);
		debugConsole = new IOConsole("Debug Console", null);
		ConsolePlugin.getDefault().getConsoleManager()
				.addConsoles(new IConsole[] { myConsole, debugConsole });
		connectionManager = new ConnectionManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		connectionManager.stop();

		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

}
