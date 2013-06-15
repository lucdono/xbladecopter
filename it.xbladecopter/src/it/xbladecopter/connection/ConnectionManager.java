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

package it.xbladecopter.connection;

import it.xbladecopter.extensions.AbstractXBladeCommunication;
import it.xbladecopter.utils.LogManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ConnectionManager {
	public static final String EXTENSION_POINT = "it.xbladecopter.extensions.com";

	private LogManager logger = LogManager.getInstance();
	private AbstractXBladeCommunication activeComm;

	public AbstractXBladeCommunication getActiveComm() {
		return activeComm;
	}

	public void stop() {
		if (activeComm != null)
			activeComm.stop();
	}

	public AbstractXBladeCommunication updateConnection(String currentState) {
		stop();
		activeComm = null;

		IExtensionRegistry registry = Platform.getExtensionRegistry();

		IExtensionPoint extensionPoint = registry
				.getExtensionPoint(EXTENSION_POINT);
		IExtension extensions[] = extensionPoint.getExtensions();

		for (IExtension extension : extensions) {
			IConfigurationElement[] config = extension
					.getConfigurationElements();

			try {
				for (IConfigurationElement e : config) {

					if (e.getName().equals("comm")) {
						Object o = e.createExecutableExtension("class");
						if (o instanceof AbstractXBladeCommunication) {
							AbstractXBladeCommunication tempComm = (AbstractXBladeCommunication) o;

							String id = tempComm.getID();
							if (id != null && id.equals(currentState)) {
								activeComm = tempComm;
								break;
							}
						}

					}
				}
			} catch (CoreException e) {
				logger.logError(
						"Error while loading extensions from registry.", e,
						ConnectionManager.class);
				activeComm = null;
			}
		}

		if (activeComm == null)
			logger.logError("Unable to find extension for command "
					+ currentState + ".", ConnectionManager.class);

		return activeComm;
	}
}
