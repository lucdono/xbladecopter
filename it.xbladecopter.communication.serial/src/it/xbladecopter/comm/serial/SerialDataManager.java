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

import it.xbladecopter.comm.utils.BytesDecode;
import it.xbladecopter.comm.utils.CRC8;
import it.xbladecopter.comm.utils.Protocol;
import it.xbladecopter.extensions.AbstractXBladeCommunication;
import it.xbladecopter.extensions.TelemetryData;
import it.xbladecopter.utils.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class SerialDataManager extends AbstractXBladeCommunication {
	private LogManager logger = LogManager.getInstance();
	public static final String COMMAND_VALUE = "it.xbladecopter.telemetry.serial";

	private Protocol p = new Protocol();

	private SerialConnection instance;
	private InputStream iStream;
	private OutputStream oStream;

	private static int WORD_SIZE = 4;
	private static int COMMAND_SIZE = WORD_SIZE * 2 + 1;

	private boolean readRadioResponse = false;

	public SerialDataManager() {
		this.instance = SerialConnection.getInstance();
	}

	@Override
	public boolean connectCopter() {
		boolean status = false;

		try {
			if (instance.isParameterSet() && instance.Connect()) {
				iStream = instance.getSerialPort().getInputStream();
				oStream = instance.getSerialPort().getOutputStream();

				if (instance.isRadio()) {
					readRadioResponse = true;
					try {
						Thread t = new Thread(new Runnable() {

							@Override
							public void run() {

								while (readRadioResponse) {
									try {
										while (iStream.available() > 0) {

											final byte[] bytes = new byte[iStream
													.available()];
											iStream.read(bytes);

											logger.logDebug(
													"ACOM Reply "
															+ BytesDecode
																	.getHex(bytes),
													SerialDataManager.class);
										}
									} catch (IOException e) {
										logger.logError(
												"Unable to read response from radio modem.",
												SerialDataManager.class);
									}
								}
							}
						});
						t.start();
						initRadio();

					} catch (InterruptedException e) {
						logger.logError("Unable to initialize radio modem.",
								SerialDataManager.class);
					}

					readRadioResponse = false;
				}
				status = true;
			} else {
				logger.logError("Serial parameters not yet set.",
						SerialDataManager.class);
				status = false;
			}
		} catch (IOException e) {
			logger.logError("Unable to open serial port.",
					SerialDataManager.class);
			status = false;
		}

		return status;
	}

	@Override
	public void disconnectCopter() {
		iStream = null;
		oStream = null;
		SerialConnection.getInstance().Disconnect();
	}

	@Override
	public boolean handleError() {
		logger.logError("Unable to read data from serial!",
				SerialDataManager.class);
		return true;
	}

	@Override
	public boolean writeCommand(float throttle, float pitch, float roll,
			float yaw) {
		byte crc[] = new byte[COMMAND_SIZE];
		byte throttleBuffer[] = new byte[1];
		byte rollBuffer[] = new byte[WORD_SIZE];
		byte pitchBuffer[] = new byte[WORD_SIZE];
		byte buffer[] = new byte[Protocol.SYNC_PATTERN.length + 1 + 1
				+ COMMAND_SIZE];
		boolean status = true;

		throttleBuffer[0] = (byte) Math.round(throttle);

		int counter = 0;
		int msgPtr = 0;

		System.arraycopy(Protocol.SYNC_PATTERN, 0, buffer, msgPtr,
				Protocol.SYNC_PATTERN.length);
		msgPtr += Protocol.SYNC_PATTERN.length;
		buffer[msgPtr++] = (byte) COMMAND_SIZE;

		System.arraycopy(throttleBuffer, 0, buffer, msgPtr, 1);
		msgPtr++;
		System.arraycopy(throttleBuffer, 0, crc, counter, 1);
		counter++;

		rollBuffer = BytesDecode.floatToBytes(roll, true);
		System.arraycopy(rollBuffer, 0, buffer, msgPtr, WORD_SIZE);
		msgPtr += WORD_SIZE;
		System.arraycopy(rollBuffer, 0, crc, counter, WORD_SIZE);
		counter += WORD_SIZE;

		pitchBuffer = BytesDecode.floatToBytes(pitch, true);
		System.arraycopy(pitchBuffer, 0, buffer, msgPtr, WORD_SIZE);
		msgPtr += WORD_SIZE;
		System.arraycopy(pitchBuffer, 0, crc, counter, WORD_SIZE);
		counter += WORD_SIZE;

		buffer[msgPtr] = CRC8.get(crc);

		try {
			oStream.write(buffer);
			oStream.flush();
			status = true;
		} catch (IOException e) {
			logger.logError("Unable to send command over serial", e,
					SerialDataManager.class);
			status = false;
		}

		return status;
	}

	@Override
	public boolean readTelemetryData(TelemetryData data) {
		boolean status = true;
		try {
			while (iStream.available() > 0) {

				final byte[] bytes = new byte[iStream.available()];
				iStream.read(bytes);

				for (int i = 0; i < bytes.length; i++) {
					p.byteReceived((byte) bytes[i]);
					if (p.isDataReady())
						p.getData(data);
				}
			}
		} catch (IOException e) {
			logger.logError(e.getMessage(), this.getClass());
			status = false;
		}
		return status;
	}

	@Override
	public String getID() {
		return COMMAND_VALUE;
	}

	public void initRadio() throws IOException, InterruptedException {

		byte ENTER_COMMAND_MODE[] = { 0x41, 0x54, 0x2B, 0x2B, 0x2B, 0x0D };
		byte EXIT_COMMAND_MODE[] = { (byte) 0xCC, 0x41, 0x54, 0x4F, 0x0D };
		byte SET_SERVER[] = { (byte) 0xCC, 0x03, 0x00 };
		byte DESTINATION_ADDRESS[] = { (byte) 0xCC, 0x10, 0x28, 0x72, 0x39 };

		oStream.write(ENTER_COMMAND_MODE);
		oStream.flush();
		TimeUnit.MILLISECONDS.sleep(100);

		oStream.write(SET_SERVER);
		oStream.flush();
		TimeUnit.MILLISECONDS.sleep(100);

		oStream.write(DESTINATION_ADDRESS);
		oStream.flush();
		TimeUnit.MILLISECONDS.sleep(100);

		oStream.write(EXIT_COMMAND_MODE);
		oStream.flush();
		TimeUnit.MILLISECONDS.sleep(100);

		TimeUnit.MILLISECONDS.sleep(500);
	}
}
