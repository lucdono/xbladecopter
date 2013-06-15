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

import it.xbladecopter.threads.GUIUpdater;
import it.xbladecopter.utils.LogManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.glu.GLU;

public class Model3d extends BaseViewPart {
	public static final String ID = "it.xbladecopter.model3d";
	private static LogManager logger = LogManager.getInstance();
	private static GLCanvas canvas;
	public static boolean fixedAxis = true;
	public static boolean enableAxis = true;
	public static boolean enableXMode = true;
	public static boolean enableWire = false;
	public static Slider zoomSlider;
	private static int rot = 0;
	static private FloatBuffer working = BufferUtils.createFloatBuffer(4);

	static float no_mat[] = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	static float mat_ambient[] = new float[] { 0.0f, 0.0f, 1.0f, 1.0f };
	static float mat_ambient_colorY[] = new float[] { 1.0f, 1.0f, 0.0f, 1.0f };
	static float mat_ambient_colorB[] = new float[] { 0.0f, 0.0f, 1.0f, 1.0f };
	static float mat_ambient_colorDark[] = new float[] { 0.4f, 0.4f, 0.4f, 1.0f };
	static float mat_diffuse_Dark[] = new float[] { 0.4f, 0.4f, 0.4f, 1.0f };
	static float mat_emission_Dark[] = new float[] { 0.4f, 0.4f, 0.4f, 0.0f };
	static float mat_ambient_colorR[] = new float[] { 1.0f, 0.0f, 0.0f, 1.0f };
	static float mat_ambient_colorGlass[] = new float[] { 1.0f, 0.5f, 0.0f,
			1.0f };
	static float mat_diffuseGlass[] = new float[] { 0.0f, 0.0f, 0.8f, 1.0f };
	static float mat_specularGlass[] = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	static float mat_emissionGlass[] = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
	static float high_shininess_Glass[] = new float[] { 100.0f };
	static float mat_diffuse[] = new float[] { 0.8f, 0.5f, 0.8f, 1.0f };
	static float mat_specular[] = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	static float no_diffuse[] = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	static float no_specular[] = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
	static float no_shininess[] = new float[] { 0.0f };
	static float mat_emission_w[] = new float[] { 1.0f, 1.0f, 1.0f, 0.0f };
	static float mat_emission_y[] = new float[] { 1.0f, 1.0f, 0.0f, 0.0f };
	static float low_shininess[] = new float[] { 5.0f };
	static float high_shininess[] = new float[] { 100.0f };
	static float mat_emission[] = new float[] { 0.3f, 0.2f, 0.2f, 0.0f };
	static float mat_emissionR[] = new float[] { 1.0f, 0.0f, 0.0f, 0.0f };
	static float mat_emissionB[] = new float[] { 0.0f, 0.0f, 1.0f, 0.0f };

	public static void initLighting() {
		ByteBuffer buffer = ByteBuffer.allocateDirect(64).order(
				ByteOrder.nativeOrder());

		float[] global = { 0.5f, 0.5f, 0.5f, 1.0f };

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, (FloatBuffer) buffer
				.asFloatBuffer().put(global));
		GL11.glShadeModel(GL11.GL_SMOOTH);

