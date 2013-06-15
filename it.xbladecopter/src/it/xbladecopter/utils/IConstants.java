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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

public interface IConstants {
	public static FontData FONT_SANS_8 = new FontData("Sans", 8, SWT.NORMAL);
	public static FontData FONT_SANS_10_BOLD = new FontData("Sans", 10,
			SWT.BOLD);
}
