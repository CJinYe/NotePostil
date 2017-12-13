package icox.cjy.notepostillibrary.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

import icox.cjy.notepostillibrary.R;
import icox.cjy.notepostillibrary.adapter.ScrawlPagerAdapter;
import icox.cjy.notepostillibrary.constants.NoteBeanConf;
import icox.cjy.notepostillibrary.constants.NotePositConf;
import icox.cjy.notepostillibrary.dialog.ColorPickerDialog;
import icox.cjy.notepostillibrary.dialog.PostilSaveDialog;
import icox.cjy.notepostillibrary.utils.DateTimeUtil;
import icox.cjy.notepostillibrary.utils.SDcardUtil;
import icox.cjy.notepostillibrary.views.TuyaViewPage;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-14 18:25
 */
public class PostilActivity extends NotePageActivityBase {

    private ImageButton mButLine;
    private ImageButton mButCircle;
    private ImageButton mButRect;
    private ImageButton mButNormal;
    private ImageButton mButTypeLine;
    private ImageButton mButTypeLineTwo;
    private ImageButton mButTypeEmpty;
    private ImageButton mButTypeWave;
    private ImageButton mButTypeWaveTwo;
    private int mRotation;
    private Bitmap mBitmap;
    private BroadcastReceiver mFloatVideBroadcast;

    @Override
    protected void setLayout() {
        mRotation = getWindowManager().getDefaultDisplay().getRotation();
        setContentView(R.layout.activity_note_postil);
        mBitmap = NoteBeanConf.PostilBitmap;
        mFloatVideBroadcast = new FloatViewBrocade();
        IntentFilter intentFilter = new IntentFilter("FLOAT_CLICK");
        registerReceiver(mFloatVideBroadcast, intentFilter);
    }

    @Override
    protected void initView() {
        mNoteWriteButHide.setVisibility(View.GONE);
        mNoteWriteButShow.setVisibility(View.GONE);
        mNoteWriteButOk.setVisibility(View.GONE);

        mButLine = (ImageButton) findViewById(R.id.menu_but_type_line);
        mButCircle = (ImageButton) findViewById(R.id.menu_but_type_circle);
        mButRect = (ImageButton) findViewById(R.id.menu_but_type_rec);
        mButNormal = (ImageButton) findViewById(R.id.menu_but_type_write);

        mButLine.setVisibility(View.VISIBLE);
        mButCircle.setVisibility(View.VISIBLE);
        mButRect.setVisibility(View.VISIBLE);
        mButNormal.setVisibility(View.VISIBLE);

        mButLine.setOnClickListener(new ClickListener());
        mButCircle.setOnClickListener(new ClickListener());
        mButRect.setOnClickListener(new ClickListener());
        mButNormal.setOnClickListener(new ClickListener());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        lockScreenRotation(mRotation);
    }

