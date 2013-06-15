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
import it.xbladecopter.preferences.PreferenceConstants;
import it.xbladecopter.utils.IConstants;
import it.xbladecopter.utils.LogManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.swtchart.Chart;
import org.swtchart.IAxisSet;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.ISeriesSet;
import org.swtchart.Range;

public class Plotter extends BaseViewPart {
	public static final String ID = "it.xbladecopter.plotter";
	private LogManager logger = LogManager.getInstance();

	private static Map<String, ArrayList<Double>> data = new HashMap<String, ArrayList<Double>>();
	private static Map<String, Chart> charts = new HashMap<String, Chart>();
	private static Chart overlappedChart = null;
	private static Table variables;
	private static Composite shell;
	private static Font labelFont = new Font(Display.getDefault(),
			new FontData[] { IConstants.FONT_SANS_8 });
	private static Font titleFont = new Font(Display.getDefault(),
			new FontData[] { IConstants.FONT_SANS_10_BOLD });

	private static String[] vars = new String[] { "Roll", "Pitch", "Heading",
			"Acc X", "Acc Y", "Acc Z", "Gyro X", "Gyro Y", "Gyro Z", "Magn X",
			"Magn Y", "Magn Z", };

	private String[] units = new String[] { "°", "°", "°", "g", "g", "g",
			"°/s", "°/s", "°/s", "uG", "uG", "uG", };
	private Color colorRed;
	private Color colorBlack;

	private static Range[] ranges;
	private static RGB[] colors;

	private static Button overlap;

	public Plotter() {
		updateFromPreferences();
	}

