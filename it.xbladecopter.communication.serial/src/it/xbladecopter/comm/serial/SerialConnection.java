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

package it.xbladecopter.comm.serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import it.xbladecopter.utils.LogManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

public class SerialConnection {
	private LogManager logger = LogManager.getInstance();

	public static String[] portsArray;
	private static SerialConnection instance = null;
	private int port;
	private String baud;
	private int parity;
	private int length;
	private int stop;
	private SerialPort serialPort = null;
	private boolean parameterSet = false;

	public void setParameters(int port, String string, int parity, int length,
			int stop) {
		this.port = port;
		this.baud = string;
		this.parity = parity;
		this.length = length;
		this.stop = stop;

		parameterSet = true;
	}

	@SuppressWarnings("rawtypes")
	public String[] getPorts() {

		HashSet<CommPortIdentifier> ports = new HashSet<CommPortIdentifier>();

		Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();

		while (thePorts.hasMoreElements()) {
			CommPortIdentifier com = (CommPortIdentifier) thePorts
					.nextElement();

			switch (com.getPortType()) {
			case CommPortIdentifier.PORT_SERIAL:
				try {
					CommPort thePort = com.open("CommUtil", 50);
					thePort.close();
					ports.add(com);
				} catch (PortInUseException e) {
					logger.logError("Port, " + com.getName() + ", is in use.",
							SerialConnection.class);
				} catch (Exception e) {
					logger.logError("Failed to open port " + com.getName(),
							SerialConnection.class);
				} catch (UnsatisfiedLinkError e) {
					logger.logError("Port, " + com.getName() + ", is in use.",
							SerialConnection.class);
				}

			}
		}

		ArrayList<String> p = new ArrayList<String>();
		Iterator it = ports.iterator();

		while (it.hasNext())
			p.add(((CommPortIdentifier) it.next()).getName());

		portsArray = (String[]) p.toArray(new String[p.size()]);

		return portsArray;
	}

	private SerialConnection() {
	}

	public static SerialConnection getInstance() {
		synchronized (SerialConnection.class) {
			if (instance == null) {
				instance = new SerialConnection();
			}
			return instance;
		}
	}

	public boolean Connect() {

		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier
					.getPortIdentifier(portsArray[port]);
			if (portIdentifier.isCurrentlyOwned()) {
				logger.logError("Port is currently in use ",
						SerialConnection.class);
				return false;
			} else {
				CommPort commPort = portIdentifier.open(this.getClass()
						.getName(), 2000);

				if (commPort instanceof SerialPort) {
					serialPort = (SerialPort) commPort;

					switch (parity) {
					case 0:
						parity = SerialPort.PARITY_NONE;
						break;
					case 1:
						parity = SerialPort.PARITY_ODD;
						break;
					case 2:
						parity = SerialPort.PARITY_EVEN;
						break;
					}

					switch (length) {
					case 0:
						length = SerialPort.DATABITS_8;
						break;
					case 1:
						length = SerialPort.DATABITS_7;
						break;
					case 2:
						length = SerialPort.DATABITS_6;
						break;
					case 3:
						length = SerialPort.DATABITS_5;
						break;
					}

					switch (stop) {
					case 0:
						stop = SerialPort.STOPBITS_1;
						break;
					case 1:
						stop = SerialPort.STOPBITS_2;
						break;
					}

					serialPort.setSerialPortParams(Integer.parseInt(baud),
							length, stop, parity);

					logger.logInfo("Serial port successfully connected",
							SerialConnection.class);
				}
			}
		} catch (NoSuchPortException e) {
			logger.logError("Error while connecting " + e.getMessage(),
					SerialConnection.class);
			return false;
		} catch (PortInUseException e) {
			logger.logError("Error while connecting " + e.getMessage(),
					SerialConnection.class);
			return false;
		} catch (UnsupportedCommOperationException e) {
			logger.logError("Error while connecting " + e.getMessage(),
					SerialConnection.class);
			return false;
		} catch (UnsatisfiedLinkError e) {
			logger.logError("Error while connecting " + e.getMessage(),
					SerialConnection.class);
			return false;
		}
		return true;
	}

	public void Disconnect() {

		try {
			serialPort.getOutputStream().close();
			serialPort.getInputStream().close();
		} catch (IOException e) {
		}

		if (serialPort != null)
			serialPort.close();

		logger.logInfo("Serial port successfully disconnected",
				SerialConnection.class);
	}

	public boolean isParameterSet() {
		return parameterSet;
	}

	public synchronized SerialPort getSerialPort() {
		return serialPort;
	}
}