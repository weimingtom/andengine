package org.anddev.andengine.entity.text;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.shape.BaseRenderer;
import org.anddev.andengine.entity.shape.IRenderer;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.opengl.buffer.BufferObjectManager;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.buffer.TextTextureBuffer;
import org.anddev.andengine.opengl.util.GLHelper;
import org.anddev.andengine.opengl.vertex.TextVertexBuffer;
import org.anddev.andengine.util.HorizontalAlign;
import org.anddev.andengine.util.StringUtils;

/**
 * @author Nicolas Gramlich
 * @since 18:45:22 - 28.11.2010
 */
public class TextRenderer extends BaseRenderer {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final TextTextureBuffer mTextTextureBuffer;

	private String mText;
	private String[] mLines;
	private int[] mWidths;

	private final Font mFont;

	private int mMaximumLineWidth;

	protected final int mCharactersMaximum;
	protected final int mVertexCount;

	// ===========================================================
	// Constructors
	// ===========================================================

	public TextRenderer(final Font pFont, final String pText) {
		this(pFont, pText, HorizontalAlign.LEFT);
	}

	public TextRenderer(final Font pFont, final String pText, final HorizontalAlign pHorizontalAlign) {
		this(pFont, pText, pHorizontalAlign, pText.length() - StringUtils.countOccurrences(pText, '\n'));
	}

	protected TextRenderer(final Font pFont, final String pText, final HorizontalAlign pHorizontalAlign, final int pCharactersMaximum) {
		super(new TextVertexBuffer(pCharactersMaximum, pHorizontalAlign, GL11.GL_STATIC_DRAW));

		this.mCharactersMaximum = pCharactersMaximum;
		this.mVertexCount = TextVertexBuffer.VERTICES_PER_CHARACTER * this.mCharactersMaximum;

		this.mTextTextureBuffer = new TextTextureBuffer(2 * this.mVertexCount, GL11.GL_STATIC_DRAW);
		BufferObjectManager.getActiveInstance().loadBufferObject(this.mTextTextureBuffer);
		this.mFont = pFont;

		this.updateText(pText);

		this.initBlendFunction();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	public int getCharacterCount() {
		return this.mCharactersMaximum;
	}

	@Override
	public TextVertexBuffer getVertexBuffer() {
		return (TextVertexBuffer)super.getVertexBuffer();
	}

	@Override
	protected void onInitRender(final Shape pShape, final GL10 pGL) {
		super.onInitRender(pShape, pGL);
		GLHelper.enableTextures(pGL);
		GLHelper.enableTexCoordArray(pGL);
	}

	@Override
	protected void renderVertices(final Shape pShape, final GL10 pGL, final Camera pCamera) {
		this.applyTexture(pGL);
		pGL.glDrawArrays(GL10.GL_TRIANGLES, 0, this.mVertexCount);
	}

	@Override
	protected void onUpdateVertexBuffer() {
		final Font font = this.mFont;
		if(font != null) {
			this.getVertexBuffer().update(font, this.mMaximumLineWidth, this.mWidths, this.mLines);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected void updateText(final String pText) {
		this.mText = pText;
		final Font font = this.mFont;

		this.mLines = StringUtils.split(this.mText, '\n', this.mLines);
		final String[] lines = this.mLines;

		final int lineCount = lines.length;
		final boolean widthsReusable = this.mWidths != null && this.mWidths.length == lineCount;
		if(widthsReusable == false) {
			this.mWidths = new int[lineCount];
		}
		final int[] widths = this.mWidths;

		int maximumLineWidth = 0;

		for (int i = lineCount - 1; i >= 0; i--) {
			widths[i] = font.getStringWidth(lines[i]);
			maximumLineWidth = Math.max(maximumLineWidth, widths[i]);
		}
		this.mMaximumLineWidth = maximumLineWidth;

		super.mWidth = this.mMaximumLineWidth;
		final float width = super.mWidth;
		super.mBaseWidth = width;

		super.mHeight = lineCount * font.getLineHeight() + (lineCount - 1) * font.getLineGap();
		final float height = super.mHeight;
		super.mBaseHeight = height;

		this.mRotationCenterX = width * 0.5f;
		this.mRotationCenterY = height * 0.5f;

		this.mScaleCenterX = this.mRotationCenterX;
		this.mScaleCenterY = this.mRotationCenterY;

		this.mTextTextureBuffer.update(font, lines);
		this.updateVertexBuffer();
	}

	private void initBlendFunction() {
		if(this.mFont.getTexture().getTextureOptions().mPreMultipyAlpha) {
			this.setBlendFunction(BLENDFUNCTION_SOURCE_PREMULTIPLYALPHA_DEFAULT, BLENDFUNCTION_DESTINATION_PREMULTIPLYALPHA_DEFAULT);
		}
	}

	private void applyTexture(final GL10 pGL) {
		if(GLHelper.EXTENSIONS_VERTEXBUFFEROBJECTS) {
			final GL11 gl11 = (GL11)pGL;

			this.mTextTextureBuffer.selectOnHardware(gl11);

			GLHelper.bindTexture(pGL, this.mFont.getTexture().getHardwareTextureID());
			GLHelper.texCoordZeroPointer(gl11);
		} else {
			GLHelper.bindTexture(pGL, this.mFont.getTexture().getHardwareTextureID());
			GLHelper.texCoordPointer(pGL, this.mTextTextureBuffer.getFloatBuffer());
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
