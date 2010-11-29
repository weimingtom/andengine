package org.anddev.andengine.entity.shape;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.Camera;

/**
 * @author Nicolas Gramlich
 * @since 14:59:37 - 28.11.2010
 */
public class RectangleRenderer extends BaseRenderer {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void renderVertices(final Shape pShape, final GL10 pGL, final Camera pCamera) {
		pGL.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