		float[] ambient = { 0.2f, 0.2f, 0.2f, 1.0f };
		float[] diffuse = { 0.8f, 0.8f, 0.8f, 1.0f };
		float[] specular = { 0.5f, 0.5f, 0.5f, 1.0f };
		float[] position = { 8.5f, 5.5f, 4.0f, 3.0f };

		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, (FloatBuffer) buffer
				.asFloatBuffer().put(ambient).flip());
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, (FloatBuffer) buffer
				.asFloatBuffer().put(diffuse).flip());
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, (FloatBuffer) buffer
				.asFloatBuffer().put(specular).flip());
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, (FloatBuffer) buffer
				.asFloatBuffer().put(position).flip());

		GL11.glEnable(GL11.GL_LIGHT0);
	}

	static private void set(float ambient[], float diffuse[], float specular[],
			float shininess[], float emission[]) {
		loadWorking(ambient);
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, working);
		loadWorking(diffuse);
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, working);
		loadWorking(specular);
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, working);
		GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, shininess[0]);
		loadWorking(emission);
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, working);
	}

	static private void loadWorking(float[] data) {
		working.rewind();
		working.put(data);
		working.flip();
	}

	static void drawAxis(float length) {

		float l = length + 2 * length * 80 / 100;

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		set(mat_ambient_colorY, no_diffuse, no_specular, no_shininess,
				mat_emission_y);

		GL11.glBegin(GL11.GL_QUADS);
		// X
		GL11.glColor3f(0.9f, 0.9f, 0.0f);

		GL11.glVertex3f(-l, 0, 0);
		GL11.glVertex3f(0, 0, 0);
		GL11.glVertex3f(0, 0, 0);
		GL11.glVertex3f(l, 0, 0);

		// Y
		GL11.glVertex3f(0, -l, 0);
		GL11.glVertex3f(0, 0, 0);
		GL11.glVertex3f(0, 0, 0);
		GL11.glVertex3f(0, l, 0);

		// Z
		GL11.glVertex3f(0, 0, -l);
		GL11.glVertex3f(0, 0, 0);
		GL11.glVertex3f(0, 0, 0);
		GL11.glVertex3f(0, 0, l);
		GL11.glEnd();
	}

	static void drawMotor(float x, float y, float z, float w, float h) {
		if (!enableWire)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		else
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

		GL11.glPushMatrix();

		GL11.glTranslatef(x, y, z);
		set(mat_ambient_colorB, mat_diffuse, mat_specular, high_shininess,
				mat_emissionB);

		Cylinder cylinder = new Cylinder();
		cylinder.draw(0.2f, 0.2f, h + 1, 20, 20);
		Disk disk = new Disk();
		disk.draw(0, 0.2f, 20, 20);
		GL11.glTranslatef(0, 0, h + 1);
		disk = new Disk();
		disk.draw(0, 0.2f, 20, 20);

		GL11.glTranslatef(0, 0, -(h + 1));
		set(mat_ambient_colorR, mat_diffuse, mat_specular, high_shininess,
				mat_emissionR);

		cylinder = new Cylinder();
		cylinder.draw(w / 2, w / 2, h, 20, 20);
		disk = new Disk();
		disk.draw(0, w / 2, 20, 20);
		GL11.glTranslatef(0, 0, h);
		disk = new Disk();
		disk.draw(0, w / 2, 20, 20);

		GL11.glTranslatef(0, 0, 0);
		GL11.glPopMatrix();

		GL11.glTranslatef(0, 0, 0);
		GL11.glPopMatrix();

		if (!enableWire)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		else
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL11.glPushMatrix();
		set(mat_ambient_colorDark, mat_diffuse_Dark, mat_specular,
				high_shininess, mat_emission_Dark);
		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(rot, 0.0f, 0.0f, 1.0f);
		GL11.glRotatef(15, 0.0f, 1.0f, 0.0f);
		drawCube(0.0f, -5f, h + 0.5f, 5, 0.0f, 1.0f);
		GL11.glRotatef(-30, 0.0f, 1.0f, 0.0f);
		drawCube(0.05f, 5f, h + 0.5f, 5, 0.0f, 1.0f);
		GL11.glTranslatef(0, 0, 0);
		GL11.glPopMatrix();
	}

	static void drawCube(float x, float y, float z, float w, float h, float d) {
		GL11.glBegin(GL11.GL_POLYGON);
		GL11.glNormal3f(0, 0, -1f);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(x - d, y - w, z - h);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(x - d, y + w, z - h);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(x + d, y + w, z - h);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(x + d, y - w, z - h);
		GL11.glEnd();
	}

	static void drawDrone() {
		float length = 10.0f;
		float armLength = 20.0f;
		float cubeSize = 0.5f;
		float motorSize = 2.0f;
		float motorHeight = 2.0f;
		float boxSize = 10.0f;
		float boxHeight = 3.0f;

		if (enableAxis) {
			if (fixedAxis) {
				GL11.glPushMatrix();
				GL11.glLoadIdentity();
				if (zoomSlider == null)
					GL11.glTranslatef(0.0f, 0.0f, -60.0f);
				else
					GL11.glTranslatef(0.0f, 0.0f,
							-60.0f + (float) (zoomSlider.getSelection() - 49));

				GL11.glRotatef(90, 0.0f, 0.0f, 1.0f);
				GL11.glRotatef(90, 0.0f, 1.0f, 0.0f);
			}
			drawAxis(length * 1.3f);
			if (fixedAxis)
				GL11.glPopMatrix();
		}

		for (int i = 0; i < 4; i++) {
			if (!enableWire)
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			else
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

			GL11.glPushMatrix();
			set(mat_ambient_colorDark, mat_diffuse_Dark, mat_specular,
					high_shininess, mat_emission_Dark);
			GL11.glTranslatef(-cubeSize / 2, cubeSize, 0);
			GL11.glRotatef(90, 0.0f, 1.0f, 0.0f);
			GL11.glRotatef(90 * i, 1.0f, 0.0f, 0.0f);
			Cylinder cylinder = new Cylinder();
			cylinder.draw(cubeSize, cubeSize, 2 * armLength - length, 20, 20);
			Disk disk = new Disk();
			disk.draw(0, cubeSize, 20, 20);
			GL11.glTranslatef(0, 0, 2 * armLength - length);
			disk = new Disk();
			disk.draw(0, cubeSize, 20, 20);

			GL11.glTranslatef(0, 0, 0);
			GL11.glPopMatrix();
		}
		if (!enableWire)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		else
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		set(mat_ambient_colorGlass, mat_diffuseGlass, mat_specularGlass,
				low_shininess, mat_emissionGlass);
		Cylinder cylinder = new Cylinder();
		cylinder.draw(boxSize, boxSize, boxHeight, 4, 20);
		Disk disk = new Disk();
		disk.draw(0, boxSize, 4, 20);
		GL11.glTranslatef(0, 0, boxHeight);
		disk = new Disk();
		disk.draw(0, boxSize, 4, 20);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();

		drawMotor(armLength + length / 2, cubeSize, cubeSize / 2, motorSize,
				motorHeight);
		drawMotor(-armLength - length / 2, cubeSize, cubeSize / 2, motorSize,
				motorHeight);
		drawMotor(cubeSize / 2, armLength + length / 2 + cubeSize,
				cubeSize / 2, motorSize, motorHeight);
		drawMotor(cubeSize / 2, -armLength - length / 2 + cubeSize,
				cubeSize / 2, motorSize, motorHeight);

	}

	@Override
	public void createPartControl(Composite parent) {
		logger.logInfo("Starting 3D model simulation...", this.getClass());

		GLData data = new GLData();
		data.doubleBuffer = true;

		GridData layoutData = new GridData(GridData.FILL_BOTH);

		Composite outer = new Composite(parent, SWT.BORDER);
		outer.setLayout(new GridLayout(1, false));
		outer.setLayoutData(layoutData);

		Composite comp = new Composite(outer, SWT.BORDER);
		comp.setLayout(new FillLayout());
		comp.setLayoutData(layoutData);

		canvas = new GLCanvas(comp, SWT.DOUBLE_BUFFERED, data);
		canvas.setCurrent();

		try {
			GLContext.useContext(canvas);
		} catch (LWJGLException e) {
			logger.logError(e.getMessage(), e, this.getClass());
		}

		canvas.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				updateModel();
			}
		});

		GL11.glClearColor(.3f, .5f, .8f, 1.0f);
		GL11.glColor3f(1.0f, 0.0f, 0.0f);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glClearDepth(1.0);
		GL11.glLineWidth(2);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		initLighting();

		Composite ctrl = new Composite(outer, SWT.NONE);
		layoutData = new GridData(GridData.VERTICAL_ALIGN_END);
		ctrl.setLayoutData(layoutData);
		ctrl.setLayout(new GridLayout(6, false));

		Button axis = new Button(ctrl, SWT.CHECK);
		axis.setText("Axis");
		axis.setSelection(enableAxis);
		axis.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				enableAxis = !enableAxis;
				if (!GUIUpdater.getInstance().isRunning())
					updateModel();
			}
		});

		Button xmode = new Button(ctrl, SWT.CHECK);
		xmode.setText("X Mode");
		xmode.setSelection(enableXMode);
		xmode.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				enableXMode = !enableXMode;
				if (!GUIUpdater.getInstance().isRunning())
					updateModel();
			}
		});

		Button axisStop = new Button(ctrl, SWT.CHECK);
		axisStop.setText("Fixed Axis");
		axisStop.setSelection(fixedAxis);
		axisStop.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				fixedAxis = !fixedAxis;
				if (!GUIUpdater.getInstance().isRunning())
					updateModel();
			}
		});

		Button wire = new Button(ctrl, SWT.CHECK);
		wire.setText("Wireframe");
		wire.setSelection(enableWire);
		wire.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				enableWire = !enableWire;
				if (!GUIUpdater.getInstance().isRunning())
					updateModel();
			}
		});

		zoomSlider = new Slider(ctrl, SWT.NONE);
		zoomSlider.setIncrement(1);
		zoomSlider.setMaximum(100);
		zoomSlider.setMinimum(0);
		zoomSlider.setSelection(50);
		zoomSlider.setThumb(1);
		zoomSlider.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (!GUIUpdater.getInstance().isRunning())
					updateModel();
			}
		});

		Text zoomText = new Text(ctrl, SWT.NONE);
		zoomText.setEnabled(false);
		zoomText.setText("Zoom");
	}

	public static void updateModel() {

		if (GUIUpdater.getInstance().isRunning()) {
			if (rot == 360)
				rot = 0;
			else
				rot += 30;
		}

		if (canvas != null && !canvas.isDisposed() && canvas.isVisible()) {
			canvas.setCurrent();
			try {
				GLContext.useContext(canvas);
			} catch (LWJGLException e) {
				logger.logError(e.getMessage(), e, Model3d.class);
			}
			Rectangle bounds = canvas.getBounds();
			float fAspect = (float) bounds.width / (float) bounds.height;

			GL11.glViewport(0, 0, bounds.width, bounds.height);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GLU.gluPerspective(40.0f, fAspect, 0.5f, 150.0f);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();

			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glClearColor(.3f, .5f, .8f, 1.0f);
			GL11.glLoadIdentity();

			if (zoomSlider == null)
				GL11.glTranslatef(0.0f, 0.0f, -60.0f);
			else
				GL11.glTranslatef(0.0f, 0.0f,
						-60.0f + (float) (zoomSlider.getSelection() - 49));

			GL11.glRotatef(90, 0.0f, 0.0f, 1.0f);
			GL11.glRotatef(90, 0.0f, 1.0f, 0.0f);

			GL11.glRotatef((int) rpy.getRoll(), 1.0f, 0.0f, 0.0f);
			GL11.glRotatef(-(int) rpy.getPitch(), 0.0f, 1.0f, 0.0f);
			GL11.glRotatef(-(int) rpy.getHeading()
					+ (enableXMode ? 45.0f : 0.0f), 0.0f, 0.0f, 1.0f);

			drawDrone();
			canvas.swapBuffers();
		}

	}

	@Override
	public void setFocus() {

	}
}
