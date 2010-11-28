package org.anddev.andengine.opengl;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.shape.Shape;

/**
 * @author Nicolas Gramlich
 * @since 13:40:51 - 28.11.2010
 */
public interface IRenderHandler {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	
	public void onRender(final Shape pShape, final GL10 pGL, final Camera pCamera);
}
