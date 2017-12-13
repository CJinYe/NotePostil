package icox.cjy.notepostillibrary.dialog;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

import icox.cjy.notepostillibrary.R;
import icox.cjy.notepostillibrary.service.MusicService;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-18 9:22
 */
public class MusicDialog extends BaseDialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private MusicService mMusicService;
    private File mFile;
    private SeekBar mSeekBar;
    private final Context mContext;
    private ImageButton mButStart;
    private TextView mTvProgress;
    private TextView mTvDuration;

    public MusicDialog(@NonNull Context context, File file) {
        super(context);
        mFile = file;
        mContext = context;
    }

    public MusicDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_music);
        setCanceledOnTouchOutside(false);
        mSeekBar = (SeekBar) findViewById(R.id.dialog_music_seek);
        ImageButton butBackgroundStrat = (ImageButton) findViewById(R.id.dialog_music_back_start);
        ImageButton butStop = (ImageButton) findViewById(R.id.dialog_music_stop);
        mButStart = (ImageButton) findViewById(R.id.dialog_music_start);
        ImageButton butGoBack = (ImageButton) findViewById(R.id.dialog_music_go_back);

        mTvDuration = (TextView) findViewById(R.id.dialog_music_seek_tv_duration);
        mTvProgress = (TextView) findViewById(R.id.dialog_music_seek_tv_progress);

        butBackgroundStrat.setOnClickListener(this);
        butGoBack.setOnClickListener(this);
        butStop.setOnClickListener(this);
        mButStart.setOnClickListener(this);

        mMusicService = MusicService.mMusicService;
        if (mMusicService == null) {
            Intent intent = new Intent(getContext(), MusicService.class);
            mContext.startService(intent);
            mMusicService = MusicService.mMusicService;
        }

        mHandler.sendEmptyMessageDelayed(0, 1000);

        mSeekBar.setOnSeekBarChangeListener(this);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("TEST_", "mMusicService = " + mMusicService);
            mMusicService = MusicService.mMusicService;
            if (mMusicService == null) {
                Intent intent = new Intent(getContext(), MusicService.class);
                mContext.startService(intent);
                mHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                if (mFile != null)
                    mMusicService.setDataSource(mFile);
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("MUSIC_TIME");
                intentFilter.addAction("MUSIC_COMPLETION");
                mMusicTimeBroadcast = new MusicTimeBroadcast();
                mContext.registerReceiver(mMusicTimeBroadcast, intentFilter);
                mMusicService.hideFloatView(true);
                setStartView(mMusicService.isPlaying());
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(getContext(), MusicService.class);
            mContext.stopService(intent);
            dismiss();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_music_back_start) {
            mMusicService.hideFloatView(false);
            mMusicService.startMusic();
            dismiss();
        } else if (id == R.id.dialog_music_stop) {
            mMusicService.stopMusic();
        } else if (id == R.id.dialog_music_go_back) {
            Intent intent = new Intent(getContext(), MusicService.class);
            mContext.stopService(intent);
            dismiss();
        } else if (id == R.id.dialog_music_start) {
            boolean playing = mMusicService.pauseMusic();
            setStartView(playing);
        }
    }

    private void setStartView(boolean playing) {
        if (playing)
            mButStart.setImageResource(R.drawable.selector_dialog_music_but_pause);
        else
            mButStart.setImageResource(R.drawable.selector_dialog_music_but_start);
    }

    private BroadcastReceiver mMusicTimeBroadcast;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mMusicService.seekTo(progress);
        //        if (!mMusicService.isPlaying())
        //            mMusicService.startMusic();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class MusicTimeBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("MUSIC_TIME")) {
                int duration = intent.getIntExtra("duration", 0);
                int current = intent.getIntExtra("current", 0);
                mSeekBar.setMax(duration);
                mSeekBar.setProgress(current);

                mTvDuration.setText(formatTime(duration));
                mTvProgress.setText(formatTime(current));

            } else if (intent.getAction().equals("MUSIC_COMPLETION")) {
                Intent intentMusic = new Intent(getContext(), MusicService.class);
                mContext.stopService(intentMusic);
                mButStart.setImageResource(R.drawable.selector_dialog_music_but_start);
                dismiss();
            }
        }
    }

    /**
     * 格式化时间，将其变成00:00的形式
     */
    public String formatTime(long time) {
        long secondSum = time / 1000;
        long minute = secondSum / 60;
        long second = secondSum % 60;
        String result = "";
        if (minute < 10) {
            result = "0";
        }
        result = result + minute + ":";
        if (second < 10) {
            result = result + "0";
        }
        result = result + second;
        return result;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mMusicTimeBroadcast != null)
            getContext().unregisterReceiver(mMusicTimeBroadcast);
    }
}
