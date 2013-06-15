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

package it.xbladecopter.views;

import it.xbladecopter.Activator;
import it.xbladecopter.utils.LogManager;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

public class Maps extends BaseViewPart {
	public static final String ID = "it.xbladecopter.maps";
	private LogManager logger = LogManager.getInstance();
	static Browser browser;

	public static void updateMap() {
		try {
			if (browser != null) {
				browser.evaluate("map.setCenter(new google.maps.LatLng("
						+ rpy.getLat() + "," + rpy.getLng() + "), 15)");
				browser.evaluate("marker.setPosition(map.getCenter());");
			}
		} catch (SWTException e) {
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		logger.logInfo("Starting Maps...", this.getClass());

		URL url = FileLocator.find(Platform.getBundle(Activator.PLUGIN_ID),
				new Path("html/map.html"), null);

		SashForm sash = new SashForm(parent, SWT.HORIZONTAL);

		try {
			browser = new Browser(sash, SWT.NONE);
			browser.addControlListener(new ControlListener() {

				public void controlResized(ControlEvent e) {
					browser.execute("document.getElementById('map_canvas').style.width= "
							+ (browser.getSize().x - 20) + ";");
					browser.execute("document.getElementById('map_canvas').style.height= "
							+ (browser.getSize().y - 20) + ";");
				}

				public void controlMoved(ControlEvent e) {
				}
			});
		} catch (SWTError e) {
			System.out.println("Could not instantiate Browser: "
					+ e.getMessage());
			return;
		}
		Composite c = new Composite(sash, SWT.BORDER);
		c.setLayout(new GridLayout(1, true));
		Button b = new Button(c, SWT.PUSH);
		final List list = new List(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.SINGLE);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		b.setText("Where Am I ?");
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				double lat = ((Double) browser
						.evaluate("return map.getCenter().lat();"))
						.doubleValue();
				double lng = ((Double) browser
						.evaluate("return map.getCenter().lng();"))
						.doubleValue();
				list.add(String.format("%3.4f", lat) + " : "
						+ String.format("%3.4f", lng));
			}
		});

		try {
			browser.setUrl(new File(FileLocator.toFileURL(url).toURI())
					.getAbsolutePath());
		} catch (IOException e1) {
		} catch (URISyntaxException e) {
		}
		sash.setWeights(new int[] { 4, 1 });
	}

	@Override
	public void setFocus() {
	}
}
