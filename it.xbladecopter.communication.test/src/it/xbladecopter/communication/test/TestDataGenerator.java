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

package it.xbladecopter.communication.test;

import it.xbladecopter.extensions.AbstractXBladeCommunication;
import it.xbladecopter.extensions.TelemetryData;

public class TestDataGenerator extends AbstractXBladeCommunication {

	public static final String COMMAND_VALUE = "it.xbladecopter.telemetry.test";
	
	private TelemetryData testData = new TelemetryData();
	private float heading = 0.0f;
	private float accX = 0.0f;
	private float accY = 0.0f;
	private float accZ = 0.0f;
	private float gyroX = 0.0f;
	private float gyroY = 0.0f;
	private float gyroZ = 0.0f;
	private float magX = 0.0f;
	private float magY = 0.0f;
	private float magZ = 0.0f;
	private float battery = 5.0f;
	private float lat = 43.68665f;
	private float lng = 10.484605f;
	int headingDir = 1;

	public TestDataGenerator() {
	}

	@Override
	public boolean connectCopter() {
		return true;
	}

	@Override
	public void disconnectCopter() {
	}

	@Override
	public boolean handleError() {
		return true;
	}

	@Override
	public boolean writeCommand(float throttle, float pitch, float roll,
			float yaw) {
		testData.setPitch(pitch);
		testData.setRoll(roll);

		testData.setM0(throttle);
		testData.setM1(throttle);
		testData.setM2(throttle);
		testData.setM3(throttle);
		return true;
	}

	@Override
	public boolean readTelemetryData(TelemetryData data) {

		if (heading <= 0.0f)
			headingDir = 1;
		if (heading >= 360.f)
			headingDir = -1;
		heading = heading + headingDir * 0.5f;

		accX = (float) Math.cos(heading * Math.PI / 180.0f);
		accY = (float) Math.cos(heading * Math.PI / 180.0f + Math.PI);
		accZ = (float) Math.cos(heading * Math.PI / 180.0f + 2 * Math.PI / 3);

		gyroX = (float) Math.cos(heading * Math.PI / 180.0f);
		gyroY = (float) Math.cos(heading * Math.PI / 180.0f + Math.PI);
		gyroZ = (float) Math.cos(heading * Math.PI / 180.0f + 2 * Math.PI / 3);

		magX = (float) Math.cos(heading * Math.PI / 180.0f);
		magY = (float) Math.cos(heading * Math.PI / 180.0f + Math.PI);
		magZ = (float) Math.cos(heading * Math.PI / 180.0f + 2 * Math.PI / 3);

		float latitude = (float) (lat + 0.001 * Math.sin(heading * Math.PI
				/ 180.0f));
		float logitude = (float) (lng + 0.001 * Math.cos(heading * Math.PI
				/ 180.0f));

		data.setLat(latitude);
		data.setLng(logitude);

		if (battery > 4.0f)
			battery -= 0.001f;
		else
			battery = 5.0f;

		data.setBattery((int) (8.0 - (5.0f - battery) * 10.0));

		data.setHeading(heading);
		data.setAccX(accX);
		data.setAccY(accY);
		data.setAccZ(accZ);
		data.setGyroX(gyroX);
		data.setGyroY(gyroY);
		data.setGyroZ(gyroZ);
		data.setMagX(magX);
		data.setMagY(magY);
		data.setMagZ(magZ);
		
		data.setPitch(testData.getPitch());
		data.setRoll(testData.getRoll());
		data.setM0(testData.getM0());
		data.setM1(testData.getM1());
		data.setM2(testData.getM2());
		data.setM3(testData.getM3());
		
		return true;
	}

	@Override
	public String getID() {
		return COMMAND_VALUE;
	}

}
