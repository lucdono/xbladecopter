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

import it.xbladecopter.Activator;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("rawtypes")
public class LogManager {

	private static LogManager instance = null;
	private ILog log;
	private String id;

	private LogManager() {
		this.log = Activator.getDefault().getLog();
		this.id = Activator.PLUGIN_ID;
	}

	public static LogManager getInstance() {
		synchronized (LogManager.class) {
			if (instance == null) {
				instance = new LogManager();
			}
			return instance;
		}
	}

	public void logError(String message, Throwable e, Class cls) {
		message = message.concat(" [" + cls.getName() + "] ");
		log.log(new Status(Status.ERROR, id, message, e));
		refresh(true, message, Status.ERROR);
	}

	public void logWarning(String message, Class cls) {
		message = message.concat(" [" + cls.getName() + "] ");
		log.log(new Status(Status.WARNING, id, message));
		refresh(false, message, Status.WARNING);
	}

	public void logError(String message, Class cls) {
		message = message.concat(" [" + cls.getName() + "] ");
		log.log(new Status(Status.ERROR, id, message));
		refresh(true, message, Status.ERROR);
	}

	public void logInfo(String message, Class cls) {
		refresh(false, message, Status.INFO);
	}

	private void refresh(boolean isSserious, String message, int status) {
		try {
			switch (status) {
			case Status.INFO:
				getStatusBar().setMessage(
						PlatformUI.getWorkbench().getSharedImages()
								.getImage(ISharedImages.IMG_OBJS_INFO_TSK),
						message);
				break;
			case Status.ERROR:
				getStatusBar().setMessage(
						PlatformUI.getWorkbench().getSharedImages()
								.getImage(ISharedImages.IMG_OBJS_ERROR_TSK),
						message);
				break;
			case Status.WARNING:
				getStatusBar().setMessage(
						PlatformUI.getWorkbench().getSharedImages()
								.getImage(ISharedImages.IMG_OBJS_WARN_TSK),
						message);
				break;
			}
		} catch (Exception e1) {
		}

		try {

			if (isSserious) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage()
						.showView("org.eclipse.pde.runtime.LogView");
			}
		} catch (Exception e) {
		}
	}

	private IStatusLineManager getStatusBar() {
		try {
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			IWorkbenchPage page = win.getActivePage();
			IWorkbenchPart part = page.getActivePart();
			IWorkbenchPartSite site = part.getSite();

			IViewSite vSite = (IViewSite) site;
			IActionBars actionBars = vSite.getActionBars();
			return actionBars.getStatusLineManager();
		} catch (Exception e) {
			return null;
		}
	}
}
