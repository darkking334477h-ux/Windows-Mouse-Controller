package com.qevnora.windowsmouse;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(22), dp(30), dp(22), dp(20));
        page.setGravity(Gravity.CENTER_HORIZONTAL);
        page.setBackgroundColor(0xff101820);

        TextView title = new TextView(this);
        title.setText("WINDOWS MOUSE");
        title.setTextColor(0xff62e6d5);
        title.setTextSize(25);
        title.setGravity(Gravity.CENTER);
        page.addView(title, new LinearLayout.LayoutParams(-1, dp(60)));

        TextView info = new TextView(this);
        info.setText("Use a Windows-style pointer across Android apps. Enable Accessibility and Display over other apps, then start the touchpad.");
        info.setTextColor(0xffe7edf2);
        info.setTextSize(15);
        info.setPadding(0, dp(12), 0, dp(18));
        page.addView(info);

        addButton(page, "1. Enable Accessibility Service", v ->
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        addButton(page, "2. Allow Display over other apps", v -> {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
                startActivity(i);
            }
        });
        addButton(page, "3. Start Mouse Touchpad", v -> {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())));
                return;
            }
            startService(new Intent(this, MouseOverlayService.class));
        });
        addButton(page, "Stop Mouse", v -> stopService(new Intent(this, MouseOverlayService.class)));

        TextView note = new TextView(this);
        note.setText("Note: Android requires you to turn on the Accessibility service manually. The pointer is an overlay; taps and scrolling are performed through Accessibility.");
        note.setTextColor(0xffaab8c2);
        note.setTextSize(12);
        note.setPadding(0, dp(24), 0, 0);
        page.addView(note);
        setContentView(page);
    }

    private void addButton(LinearLayout parent, String label, android.view.View.OnClickListener click) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setOnClickListener(click);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(54));
        lp.topMargin = dp(9);
        parent.addView(b, lp);
    }
}
