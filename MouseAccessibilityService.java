package com.qevnora.windowsmouse;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

public class MouseAccessibilityService extends AccessibilityService {
    private static MouseAccessibilityService instance;
    public static MouseAccessibilityService get() { return instance; }

    @Override public void onServiceConnected() { super.onServiceConnected(); instance = this; }
    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}
    @Override public void onDestroy() { if (instance == this) instance = null; super.onDestroy(); }

    public void tap(float x, float y, long duration) {
        if (Build.VERSION.SDK_INT < 24) return;
        Path p = new Path();
        p.moveTo(x, y);
        GestureDescription.StrokeDescription stroke =
            new GestureDescription.StrokeDescription(p, 0, Math.max(40, duration));
        dispatchGesture(new GestureDescription.Builder().addStroke(stroke).build(), null, null);
    }

    public void swipe(float x1, float y1, float x2, float y2, long duration) {
        if (Build.VERSION.SDK_INT < 24) return;
        Path p = new Path();
        p.moveTo(x1, y1);
        p.lineTo(x2, y2);
        GestureDescription.StrokeDescription stroke =
            new GestureDescription.StrokeDescription(p, 0, Math.max(120, duration));
        dispatchGesture(new GestureDescription.Builder().addStroke(stroke).build(), null, null);
    }
}
