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

import it.xbladecopter.utils.Utils;
import it.xbladecopter.views.AHRS;
import it.xbladecopter.views.Flight;
import it.xbladecopter.views.Maps;
import it.xbladecopter.views.Model3d;
import it.xbladecopter.views.Plotter;
import it.xbladecopter.views.Uptime;

import org.eclipse.swt.widgets.Display;

public class GUIUpdater implements Runnable {

	private boolean running;
	private static GUIUpdater instance;
	private static Thread thread;

	private GUIUpdater() {

	}

	public static GUIUpdater getInstance() {
		if (instance == null) {
			instance = new GUIUpdater();
			return instance;
		} else
			return instance;
	}

	public synchronized boolean isRunning() {
		return running;
	}

	public synchronized void setRunning(boolean running) {
		this.running = running;

		if (running) {
			thread = new Thread(instance);
			thread.start();
		} else
			thread = null;
	}

	@Override
	public void run() {
		Uptime.start();

		while (isRunning()) {
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					Display.getDefault().readAndDispatch();
					AHRS.redraw();
				}
			});
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					Display.getDefault().readAndDispatch();
					Model3d.updateModel();
				}
			});
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					Display.getDefault().readAndDispatch();
					Flight.updateFlight();
				}
			});
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					Display.getDefault().readAndDispatch();
					Plotter.updatePlotter();
				}
			});
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					Display.getDefault().readAndDispatch();
					Uptime.updateUptime();
				}
			});
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					Display.getDefault().readAndDispatch();
					Maps.updateMap();
				}
			});
			Utils.sleep(5);
		}
	}

}
