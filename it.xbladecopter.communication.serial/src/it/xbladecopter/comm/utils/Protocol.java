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

package it.xbladecopter.comm.utils;

import it.xbladecopter.extensions.TelemetryData;

public class Protocol {

	private byte[] data;
	private int innerState;
	private int syncIndex;
	private int dataSize;
	private int count = 0;
	private boolean dataReady;
	private static final int STATE_CRC = 4;
	private static final int STATE_DATA = 3;
	private static final int STATE_SIZE = 2;
	private static final int STATE_SYNC_FIND = 0;
	private static final int STATE_SYNC_NEXT = 1;
	public static final byte SYNC_PATTERN[] = { 'S', 'Y', 'N', 'C' };

	private void reset() {
		this.innerState = STATE_SYNC_FIND;
		this.syncIndex = 0;
		this.dataSize = 0;
		this.dataReady = false;
		this.data = null;
		this.count = 0;

	}

	public Protocol() {
		reset();
	}

	public boolean isDataReady() {
		return dataReady;
	}

	public void byteReceived(byte b) {
		updateStateMachine(b);
	}

	private void updateStateMachine(byte b) {
		switch (innerState) {
		case STATE_SYNC_FIND: {
			if (syncIndex == 0 && !dataReady) {
				if (SYNC_PATTERN[syncIndex] == b) {
					innerState = STATE_SYNC_NEXT;
					syncIndex++;
					break;
				}
			}
			reset();
		}
			break;
		case STATE_SYNC_NEXT: {
			if (!dataReady) {
				if (syncIndex > 0 && syncIndex < SYNC_PATTERN.length) {
					if (SYNC_PATTERN[syncIndex] == b) {
						syncIndex++;
						if (syncIndex == SYNC_PATTERN.length)
							innerState = STATE_SIZE;
						else
							innerState = STATE_SYNC_NEXT;
						break;
					}
				}
			}
			reset();
		}
			break;
		case STATE_SIZE: {
			if (!dataReady) {
				syncIndex = 0;
				dataSize = b;
				innerState = STATE_DATA;
				if (dataSize > 0)
					data = new byte[dataSize];
				break;
			}
			reset();
		}
			break;
		case STATE_DATA: {
			if (!dataReady) {
				if (count < dataSize) {
					data[count++] = b;
					if (count == dataSize)
						innerState = STATE_CRC;
					else
						innerState = STATE_DATA;
					break;
				}
			}
			reset();
		}
			break;
		case STATE_CRC: {
			if (!dataReady) {
				if (CRC8.get(data) == b) {
					dataReady = true;
					break;
				} else
					System.out.println("Wrong data received!");
			}
			reset();
		}
			break;
		default: {
			reset();
		}
			break;

		}
	}

	public void getData(TelemetryData tel) {
		if (dataReady) {

			/*
			 * TODO IMPLEMENT: Maps add GPS info in protocol
			 */

			tel.setAccX(BytesDecode.bytesToFloat(data, 0, 4, true));
			tel.setAccY(BytesDecode.bytesToFloat(data, 4, 4, true));
			tel.setAccZ(BytesDecode.bytesToFloat(data, 8, 4, true));

			tel.setGyroX(BytesDecode.bytesToFloat(data, 12, 4, true));
			tel.setGyroY(BytesDecode.bytesToFloat(data, 16, 4, true));
			tel.setGyroZ(BytesDecode.bytesToFloat(data, 20, 4, true));

			tel.setMagX(BytesDecode.bytesToFloat(data, 24, 4, true));
			tel.setMagY(BytesDecode.bytesToFloat(data, 28, 4, true));
			tel.setMagZ(BytesDecode.bytesToFloat(data, 32, 4, true));

			tel.setRoll(-BytesDecode.bytesToFloat(data, 36, 4, true));
			tel.setPitch(BytesDecode.bytesToFloat(data, 40, 4, true));
			tel.setHeading(BytesDecode.bytesToFloat(data, 44, 4, true));

			tel.setM0(BytesDecode.bytesToFloat(data, 48, 4, true));
			tel.setM1(BytesDecode.bytesToFloat(data, 52, 4, true));
			tel.setM2(BytesDecode.bytesToFloat(data, 56, 4, true));
			tel.setM3(BytesDecode.bytesToFloat(data, 60, 4, true));

			reset();
		}
	}
}
