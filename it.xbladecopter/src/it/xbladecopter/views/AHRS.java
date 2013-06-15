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

import java.awt.Font;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class AHRS extends BaseViewPart {
	public static final String ID = "it.xbladecopter.ahrs";
	private LogManager logger = LogManager.getInstance();
	private static GLCanvas canvas;
	static private FloatBuffer working = BufferUtils.createFloatBuffer(4);

	static float no_mat[] = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	static float mat_ambient[] = new float[] { 0.0f, 0.0f, 1.0f, 1.0f };
	static float mat_ambient_colorO[] = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	static float mat_ambient_colorV[] = new float[] { 1.0f, 1.0f, 0.0f, 1.0f };
	static float mat_ambient_colorR[] = new float[] { 1.0f, 0.5f, 0.0f, 1.0f };
	static float mat_ambient_colorGlass[] = new float[] { 1.0f, 0.5f, 0.0f,
			1.0f };
	static float mat_diffuseGlass[] = new float[] { 0.0f, 0.0f, 0.8f, 1.0f };
	static float mat_specularGlass[] = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	static float mat_emissionGlass[] = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
	static float high_shininess_Glass[] = new float[] { 100.0f };
	static float mat_diffuse[] = new float[] { 0.1f, 0.5f, 0.8f, 1.0f };
	static float mat_specular[] = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	static float no_diffuse[] = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	static float no_specular[] = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
	static float no_shininess[] = new float[] { 0.0f };
	static float mat_emission_w[] = new float[] { 1.0f, 1.0f, 1.0f, 0.0f };
	static float mat_emission_y[] = new float[] { 1.0f, 1.0f, 0.0f, 0.0f };
	static float low_shininess[] = new float[] { 5.0f };
	static float high_shininess[] = new float[] { 100.0f };
	static float mat_emission[] = new float[] { 0.3f, 0.2f, 0.2f, 0.0f };
	static float mat_emissionR[] = new float[] { 0.8f, 0.3f, 0.0f, 0.0f };

	private static UnicodeFont font;

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

	static void createarc(float k, float r, float h, float s, int start, int end) {
		GL11.glBegin(GL11.GL_QUADS);
		for (int i = start; i < end; i++) {
			float x = (float) (r * Math.cos(i * Math.PI / 180.0f) - h);
			float y = (float) (r * Math.sin(i * Math.PI / 180.0f) + k);
			GL11.glVertex3f(x + k, s, y - h);
		}
		GL11.glEnd();
	}

	/**
	 * Enables orthographic view.
	 */
	static float scale = 0.01f;

	public static void startRenderText() {
		GL11.glPushMatrix();

		GL11.glScalef(scale, -scale, 1);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glEnable(GL11.GL_BLEND); // required
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); // required
	}

	/**
	 * Disables the orthographic view.
	 */
	public static void endRenderText() {
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix(); // Pop The Matrix
	}

	public static void renderString(String text, float x, float y, Color color) {
		font.drawString(x / scale, y / scale, text, color);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void createPartControl(Composite parent) {
		logger.logInfo("Starting AHRS...", this.getClass());

		GridData layout = new GridData(GridData.FILL_BOTH);

		final Composite rpyComp = new Composite(parent, SWT.NONE);
		rpyComp.setLayout(new FillLayout());
		rpyComp.setLayoutData(layout);

		GLData data = new GLData();
		data.doubleBuffer = true;
		canvas = new GLCanvas(rpyComp, SWT.DOUBLE_BUFFERED, data);

		canvas.setCurrent();
		try {
			GLContext.useContext(canvas);
		} catch (LWJGLException e) {
			logger.logError(e.getMessage(), e, this.getClass());
		}

		canvas.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				updateAHRS();
			}
		});
		GL11.glClearColor(.3f, .5f, .8f, 1.0f);
		GL11.glColor3f(1.0f, 0.0f, 0.0f);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glClearDepth(1.0);
		GL11.glLineWidth(2);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		initLighting();

		Font awtFont = new Font("Courier", Font.PLAIN, 40);
		font = new UnicodeFont(awtFont);
		font.getEffects().add(new ColorEffect(java.awt.Color.white));
		font.addAsciiGlyphs();
		try {
			font.loadGlyphs();
		} catch (SlickException e) {
			logger.logError(e.getMessage(), e, this.getClass());
		}
	}

	@Override
	public void setFocus() {
	}

	private static void updateAHRS() {

		if (!canvas.isDisposed() && canvas.isVisible()) {

			canvas.setCurrent();
			try {
				GLContext.useContext(canvas);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
			float roll = -rpy.getRoll();
			float pitch = rpy.getPitch();
			float heading = rpy.getHeading();

			Rectangle bounds = canvas.getBounds();
			float fAspect = (float) bounds.width / (float) bounds.height;
			GL11.glViewport(0, 0, bounds.width, bounds.height);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GLU.gluPerspective(50.0f, fAspect, 0.5f, 40.0f);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();

			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glClearColor(.3f, .5f, .8f, 1.0f);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0f, 0.0f, -10.0f);

			GL11.glRotatef(roll, 0.0f, 0.0f, 1.0f);
			GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);

			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

			/*
			 * Roll Grid
			 */

			GL11.glPushMatrix();
			GL11.glTranslatef(0.0f, 0.0f, 0.0f);
			GL11.glRotatef(roll, 0.0f, 0.0f, -1.0f);
			set(mat_ambient_colorO, no_diffuse, no_specular, no_shininess,
					mat_emission_w);

			GL11.glBegin(GL11.GL_LINES);
			for (int i = 0; i <= 360; i += 10.0f) {
				double angle = Math.PI * i / 180.0;
				GL11.glVertex3f((float) (4.0f * Math.cos(angle)),
						(float) (4.0f * Math.sin(angle)), 0.0f);
				GL11.glVertex3f((float) (3.8f * Math.cos(angle)),
						(float) (3.8f * Math.sin(angle)), 0.0f);
			}
			GL11.glEnd();
			GL11.glPopMatrix();

			/*
			 * ROLL Indicator
			 */

			GL11.glPushMatrix();
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(-4.0f, 0.0f, 0);
			GL11.glVertex3f(4.0f, 0.0f, 0);
			GL11.glEnd();
			GL11.glPopMatrix();

			/*
			 * Heading Indicator
			 */

			GL11.glPushMatrix();
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(0.0f, 0.0f, -4.0f);
			GL11.glVertex3f(0.0f, 0.0f, 4.0f);
			GL11.glEnd();
			GL11.glPopMatrix();

			/*
			 * Pitch Grid
			 */

			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0f, 0.0f, 0.0f);
			GL11.glRotatef(pitch, -1.0f, 0.0f, 0.0f);
			set(mat_ambient_colorV, no_diffuse, no_specular, no_shininess,
					mat_emission_y);
			float r = 3.1f;
			for (float theta = -30.0f; theta <= 30.0f; theta += 10.0f)
				createarc(r, (float) (r * Math.cos(theta * Math.PI / 180.0f)),
						r, (float) (r * Math.sin(theta * Math.PI / 180.0f)),
						70, 110);
			GL11.glPopMatrix();

			/*
			 * Draw Spheres
			 */
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

			GL11.glPushMatrix(); // Push The Matrix Onto The Stack
			GL11.glTranslatef(0.0f, -0.01f, 0.0f);
			set(mat_ambient_colorR, mat_diffuse, mat_specular, high_shininess,
					mat_emissionR);
			new Sphere().draw(3.0f, 40, 40);
			GL11.glPopMatrix(); // Pop The Matrix Off The Stack

			GL11.glPushMatrix();
			GL11.glTranslatef(0.0f, 0.0f, 0.0f);
			set(mat_ambient, mat_diffuse, mat_specular, high_shininess,
					mat_emission);
			Sphere sphere = new Sphere();
			sphere.draw(3.0f, 40, 40);
			GL11.glPopMatrix();

			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			GL11.glPushMatrix();
			set(mat_ambient_colorO, no_diffuse, no_specular, no_shininess,
					mat_emission_w);
			createarc(3.0f, 3.0f, 3.0f, 0.0f, 0, 360);
			GL11.glPopMatrix();

			/*
			 * Draw Heading indicator
			 */
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
			GL11.glPushMatrix();
			set(mat_ambient_colorGlass, mat_diffuseGlass, mat_specularGlass,
					high_shininess_Glass, mat_emissionGlass);
			GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
			GL11.glRotatef(heading, 0.0f, 0.0f, 1.0f);
			GL11.glTranslatef(0.0f, 0.0f, -0.1f);
			Cylinder cylinder = new Cylinder();
			cylinder.draw(4.0f, 4.0f, 0.2f, 36, 36);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();

			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			GL11.glPushMatrix();
			set(mat_ambient_colorO, no_diffuse, no_specular, no_shininess,
					mat_emission_w);
			GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
			GL11.glRotatef(heading + 45, 0.0f, 0.0f, 1.0f);
			GL11.glTranslatef(0.0f, 0.0f, -0.1f);
			cylinder = new Cylinder();
			cylinder.draw(4.0f, 4.0f, 0.2f, 36, 1);
			GL11.glPopMatrix();

			/*
			 * Pitch values
			 */
			GL11.glPushMatrix();
			GL11.glRotatef(pitch, -1.0f, 0.0f, 0.0f);
			startRenderText();
			for (float theta = -30.0f; theta <= 30.0f; theta += 10.0f) {
				float value = theta;
				float offsetX = String.valueOf(value).length() * 0.1f;
				float offsetY = 0.5f;
				float scale = 0.8f;

				GL11.glPushMatrix();
				GL11.glScalef(scale, scale, 1.0f);
				GL11.glTranslatef(
						0.0f,
						0.0f,
						(float) ((r + 0.2) * Math.cos(theta * Math.PI / 180.0f)));

				renderString(String.valueOf(value), -offsetX, (1.0f / scale)
						* (float) (r * Math.sin(theta * Math.PI / 180.0f))
						- offsetY, Color.white);
				GL11.glPopMatrix();
			}
			endRenderText();
			GL11.glPopMatrix();

			/*
			 * Heading values
			 */
			GL11.glPushMatrix();
			GL11.glRotatef(heading, 0.0f, 1.0f, 0.0f);
			startRenderText();
			for (int theta = 0; theta < 360; theta += 45) {
				String text = new String();

				GL11.glPushMatrix();
				float z = (float) (4.2 * Math.cos(theta * Math.PI / 180.0f));
				float x = (float) (4.2 * Math.sin(theta * Math.PI / 180.0f));
				GL11.glTranslatef(x, 0, z);

				switch (theta) {
				case 0:
					text = "N";
					break;
				case 45:
					text = "NE";
					break;
				case 90:
					text = "E";
					break;
				case 135:
					text = "SE";
					break;
				case 180:
					text = "S";
					break;
				case 225:
					text = "NO";
					break;
				case 270:
					text = "O";
					break;
				case 315:
					text = "SO";
					break;
				}
				float offsetX = text.length() * 0.1f;
				float offsetY = 0.3f;

				renderString(text, x - offsetX, -offsetY, Color.yellow);

				GL11.glPopMatrix();

			}
			endRenderText();
			GL11.glPopMatrix();

			/*
			 * Roll values
			 */
			GL11.glTranslatef(0.0f, 0.0f, 0.0f);
			GL11.glRotatef(roll, 0.0f, 0.0f, -1.0f);
			startRenderText();
			for (int i = 0; i < 360; i += 30.0f) {
				double angle = Math.PI * i / 180.0;
				int value = i;

				if (value > 180)
					value -= 360;

				float offsetX = String.valueOf(value).length() * 0.2f;
				float offsetY = 0.3f;

				if (Math.abs(value) < 90)
					offsetX = 0;

				renderString(String.valueOf(value),
						(float) (4.2f * Math.cos(angle)) - offsetX,
						(float) (4.2f * Math.sin(angle)) - offsetY, Color.white);
			}
			endRenderText();
			GL11.glPopMatrix();

			canvas.swapBuffers();
		}
	}

	public static void redraw() {
		if (canvas != null && !canvas.isDisposed() && canvas.isVisible())
			updateAHRS();
	}
}
