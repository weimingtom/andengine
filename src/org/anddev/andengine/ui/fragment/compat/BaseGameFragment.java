package org.anddev.andengine.ui.fragment.compat;

import org.anddev.andengine.audio.music.MusicManager;
import org.anddev.andengine.audio.sound.SoundManager;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.WakeLockOptions;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.opengl.font.FontManager;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.anddev.andengine.opengl.view.RenderSurfaceView;
import org.anddev.andengine.sensor.accelerometer.AccelerometerSensorOptions;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.sensor.location.ILocationListener;
import org.anddev.andengine.sensor.location.LocationSensorOptions;
import org.anddev.andengine.sensor.orientation.IOrientationListener;
import org.anddev.andengine.sensor.orientation.OrientationSensorOptions;
import org.anddev.andengine.ui.IGameInterface;
import org.anddev.andengine.util.ActivityUtils;
import org.anddev.andengine.util.Debug;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

/**
 * <p>Fragment implementation of {@link BaseGameActivity}</p>
 * 
 * <p>This implementation uses the {@link android.support.v4.app.Fragment} from the <a href="http://developer.android.com/sdk/compatibility-library.html">Compatibility Package</a>.
 * To use this class, you must include the Android Compatibility Package in your project.
 * If you want to use the native Android 3.0 (Honeycomb) support for Fragments, use {@link org.anddev.andengine.ui.fragment.BaseGameFragment}.</p>
 *
 * <p>(c) 2011 Nicolas Gramlich<br>(c) 2011 Zynga Inc.</p>
 * 
 * @author Nicolas Gramlich
 * @author Scott Kennedy
 * @since 14:10:00 - 04.08.2010
 */
public abstract class BaseGameFragment extends Fragment implements IGameInterface {
 // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    protected Engine mEngine;
    private WakeLock mWakeLock;
    protected RenderSurfaceView mRenderSurfaceView;
    protected boolean mHasWindowFocused;
    private boolean mPaused;
    private boolean mGameLoaded;
    
    /**
     * If true, we will pause rendering when the Fragment is paused.  If false, we will not.
     */
    private boolean mShouldPauseRendering = true;
    
