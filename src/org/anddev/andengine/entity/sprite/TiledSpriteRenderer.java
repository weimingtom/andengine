package org.anddev.andengine.entity.sprite;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.shape.BaseRenderer;
import org.anddev.andengine.entity.shape.IRenderer;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.opengl.util.GLHelper;

/**
 * @author Nicolas Gramlich
 * @since 15:03:10 - 28.11.2010
 */
public class TiledSpriteRenderer extends BaseRenderer {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final TiledTextureRegion mTiledTextureRegion;

	// ===========================================================
	// Constructors
	// ===========================================================

	protected TiledSpriteRenderer(final TiledTextureRegion pTiledTextureRegion) {
		this.mTiledTextureRegion = pTiledTextureRegion;
		this.initBlendFunction();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public TiledTextureRegion getTiledTextureRegion() {
		return this.mTiledTextureRegion;
	}
	
	public int getCurrentTileIndex() {
		return this.mTiledTextureRegion.getCurrentTileIndex();
	}

	public void setCurrentTileIndex(final int pTileIndex) {
		this.mTiledTextureRegion.setCurrentTileIndex(pTileIndex);
	}

	public void setCurrentTileIndex(final int pTileColumn, final int pTileRow) {
		this.mTiledTextureRegion.setCurrentTileIndex(pTileColumn, pTileRow);
	}

	public void nextTile() {
		this.mTiledTextureRegion.nextTile();
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onInitRender(final Shape pShape, final GL10 pGL) {
		super.onInitRender(pShape, pGL);
		GLHelper.enableTextures(pGL);
		GLHelper.enableTexCoordArray(pGL);
	}

	@Override
	protected void renderVertices(final Shape pShape, final GL10 pGL, final Camera pCamera) {
		this.mTiledTextureRegion.onApply(pGL);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void initBlendFunction() {
		if(this.mTiledTextureRegion.getTexture().getTextureOptions().mPreMultipyAlpha) {
			this.setBlendFunction(IRenderer.BLENDFUNCTION_SOURCE_PREMULTIPLYALPHA_DEFAULT, IRenderer.BLENDFUNCTION_DESTINATION_PREMULTIPLYALPHA_DEFAULT);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
