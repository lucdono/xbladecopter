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

import it.xbladecopter.Activator;
import it.xbladecopter.ICommandIds;
import it.xbladecopter.connection.ConnectionManager;
import it.xbladecopter.extensions.AbstractXBladeCommunication;
import it.xbladecopter.threads.GUIUpdater;
import it.xbladecopter.utils.LogManager;

import org.eclipse.core.commands.AbstractHandlerWithState;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.handlers.HandlerUtil;

public class TelemetryHandler extends AbstractHandlerWithState {
	private LogManager logger = LogManager.getInstance();

	@Override
	public void handleStateChange(State state, Object oldValue) {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		if (HandlerUtil.matchesRadioState(event))
			return null; // we are already in the updated state - do nothing

		String currentState = event.getParameter(ICommandIds.RADIO_STATE);
		ConnectionManager cM = Activator.getDefault().getConnectionManager();

		if (currentState.equals(ICommandIds.RADIO_STATE_DISABLE)) {
			logger.logInfo("Telemetry stopped.", this.getClass());

			cM.stop();

			if (GUIUpdater.getInstance().isRunning())
				GUIUpdater.getInstance().setRunning(false);
		} else {
			AbstractXBladeCommunication comm = cM
					.updateConnection(currentState);

			if (comm == null) {
				logger.logError("Error retrieving communication plugin.",
						this.getClass());

				if (GUIUpdater.getInstance().isRunning())
					GUIUpdater.getInstance().setRunning(false);

				HandlerUtil.updateRadioState(event.getCommand(),
						ICommandIds.RADIO_STATE_DISABLE);
			} else {
				comm.start();

				if (!GUIUpdater.getInstance().isRunning())
					GUIUpdater.getInstance().setRunning(true);

				logger.logInfo("Telemetry started", this.getClass());
			}
		}
		HandlerUtil.updateRadioState(event.getCommand(), currentState);

		return null;
	}
}