    @Override
    protected void saveNote(String content, String pwd) {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected ScrawlPagerAdapter initPagerAdapter() {
        try {
            mDesFile = SDcardUtil.getPostilSavaPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ScrawlPagerAdapter(getSupportFragmentManager(), this,
                mScreenWidth, mScreenHeight, mDesFile, true);
    }

    @Override
    protected void initColorPickerDialog(ColorPickerDialog colorPickerDialog) {
        colorPickerDialog.mLlBackground.setVisibility(View.GONE);
        colorPickerDialog.findViewById(R.id.dialog_color_layout_line_type).setVisibility(View.VISIBLE);
        mButTypeLine = (ImageButton) colorPickerDialog.findViewById(R.id.dialog_color_type_line);
        mButTypeLineTwo = (ImageButton) colorPickerDialog.findViewById(R.id.dialog_color_type_line_two);
        mButTypeEmpty = (ImageButton) colorPickerDialog.findViewById(R.id.dialog_color_type_line_empty);
        mButTypeWave = (ImageButton) colorPickerDialog.findViewById(R.id.dialog_color_type_wave);
        mButTypeWaveTwo = (ImageButton) colorPickerDialog.findViewById(R.id.dialog_color_type_wave_two);

        mButTypeLine.setOnClickListener(new ClickListener());
        mButTypeLineTwo.setOnClickListener(new ClickListener());
        mButTypeEmpty.setOnClickListener(new ClickListener());
        mButTypeWave.setOnClickListener(new ClickListener());
        mButTypeWaveTwo.setOnClickListener(new ClickListener());

        initTypeLineButton();
        TuyaViewPage tuyaViewPage = mPagerAdapter.mNoteFragment.getTuyaView();
        if (tuyaViewPage.DRAW_TYPE == NotePositConf.DRAW_TYPE_LINE) {
            switch (tuyaViewPage.DRAW_LINE_TYPE) {
                case NotePositConf.DRAW_LINE_TYPE_JUST:
                    mButTypeLine.setImageResource(R.drawable.dialog_setting_type_line_check);
                    break;
                case NotePositConf.DRAW_LINE_TYPE_JUST_TWO:
                    mButTypeLineTwo.setImageResource(R.drawable.dialog_setting_type_two_line_check);
                    break;
                case NotePositConf.DRAW_LINE_TYPE_EMPTY:
                    mButTypeEmpty.setImageResource(R.drawable.dialog_setting_type_emtry_check);
                    break;
                case NotePositConf.DRAW_LINE_TYPE_WAVE:
                    mButTypeWave.setImageResource(R.drawable.dialog_setting_type_wale_check);
                    break;
                case NotePositConf.DRAW_LINE_TYPE_WAVE_TWO:
                    mButTypeWaveTwo.setImageResource(R.drawable.dialog_setting_type_two_wale_check);
                    break;
                default:
                    break;
            }
        }
    }

    private class FloatViewBrocade extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("FLOAT_CLICK")) {
                Toast.makeText(mContext, "正在缓存，请稍等片刻", Toast.LENGTH_LONG).show();
                //隐藏当前所有的页面方便截取背景

                mHandler.sendEmptyMessageDelayed(1, 100);

                mHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mMainLayout.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    try {

                        //当前时间
                        String time = DateTimeUtil.getCurrentTime(System.currentTimeMillis());

                        mPagerAdapter.savePostil(time, mBitmap);

                        Log.e("TEST_", "保存 成功");
                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "保存出错了 ：" + e, Toast.LENGTH_LONG).show();
                        Log.e("TEST_", "出错 " + e);
                    }
                    break;
                case 2:
                    try {
                        //当前时间
                        String time = DateTimeUtil.getCurrentTime(System.currentTimeMillis());
                        SaveBean saveBean = (SaveBean) msg.obj;
                        mPagerAdapter.savePostil(time, saveBean.content, saveBean.pwd, mBitmap);

                        if (mDesFile != null)
                            for (File f : mDesFile.listFiles()) {
                                if (f.exists())
                                    f.delete();
                            }

                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "保存出错了 ：" + e, Toast.LENGTH_LONG).show();
                        Log.e("TEST_", "出错 " + e);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private class SaveBean {
        public String content;
        public String pwd;

        public SaveBean(String content, String pwd) {
            this.content = content;
            this.pwd = pwd;
        }
    }

    @Override
    public void goBackDialog() {
        PostilSaveDialog dialog = new PostilSaveDialog(this) {
            @Override
            public void sure(String content, String pwd) {

                Toast.makeText(mContext, "正在缓存，请稍等片刻", Toast.LENGTH_LONG).show();
                //隐藏当前所有的页面方便截取背景

                Message message = new Message();
                message.obj = new SaveBean(content, pwd);
                message.what = 2;
                mHandler.sendMessageDelayed(message, 100);
                //                mHandler.sendEmptyMessageDelayed(2, 100);

                mHandler.sendEmptyMessageDelayed(0, 1000);

            }
        };
        dialog.show();
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            TuyaViewPage tuyaViewPage = mPagerAdapter.mNoteFragment.getTuyaView();
            if (id == R.id.menu_but_type_write) {
                tuyaViewPage.setDrawType(NotePositConf.DRAW_TYPE_NORMAL);
            } else if (id == R.id.menu_but_type_line) {
                tuyaViewPage.setDrawType(NotePositConf.DRAW_TYPE_LINE);
            } else if (id == R.id.menu_but_type_circle) {
                tuyaViewPage.setDrawType(NotePositConf.DRAW_TYPE_CIRCLE);
            } else if (id == R.id.menu_but_type_rec) {
                tuyaViewPage.setDrawType(NotePositConf.DRAW_TYPE_REC);
            } else if (id == R.id.dialog_color_type_line) {
                initTypeLineButton();
                mButTypeLine.setImageResource(R.drawable.dialog_setting_type_line_check);
                tuyaViewPage.setDrawType(NotePositConf.DRAW_TYPE_LINE);
                tuyaViewPage.setDrawLineType(NotePositConf.DRAW_LINE_TYPE_JUST);
            } else if (id == R.id.dialog_color_type_line_two) {
                initTypeLineButton();
                mButTypeLineTwo.setImageResource(R.drawable.dialog_setting_type_two_line_check);
                tuyaViewPage.setDrawType(NotePositConf.DRAW_TYPE_LINE);
                tuyaViewPage.setDrawLineType(NotePositConf.DRAW_LINE_TYPE_JUST_TWO);
            } else if (id == R.id.dialog_color_type_line_empty) {
                initTypeLineButton();
                mButTypeEmpty.setImageResource(R.drawable.dialog_setting_type_emtry_check);
                tuyaViewPage.setDrawType(NotePositConf.DRAW_TYPE_LINE);
                tuyaViewPage.setDrawLineType(NotePositConf.DRAW_LINE_TYPE_EMPTY);
            } else if (id == R.id.dialog_color_type_wave) {
                initTypeLineButton();
                mButTypeWave.setImageResource(R.drawable.dialog_setting_type_wale_check);
                tuyaViewPage.setDrawType(NotePositConf.DRAW_TYPE_LINE);
                tuyaViewPage.setDrawLineType(NotePositConf.DRAW_LINE_TYPE_WAVE);
            } else if (id == R.id.dialog_color_type_wave_two) {
                initTypeLineButton();
                mButTypeWaveTwo.setImageResource(R.drawable.dialog_setting_type_two_wale_check);
                tuyaViewPage.setDrawType(NotePositConf.DRAW_TYPE_LINE);
                tuyaViewPage.setDrawLineType(NotePositConf.DRAW_LINE_TYPE_WAVE_TWO);
            }
        }
    }

    private void initTypeLineButton() {
        mButTypeLine.setImageResource(R.drawable.dialog_setting_type_line_normal);
        mButTypeLineTwo.setImageResource(R.drawable.dialog_setting_type_two_line_noraml);
        mButTypeEmpty.setImageResource(R.drawable.dialog_setting_type_emtry_noraml);
        mButTypeWave.setImageResource(R.drawable.dialog_setting_type_wale_noraml);
        mButTypeWaveTwo.setImageResource(R.drawable.dialog_setting_type_two_wale_noraml);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mFloatVideBroadcast);
    }
}
