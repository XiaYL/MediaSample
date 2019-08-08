package net.luculent.mediasample.tools;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

/**
 * author xiayanlei
 */
public class ActivityUtils {

    public static void fullScreen(Activity activity) {
        //取消标题
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }
}
