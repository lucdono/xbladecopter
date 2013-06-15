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
import it.xbladecopter.views.Plotter;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PlotterPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PlotterPreferencePage() {
		super(GRID);

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		setDescription("Plotter charts ranges and colors.");
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
	public void createFieldEditors() {

		Composite comp = new Composite(getFieldEditorParent(), SWT.NONE);
		comp.setLayout(new GridLayout(2, true));

		for (int l = 0, c = 0; l < PreferenceConstants.P_PLOTTER_GROUPS.length; l++) {
			Group group = new Group(comp, SWT.NONE);
			group.setLayout(new GridLayout(6, true));
			group.setText(PreferenceConstants.P_PLOTTER_GROUPS[l]);
			GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			layoutData.horizontalSpan = 2;
			group.setLayoutData(layoutData);

			for (int i = l * PreferenceConstants.ITEM_PER_GROUP; i < l
					* PreferenceConstants.ITEM_PER_GROUP
					+ PreferenceConstants.ITEM_PER_GROUP; i++) {

				FloatFieldEditor field = new FloatFieldEditor(
						PreferenceConstants.P_ATTITUDE[i],
						PreferenceConstants.P_LABEL_PLOTTER[i], group);
				field.setValidateStrategy(FloatFieldEditor.VALIDATE_ON_FOCUS_LOST);
				field.setEmptyStringAllowed(false);
				addField(field);

				if ((i + 1) % 2 == 0) {
					ColorFieldEditor colorField = new ColorFieldEditor(
							PreferenceConstants.P_COLORS_ID[c], "", group);
					addField(colorField);
					c++;
				}

			}
			GridLayout layout = (GridLayout) group.getLayout();
			layout.numColumns = 6;
		}
		GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 2;
	}

	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		if (result)
			Plotter.updateFromPreferences();
		return result;
	}

	@Override
	protected void performApply() {
		super.performApply();
		Plotter.updateFromPreferences();
	}
}