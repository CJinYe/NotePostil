package icox.cjy.notepostillibrary.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

/**
 * @author 陈锦业
 * @version $Rev$
 */
public class BaseActivity extends FragmentActivity implements ViewTreeObserver.OnGlobalLayoutListener {
    protected Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initWindow();
//        findViewById(android.R.id.content).getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public void initWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onGlobalLayout() {
        initWindow();
        findViewById(android.R.id.content).getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    protected void lockScreenRotation(int rotation) {

        int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;


        switch (rotation) {

            case Surface.ROTATION_0:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;//1
                break;
            case Surface.ROTATION_90:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;//0
                break;
            case Surface.ROTATION_180:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;//9
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;//1
                break;
            case Surface.ROTATION_270:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;//8
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;//0
                break;
            default:
                break;
        }

        setRequestedOrientation(orientation);

    }
}
