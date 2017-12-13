package icox.cjy.notepostillibrary.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import icox.cjy.notepostillibrary.R;
import icox.cjy.notepostillibrary.activity.PostilActivity;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-22 16:26
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class PostilSetvice extends Service {

    public static PostilSetvice mPostilSetvice;
    private WindowManager mWindowManager;
    private View mFloatView;
    private String mBookLocation;
    private String mBookName;

    public PostilSetvice() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mPostilSetvice == null)
            mPostilSetvice = this;

        if (mFloatView == null)
            createFloatView();

        mBookLocation = intent.getStringExtra("BookLocation");
        mBookName = intent.getStringExtra("BookName");
        return super.onStartCommand(intent, flags, startId);
    }

    private void createFloatView() {
        WindowManager.LayoutParams windowPatams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
        mFloatView = LayoutInflater.from(getApplication()).inflate(R.layout.popup_music, null);

        windowPatams.type = WindowManager.LayoutParams.TYPE_TOAST;
        windowPatams.format = PixelFormat.RGBA_8888;
        windowPatams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowPatams.y = 0;
        windowPatams.x = 0;
        windowPatams.gravity = Gravity.TOP | Gravity.LEFT;
        windowPatams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
        windowPatams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowPatams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mWindowManager.addView(mFloatView, windowPatams);

        ImageButton button = (ImageButton) mFloatView.findViewById(R.id.popup_button_stop);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(floatViewClickInernt);

                Intent intent = new Intent(getApplication(), PostilActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("BookLocation", mBookLocation);
                intent.putExtra("BookName", mBookName);
                startActivity(intent);
            }
        });
    }

    private Intent floatViewClickInernt = new Intent("FLOAT_CLICK");

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPostilSetvice = null;
        mWindowManager.removeViewImmediate(mFloatView);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
