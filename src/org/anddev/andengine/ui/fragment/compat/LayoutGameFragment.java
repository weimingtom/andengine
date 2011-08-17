package org.anddev.andengine.ui.fragment.compat;

import org.anddev.andengine.opengl.view.RenderSurfaceView;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * <p>Fragment implementation of {@link LayoutGameActivity}</p>
 * 
 * <p>This implementation uses the {@link android.support.v4.app.Fragment} from the <a href="http://developer.android.com/sdk/compatibility-library.html">Compatibility Package</a>.
 * To use this class, you must include the Android Compatibility Package in your project.
 * If you want to use the native Android 3.0 (Honeycomb) support for Fragments, use {@link org.anddev.andengine.ui.fragment.LayoutGameFragment}.</p>
 *
 * <p>(c) 2011 Nicolas Gramlich<br>(c) 2011 Zynga Inc.</p>
 * 
 * @author Nicolas Gramlich
 * @author Scott Kennedy
 * @since 09:20:00 - 05.08.2010
 */
public abstract class LayoutGameFragment extends BaseGameFragment {
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

        protected abstract int getLayoutID();
        protected abstract int getRenderSurfaceViewID();

        @Override
        protected View createView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
            final View view = inflater.inflate(getLayoutID(), container);
            
            this.mRenderSurfaceView = (RenderSurfaceView) view.findViewById(getRenderSurfaceViewID());

            this.mRenderSurfaceView.setEGLConfigChooser(false);
            this.mRenderSurfaceView.setRenderer(this.mEngine);
            
            return view;
        }

        // ===========================================================
        // Methods
        // ===========================================================

        // ===========================================================
        // Inner and Anonymous Classes
        // ===========================================================
}
