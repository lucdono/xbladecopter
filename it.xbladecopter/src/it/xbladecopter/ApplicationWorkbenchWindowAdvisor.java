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

import it.xbladecopter.views.AHRS;
import it.xbladecopter.views.Flight;
import it.xbladecopter.views.Model3d;
import it.xbladecopter.views.Plotter;
import it.xbladecopter.views.Uptime;

import java.io.PrintStream;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.console.IOConsoleOutputStream;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public boolean preWindowShellClose() {
		if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(),
				"XBlade Control Panel", "Close application?"))
			return super.preWindowShellClose();
		else
			return false;
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1024, 800));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);

		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS,
				false);
	}

	@Override
	public void postWindowOpen() {
		super.postWindowOpen();

		ICommandService commandService = (ICommandService) PlatformUI
				.getWorkbench().getAdapter(ICommandService.class);

		Command resetCommand = commandService
				.getCommand("org.eclipse.ui.window.resetPerspective");
		resetCommand.addExecutionListener(new IExecutionListener() {

			@Override
			public void preExecute(String commandId, ExecutionEvent event) {

			}

			@Override
			public void postExecuteSuccess(String commandId, Object returnValue) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						updateGUI();
					}
				});
			}

			@Override
			public void postExecuteFailure(String commandId,
					ExecutionException exception) {
			}

			@Override
			public void notHandled(String commandId,
					NotHandledException exception) {

			}
		});

		IOConsoleOutputStream stream = Activator.getDefault().getMyConsole()
				.newOutputStream();

		PrintStream myS = new PrintStream(stream);
		System.setOut(myS);
		System.setErr(myS);

		updateGUI();
	}

	private void updateGUI() {
		AHRS.redraw();
		Model3d.updateModel();
		Plotter.updatePlotter();
		Flight.updateFlight();
		Uptime.updateUptime();
	}
}
