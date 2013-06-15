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

public class TelemetryData {
	private float roll;
	private float pitch;
	private float heading;
	private float accX;
	private float accY;
	private float accZ;
	private float gyroX;
	private float gyroY;
	private float gyroZ;
	private float magX;
	private float magY;
	private float magZ;
	private float m0;
	private float m1;
	private float m2;
	private float m3;
	private int battery;
	private float lng;
	private float lat;

	public float getLng() {
		return lng;
	}

	public void setLng(float lng) {
		this.lng = lng;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public int getBattery() {
		return battery;
	}

	public void setBattery(int battery) {
		this.battery = battery;
	}

	public float getM0() {
		return m0;
	}

	public void setM0(float gas) {
		this.m0 = gas;
	}

	public float getM1() {
		return m1;
	}

	public void setM1(float gas) {
		this.m1 = gas;
	}

	public float getM2() {
		return m2;
	}

	public void setM2(float gas) {
		this.m2 = gas;
	}

	public float getM3() {
		return m3;
	}

	public void setM3(float gas) {
		this.m3 = gas;
	}

	public void setGyroX(float gyroX) {
		this.gyroX = gyroX;
	}

	public void setGyroY(float gyroY) {
		this.gyroY = gyroY;
	}

	public void setGyroZ(float gyroZ) {
		this.gyroZ = gyroZ;
	}

	public void setMagX(float magX) {
		this.magX = magX;
	}

	public void setMagY(float magY) {
		this.magY = magY;
	}

	public void setMagZ(float magZ) {
		this.magZ = magZ;
	}

	public float getAccX() {
		return accX;
	}

	public float getGyroX() {
		return gyroX;
	}

	public float getGyroY() {
		return gyroY;
	}

	public float getGyroZ() {
		return gyroZ;
	}

	public float getMagX() {
		return magX;
	}

	public float getMagY() {
		return magY;
	}

	public float getMagZ() {
		return magZ;
	}

	public float getAccY() {
		return accY;
	}

	public float getAccZ() {
		return accZ;
	}

	public float getRoll() {
		return roll;
	}

	public float getPitch() {
		return pitch;
	}

	public float getHeading() {
		return heading;
	}

	public TelemetryData() {
		super();
		this.roll = 0;
		this.pitch = 0;
		this.heading = 0;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setHeading(float heading) {
		this.heading = heading;
	}

	public void setAccX(float accX) {
		this.accX = accX;
	}

	public void setAccY(float accY) {
		this.accY = accY;
	}

	public void setAccZ(float accZ) {
		this.accZ = accZ;
	}
}
