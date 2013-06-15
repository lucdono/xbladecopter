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

package it.xbladecopter.utils;

public class Transform2D {

	public static double rotateX(double x, double y, int angle) {
		return x * Math.cos(angle * Math.PI / 180.0) - y
				* Math.sin(angle * Math.PI / 180.0);
	}

	public static double rotateY(double x, double y, int angle) {
		return x * Math.sin(angle * Math.PI / 180.0) + y
				* Math.cos(angle * Math.PI / 180.0);
	}

	public static int translateX(double x, double x0, double x1, double w) {
		return (int) (w - (x - x0) / (x1 - x0) * w);
	}

	public static int translateY(double y, double y0, double y1, double w) {
		return (int) (w - (y - y0) / (y1 - y0) * w);
	}

}
