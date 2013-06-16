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

package it.xbladecopter.comm.gui;

import it.xbladecopter.comm.serial.SerialConnection;
import it.xbladecopter.utils.LogManager;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ConnectionDialog extends Dialog {
	private LogManager logger = LogManager.getInstance();

	private Combo stop;
	private Combo word;
	private Combo parity;
	private Combo baud;
	private Combo ports;

	private Button isRadio;

	public ConnectionDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createContents(Composite parent) {
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		layoutData.minimumWidth = 150;
		layoutData.widthHint = 80;

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, true));

		Label label = new Label(comp, SWT.NONE);
		label.setText("Serial Port: ");

		ports = new Combo(comp, SWT.READ_ONLY);
		ports.setLayoutData(layoutData);
		ports.setItems(SerialConnection.getInstance().getPorts());
		ports.select(0);

		label = new Label(comp, SWT.NONE);
		label.setText("Baud Rate: ");

		baud = new Combo(comp, SWT.READ_ONLY);
		baud.setLayoutData(layoutData);
		baud.setItems(new String[] { "115200", "57600", "38400", "19200",
				"9600", "4800" });
		baud.select(0);

		label = new Label(comp, SWT.NONE);
		label.setText("Parity: ");

		parity = new Combo(comp, SWT.READ_ONLY);
		parity.setLayoutData(layoutData);
		parity.setItems(new String[] { "None", "Odd", "Even" });
		parity.select(0);

		label = new Label(comp, SWT.NONE);
		label.setText("Word Length: ");

		word = new Combo(comp, SWT.READ_ONLY);
		word.setLayoutData(layoutData);
		word.setItems(new String[] { "8", "7", "6", "5" });
		word.select(0);

		label = new Label(comp, SWT.NONE);
		label.setText("Stop Bits: ");

		stop = new Combo(comp, SWT.READ_ONLY);
		stop.setLayoutData(layoutData);
		stop.setItems(new String[] { "1", "2" });
		stop.select(0);

		isRadio = new Button(comp, SWT.CHECK);
		isRadio.setText("Send AT commands");
		isRadio.setToolTipText("Configure ACOMM 4868 module.");

		return super.createContents(parent);
	}

	@Override
	protected void configureShell(Shell shell) {
		setBlockOnOpen(true);
		shell.setText("Connection Dialog");
		shell.setLayout(new GridLayout());
		shell.setLayoutData(GridData.FILL_BOTH);
		super.configureShell(shell);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == 0) {
			SerialConnection.getInstance().setParameters(
					ports.getSelectionIndex(), baud.getText(),
					parity.getSelectionIndex(), word.getSelectionIndex(),
					stop.getSelectionIndex(),isRadio.getSelection());
			logger.logInfo("Serial paramters have been set", this.getClass());
		} else
			logger.logWarning("Serial paramenters not set.", this.getClass());
		super.buttonPressed(buttonId);
	}

}
