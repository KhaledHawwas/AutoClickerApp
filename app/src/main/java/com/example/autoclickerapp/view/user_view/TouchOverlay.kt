package com.example.autoclickerapp.view.user_view

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

class OverlayTouchView(context: Context) : View(context) {
    private var onPositionSelected: ((Int, Int) -> Unit)? = null
    private val windowManager: WindowManager= context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    companion object{
        private const val TAG = "OverlayTouchView"
        private var overlayTouchView: OverlayTouchView? = null
        fun showTouchOverlay(context: Context, onPositionSelected: (x: Int, y: Int) -> Unit) {
            if (overlayTouchView != null) {
                Log.d(TAG, "Overlay already displayed")
                overlayTouchView!!.setOnPositionSelectedListener(onPositionSelected)
                return
            }
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

            val overlayView = OverlayTouchView(context).apply {
                setOnPositionSelectedListener { x, y ->
                    Log.d("OverlayTouch", "User selected position: x=$x, y=$y")
                    onPositionSelected(x, y)
                }
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            )

            wm.addView(overlayView, params)
        }

    }
    init {
        setBackgroundColor(Color.argb(100, 0, 0, 255)) // semi-transparent blue
    }


    fun setOnPositionSelectedListener(listener: (Int, Int) -> Unit) {
        onPositionSelected = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.rawX.toInt()
            val y = event.rawY.toInt()
            onPositionSelected?.invoke(x, y)
            // Optional: Remove overlay after selection
            windowManager.removeView(this)
            overlayTouchView = null
            return true
        }
        return super.onTouchEvent(event)
    }
}
