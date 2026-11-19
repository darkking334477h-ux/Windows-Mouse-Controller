package com.qevnora.windowsmouse;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MouseOverlayService extends Service {
    private WindowManager wm;
    private PointerView pointer;
    private LinearLayout pad;
    private WindowManager.LayoutParams pointerLp, padLp;
    private float cursorX = 300, cursorY = 400;
    private float downX, downY, startX, startY;
    private boolean moved;

    @Override public void onCreate() {
        super.onCreate();
        wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        int type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        pointer = new PointerView(this);
        pointerLp = new WindowManager.LayoutParams(-1, -1, type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);
        pointerLp.gravity = Gravity.TOP | Gravity.LEFT;
        wm.addView(pointer, pointerLp);

        pad = new LinearLayout(this);
        pad.setOrientation(LinearLayout.VERTICAL);
        pad.setPadding(14, 10, 14, 10);
        pad.setBackgroundColor(0xee18232d);
        TextView heading = label("WINDOWS MOUSE  •  drag pad to move");
        pad.addView(heading);
        TextView surface = label("TOUCHPAD  —  move pointer");
        surface.setBackgroundColor(0xff263846);
        surface.setGravity(Gravity.CENTER);
        pad.addView(surface, new LinearLayout.LayoutParams(-1, 105));
        surface.setOnTouchListener((v, e) -> handlePad(e));
        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        addAction(buttons, "LEFT", () -> click(0));
        addAction(buttons, "RIGHT", () -> click(1));
        addAction(buttons, "DOUBLE", () -> { click(0); new android.os.Handler().postDelayed(() -> click(0), 120); });
        pad.addView(buttons);
        LinearLayout scrolls = new LinearLayout(this);
        addAction(scrolls, "SCROLL ▲", () -> scroll(-1));
        addAction(scrolls, "SCROLL ▼", () -> scroll(1));
        addAction(scrolls, "CLOSE", () -> stopSelf());
        pad.addView(scrolls);
        padLp = new WindowManager.LayoutParams(-1, -2, type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT);
        padLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        wm.addView(pad, padLp);
    }

    private TextView label(String s) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextColor(0xffffffff); t.setTextSize(13);
        t.setPadding(8, 9, 8, 9);
        return t;
    }
    private void addAction(LinearLayout row, String name, Runnable action) {
        TextView t = label(name);
        t.setGravity(Gravity.CENTER);
        t.setBackgroundColor(0xff38566b);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 48, 1);
        lp.setMargins(3, 4, 3, 2);
        row.addView(t, lp);
        t.setOnClickListener(v -> action.run());
    }
    private boolean handlePad(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = e.getRawX(); downY = e.getRawY();
                startX = cursorX; startY = cursorY; moved = false; return true;
            case MotionEvent.ACTION_MOVE:
                float dx = e.getRawX() - downX, dy = e.getRawY() - downY;
                if (Math.abs(dx) + Math.abs(dy) > 4) moved = true;
                cursorX = clamp(startX + dx * 1.7f, 3, getResources().getDisplayMetrics().widthPixels - 4);
                cursorY = clamp(startY + dy * 1.7f, 3, getResources().getDisplayMetrics().heightPixels - 160);
                pointer.invalidate(); return true;
            case MotionEvent.ACTION_UP:
                if (!moved) click(0);
                return true;
        }
        return true;
    }
    private float clamp(float v, float min, float max) { return Math.max(min, Math.min(max, v)); }
    private void click(int button) {
        MouseAccessibilityService a = MouseAccessibilityService.get();
        if (a != null) a.tap(cursorX, cursorY, button == 1 ? 90 : 55);
    }
    private void scroll(int direction) {
        MouseAccessibilityService a = MouseAccessibilityService.get();
        if (a != null) a.swipe(cursorX, cursorY + direction * 110, cursorX, cursorY - direction * 110, 350);
    }
    @Override public int onStartCommand(Intent i, int flags, int id) { return START_STICKY; }
    @Override public void onDestroy() {
        try { if (pointer != null) wm.removeView(pointer); } catch (Exception ignored) {}
        try { if (pad != null) wm.removeView(pad); } catch (Exception ignored) {}
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent intent) { return null; }

    private class PointerView extends View {
        private Paint p = new Paint(3);
        PointerView(Context c) { super(c); setLayerType(View.LAYER_TYPE_SOFTWARE, null); }
        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            c.save();
            c.translate(cursorX, cursorY);
            Path arrow = new Path();
            arrow.moveTo(0, 0); arrow.lineTo(0, 27); arrow.lineTo(7, 21);
            arrow.lineTo(12, 32); arrow.lineTo(17, 30); arrow.lineTo(12, 19);
            arrow.lineTo(22, 18); arrow.close();
            p.setColor(0xffffffff); p.setStyle(Paint.Style.FILL); c.drawPath(arrow, p);
            p.setColor(0xff111111); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(2); c.drawPath(arrow, p);
            c.restore();
        }
    }
}
