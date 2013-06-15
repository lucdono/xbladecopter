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

package it.xbladecopter.preferences;

import org.eclipse.swt.graphics.RGB;

public class PreferenceConstants {

	/*
	 * Plotter View Preferences
	 */
	public static final int ITEM_PER_GROUP = 6;
	public static final String P_PLOTTER_GROUPS[] = { "Attitude",
			"Acceleration", "Angular Speed", "Magnetic field" };

	public static final String P_ATTITUDE[] = { "rollMinPref", "rollMaxPref",
			"pitchMinPref", "pitchMaxPref", "headingMinPref", "headingMaxPref",
			"accXMinPref", "accXMaxPref", "accYMinPref", "accYMaxPref",
			"accZMinPref", "accZMaxPref", "gyroXMinPref", "gyroXMaxPref",
			"gyroYMinPref", "gyroYMaxPref", "gyroZMinPref", "gyroZMaxPref",
			"magXMinPref", "magXMaxPref", "magYMinPref", "magYMaxPref",
			"magZMinPref", "magZMaxPref" };

	public static final float P_ATTITUDE_DEFAULT[] = { -90.0f, 90.0f, -90.0f,
			90.0f, 0.0f, 360.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -10.0f,
			10.0f, -10.0f, 10.0f, -10.0f, 10.0f, -150.0f, 150.0f, -150.0f,
			150.0f, -150.0f, 150.0f };

	public static final String P_COLORS_ID[] = { "rollColPref", "pitchColPref",
			"headingColPref", "accXColPref", "accYColPref", "accZColPref",
			"gyroXColPref", "gyroYColPref", "gyroZColPref", "magXColPref",
			"magYColPref", "magZColPref" };

	public static final String P_LABEL_PLOTTER[] = { "Roll Min:", "Roll Max:",
			"Pitch Min:", "Pitch Max:", "Heading", "Heading Max:",
			"Acc X Min:", "Acc X Max:", "Acc Y Min:", "Acc Y Max:",
			"Acc Z Min:", "Acc Z Max:", "Gyro X Min:", "Gyro X Max:",
			"Gyro Y Min:", "Gyro Y Max:", "Gyro Z Min:", "Gyro Z Max:",
			"Magn X Min:", "Magn X Max:", "Magn Y Min:", "Magn Y Max:",
			"Magn Z Min:", "Magn Z Max:" };

	public static final RGB[] P_COLORS_PLOTTER = { new RGB(0, 0, 255),
			new RGB(255, 0, 0), new RGB(0, 255, 255), new RGB(192, 192, 192),
			new RGB(0, 255, 0), new RGB(255, 0, 255), new RGB(255, 255, 0),
			new RGB(0, 0, 128), new RGB(0, 128, 128), new RGB(128, 128, 128),
			new RGB(0, 128, 0), new RGB(128, 0, 128) };

	/*
	 * Command View Preferences
	 */
	public static final String P_CMD_LABEL_ROLL = "Max Roll:";
	public static final String P_CMD_ROLL = "rollCmdPref";
	public static final int P_CMD_ROLL_DEFAULT = 50;
	public static final String P_CMD_LABEL_PITCH = "Max Pitch:";
	public static final String P_CMD_PITCH = "pitchCmdPref";
	public static final int P_CMD_PITCH_DEFAULT = 50;
}
