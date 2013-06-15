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

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class Uptime extends BaseViewPart {
	private static LogManager logger = LogManager.getInstance();

	public static final String ID = "it.xbladecopter.uptime";
	private static DecimalFormat tflz = new DecimalFormat("00");
	private static Canvas canvas;
	private static Calendar myCal = null;
	private static Image clockHot;
	private static int battery = 8;

	private Image[] batteryIcons = new Image[8];

	public static void updateUptime() {
		if (canvas != null && !canvas.isDisposed() && canvas.isVisible())
			canvas.redraw();
	}

	public static void start() {
		logger.logInfo("Starting uptime clock...", Uptime.class);
		myCal = Calendar.getInstance();
	}

	@Override
	public void createPartControl(final Composite parent) {
		logger.logInfo("Starting uptime view...", Uptime.class);

		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayout(new FillLayout());
		GridData layout = new GridData(GridData.FILL);
		comp.setLayoutData(layout);

		try {
			URL url = FileLocator.find(Platform.getBundle(Activator.PLUGIN_ID),
					new Path("icons/time_hot.png"), null);
			clockHot = new Image(Display.getCurrent(), url.openStream());
		} catch (Exception e) {
			logger.logError("Malformed url Uptime view images", e, Uptime.class);
		}

		canvas = new Canvas(comp, SWT.DOUBLE_BUFFERED);
		final Color foregroundColor1 = new Color(Display.getCurrent(), 100,
				100, 100);
		final Font font = new Font(Display.getCurrent(),
				new FontData[] { new FontData("Arial", 30, SWT.NORMAL) });

		for (int i = 0; i < 8; i++) {
			URL url = null;
			try {
				switch (i) {
				case 0:
					url = FileLocator.find(Platform
							.getBundle(Activator.PLUGIN_ID), new Path(
							"icons/battery_000.png"), null);
					break;
				case 1:
					url = FileLocator.find(Platform
							.getBundle(Activator.PLUGIN_ID), new Path(
							"icons/battery_001.png"), null);
					break;
				case 2:
					url = FileLocator.find(Platform
							.getBundle(Activator.PLUGIN_ID), new Path(
							"icons/battery_002.png"), null);
					break;
				case 3:
					url = FileLocator.find(Platform
							.getBundle(Activator.PLUGIN_ID), new Path(
							"icons/battery_003.png"), null);
					break;
				case 4:
					url = FileLocator.find(Platform
							.getBundle(Activator.PLUGIN_ID), new Path(
							"icons/battery_004.png"), null);
					break;
				case 5:
					url = FileLocator.find(Platform
							.getBundle(Activator.PLUGIN_ID), new Path(
							"icons/battery_005.png"), null);
					break;
				case 6:
					url = FileLocator.find(Platform
							.getBundle(Activator.PLUGIN_ID), new Path(
							"icons/battery_006.png"), null);
					break;
				case 7:
					url = FileLocator.find(Platform
							.getBundle(Activator.PLUGIN_ID), new Path(
							"icons/battery_007.png"), null);
					break;
				case 8:
					url = FileLocator.find(Platform
							.getBundle(Activator.PLUGIN_ID), new Path(
							"icons/battery_008.png"), null);
					break;
				}
				batteryIcons[i] = new Image(Display.getCurrent(),
						url.openStream());

			} catch (IOException e1) {
				logger.logError("Malformed url Uptime view images", e1,
						Uptime.class);
			}

		}
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(clockHot, 20, 50);

				e.gc.setForeground(foregroundColor1);
				e.gc.setFont(font);

				Calendar now = Calendar.getInstance();

				long mins = 0;
				long secs = 0;

				if (myCal != null) {
					long diff = now.getTimeInMillis() - myCal.getTimeInMillis();
					secs = diff / 1000;
					mins = secs / 60;
				}

				StringBuffer sb = new StringBuffer();
				sb.append(tflz.format(mins));
				sb.append(':');
				sb.append(tflz.format(secs % 60));

				e.gc.drawText(sb.toString(), 35, 0);

				if (battery < 8 && battery >= 0)
					e.gc.drawImage(batteryIcons[battery], 20, 175);
				else
					e.gc.drawImage(batteryIcons[0], 20, 175);

				battery = rpy.getBattery();
			}
		});
		canvas.redraw();
	}

	@Override
	public void setFocus() {
	}

}
