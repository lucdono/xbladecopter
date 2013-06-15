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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		/*
		 * Initializing plotter view preferences
		 */
		for (int i = 0; i < PreferenceConstants.P_ATTITUDE.length; i++)
			store.setDefault(PreferenceConstants.P_ATTITUDE[i],
					PreferenceConstants.P_ATTITUDE_DEFAULT[i]);

		for (int i = 0; i < PreferenceConstants.P_COLORS_ID.length; i++)
			PreferenceConverter.setDefault(store,
					PreferenceConstants.P_COLORS_ID[i],
					PreferenceConstants.P_COLORS_PLOTTER[i]);

		/*
		 * Initializing command view reference
		 */
		store.setDefault(PreferenceConstants.P_CMD_PITCH,
				PreferenceConstants.P_CMD_PITCH_DEFAULT);
		store.setDefault(PreferenceConstants.P_CMD_ROLL,
				PreferenceConstants.P_CMD_ROLL_DEFAULT);
	}

}
