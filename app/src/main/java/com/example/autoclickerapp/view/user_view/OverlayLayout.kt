package com.example.autoclickerapp.view.user_view


import android.content.*
import android.util.*
import android.view.*
import android.widget.*
import com.example.autoclickerapp.*
import com.example.autoclickerapp.auto_scroll.*
import com.example.autoclickerapp.data.*
import com.example.autoclickerapp.databinding.*
import com.example.autoclickerapp.model.*
import kotlinx.coroutines.*


class OverlayLayout(private val context: Context) {
    companion object {
        private var overlayLayout: OverlayLayout? = null
        private var TAG = "KH_OverlayLayout"
    }

    private var overlayView: View? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private lateinit var binding: OverlayLayoutBinding
    private var isOverlayAdded = false
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f


    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        android.graphics.PixelFormat.TRANSLUCENT
    ).apply {
        // Set the gravity to a specific position, e.g., top left
        gravity = Gravity.TOP or Gravity.START

        // Adjust the x and y properties to move the view out of the screen
        x = -100 // Move it 100 pixels to the left
        y = 800
        //set y to the bottom of the screen
        // Keep it at the top
    }

    init {
        initOverlay()
        overlayLayout = this
    }

    private fun initOverlay() {
        if (overlayView != null) {
            return // Overlay already displayed
        }

        binding = OverlayLayoutBinding.inflate(LayoutInflater.from(context))
        overlayView = binding.root
        binding.runBtn.setOnClickListener {
            if (AutoScrollService.isScrolling()) {
                AutoScrollService.instance!!.stopScrolling()
            } else {
                if (AutoScrollService.instance == null) {
                    Toast.makeText(context, "You didn't get the permission ", Toast.LENGTH_SHORT)
                    return@setOnClickListener
                }
                AutoScrollService.instance!!.scrollScreen()
            }
            rebind()
        }
        binding.closeBtn.setOnClickListener {
            removeOverlay()
            //  MyAccessibilityService.instance?.stopSelf() todo
            Log.d(TAG, "Overlay removed")
        }
        binding.editClick.setOnClickListener {
            binding.root.visibility = View.GONE
            OverlayTouchView.showTouchOverlay(context) { x, y ->
                Toast.makeText(context, "Selected position: x=$x, y=$y", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    DataStoreManager(context).saveClickPosition(pos = Pos(x, y))
                }
                binding.root.visibility = View.VISIBLE
            }
        }

        binding.root.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams.x
                    initialY = layoutParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    layoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                    layoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(overlayView, layoutParams)
                    true
                }

                else -> false
            }
        }
    }

    fun showOverlay() {
        initOverlay()
        if (!isOverlayAdded) {
            windowManager.addView(binding.root, layoutParams)
            isOverlayAdded = true
        }
        rebind()

    }


    private fun removeOverlay() {
        isOverlayAdded = false
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }


    private fun rebind() {
        binding.runBtn.setImageDrawable(
            if (AutoScrollService.isScrolling()) {
                context.getDrawable(R.drawable.baseline_pause_24)
            } else {
                context.getDrawable(R.drawable.circle_down)
            }
        )


    }


}
