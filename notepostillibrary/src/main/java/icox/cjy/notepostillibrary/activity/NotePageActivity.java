package icox.cjy.notepostillibrary.activity;

import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import icox.cjy.notepostillibrary.R;
import icox.cjy.notepostillibrary.adapter.ScrawlPagerAdapter;
import icox.cjy.notepostillibrary.constants.NoteBeanConf;
import icox.cjy.notepostillibrary.dialog.ColorPickerDialog;
import icox.cjy.notepostillibrary.dialog.GoBackDialog;
import icox.cjy.notepostillibrary.utils.DateTimeUtil;
import icox.cjy.notepostillibrary.utils.SDcardUtil;
import icox.cjy.notepostillibrary.views.TuyaViewPage;


/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-3-30 9:30
 */
public class NotePageActivity extends NotePageActivityBase implements View.OnClickListener {

    private ImageButton mButBgNormal;
    private ImageButton mButBgLine;
    private LinearLayout mLayoutVerticalOne;
    private LinearLayout mLayoutVerticalMain;
    private LinearLayout mLayoutVerticalThree;
    private LinearLayout mLayoutHorizontalLeft;
    private LinearLayout mLayoutHorizontalMain;
    private LinearLayout mLayoutHorizontalRight;

    @Override
    protected void initView() {
        mLayoutVerticalOne = (LinearLayout) findViewById(R.id.main_layout_vertical_one);
        mLayoutVerticalMain = (LinearLayout) findViewById(R.id.main_layout_vertical_main);
        mLayoutVerticalThree = (LinearLayout) findViewById(R.id.main_layout_vertical_three);
        mLayoutHorizontalLeft = (LinearLayout) findViewById(R.id.main_layout_horizontal_left);
        mLayoutHorizontalMain = (LinearLayout) findViewById(R.id.main_layout_horizontal_main);
        mLayoutHorizontalRight = (LinearLayout) findViewById(R.id.main_layout_horizontal_right);

        initConfigtion(getScreenWidth() > getScreenHeight());
    }

    @Override
    protected void setLayout() {
        setContentView(R.layout.activity_note);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //        try {
        //            Thread.sleep(20000);
        //        } catch (InterruptedException e) {
        //            Toast.makeText(this,"e = "+e,Toast.LENGTH_LONG).show();
        //            e.printStackTrace();
        //        }

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            initConfigtion(true);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            initConfigtion(false);
        }

        super.onConfigurationChanged(newConfig);

    }

    private void initConfigtion(boolean isLand) {

        if (isLand) {//横屏
            mLayoutVerticalOne.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 710f));
            mLayoutVerticalMain.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 70f));
            mLayoutVerticalThree.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 20f));

            mLayoutHorizontalLeft.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 345f));
            mLayoutHorizontalMain.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 590f));
            mLayoutHorizontalRight.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 345f));
        } else {//竖屏
            mLayoutVerticalOne.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1193f));
            mLayoutVerticalMain.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 65f));
            mLayoutVerticalThree.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 20f));

            mLayoutHorizontalLeft.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 100f));
            mLayoutHorizontalMain.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 600f));
            mLayoutHorizontalRight.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 100f));
        }
    }

    @Override
    protected void saveNote(String content, String pwd) {
        String time = DateTimeUtil.getCurrentTime(System.currentTimeMillis());

        String path = null;
        try {
            mPagerAdapter.setWidthHeight(getScreenWidth(), getScreenHeight());
            path = mPagerAdapter.saveNote(time, content, pwd, null);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(NotePageActivity.this, "请检查你的存储设备是否可用！" + e, Toast.LENGTH_LONG).show();
            return;
        }
        if (path.contains("保存出错了,错误原因")) {
            Toast.makeText(NotePageActivity.this, path, Toast.LENGTH_SHORT).show();
            return;
        }

        mSaveDialog.deleteFile();
        Toast.makeText(NotePageActivity.this, "保存完成！", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void initData() {
        //        mNoteDbDao = new NoteDbDao(this);
        //        if (mBookLocation != null) {
        //            mNoteBean = mNoteDbDao.queryNote(mBookLocation);
        //        } else if (mFilePath != null) {
        //            mNoteBean = mNoteDbDao.queryNotePaht(mFilePath);
        //        } else {
        //            mNoteBean = null;
        //        }
    }

    @Override
    protected ScrawlPagerAdapter initPagerAdapter() {
        return new ScrawlPagerAdapter(getSupportFragmentManager(), this,
                mScreenWidth, mScreenHeight, mDesFile, false);
    }

    @Override
    protected void initColorPickerDialog(ColorPickerDialog colorPickerDialog) {
        colorPickerDialog.mLlBackground.setVisibility(View.GONE);
        colorPickerDialog.findViewById(R.id.dialog_color_layout_bg_type).setVisibility(View.VISIBLE);
        mButBgNormal = (ImageButton) colorPickerDialog.findViewById(R.id.dialog_color_but_bg_normal);
        mButBgLine = (ImageButton) colorPickerDialog.findViewById(R.id.dialog_color_but_bg_line);
        mButBgNormal.setOnClickListener(new ViewClickListener());
        mButBgLine.setOnClickListener(new ViewClickListener());

        TuyaViewPage tuyaViewPage = mPagerAdapter.mNoteFragment.getTuyaView();
        if (tuyaViewPage.getBackgroundType() == 0) {
            mButBgNormal.setImageResource(R.drawable.dialog_setting_bg_type_deft_check);
            mButBgLine.setImageResource(R.drawable.dialog_setting_bg_type_line_normal);
        } else {
            mButBgNormal.setImageResource(R.drawable.dialog_setting_bg_type_deft_normal);
            mButBgLine.setImageResource(R.drawable.dialog_setting_bg_type_line_check);
        }

    }

    @Override
    public void goBackDialog() {
        GoBackDialog goBackDialog = new GoBackDialog(this) {
            @Override
            public void sure() {
                finish();
            }
        };
        goBackDialog.show();
    }

    private class ViewClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            TuyaViewPage tuyaView = mPagerAdapter.mNoteFragment.getTuyaView();
            if (id == R.id.dialog_color_but_bg_normal) {
                mButBgNormal.setImageResource(R.drawable.dialog_setting_bg_type_deft_check);
                mButBgLine.setImageResource(R.drawable.dialog_setting_bg_type_line_normal);
                tuyaView.setBackgroundType(1);
                tuyaView.selectorCanvasColor(NoteBeanConf.createBgNormalBitmap(getResources(), getScreenWidth(), getScreenHeight()));
            } else if (id == R.id.dialog_color_but_bg_line) {
                mButBgNormal.setImageResource(R.drawable.dialog_setting_bg_type_deft_normal);
                mButBgLine.setImageResource(R.drawable.dialog_setting_bg_type_line_check);
                tuyaView.setBackgroundType(2);
                tuyaView.selectorCanvasColor(NoteBeanConf.createBgLineBitmap(getResources(), getScreenWidth(), getScreenHeight()));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁的时候把解压出来的图片删除
        SDcardUtil.deleteDirWihtFile(mDesFile);
    }
}
