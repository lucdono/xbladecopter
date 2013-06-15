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

package it.xbladecopter.threads;

import it.xbladecopter.Activator;
import it.xbladecopter.connection.ConnectionManager;
import it.xbladecopter.extensions.AbstractXBladeCommunication;
import it.xbladecopter.extensions.TelemetryData;
import it.xbladecopter.utils.Utils;

public class CommandThread implements Runnable {

	boolean running = false;
	private TelemetryData userData = new TelemetryData();
	private boolean pause = false;

	public synchronized boolean isRunning() {
		return running;

	}

	public synchronized void setRunning(boolean running) {
		this.running = running;
	}

	public synchronized TelemetryData getData() {
		return userData;
	}

	public synchronized void setData(TelemetryData data) {
		this.userData = data;
	}

	@Override
	public void run() {
		while (isRunning()) {

			ConnectionManager cM = Activator.getDefault()
					.getConnectionManager();
			AbstractXBladeCommunication comm = cM.getActiveComm();

			if (comm != null
					&& comm.getStatus() == AbstractXBladeCommunication.STATUS_RUNNING) {
				float throttle = Math.max(
						Math.max(userData.getM0(), userData.getM2()),
						Math.max(userData.getM3(), userData.getM1()));

				if (!isPause())
					comm.writeCommand(throttle, userData.getPitch(),
							userData.getRoll(), 0.0f);
			}

			Utils.sleep(100);
		}

	}

	public synchronized boolean isPause() {
		return pause;
	}

	public synchronized void setPause(boolean pause) {
		this.pause = pause;
	}
}
