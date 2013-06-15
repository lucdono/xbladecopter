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

package it.xbladecopter.actions;

import it.xbladecopter.ICommandIds;
import it.xbladecopter.connection.TelemetryDataSingleton;
import it.xbladecopter.utils.LogManager;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;

public class SaveLogAction extends Action {
	private LogManager logger = LogManager.getInstance();

	public SaveLogAction(String text, IWorkbenchWindow window) {
		super(text);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_SAVE_TELEMETRY);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_SAVE_TELEMETRY);
	}

	public void run() {

		FileDialog dialog = new FileDialog(Display.getCurrent()
				.getActiveShell(), SWT.SAVE);
		dialog.setFilterNames(new String[] { "Text File (*.txt)",
				"All Files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.txt", "*.*" });
		dialog.setOverwrite(true);
		dialog.setFileName("XBlade_Log.txt");
		String fileName = dialog.open();

		if (fileName != null) {
			TelemetryDataSingleton.getInstance().setLogFileName(fileName);
			logger.logInfo("Log file set to: " + fileName, this.getClass());
		}
	}
}