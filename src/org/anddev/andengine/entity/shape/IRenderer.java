package org.anddev.andengine.entity.shape;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.Camera;

/**
 * @author Nicolas Gramlich
 * @since 13:40:51 - 28.11.2010
 */
public interface IRenderer {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final int BLENDFUNCTION_SOURCE_DEFAULT = GL10.GL_SRC_ALPHA;
	public static final int BLENDFUNCTION_DESTINATION_DEFAULT = GL10.GL_ONE_MINUS_SRC_ALPHA;

	public static final int BLENDFUNCTION_SOURCE_PREMULTIPLYALPHA_DEFAULT = GL10.GL_ONE;
	public static final int BLENDFUNCTION_DESTINATION_PREMULTIPLYALPHA_DEFAULT = GL10.GL_ONE_MINUS_SRC_ALPHA;

	// ===========================================================
	// Methods
	// ===========================================================

	public void onRender(final Shape pShape, final GL10 pGL, final Camera pCamera);

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static final class NullRenderer implements IRenderer {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		private static NullRenderer INSTANCE;

		// ===========================================================
		// Constructors
		// ===========================================================

		private NullRenderer(){

		}

		public static NullRenderer getInstance(){
			if(NullRenderer.INSTANCE == null){
				NullRenderer.INSTANCE = new NullRenderer();
			}
			return NullRenderer.INSTANCE;
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		@Override
		public void onRender(final Shape pShape, final GL10 pGL, final Camera pCamera) {
			/* Nothing. */
		}

		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}
