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
import it.xbladecopter.connection.TelemetryDataSingleton;
import it.xbladecopter.preferences.PreferenceConstants;
import it.xbladecopter.threads.CommandThread;
import it.xbladecopter.utils.LogManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class Command extends ViewPart {
	private LogManager logger = LogManager.getInstance();

	final static Color background1 = new Color(Display.getCurrent(), new RGB(
			76, 127, 220));
	final static Color background2 = new Color(Display.getCurrent(), new RGB(
			255, 130, 0));
	final static Color foregroundWhite = new Color(Display.getCurrent(),
			new RGB(255, 255, 255));

	private float roll = 0;
	private float pitch = 0;
	private boolean enable = false;
	private boolean enableDoubleClick = false;
	private Text rollText;
	private Text pitchText;
	private static Text rollMax;
	private Button lockRoll;
	private Button lockPitch;
	private static Text pitchMax;
	private Canvas canvas;
	boolean running = true;
	private Text throttleText;
	private CommandThread commandThread;
	private int prevX = 0;
	private int prevY = 0;

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "it.xbladecopter.command";

	/**
	 * The constructor.
	 */
	public Command() {
		commandThread = new CommandThread();
		commandThread.setRunning(true);

		ExecutorService exec = Executors.newSingleThreadExecutor();
		exec.execute(commandThread);
		exec.shutdown();
	}

	@Override
	public void dispose() {
		commandThread.setRunning(false);
		super.dispose();
	}

	public static void updateFromPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		if (rollMax != null && !rollMax.isDisposed())
			rollMax.setText(store.getString(PreferenceConstants.P_CMD_ROLL));
		if (pitchMax != null && !pitchMax.isDisposed())
			pitchMax.setText(store.getString(PreferenceConstants.P_CMD_PITCH));
	}

	public void createPartControl(final Composite parent) {

		parent.setLayout(new GridLayout(3, false));

		final Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayout(new FillLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Scale scale = new Scale(parent, SWT.BORDER | SWT.VERTICAL);
		scale.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		scale.setMaximum(100);
		scale.setMinimum(0);
		scale.setPageIncrement(1);
		scale.setSelection(100);
		scale.setToolTipText("Throttle Command.");
		scale.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int value = 100 - scale.getSelection();
				// Inverted! from 100 to 0.
				if (throttleText != null)
					throttleText.setText(String.valueOf(value) + " %");

				commandThread.getData().setM2(value);
				commandThread.getData().setM3(value);
				commandThread.getData().setM1(value);
				commandThread.getData().setM0(value);
			}
		});

		canvas = new Canvas(comp, SWT.DOUBLE_BUFFERED);

		final Composite data = new Composite(parent, SWT.NONE);
		data.setLayout(new GridLayout(1, true));
		data.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		Group groupThrottle = new Group(data, SWT.NONE);
		groupThrottle.setText("Throttle Command");
		groupThrottle.setLayout(new GridLayout(2, true));
		groupThrottle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(groupThrottle, SWT.NONE);
		label.setText("Throttle:");

		throttleText = new Text(groupThrottle, SWT.BORDER);
		throttleText.setEditable(false);
		throttleText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		throttleText.setText("0 %");

		Group groupRoll = new Group(data, SWT.NONE);
		groupRoll.setText("Roll Command");
		groupRoll.setLayout(new GridLayout(2, true));
		groupRoll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group groupPitch = new Group(data, SWT.NONE);
		groupPitch.setText("Pitch Command");
		groupPitch.setLayout(new GridLayout(2, true));
		groupPitch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(groupRoll, SWT.NONE);
		label.setText("Lock Roll:");

		lockRoll = new Button(groupRoll, SWT.CHECK);

		label = new Label(groupRoll, SWT.NONE);
		label.setText("Roll (°):");

		rollText = new Text(groupRoll, SWT.BORDER);
		rollText.setEditable(false);
		rollText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(groupPitch, SWT.NONE);
		label.setText("Lock Pitch:");

		lockPitch = new Button(groupPitch, SWT.CHECK);

		label = new Label(groupPitch, SWT.NONE);
		label.setText("Pitch (°):");

		pitchText = new Text(groupPitch, SWT.BORDER);
		pitchText.setEditable(false);
		pitchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(groupRoll, SWT.NONE);
		label.setText("Max Roll (°):");

		rollMax = new Text(groupRoll, SWT.BORDER);
		rollMax.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		rollMax.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				String value = ((Text) e.widget).getText();
				IPreferenceStore store = Activator.getDefault()
						.getPreferenceStore();

				int intVal = 0;
				try {
					intVal = Integer.valueOf(value);
				} catch (NumberFormatException e1) {
					((Text) e.widget).setText(store
							.getString(PreferenceConstants.P_CMD_ROLL));
				}

				if (intVal > 90 || intVal < 0)
					((Text) e.widget).setText(store
							.getString(PreferenceConstants.P_CMD_ROLL));
			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});

		label = new Label(groupPitch, SWT.NONE);
		label.setText("Max Pitch (°):");

		pitchMax = new Text(groupPitch, SWT.BORDER);
		pitchMax.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pitchMax.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				String value = ((Text) e.widget).getText();
				IPreferenceStore store = Activator.getDefault()
						.getPreferenceStore();

				int intVal = 0;
				try {
					intVal = Integer.valueOf(value);
				} catch (NumberFormatException e1) {
					((Text) e.widget).setText(store
							.getString(PreferenceConstants.P_CMD_PITCH));
				}

				if (intVal > 90 || intVal < 0)
					((Text) e.widget).setText(store
							.getString(PreferenceConstants.P_CMD_PITCH));
			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});

		final Composite buttons = new Composite(data, SWT.NONE);
		buttons.setLayout(new GridLayout(2, true));
		buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button recordBtn = new Button(buttons, SWT.TOGGLE);
		recordBtn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (recordBtn.getSelection())
					TelemetryDataSingleton.getInstance().setLogEnabled(true);
				else
					TelemetryDataSingleton.getInstance().setLogEnabled(false);

				recordBtn.setSelection(TelemetryDataSingleton.getInstance()
						.getLogEnabled());

				logger.logInfo(
						"Data recording "
								+ (recordBtn.getSelection() ? "enabled."
										: "disabled."), this.getClass());
			}
		});
		recordBtn.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_ELCL_STOP));
		recordBtn.setLayoutData(new GridData(GridData.FILL_BOTH));
		recordBtn.setToolTipText("Record telemetry to file.");

		final Button enableBtn = new Button(buttons, SWT.TOGGLE);
		enableBtn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (enableBtn.getSelection())
					commandThread.setPause(true);
				else
					commandThread.setPause(false);

				recordBtn.setSelection(TelemetryDataSingleton.getInstance()
						.getLogEnabled());

				if (enableBtn.getSelection())
					logger.logInfo("Command data sent enabled.",
							this.getClass());
				else
					logger.logWarning("Command data sent disabled.",
							this.getClass());
			}
		});
		enableBtn.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_ELCL_COLLAPSEALL));
		enableBtn.setLayoutData(new GridData(GridData.FILL_BOTH));
		enableBtn.setToolTipText("Enable command data sent.");

		canvas.setToolTipText("Pitch [left] & Roll [right] Commands.");
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				// Get the canvas for drawing and its width and height
				Canvas canvas = (Canvas) e.widget;

				int width = canvas.getSize().x;
				int height = canvas.getSize().y - comp.getBorderWidth();

				drawAttitudeControls(e.gc, width, height);
			}
		});

		canvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				enable = false;
				roll = 0;
				pitch = 0;

				update();
			}

			@Override
			public void mouseDown(MouseEvent e) {
				enable = true;
				prevX = e.x;
				prevY = e.y;
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (enableDoubleClick) {
					enableDoubleClick = false;
					roll = 0;
					pitch = 0;

					update();
				} else
					enableDoubleClick = true;
			}
		});

		canvas.addMouseMoveListener(new MouseMoveListener() {

			private float pitchAngleStep;
			private float rollAngleStep;

			@Override
			public void mouseMove(MouseEvent e) {

				if (enable || enableDoubleClick) {
					int maxRoll = Integer.valueOf(rollMax.getText());
					int maxPitch = Integer.valueOf(pitchMax.getText());

					// Calculate deltas and scale value
					rollAngleStep = Math.abs(e.x - prevX) * 0.5f;
					pitchAngleStep = Math.abs(e.y - prevY) * 1.5f;

					if (roll >= maxRoll) {
						roll = maxRoll;
					} else if (roll <= -maxRoll) {
						roll = -maxRoll;
					}

					if (pitch >= maxPitch) {
						pitch = maxPitch;
					} else if (pitch <= -maxPitch) {
						pitch = -maxPitch;
					}

					if (e.x < prevX && !lockRoll.getSelection())
						roll -= rollAngleStep;
					else if (e.x > prevX && !lockRoll.getSelection())
						roll += rollAngleStep;
					else if (e.y < prevY && !lockPitch.getSelection())
						pitch -= pitchAngleStep;
					else if (e.y > prevY && !lockPitch.getSelection())
						pitch += pitchAngleStep;

					prevX = e.x;
					prevY = e.y;

					update();
				}
			}
		});

		updateFromPreferences();
	}

	private void update() {
		commandThread.getData().setPitch(-pitch);
		commandThread.getData().setRoll(-roll);

		if (pitchText != null) {
			pitchText.setText(String.valueOf(-pitch));
			pitchText.update();
		}

		if (rollText != null) {
			rollText.setText(String.valueOf(-roll));
			rollText.update();
		}

		if (canvas != null)
			canvas.redraw();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}

	private void drawAttitudeControls(GC gc, int width, int height) {
		int size = Math.min(width / 2, height);
		int verticalOffset = (height - size) / 2;

		gc.setLineWidth(2);

		gc.setBackground(background1);
		gc.fillArc(width / 2 - size, verticalOffset, size, size, 0, 360);
		gc.fillArc(width / 2, verticalOffset, size, size, 0, 360);

		gc.setBackground(background2);
		gc.fillArc(width / 2 - size, verticalOffset, size, size, (int) pitch,
				-180);
		gc.fillArc(width / 2, verticalOffset, size, size, (int) roll, -180);

		gc.setForeground(foregroundWhite);
		gc.drawArc(width / 2 - size, verticalOffset, size, size, 0, 360);
		gc.drawArc(width / 2, verticalOffset, size, size, 0, 360);

		String value = "";
		gc.setForeground(foregroundWhite);

		for (int i = 0; i < 2; i++) {
			int centerX = width / 2 - i * size;
			int tickSize = 20;
			int step = 20;

			for (int theta = 10; theta <= 360; theta += step) {
				int x = (int) (size / 2 + size / 2
						* Math.cos(theta * Math.PI / 180.0))
						+ centerX;
				int y = (int) (size / 2 + size / 2
						* Math.sin(theta * Math.PI / 180.0));

				int xx = (int) ((size) / 2 + (size - tickSize) / 2
						* Math.cos(theta * Math.PI / 180.0))
						+ centerX;
				int yy = (int) ((size) / 2 + (size - tickSize) / 2
						* Math.sin(theta * Math.PI / 180.0));

				gc.drawLine(x, verticalOffset + y, xx, verticalOffset + yy);

				if (theta < 180) {
					if (theta > 90 && theta < 180) {
						yy = yy - 15;
						xx = xx + 2;
						value = String.valueOf(theta);
					} else if (theta > 45 && theta <= 90) {
						yy = yy - 15;
						xx = xx - 10;
						value = String.valueOf(theta);
					} else if (theta < 45) {
						yy = yy - 15;
						xx = xx - 15;
						value = String.valueOf(theta);
					}
					gc.drawText(value, xx, verticalOffset + yy, true);
				} else {
					if (theta > 270) {
						xx = xx - 9;
						value = String.valueOf(theta - 360);
					} else {
						xx = xx - 3;
						value = String.valueOf(theta - 360);
					}

					if (theta == 180) {
						yy = yy - 7;
						xx = xx + 10;
					} else if (theta == 360) {
						yy = yy - 7;
						xx = xx - 3;
					}
					gc.drawText(value, xx, verticalOffset + yy, true);
				}
			}
		}
	}

}