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

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class FloatFieldEditor extends StringFieldEditor {

	public FloatFieldEditor(String pRollMax, String pLabelRollMax,
			Composite composite) {
		super(pRollMax, pLabelRollMax, composite);
	}

	@Override
	protected boolean doCheckState() {
		try {
			Float.valueOf(getStringValue());
		} catch (NumberFormatException e) {
			setErrorMessage("Only float or integer values allowed.");
			return false;
		}
		return true;
	}
}
