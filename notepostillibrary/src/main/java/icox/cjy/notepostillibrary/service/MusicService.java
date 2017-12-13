package icox.cjy.notepostillibrary.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;

import icox.cjy.notepostillibrary.R;
import icox.cjy.notepostillibrary.dialog.MusicDialog;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-17 18:36
 */
public class MusicService extends Service {

    public MediaPlayer mMediaPlayer;
    public static MusicService mMusicService;
    private WindowManager mWindowManager;
    private View mFloatView;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMusicService = this;
        if (mMediaPlayer == null)
            mMediaPlayer = new MediaPlayer();
        if (mFloatView == null)
            createFloatView();
        mHandler.sendEmptyMessageDelayed(0, 1);

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mMediaPlayer != null)
                    sendBroadcast(mMusicCompletionIntent);
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public MusicService() {

    }

    private Intent mMusicTimeIntent = new Intent("MUSIC_TIME");
    private Intent mMusicCompletionIntent = new Intent("MUSIC_COMPLETION");
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mMediaPlayer != null) {
                int duration = mMediaPlayer.getDuration();
                int current = mMediaPlayer.getCurrentPosition();
                mMusicTimeIntent.putExtra("duration", duration);
                mMusicTimeIntent.putExtra("current", current);
                sendBroadcast(mMusicTimeIntent);
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    private void createFloatView() {
        WindowManager.LayoutParams windowPatams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
        mFloatView = LayoutInflater.from(getApplication()).inflate(R.layout.popup_music, null);

        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getRealMetrics(metrics);
        windowPatams.type = WindowManager.LayoutParams.TYPE_TOAST;
        windowPatams.format = PixelFormat.RGBA_8888;
        windowPatams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowPatams.y = -100;
        windowPatams.x = metrics.widthPixels;
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
                MusicDialog musicDialog = new MusicDialog(getApplication());
                musicDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                musicDialog.show();
            }
        });
    }

    public void hideFloatView(boolean hide) {
        if (hide)
            mFloatView.setVisibility(View.GONE);
        else
            mFloatView.setVisibility(View.VISIBLE);
    }

    public void setDataSource(File file) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(file.getPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sert() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                }
            });
        }
    }

    public void startMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void seekTo(int time) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(time);
        }
    }

    public int getDuration() {
        if (mMediaPlayer != null)
            return mMediaPlayer.getDuration();
        else
            return 0;
    }

    public boolean pauseMusic() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                return false;
            } else {
                mMediaPlayer.start();
                return true;
            }
        }
        return false;
    }

    public void stopMusic() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            try {
                mMediaPlayer.prepare();
                mMediaPlayer.seekTo(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if (mWindowManager != null && mFloatView != null)
            mWindowManager.removeViewImmediate(mFloatView);

        mMusicService = null;
    }


}
