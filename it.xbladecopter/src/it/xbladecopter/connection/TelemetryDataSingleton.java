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

import it.xbladecopter.extensions.TelemetryData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class TelemetryDataSingleton {

	private TelemetryData data = null;
	private String logFileName = null;
	private Boolean logEnabled = false;
	private OutputStreamWriter oStream;

	private static TelemetryDataSingleton instance = null;

	private TelemetryDataSingleton() {
		data = new TelemetryData();
	}

	public static TelemetryDataSingleton getInstance() {
		synchronized (TelemetryDataSingleton.class) {
			if (instance == null) {
				instance = new TelemetryDataSingleton();
			}
			return instance;
		}
	}

	public synchronized TelemetryData getData() {
		return data;
	}

	public synchronized void setData(TelemetryData data) {
		this.data = data;
	}

	public String getLogFileName() {
		return logFileName;
	}

	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}

	public Boolean getLogEnabled() {
		return logEnabled;
	}

	public void setLogEnabled(Boolean logEnabled) {
		if (logEnabled) {
			try {
				oStream = new OutputStreamWriter(new FileOutputStream(new File(
						logFileName)));
				this.logEnabled = true;
			} catch (Exception e) {
				this.logEnabled = false;
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						"XBlade Control Panel",
						"Error while creating log file.");
			}
		} else {
			if (oStream != null)
				try {
					oStream.close();
				} catch (IOException e) {
				}
			this.logEnabled = false;
			oStream = null;
		}
	}

	public OutputStreamWriter getLogStream() {
		return oStream;
	}

}
