package com.example.autoclickerapp.auto_scroll

import android.accessibilityservice.*
import android.graphics.*
import android.util.*
import android.view.accessibility.*
import android.widget.*
import com.example.autoclickerapp.Constants.scrollDelay
import com.example.autoclickerapp.data.*
import com.example.autoclickerapp.model.*
import kotlinx.coroutines.*

class AutoScrollService: AccessibilityService() {

    companion object {
        var instance: AutoScrollService? = null
        private var job: Job? = null
        const val TAG = "MyAccessibilityService"
        fun isScrolling(): Boolean {
            return job?.isActive ?: false
        }

        var pos: Pos? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        // Configure the service to listen for specific event types
        val info = AccessibilityServiceInfo().apply {
            eventTypes =
                AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_VIEW_SCROLLED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 100
            packageNames = null // null means all apps
        }

        serviceInfo = info
        CoroutineScope(Dispatchers.IO).launch {
            DataStoreManager(this@AutoScrollService).clickPositionFlow.collect {
                pos = it
                Log.d(TAG, "Click position: $it")
            }
        }
    }
    /**
     * get array of indexes of that node
     */
    fun getViewTreeTrace(node: AccessibilityNodeInfo): Array<Int> {

        val trace = mutableListOf<Int>()
        var currentNode: AccessibilityNodeInfo? = node

        while (currentNode != null) {
            val parent = currentNode.parent
            if (parent != null) {
                for (i in 0 until parent.childCount) {
                    if (parent.getChild(i) == currentNode) {
                        trace.add(0, i)
                        break
                    }
                }
            }
            currentNode = parent
        }

        return trace.toTypedArray()
    }


    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            val node = event.source ?: return

            val viewIndex = getViewTreeTrace(node)
            Log.d("ButtonInfo", "Clicked: id=${viewIndex.contentToString()}")

            // Store this info somewhere if needed
        }
    }

    fun clickAt(x: Int, y: Int) {
        val path = Path().apply {
            moveTo(x.toFloat(), y.toFloat())
        }

        val gestureBuilder = GestureDescription.Builder()
            .addStroke(
                GestureDescription.StrokeDescription(path, 0, 50)
            )

        dispatchGesture(gestureBuilder.build(), object: GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                Log.d("Gesture", "✅ Click at ($x, $y) completed")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.d("Gesture", "❌ Click at ($x, $y) cancelled")
            }
        }, null)
    }

    fun scrollScreen() {

        if (isScrolling()) {
            return
            Log.d(TAG, "Job already running")
        }
        job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(scrollDelay)
                val node = rootInActiveWindow
                if (node != null) {
                    scrollNode(node)
                    Log.d(TAG, "-------")
                }
                pos?.let {
                    clickAt(it.x, it.y)
                }
            }
        }
    }

    private fun scrollNode(node: AccessibilityNodeInfo): Boolean {
        if (node.actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD)) {
            val performed = node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
            Log.d(TAG, "${node.windowId}: $performed")
            return false
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null && scrollNode(child)) {
                return false
            }
        }

        return false
    }

    fun stopScrolling() {
        job?.cancel()

    }
}
