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

import it.xbladecopter.utils.LogManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

/*
 * TODO IMPLEMENT: Camera (view, preferences, help)
 */
public class Camera extends ViewPart {
	public static final String ID = "it.xbladecopter.camera";
	private LogManager logger = LogManager.getInstance();

	@Override
	public void createPartControl(Composite parent) {
		logger.logInfo("Starting Camera...", this.getClass());

		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayout(new FillLayout());
		comp.setLayoutData(new GridData(GridData.FILL));

		Canvas cameraCanvas = new Canvas(comp, SWT.NO_BACKGROUND);
		cameraCanvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				// Get the canvas for drawing and its width and height
				Canvas canvas = (Canvas) e.widget;

				int w = canvas.getBounds().width;
				int h = canvas.getBounds().height;

				Image image = new Image(Display.getDefault(), w, h);
				GC gc = new GC(image);

				gc.setForeground(new Color(Display.getCurrent(), 255, 0, 0));

				gc.drawText("Image not available!", w / 2 - 50, h / 2 - 10);

				logger.logError("Camera image not available!", this.getClass());

				e.gc.drawImage(image, 0, 0);
				gc.dispose();
			}
		});
		cameraCanvas.redraw();

	}

	@Override
	public void setFocus() {

	}
}
