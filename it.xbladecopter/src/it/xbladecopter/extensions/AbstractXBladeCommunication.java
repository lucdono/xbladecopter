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

package it.xbladecopter.extensions;

import it.xbladecopter.connection.TelemetryDataSingleton;
import it.xbladecopter.utils.Utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractXBladeCommunication {
	/**
	 * The header of log data file.
	 */
	private static String header[] = new String[] { "Roll", "Pitch", "Heading",
			"AccX", "AccY", "AccZ", "GyroX", "GyroY", "GyroZ", "MagX", "MagY",
			"MagZ", "Mot0", "Mot1", "Mot2", "Mot3" };

	/**
	 * The log header enable flag.
	 */
	private boolean writeLogHeader = true;

	/**
	 * Handle error state
	 */
	public static final int STATUS_ERROR = -2;

	/**
	 * Handle disconnection state
	 */
	public static final int STATUS_DISCONNECT = -1;

	/**
	 * Ready to connect
	 */
	public static final int STATUS_READY = 0;

	/**
	 * Handle connection state
	 */
	public static final int STATUS_CONNECT = 1;

	/**
	 * Receiving data
	 */
	public static final int STATUS_RUNNING = 2;

	/**
	 * Default state machine thread delay
	 */
	public static final int DEFAULT_DELAY = 5;

	/**
	 * Internal status
	 */
	private int status;

	/**
	 * The executor service
	 */
	private ExecutorService executor;

	/**
	 * State machine thread delay
	 */
	private int delay;

	/**
	 * The telemetry data received from the communication channel
	 */
	private TelemetryData data;

	/**
	 * The state machine runnable
	 */
	private Runnable stateMachineThread = new Runnable() {

		/**
		 * Running flag of the state machine thred
		 */
		private boolean running = true;

		@Override
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			while (running) {
				switch (getStatus()) {
				case STATUS_READY:
					break;
				case STATUS_CONNECT:
					if (connectCopter())
						setStatus(STATUS_RUNNING);
					else
						setStatus(STATUS_DISCONNECT);
					break;
				case STATUS_DISCONNECT:
					disconnectCopter();
					running = false;
					break;
				case STATUS_RUNNING:
					if (readTelemetryData(data)) {
						TelemetryData tel = TelemetryDataSingleton
								.getInstance().getData();
						tel.setAccX(data.getAccX());
						tel.setAccY(data.getAccY());
						tel.setAccZ(data.getAccZ());
						tel.setBattery(data.getBattery());
						tel.setGyroX(data.getGyroX());
						tel.setGyroY(data.getGyroY());
						tel.setGyroZ(data.getGyroZ());
						tel.setMagX(data.getMagX());
						tel.setMagY(data.getMagY());
						tel.setMagZ(data.getMagZ());
						tel.setLat(data.getLat());
						tel.setLng(data.getLng());
						tel.setHeading(data.getHeading());
						tel.setPitch(data.getPitch());
						tel.setRoll(data.getRoll());
						tel.setM0(data.getM0());
						tel.setM1(data.getM1());
						tel.setM2(data.getM2());
						tel.setM3(data.getM3());

						logData();
					} else
						setStatus(STATUS_ERROR);
					break;
				case STATUS_ERROR:
					if (handleError())
						setStatus(STATUS_RUNNING);
					else
						setStatus(STATUS_DISCONNECT);
					break;
				}
				Utils.sleep(delay);
			}
		}
	};

	/**
	 * The constructor
	 */
	public AbstractXBladeCommunication() {
		this.executor = Executors.newSingleThreadExecutor();
		this.status = STATUS_READY;
		this.delay = DEFAULT_DELAY;
		this.data = new TelemetryData();
		executor.execute(stateMachineThread);
		executor.shutdown();
	}

	/**
	 * Set the internal status
	 * 
	 * @param status
	 *            The status to set
	 */
	private synchronized void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Start connection. The connection class will remain in STATUS_READY if
	 * connection has not been established.
	 */
	public synchronized void start() {
		this.status = STATUS_CONNECT;
	}

	/**
	 * Disconnect communication channel. Release resource and stop state machine
	 * thread.
	 */
	public synchronized void stop() {
		this.status = STATUS_DISCONNECT;
	}

	/**
	 * Get connection status.
	 * 
	 * @return The connection status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Get state machine thread delay.
	 * 
	 * @return The state machine thread delay.
	 */
	public int getDelay() {
		return delay;
	}

	private void logData() {
		TelemetryDataSingleton instance = TelemetryDataSingleton.getInstance();
		if (instance.getLogEnabled() && instance.getLogStream() != null) {
			OutputStreamWriter oStream = instance.getLogStream();
			try {
				oStream.write(buildLogString() + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String buildLogString() {
		String string = new String("");
		if (writeLogHeader) {
			for (int i = 0; i < header.length; i++)
				string = String.format("%s%-9s", string, header[i]);
			string = string.concat("\n");
			int max = string.length();
			for (int i = 0; i < max; i++)
				string = string.concat("-");
			writeLogHeader = false;
		} else {
			string = String
					.format("%8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f",
							data.getRoll(), data.getPitch(), data.getHeading(),
							data.getAccX(), data.getAccY(), data.getAccZ(),
							data.getGyroX(), data.getGyroY(), data.getGyroZ(),
							data.getMagX(), data.getMagY(), data.getMagZ(),
							data.getM0(), data.getM1(), data.getM2(),
							data.getM3());
		}
		return string;
	}

	/**
	 * Set the state machine thread delay.
	 * 
	 * @param delay
	 *            The delay to set.
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * Connection callback invoked when trying to connect.
	 * 
	 * @return true on success, false otherwise
	 */
	public abstract boolean connectCopter();

	/**
	 * Disconnection callback invoked when disconnecting.
	 */
	public abstract void disconnectCopter();

	/**
	 * Error handler callback.
	 * 
	 * @return true if connection shall remain in STATUS_RUNNING state, false if
	 *         it shall disconnect.
	 */
	public abstract boolean handleError();

	/**
	 * Send a control command over the communication channel. This function will
	 * be used in the internal command thread class of the Command view.
	 * 
	 * @param throttle
	 *            The throttle.
	 * @param pitch
	 *            The pitch angle.
	 * @param roll
	 *            The roll angle.
	 * @param yaw
	 *            The yaw angle.
	 * @return true on success, false otherwise.
	 */
	public abstract boolean writeCommand(float throttle, float pitch,
			float roll, float yaw);

	/**
	 * Read telemetry data callback. It reads data from communication channel.
	 * 
	 * @param data
	 *            The telemetry data read.
	 * @return true on success, false otherwise.
	 */
	public abstract boolean readTelemetryData(TelemetryData data);

	/**
	 * Return the command state value.
	 * 
	 * @return The command state value.
	 */
	public abstract String getID();

}
