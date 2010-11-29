package org.anddev.andengine.entity.shape;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.opengl.util.GLHelper;

/**
 * @author Nicolas Gramlich
 * @since 14:03:05 - 28.11.2010
 */
public abstract class BaseRenderer implements IRenderer {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	private int mSourceBlendFunction = BLENDFUNCTION_SOURCE_DEFAULT;
	private int mDestinationBlendFunction = BLENDFUNCTION_DESTINATION_DEFAULT;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setBlendFunction(final int pSourceBlendFunction, final int pDestinationBlendFunction) {
		this.mSourceBlendFunction = pSourceBlendFunction;
		this.mDestinationBlendFunction = pDestinationBlendFunction;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	protected void onApplyVertices(final Shape pShape, final GL10 pGL) {
		if(GLHelper.EXTENSIONS_VERTEXBUFFEROBJECTS) {
			final GL11 gl11 = (GL11)pGL;

			pShape.getVertexBuffer().selectOnHardware(gl11);
			GLHelper.vertexZeroPointer(gl11);
		} else {
			GLHelper.vertexPointer(pGL, pShape.getVertexBuffer().getFloatBuffer());
		}
	}
	
	protected abstract void renderVertices(final Shape pShape, final GL10 pGL, final Camera pCamera);

	@Override
	public void onRender(final Shape pShape, final GL10 pGL, final Camera pCamera) {
		this.onInitRender(pShape, pGL);

		pGL.glPushMatrix();
		{
			this.onApplyVertices(pShape, pGL);
			this.onApplyTransformations(pShape, pGL);
			this.renderVertices(pShape, pGL, pCamera);
		}
		pGL.glPopMatrix();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected void onInitRender(final Shape pShape, final GL10 pGL) {
		GLHelper.setColor(pGL, pShape.mRed, pShape.mGreen, pShape.mBlue, pShape.mAlpha);

		GLHelper.enableVertexArray(pGL);
		GLHelper.blendFunction(pGL, this.mSourceBlendFunction, this.mDestinationBlendFunction);
	}

	protected void onApplyTransformations(final Shape pShape, final GL10 pGL) {
		this.applyTranslation(pShape, pGL);

		this.applyRotation(pShape, pGL);

		this.applyScale(pShape, pGL);
	}

	protected void applyTranslation(final Shape pShape, final GL10 pGL) {
		pGL.glTranslatef(pShape.mX, pShape.mY, 0);
	}

	protected void applyRotation(final Shape pShape, final GL10 pGL) {
		final float rotation = pShape.mRotation;

		if(rotation != 0) {
			final float rotationCenterX = pShape.mRotationCenterX;
			final float rotationCenterY = pShape.mRotationCenterY;

			pGL.glTranslatef(rotationCenterX, rotationCenterY, 0);
			pGL.glRotatef(rotation, 0, 0, 1);
			pGL.glTranslatef(-rotationCenterX, -rotationCenterY, 0);
		}
	}

	protected void applyScale(final Shape pShape, final GL10 pGL) {
		final float scaleX = pShape.mScaleX;
		final float scaleY = pShape.mScaleY;

		if(scaleX != 1 || scaleY != 1) {
			final float scaleCenterX = pShape.mScaleCenterX;
			final float scaleCenterY = pShape.mScaleCenterY;

			pGL.glTranslatef(scaleCenterX, scaleCenterY, 0);
			pGL.glScalef(scaleX, scaleY, 1);
			pGL.glTranslatef(-scaleCenterX, -scaleCenterY, 0);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
