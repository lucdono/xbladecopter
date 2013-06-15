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

import it.xbladecopter.Activator;
import it.xbladecopter.views.Command;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class CommandPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public CommandPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Change values of max allowed angles for roll and pitch.");
	}

	public void createFieldEditors() {

		FloatFieldEditor field = new FloatFieldEditor(
				PreferenceConstants.P_CMD_PITCH,
				PreferenceConstants.P_CMD_LABEL_PITCH, getFieldEditorParent());
		field.setValidateStrategy(FloatFieldEditor.VALIDATE_ON_FOCUS_LOST);
		field.setEmptyStringAllowed(false);
		addField(field);

		field = new FloatFieldEditor(PreferenceConstants.P_CMD_ROLL,
				PreferenceConstants.P_CMD_LABEL_ROLL, getFieldEditorParent());
		field.setValidateStrategy(FloatFieldEditor.VALIDATE_ON_FOCUS_LOST);
		field.setEmptyStringAllowed(false);
		addField(field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		if (result)
			Command.updateFromPreferences();
		return result;
	}

	@Override
	protected void performApply() {
		super.performApply();
		Command.updateFromPreferences();
	}
}