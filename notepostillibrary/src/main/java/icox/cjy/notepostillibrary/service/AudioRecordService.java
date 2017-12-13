package icox.cjy.notepostillibrary.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import icox.cjy.notepostillibrary.R;
import icox.cjy.notepostillibrary.constants.Constants;
import icox.cjy.notepostillibrary.dialog.RecordDialog;
import icox.cjy.notepostillibrary.utils.AudioRecoderUtils;
import icox.cjy.notepostillibrary.utils.SDcardUtil;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-16 17:50
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class AudioRecordService extends Service {

    private AudioRecoderUtils mAudioRecoderUtils;
    private boolean mIsStop = false;
    private WindowManager mWindowManager;
    private View mFloatView;
    private String mBookLocation;
    private String mBookName;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Constants.BOOK_LOCATION = mBookLocation = intent.getStringExtra("BookLocation");
            Constants.BOOK_NAME = mBookName = intent.getStringExtra("BookName");
            mAudioRecoderUtils = new AudioRecoderUtils(SDcardUtil.getSavaPath(mBookName, mBookLocation));
            createFloatView();
            mAudioRecoderUtils.startRecord();
            mAudioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {
                @Override
                public void onUpdate(double db, long time) {

                }

                @Override
                public void onStop(String filePath) {
                    Message message = new Message();
                    message.obj = filePath;
                    mHandler.sendMessage(message);
                    Intent intent = new Intent(getApplication(), AudioRecordService.class);
                    stopService(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void createFloatView() {
        WindowManager.LayoutParams windowPatams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);

        windowPatams.type = WindowManager.LayoutParams.TYPE_TOAST;
        windowPatams.format = PixelFormat.RGBA_8888;
        windowPatams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowPatams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
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
        mFloatView = LayoutInflater.from(getApplication()).inflate(R.layout.popup_record, null);
        mWindowManager.addView(mFloatView, windowPatams);

        Button button = (Button) mFloatView.findViewById(R.id.popup_button_stop);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RecordDialog dialog = new RecordDialog(getApplication());
                dialog.setClickSaveListener(new RecordDialog.ClickSaveListener() {
                    @Override
                    public void clickNoSave() {
                        mAudioRecoderUtils.cancelRecord();
                    }

                    @Override
                    public void clickSave() {
                        mAudioRecoderUtils.stopRecord();
                        mIsStop = true;
                    }
                });
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.show();
                dialog.setLayoutSave();
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            String filePath = (String) msg.obj;
            Toast.makeText(getApplication(), "录音完成：" + filePath, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mIsStop)
            mAudioRecoderUtils.cancelRecord();

        mWindowManager.removeViewImmediate(mFloatView);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
