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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class Flight extends BaseViewPart {
	public static final String ID = "it.xbladecopter.flight";
	private LogManager logger = LogManager.getInstance();

	private static Group sensorData;
	private static Text rollText;
	private static Text pitchText;
	private static Text headingText;
	private static Text accXText;
	private static Text accYText;
	private static Text accZText;
	private static Text awXText;
	private static Text awYText;
	private static Text awZText;
	private static Text magXText;
	private static Text magZText;
	private static Text magYText;
	private static Text m0Text;
	private static Text m1Text;
	private static Text m2Text;
	private static Text m3Text;
	private static CLabel gM0;
	private static CLabel gM1;
	private static CLabel gM2;
	private static CLabel gM3;

	@Override
	public void createPartControl(Composite parent) {

		logger.logInfo("Starting flight data view...", this.getClass());

		sensorData = new Group(parent, SWT.NONE);
		sensorData.setLayout(new GridLayout(1, false));
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 1;
		layoutData.grabExcessVerticalSpace = true;
		sensorData.setLayoutData(layoutData);
		sensorData.setText("Flight Data");

		GridData layoutGradient = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		layoutGradient.widthHint = 40;
		layoutGradient.heightHint = 15;
		layoutGradient.horizontalSpan = 1;

		GridData layoutSensor = new GridData();
		layoutSensor.minimumWidth = 20;
		layoutSensor.widthHint = 50;

		GridData layoutMotor = new GridData();
		layoutMotor.minimumWidth = 20;
		layoutMotor.widthHint = 30;

		GridData layoutLabelAttitude = new GridData();
		layoutLabelAttitude.minimumWidth = 20;
		layoutLabelAttitude.widthHint = 50;

		GridData layoutLabelS = new GridData();
		layoutLabelS.minimumWidth = 20;
		layoutLabelS.widthHint = 30;

		GridData layoutLabel = new GridData();
		layoutLabel.minimumWidth = 20;
		layoutLabel.widthHint = 50;

		Group angleData = new Group(sensorData, SWT.NONE);
		angleData.setLayout(new GridLayout(4, true));
		angleData.setText("Attitude Angles (°)");
		angleData.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(angleData, SWT.NONE);
		label.setText("Roll: ");
		label.setLayoutData(layoutLabelAttitude);
		rollText = new Text(angleData, SWT.NONE);
		rollText.setEnabled(false);
		rollText.setLayoutData(layoutSensor);
		rollText.setText("0.0");

		label = new Label(angleData, SWT.NONE);
		label.setText("Pitch: ");
		label.setLayoutData(layoutLabelAttitude);
		pitchText = new Text(angleData, SWT.NONE);
		pitchText.setEnabled(false);
		pitchText.setLayoutData(layoutSensor);
		pitchText.setText("0.0");

		label = new Label(angleData, SWT.NONE);
		label.setText("Heading: ");
		label.setLayoutData(layoutLabelAttitude);
		headingText = new Text(angleData, SWT.NONE);
		headingText.setEnabled(false);
		headingText.setLayoutData(layoutSensor);
		headingText.setText("0.0");

		Group dof1Data = new Group(sensorData, SWT.NONE);
		dof1Data.setLayout(new GridLayout(6, false));
		dof1Data.setText("Acceleration (g)");
		dof1Data.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(dof1Data, SWT.NONE);
		label.setText("x: ");
		label.setLayoutData(layoutLabelS);
		accXText = new Text(dof1Data, SWT.NONE);
		accXText.setEnabled(false);
		accXText.setLayoutData(layoutSensor);
		accXText.setText("0.0");

		label = new Label(dof1Data, SWT.NONE);
		label.setText("y: ");
		label.setLayoutData(layoutLabelS);
		accYText = new Text(dof1Data, SWT.NONE);
		accYText.setEnabled(false);
		accYText.setLayoutData(layoutSensor);
		accYText.setText("0.0");

		label = new Label(dof1Data, SWT.NONE);
		label.setText("z: ");
		label.setLayoutData(layoutLabelS);
		accZText = new Text(dof1Data, SWT.NONE);
		accZText.setEnabled(false);
		accZText.setLayoutData(layoutSensor);
		accZText.setText("0.0");

		Group dof2Data = new Group(sensorData, SWT.NONE);
		dof2Data.setLayout(new GridLayout(6, false));
		dof2Data.setText("Angular Velocity (°/s)");
		dof2Data.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(dof2Data, SWT.NONE);
		label.setText("x: ");
		label.setLayoutData(layoutLabelS);
		awXText = new Text(dof2Data, SWT.NONE);
		awXText.setEnabled(false);
		awXText.setLayoutData(layoutSensor);
		awXText.setText("0.0");

		label = new Label(dof2Data, SWT.NONE);
		label.setText("y: ");
		label.setLayoutData(layoutLabelS);
		awYText = new Text(dof2Data, SWT.NONE);
		awYText.setEnabled(false);
		awYText.setLayoutData(layoutSensor);
		awYText.setText("0.0");

		label = new Label(dof2Data, SWT.NONE);
		label.setText("z: ");
		label.setLayoutData(layoutLabelS);
		awZText = new Text(dof2Data, SWT.NONE);
		awZText.setEnabled(false);
		awZText.setLayoutData(layoutSensor);
		awZText.setText("0.0");

		Group dof3Data = new Group(sensorData, SWT.NONE);
		dof3Data.setLayout(new GridLayout(6, false));
		dof3Data.setText("Magnetic Field (uGauss)");
		dof3Data.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(dof3Data, SWT.NONE);
		label.setText("x: ");
		label.setLayoutData(layoutLabelS);
		magXText = new Text(dof3Data, SWT.NONE);
		magXText.setEnabled(false);
		magXText.setLayoutData(layoutSensor);
		magXText.setText("0.0");

		label = new Label(dof3Data, SWT.NONE);
		label.setText("y: ");
		label.setLayoutData(layoutLabelS);
		magYText = new Text(dof3Data, SWT.NONE);
		magYText.setEnabled(false);
		magYText.setLayoutData(layoutSensor);
		magYText.setText("0.0");

		label = new Label(dof3Data, SWT.NONE);
		label.setText("z: ");
		label.setLayoutData(layoutLabelS);
		magZText = new Text(dof3Data, SWT.NONE);
		magZText.setEnabled(false);
		magZText.setLayoutData(layoutSensor);
		magZText.setText("0.0");

		Group control = new Group(sensorData, SWT.NONE);
		control.setLayout(new GridLayout(6, false));
		layoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		layoutData.horizontalSpan = 6;
		control.setLayoutData(layoutData);
		control.setText("Motors Speed");

		label = new Label(control, SWT.NONE);
		label.setText("Front (R): ");
		label.setLayoutData(layoutLabel);

		gM0 = new CLabel(control, SWT.SHADOW_IN);
		gM0.setLayoutData(layoutGradient);
		gM0.setBackground(
				new Color[] {
						Display.getDefault().getSystemColor(SWT.COLOR_GREEN),
						Display.getDefault().getSystemColor(SWT.COLOR_YELLOW),
						Display.getDefault().getSystemColor(SWT.COLOR_RED) },
				new int[] { 100, 100 });
		m0Text = new Text(control, SWT.NONE);
		m0Text.setEnabled(false);
		m0Text.setLayoutData(layoutMotor);
		m0Text.setText("0.0");

		label = new Label(control, SWT.NONE);
		label.setText("Front (L): ");
		label.setLayoutData(layoutLabel);

		gM1 = new CLabel(control, SWT.SHADOW_IN);
		gM1.setLayoutData(layoutGradient);
		gM1.setBackground(
				new Color[] {
						Display.getDefault().getSystemColor(SWT.COLOR_GREEN),
						Display.getDefault().getSystemColor(SWT.COLOR_YELLOW),
						Display.getDefault().getSystemColor(SWT.COLOR_RED) },
				new int[] { 100, 100 });
		m1Text = new Text(control, SWT.NONE);
		m1Text.setEnabled(false);
		m1Text.setLayoutData(layoutMotor);
		m1Text.setText("0.0");

		label = new Label(control, SWT.NONE);
		label.setText("Back (R): ");
		label.setLayoutData(layoutLabel);

		gM2 = new CLabel(control, SWT.SHADOW_IN);
		gM2.setLayoutData(layoutGradient);
		gM2.setBackground(
				new Color[] {
						Display.getDefault().getSystemColor(SWT.COLOR_GREEN),
						Display.getDefault().getSystemColor(SWT.COLOR_YELLOW),
						Display.getDefault().getSystemColor(SWT.COLOR_RED) },
				new int[] { 100, 100 });
		m2Text = new Text(control, SWT.NONE);
		m2Text.setEnabled(false);
		m2Text.setLayoutData(layoutMotor);
		m2Text.setText("0.0");

		label = new Label(control, SWT.NONE);
		label.setText("Back (L): ");
		label.setLayoutData(layoutLabel);

		gM3 = new CLabel(control, SWT.SHADOW_IN);
		gM3.setLayoutData(layoutGradient);
		gM3.setBackground(
				new Color[] {
						Display.getDefault().getSystemColor(SWT.COLOR_GREEN),
						Display.getDefault().getSystemColor(SWT.COLOR_YELLOW),
						Display.getDefault().getSystemColor(SWT.COLOR_RED) },
				new int[] { 100, 100 });
		m3Text = new Text(control, SWT.NONE);
		m3Text.setEnabled(false);
		m3Text.setLayoutData(layoutMotor);
		m3Text.setText("0.0");
	}

	public static void updateFlight() {
		try {
			if (!sensorData.isDisposed() && sensorData.isVisible()) {
				rollText.setText(String.format("%3.3f", rpy.getRoll()));
				pitchText.setText(String.format("%3.3f", rpy.getPitch()));
				headingText.setText(String.format("%3.3f", rpy.getHeading()));

				accXText.setText(String.format("%3.3f", rpy.getAccX()));
				accYText.setText(String.format("%3.3f", rpy.getAccY()));
				accZText.setText(String.format("%3.3f", rpy.getAccZ()));

				awXText.setText(String.format("%3.3f", rpy.getGyroX()));
				awYText.setText(String.format("%3.3f", rpy.getGyroY()));
				awZText.setText(String.format("%3.3f", rpy.getGyroZ()));

				magXText.setText(String.format("%3.3f", rpy.getMagX()));
				magYText.setText(String.format("%3.3f", rpy.getMagY()));
				magZText.setText(String.format("%3.3f", rpy.getMagZ()));

				if (rpy.getM3() <= 100 && rpy.getM3() >= 0) {
					gM1.setBackground(
							new Color[] {
									Display.getDefault().getSystemColor(
											SWT.COLOR_GREEN),
									Display.getDefault().getSystemColor(
											SWT.COLOR_YELLOW),
									Display.getDefault().getSystemColor(
											SWT.COLOR_RED) }, new int[] {
									(int) (100 - rpy.getM3()), 100 });
					m1Text.setText(String.format("%3.1f", rpy.getM3()));
					gM1.layout();
				}

				if (rpy.getM2() <= 100 && rpy.getM2() >= 0) {
					gM0.setBackground(
							new Color[] {
									Display.getDefault().getSystemColor(
											SWT.COLOR_GREEN),
									Display.getDefault().getSystemColor(
											SWT.COLOR_YELLOW),
									Display.getDefault().getSystemColor(
											SWT.COLOR_RED) }, new int[] {
									(int) (100 - rpy.getM2()), 100 });
					m0Text.setText(String.format("%3.1f", rpy.getM2()));
					gM0.layout();
				}

				if (rpy.getM0() <= 100 && rpy.getM0() >= 0) {
					gM3.setBackground(
							new Color[] {
									Display.getDefault().getSystemColor(
											SWT.COLOR_GREEN),
									Display.getDefault().getSystemColor(
											SWT.COLOR_YELLOW),
									Display.getDefault().getSystemColor(
											SWT.COLOR_RED) }, new int[] {
									(int) (100 - rpy.getM0()), 100 });
					m3Text.setText(String.format("%3.1f", rpy.getM0()));
					gM3.layout();
				}

				if (rpy.getM1() <= 100 && rpy.getM1() >= 0) {
					gM2.setBackground(
							new Color[] {
									Display.getDefault().getSystemColor(
											SWT.COLOR_GREEN),
									Display.getDefault().getSystemColor(
											SWT.COLOR_YELLOW),
									Display.getDefault().getSystemColor(
											SWT.COLOR_RED) }, new int[] {
									(int) (100 - rpy.getM1()), 100 });
					m2Text.setText(String.format("%3.1f", rpy.getM1()));
					gM2.layout();
				}
			}
		} catch (Exception e) {
		}

	}

	@Override
	public void setFocus() {
	}
}
