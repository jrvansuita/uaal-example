package com.unity.mynativeapp

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Toast
import com.unity.mynativeapp.databinding.CustomLayoutBinding
import com.unity3d.player.IUnityPlayerLifecycleEvents
import com.unity3d.player.MultiWindowSupport
import com.unity3d.player.UnityPlayer

class ContainedActivity : Activity(), IUnityPlayerLifecycleEvents {

    private var toast: Toast? = null
    private val binding by lazy { CustomLayoutBinding.inflate(layoutInflater) }
    private var mUnityPlayer: UnityPlayer? = null

    private fun updateUnityCommandLineArguments(cmdLine: String): String {
        return cmdLine
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cmdLine: String = updateUnityCommandLineArguments(intent.getStringExtra("unity") ?: "")
        intent.putExtra("unity", cmdLine)

        mUnityPlayer = UnityPlayer(this, this)
        binding.unityFrame.addView(mUnityPlayer!!.view)
        setContentView(binding.root)

        mUnityPlayer!!.requestFocus()

        setUpBottomNavigation()
    }


    private fun setUpBottomNavigation() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            toast?.cancel()

            toast = Toast.makeText(this, "${it.title} clicked!", Toast.LENGTH_LONG)
            toast?.show()
            true
        }
    }

    // When Unity player unloaded move task to background
    override fun onUnityPlayerUnloaded() {
        moveTaskToBack(true)
    }

    // Callback before Unity player process is killed
    override fun onUnityPlayerQuitted() {}

    override fun onNewIntent(intent: Intent?) {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent)
        mUnityPlayer!!.newIntent(intent)
    }

    // Quit Unity
    override fun onDestroy() {
        mUnityPlayer!!.destroy()
        super.onDestroy()
    }

    // If the activity is in multi window mode or resizing the activity is allowed we will use
    // onStart/onStop (the visibility callbacks) to determine when to pause/resume.
    // Otherwise it will be done in onPause/onResume as Unity has done historically to preserve
    // existing behavior.
    override fun onStop() {
        super.onStop()
        if (!MultiWindowSupport.getAllowResizableWindow(this)) return
        mUnityPlayer!!.pause()
    }

    override fun onStart() {
        super.onStart()
        if (!MultiWindowSupport.getAllowResizableWindow(this)) return
        mUnityPlayer!!.resume()
    }

    // Pause Unity
    override fun onPause() {
        super.onPause()
        MultiWindowSupport.saveMultiWindowMode(this)
        if (MultiWindowSupport.getAllowResizableWindow(this)) return
        mUnityPlayer!!.pause()
    }

    // Resume Unity
    override fun onResume() {
        super.onResume()
        if (MultiWindowSupport.getAllowResizableWindow(this) && !MultiWindowSupport.isMultiWindowModeChangedToTrue(
                this
            )
        ) return
        mUnityPlayer!!.resume()
    }

    // Low Memory Unity
    override fun onLowMemory() {
        super.onLowMemory()
        mUnityPlayer!!.lowMemory()
    }

    // Trim Memory Unity
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer!!.lowMemory()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mUnityPlayer!!.configurationChanged(newConfig)
    }

    // Notify Unity of the focus change.
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mUnityPlayer!!.windowFocusChanged(hasFocus)
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.action == KeyEvent.ACTION_MULTIPLE) mUnityPlayer!!.injectEvent(event) else super.dispatchKeyEvent(
            event
        )
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return mUnityPlayer!!.injectEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return mUnityPlayer!!.injectEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mUnityPlayer!!.injectEvent(event)
    }

    /*API12*/
    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        return mUnityPlayer!!.injectEvent(event)
    }

}