    /**
     * If true, we paused mRenderSurfaceView, so we need to resume it.
     */
    private boolean mPausedRendering = true;

    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    public void onCreate(final Bundle pSavedInstanceState) {
            super.onCreate(pSavedInstanceState);
            this.mPaused = true;
            this.mHasWindowFocused = true; // TODO: Tie into Fragment lifecycle

            this.mEngine = this.onLoadEngine();

            this.applyEngineOptions(this.mEngine.getEngineOptions());
    }
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return createView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(this.mPaused && this.mHasWindowFocused) {
            this.doResume();
        }
    }

    // TODO: Get this (or some equivalent) working with Fragments
    /*@Override
    public void onWindowFocusChanged(final boolean pHasWindowFocus) {
            super.onWindowFocusChanged(pHasWindowFocus);

            if(pHasWindowFocus) {
                    if(this.mPaused) {
                            this.doResume();
                    }
                    this.mHasWindowFocused = true;
            } else {
                    if(!this.mPaused) {
                            this.doPause();
                    }
                    this.mHasWindowFocused = false;
            }
    }*/

    @Override
    public void onPause() {
        super.onPause();

        if(!this.mPaused) {
            this.doPause();
        }
    }

    @Override
    public void onDestroy() {
        if (!this.mPausedRendering) {
            this.doPause();
            this.mPausedRendering = true;
        }
        
        super.onDestroy();

        this.mEngine.interruptUpdateThread();

        this.onUnloadResources();
    }

    @Override
    public void onUnloadResources() {
        if(this.mEngine.getEngineOptions().needsMusic()) {
            this.getMusicManager().releaseAll();
        }
        if(this.mEngine.getEngineOptions().needsSound()) {
            this.getSoundManager().releaseAll();
        }
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public Engine getEngine() {
        return this.mEngine;
    }
    
    public TextureManager getTextureManager() {
        return this.mEngine.getTextureManager();
    }
    
    public FontManager getFontManager() {
        return this.mEngine.getFontManager();
    }

    public SoundManager getSoundManager() {
        return this.mEngine.getSoundManager();
    }

    public MusicManager getMusicManager() {
        return this.mEngine.getMusicManager();
    }
    
    /**
     * Sets whether the Fragment should pause its rendering after hitting onPause.
     * @param pShouldPauseRendering true to pause rendering in onPause, false to keep it going
     */
    public void setShouldPauseRendering(final boolean pShouldPauseRendering) {
        this.mShouldPauseRendering = pShouldPauseRendering;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void onResumeGame() {

    }

    @Override
    public void onPauseGame() {

    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void doResume() {
        if(!this.mGameLoaded) {
            this.onLoadResources();
            final Scene scene = this.onLoadScene();
            this.mEngine.onLoadComplete(scene);
            this.onLoadComplete();
            this.mGameLoaded = true;
        }

        this.mPaused = false;
        this.acquireWakeLock(this.mEngine.getEngineOptions().getWakeLockOptions());

        if (this.mPausedRendering) {
            this.mEngine.onResume();
            this.mRenderSurfaceView.onResume();
            this.mPausedRendering = false;
            
            this.mEngine.start();
        }
        this.onResumeGame();
    }

    private void doPause() {
        this.mPaused = true;
        this.releaseWakeLock();
        
        if (this.mShouldPauseRendering) {
            this.mEngine.onPause();
            this.mEngine.stop();
            
            this.mRenderSurfaceView.onPause();
            this.mPausedRendering = true;
        }
        
        this.onPauseGame();
    }

    public void runOnUpdateThread(final Runnable pRunnable) {
        this.mEngine.runOnUpdateThread(pRunnable);
    }
    
    protected View createView(@SuppressWarnings("unused") final LayoutInflater inflater,
            @SuppressWarnings("unused") final ViewGroup container,
            @SuppressWarnings("unused") final Bundle savedInstanceState) {
        this.mRenderSurfaceView = new RenderSurfaceView(getActivity());
        this.mRenderSurfaceView.setEGLConfigChooser(false);
        this.mRenderSurfaceView.setRenderer(this.mEngine);

        this.mRenderSurfaceView.setLayoutParams(createSurfaceViewLayoutParams());
        
        return this.mRenderSurfaceView;
    }

    private void acquireWakeLock(final WakeLockOptions pWakeLockOptions) {
        if(pWakeLockOptions == WakeLockOptions.SCREEN_ON) {
            ActivityUtils.keepScreenOn(getActivity());
        } else {
            final PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
            this.mWakeLock = pm.newWakeLock(pWakeLockOptions.getFlag() | PowerManager.ON_AFTER_RELEASE, "AndEngine");
            try {
                this.mWakeLock.acquire();
            } catch (final SecurityException e) {
                Debug.e("You have to add\n\t<uses-permission android:name=\"android.permission.WAKE_LOCK\"/>\nto your AndroidManifest.xml !", e);
            }
        }
    }

    private void releaseWakeLock() {
        if(this.mWakeLock != null && this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
    }

    private void applyEngineOptions(final EngineOptions pEngineOptions) {
        if(pEngineOptions.isFullscreen()) {
            ActivityUtils.requestFullscreen(getActivity());
        }

        if(pEngineOptions.needsMusic() || pEngineOptions.needsSound()) {
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }

        // TODO: Is this really something we want to do in a Fragment?
        switch(pEngineOptions.getScreenOrientation()) {
            case LANDSCAPE:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case PORTRAIT:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }
    }

    protected static FrameLayout.LayoutParams createSurfaceViewLayoutParams() {
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        return layoutParams;
    }

    protected void enableVibrator() {
        this.mEngine.enableVibrator(getActivity());
    }

    /**
     * @see {@link Engine#enableLocationSensor(Context, ILocationListener, LocationSensorOptions)}
     */
    protected void enableLocationSensor(final ILocationListener pLocationListener, final LocationSensorOptions pLocationSensorOptions) {
        this.mEngine.enableLocationSensor(getActivity(), pLocationListener, pLocationSensorOptions);
    }

    /**
     * @see {@link Engine#disableLocationSensor(Context)}
     */
    protected void disableLocationSensor() {
        this.mEngine.disableLocationSensor(getActivity());
    }

    /**
     * @see {@link Engine#enableAccelerometerSensor(Context, IAccelerometerListener)}
     */
    protected boolean enableAccelerometerSensor(final IAccelerometerListener pAccelerometerListener) {
        return this.mEngine.enableAccelerometerSensor(getActivity(), pAccelerometerListener);
    }

    /**
     * @see {@link Engine#enableAccelerometerSensor(Context, IAccelerometerListener, AccelerometerSensorOptions)}
     */
    protected boolean enableAccelerometerSensor(final IAccelerometerListener pAccelerometerListener, final AccelerometerSensorOptions pAccelerometerSensorOptions) {
        return this.mEngine.enableAccelerometerSensor(getActivity(), pAccelerometerListener, pAccelerometerSensorOptions);
    }

    /**
     * @see {@link Engine#disableAccelerometerSensor(Context)}
     */
    protected boolean disableAccelerometerSensor() {
        return this.mEngine.disableAccelerometerSensor(getActivity());
    }

    /**
     * @see {@link Engine#enableOrientationSensor(Context, IOrientationListener)}
     */
    protected boolean enableOrientationSensor(final IOrientationListener pOrientationListener) {
        return this.mEngine.enableOrientationSensor(getActivity(), pOrientationListener);
    }

    /**
     * @see {@link Engine#enableOrientationSensor(Context, IOrientationListener, OrientationSensorOptions)}
     */
    protected boolean enableOrientationSensor(final IOrientationListener pOrientationListener, final OrientationSensorOptions pLocationSensorOptions) {
        return this.mEngine.enableOrientationSensor(getActivity(), pOrientationListener, pLocationSensorOptions);
    }

    /**
     * @see {@link Engine#disableOrientationSensor(Context)}
     */
    protected boolean disableOrientationSensor() {
        return this.mEngine.disableOrientationSensor(getActivity());
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