	@Override
	public void createPartControl(Composite parent) {
		logger.logInfo("Starting plotter...", this.getClass());

		SashForm comp = new SashForm(parent, SWT.HORIZONTAL);

		Composite ctrl = new Composite(comp, SWT.NULL);
		ctrl.setLayout(new GridLayout(2, false));
		ctrl.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));

		Label label = new Label(ctrl, SWT.NONE);
		label.setText("Overlap");

		overlap = new Button(ctrl, SWT.CHECK);
		overlap.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				if (overlap.getSelection()) {

					Set<String> chartKeys = data.keySet();
					Iterator<String> it = chartKeys.iterator();

					while (it.hasNext()) {
						String variable = it.next();

						if (charts.containsKey(variable))
							charts.remove(variable).dispose();
					}

					overlappedChart = new Chart(shell, SWT.NONE);
					overlappedChart.setLayoutData(new GridData(
							GridData.FILL_BOTH));

					// set legend
					overlappedChart.getLegend().setVisible(true);

					// set title
					overlappedChart.getTitle().setText("Measured values");
					overlappedChart.getTitle().setFont(titleFont);
					overlappedChart.getTitle()
							.setForeground(
									new Color(Display.getDefault(), new RGB(
											255, 0, 0)));

					// set axis
					IAxisSet axisSet = overlappedChart.getAxisSet();
					axisSet.getXAxis(0).getTitle().setText("");
					axisSet.getXAxis(0).getTitle().setFont(labelFont);
					axisSet.getXAxis(0)
							.getTitle()
							.setForeground(
									new Color(Display.getDefault(), new RGB(0,
											0, 0)));

					axisSet.getYAxis(0).getTitle().setText("Measures");
					axisSet.getYAxis(0).getTitle().setFont(labelFont);
					axisSet.getYAxis(0)
							.getTitle()
							.setForeground(
									new Color(Display.getDefault(), new RGB(0,
											0, 0)));

					shell.setLayout(new GridLayout(1, true));

					shell.layout();
				} else {
					if (overlappedChart != null)
						overlappedChart.dispose();
					overlappedChart = null;

					updateCharts();
				}
			}
		});

		variables = new Table(ctrl, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FILL);
		variables.setLayout(new FillLayout());
		variables.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		variables.setHeaderVisible(true);

		TableColumn tc1 = new TableColumn(variables, SWT.LEFT);
		TableColumn tc2 = new TableColumn(variables, SWT.LEFT);
		TableColumn tc3 = new TableColumn(variables, SWT.LEFT);
		TableColumn tc4 = new TableColumn(variables, SWT.LEFT);
		tc1.setText("Variable");
		tc1.setWidth(100);
		tc2.setText("Unit");
		tc2.setWidth(50);
		tc3.setText("Min");
		tc3.setWidth(50);
		tc4.setText("Max");
		tc4.setWidth(50);

		for (int i = 0; i < vars.length; i++) {
			final TableItem item = new TableItem(variables, SWT.NONE);
			item.setText(0, vars[i]);
			item.setText(1, units[i]);
			item.setText(2, String.valueOf(ranges[i].lower));
			item.setText(3, String.valueOf(ranges[i].upper));
		}

		variables.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				updateCharts();
			}
		});

		final TableEditor editor = new TableEditor(variables);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		variables.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Rectangle clientArea = variables.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = variables.getTopIndex();
				while (index < variables.getItemCount()) {
					boolean visible = false;
					final TableItem item = variables.getItem(index);
					for (int i = 2; i < variables.getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							final int column = i;
							final int row = index;
							final Text text = new Text(variables, SWT.NONE);
							Listener textListener = new Listener() {
								private void update() {
									try {
										Double value = Double.valueOf(text
												.getText());
										if (column == 2
												&& value < ranges[row].upper) {
											ranges[row].lower = value;
											item.setText(column, text.getText());
										} else if (column == 3
												&& value > ranges[row].lower) {
											ranges[row].upper = value;
											item.setText(column, text.getText());
										}

									} catch (NumberFormatException e1) {
										item.setText(
												column,
												String.valueOf((column == 2 ? ranges[row].lower
														: ranges[row].upper)));
									}
								}

								public void handleEvent(final Event e) {
									switch (e.type) {
									case SWT.FocusOut:
										update();
										text.dispose();
										break;
									case SWT.Traverse:
										switch (e.detail) {
										case SWT.TRAVERSE_RETURN:
											update();
											// FALL THROUGH
										case SWT.TRAVERSE_ESCAPE:
											text.dispose();
											e.doit = false;
										}
										break;
									}
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							editor.setEditor(text, item, i);
							text.setText(item.getText(i));
							text.selectAll();
							text.setFocus();
							return;
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});
		shell = new Composite(comp, SWT.BORDER);
		shell.setLayout(new GridLayout(1, true));
		shell.setLayoutData(new GridData(GridData.FILL_BOTH));

		colorRed = new Color(Display.getDefault(), new RGB(255, 0, 0));
		colorBlack = new Color(Display.getDefault(), new RGB(0, 0, 0));
		comp.setWeights(new int[] { 6, 10 });
	}

	public static void updateFromPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		ranges = new Range[PreferenceConstants.P_ATTITUDE.length / 2];

		for (int i = 0, l = 0; i < ranges.length; i++, l += 2)
			ranges[i] = new Range(
					store.getFloat(PreferenceConstants.P_ATTITUDE[l]),
					store.getFloat(PreferenceConstants.P_ATTITUDE[l + 1]));

		colors = new RGB[PreferenceConstants.P_COLORS_PLOTTER.length];
		for (int i = 0; i < PreferenceConstants.P_COLORS_PLOTTER.length; i++)
			colors[i] = PreferenceConverter.getColor(store,
					PreferenceConstants.P_COLORS_ID[i]);

		if (variables != null && !variables.isDisposed()) {
			for (int i = 0; i < variables.getItemCount(); i++) {
				TableItem item = variables.getItem(i);

				item.setText(2, String.valueOf(ranges[i].lower));
				item.setText(3, String.valueOf(ranges[i].upper));
			}
		}
	}

	@Override
	public void setFocus() {
	}

	public static void updatePlotter() {
		if (shell != null && !shell.isDisposed()) {
			updateValue(vars[0], rpy.getRoll());
			updateValue(vars[1], rpy.getPitch());
			updateValue(vars[2], rpy.getHeading());

			updateValue(vars[3], rpy.getAccX());
			updateValue(vars[4], rpy.getAccY());
			updateValue(vars[5], rpy.getAccZ());

			updateValue(vars[6], rpy.getGyroX());
			updateValue(vars[7], rpy.getGyroY());
			updateValue(vars[8], rpy.getGyroZ());

			updateValue(vars[9], rpy.getMagX());
			updateValue(vars[10], rpy.getMagY());
			updateValue(vars[11], rpy.getMagZ());
		}
	}

	private static void updateValue(String variable, float value) {
		int index = 0;

		if (variable != null && data.containsKey(variable)) {

			ArrayList<Double> values = data.get(variable);

			values.add((double) value);

			if (values.size() > 100)
				values.remove(0);

			double[] arrayV = new double[values.size()];

			for (int i = 0; i < arrayV.length; i++)
				arrayV[i] = values.get(i);

			if (!overlap.isDisposed() && !overlap.getSelection()) {
				if (charts.containsKey(variable)
						&& !charts.get(variable).isDisposed()) {
					ISeriesSet seriesSet = charts.get(variable).getSeriesSet();

					ILineSeries series = (ILineSeries) seriesSet.createSeries(
							SeriesType.LINE, variable);

					series.setYSeries(arrayV);

					series.setSymbolSize(1);

					for (int i = 0; i < vars.length; i++)
						if (vars[i].equals(variable))
							index = i;
					series.setLineColor(new Color(Display.getDefault(),
							colors[index]));

					IAxisSet axisSet = charts.get(variable).getAxisSet();
					axisSet.adjustRange();

					for (int i = 0; i < vars.length; i++)
						if (variable.equals(vars[i]))
							axisSet.getYAxis(0).setRange(ranges[i]);

					charts.get(variable).redraw();
				}
			} else {
				ILineSeries series;

				if (data.containsKey(variable)) {
					if (overlappedChart.getSeriesSet().getSeries(variable) == null)
						series = (ILineSeries) overlappedChart.getSeriesSet()
								.createSeries(SeriesType.LINE, variable);
					else
						series = (ILineSeries) overlappedChart.getSeriesSet()
								.getSeries(variable);

					series.setYSeries(arrayV);

					for (int i = 0; i < vars.length; i++)
						if (vars[i].equals(variable))
							index = i;

					series.setSymbolSize(1);
					series.setLineColor(new Color(Display.getDefault(),
							colors[index]));

					IAxisSet axisSet = overlappedChart.getAxisSet();
					axisSet.adjustRange();

					overlappedChart.redraw();
				}

			}
		}
	}

	private void updateCharts() {
		TableItem[] keys = variables.getItems();

		for (TableItem key : keys) {
			String variable = key.getText(0);

			if (key.getChecked()) {
				if (!data.containsKey(variable))
					data.put(variable, new ArrayList<Double>());

				if (!charts.containsKey(variable) && !overlap.getSelection()) {
					Chart chart = new Chart(shell, SWT.NONE);
					chart.setLayoutData(new GridData(GridData.FILL_BOTH));

					// set legend
					chart.getLegend().setVisible(false);

					// set titles
					chart.getTitle().setText(variable);
					chart.getAxisSet().getXAxis(0).getTitle().setText("");
					chart.getAxisSet().getYAxis(0).getTitle()
							.setText(key.getText(1));

					// set fonts
					chart.getTitle().setFont(titleFont);
					chart.getAxisSet().getXAxis(0).getTitle()
							.setFont(labelFont);
					chart.getAxisSet().getYAxis(0).getTitle()
							.setFont(labelFont);

					// set colors
					chart.getTitle().setForeground(colorRed);
					chart.getAxisSet().getXAxis(0).getTitle()
							.setForeground(colorBlack);
					chart.getAxisSet().getYAxis(0).getTitle()
							.setForeground(colorBlack);

					charts.put(variable, chart);
				}

			} else {
				if (data.containsKey(variable))
					data.remove(variable);
				if (charts.containsKey(variable))
					charts.remove(variable).dispose();

				try {
					overlappedChart.getSeriesSet().deleteSeries(variable);
				} catch (Exception e) {
				}
			}
		}

		if (charts.size() <= 1)
			shell.setLayout(new GridLayout(1, true));
		else
			shell.setLayout(new GridLayout(2, true));

		shell.layout();

	}
}
