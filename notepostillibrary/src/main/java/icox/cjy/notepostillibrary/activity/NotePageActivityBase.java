package icox.cjy.notepostillibrary.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import icox.cjy.notepostillibrary.R;
import icox.cjy.notepostillibrary.adapter.ScrawlPagerAdapter;
import icox.cjy.notepostillibrary.constants.Constants;
import icox.cjy.notepostillibrary.constants.NoteBeanConf;
import icox.cjy.notepostillibrary.dialog.SaveDialog;
import icox.cjy.notepostillibrary.dialog.ShareDialog;
import icox.cjy.notepostillibrary.utils.CacheBitmapUtil;
import icox.cjy.notepostillibrary.views.NoPreloadViewPager;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-3-30 9:30
 */
public abstract class NotePageActivityBase extends NotePageActivitySupperBase implements View.OnClickListener {
    private static final String TAG = "NoteActvitit";
    NoPreloadViewPager mNoteViewPager;
    LinearLayout mBoomButtonsMenus;
    public ImageView mNoteWriteIvEraser;
    ImageButton mNoteIbNextPage;
    ImageButton mNoteWriteButOk;
    ImageButton mNoteWriteButEraser;
    ImageButton mNoteWriteButClear;
    ImageButton mNoteWriteButGoBack;
    TextView mNoteTvPageNumber;
    ImageButton mNoteWriteButRepeal;
    ImageButton mNoteWriteButSize;
    LinearLayout mBoomButtonMenus;
    ImageButton mNoteWriteButHide;
    ImageButton mNoteWriteButShow;
    LinearLayout mMainLayout;
    private ImageButton mNoteIbLastPage;

    public String mBookLocation;
    public String mBookName;
    public String mFilePath;
    public ScrawlPagerAdapter mPagerAdapter;
    public File mDesFile;
    public boolean mIsHideMenus = false;
    public ShareDialog mShareDialog;
    public int mScreenWidth;
    public int mScreenHeight;
    public SaveDialog mSaveDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        initViews();

        Constants.BOOK_LOCATION = mBookLocation = getIntent().getStringExtra("BookLocation");
        Constants.BOOK_NAME = mBookName = getIntent().getStringExtra("BookName");

        mFilePath = getIntent().getStringExtra("FilePath");
        if (mFilePath != null) {
            mDesFile = new File(mFilePath);
        }
        initData();
        initCanvas();
    }

    protected void initViews() {
        mMainLayout = (LinearLayout) findViewById(R.id.note_main_layout);

        mNoteViewPager = (NoPreloadViewPager) findViewById(R.id.note_write_tablet_view);
        mBoomButtonsMenus = (LinearLayout) findViewById(R.id.boom_buttons_menus);
        mNoteWriteIvEraser = (ImageView) findViewById(R.id.note_write_iv_eraser);

        mNoteIbNextPage = (ImageButton) findViewById(R.id.menu_but_next_pager);
        mNoteIbLastPage = (ImageButton) findViewById(R.id.menu_but_last_pager);
        mNoteWriteButOk = (ImageButton) findViewById(R.id.menu_but_save);
        mNoteWriteButEraser = (ImageButton) findViewById(R.id.menu_but_eraser);
        mNoteWriteButClear = (ImageButton) findViewById(R.id.menu_but_clear);
        mNoteWriteButGoBack = (ImageButton) findViewById(R.id.menu_but_go_back);
        mNoteTvPageNumber = (TextView) findViewById(R.id.menu_tv_number);
        mNoteWriteButRepeal = (ImageButton) findViewById(R.id.menu_but_repeal);
        mNoteWriteButSize = (ImageButton) findViewById(R.id.menu_but_setting);
        mBoomButtonMenus = (LinearLayout) findViewById(R.id.boom_button_menus);
        mNoteWriteButHide = (ImageButton) findViewById(R.id.menu_but_hide);
        mNoteWriteButShow = (ImageButton) findViewById(R.id.note_write_but_show);

        initView();
    }

    protected abstract void initView();

    protected abstract void setLayout();

    protected abstract void saveNote(String content, String pwd);

    protected abstract void initData();

    protected abstract ScrawlPagerAdapter initPagerAdapter();

    public void initSaveDialogView(SaveDialog saveDialog) {
    }

    private void initCanvas() {

        mScreenWidth = getScreenWidth();
        mScreenHeight = getScreenHeight();

        mPagerAdapter = initPagerAdapter();
        mNoteViewPager.setAdapter(mPagerAdapter);

        mNoteWriteButRepeal.setOnClickListener(this);
        mNoteWriteButClear.setOnClickListener(this);
        mNoteWriteButOk.setOnClickListener(this);
        mNoteWriteButSize.setOnClickListener(this);
        mNoteIbLastPage.setOnClickListener(this);
        mNoteIbNextPage.setOnClickListener(this);
        mNoteWriteButGoBack.setOnClickListener(this);
        mNoteWriteButEraser.setOnClickListener(this);
        mNoteWriteButHide.setOnClickListener(this);
        mNoteWriteButShow.setOnClickListener(this);

        // TODO: 2017-11-18 修改加页下页视图，批注不一样
        if (mNoteViewPager.getCurrentItem() == mNoteViewPager.getAdapter().getCount() - 1) {
            mNoteIbNextPage.setImageResource(R.drawable.selector_menu_but_add_pager);
        } else {
            mNoteIbNextPage.setImageResource(R.drawable.selector_menu_but_next_pager);
        }

        mNoteTvPageNumber.setText((mNoteViewPager.getCurrentItem() + 1) + "/" + mPagerAdapter.getCount());

        mPagerAdapter.setEraserView(mNoteWriteIvEraser);

        viewPagerScrollListener(mNoteViewPager, mNoteIbNextPage);

        //把橡皮擦控件传给子类
        mPagerAdapter.setEraserView(mNoteWriteIvEraser);

    }

    private boolean isShowPopupWindon = false;

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.menu_but_setting) {
            changerTuyaViewConf(mPagerAdapter.mNoteFragment.getTuyaView()
                    , mNoteWriteIvEraser, false);

        } else if (i == R.id.menu_but_save) {
            clickSave();

        } else if (i == R.id.menu_but_clear) {
            clickClear(mPagerAdapter, mNoteViewPager);

        } else if (i == R.id.menu_but_repeal) {
            mPagerAdapter.undo(mNoteViewPager.getCurrentItem());

        } else if (i == R.id.menu_but_go_back) {
            goBack();

        } else if (i == R.id.menu_but_eraser) {
            clickEraser(mPagerAdapter.mNoteFragment.getTuyaView(), mNoteWriteIvEraser);

        } else if (i == R.id.menu_but_last_pager) {
            clickUpPage(mNoteIbNextPage, mNoteTvPageNumber, mNoteViewPager, mPagerAdapter, mNoteWriteIvEraser);

        } else if (i == R.id.menu_but_next_pager) {
            clickNextPage(mNoteIbNextPage, mNoteTvPageNumber, mNoteViewPager, mPagerAdapter, mNoteWriteIvEraser);

        } else if (i == R.id.menu_but_hide) {
            //            mIsHideMenus = !mIsHideMenus;
            //            if (mIsHideMenus) {
            //                mBoomButtonMenus.setVisibility(View.GONE);
            //                mNoteWriteButShow.setVisibility(View.VISIBLE);
            //            } else {
            //                mBoomButtonMenus.setVisibility(View.VISIBLE);
            //                mNoteWriteButShow.setVisibility(View.GONE);
            //            }
            mBoomButtonMenus.setVisibility(View.GONE);
            mNoteWriteButShow.setVisibility(View.VISIBLE);

        } else if (i == R.id.note_write_but_show) {
            mBoomButtonMenus.setVisibility(View.VISIBLE);
            mNoteWriteButShow.setVisibility(View.GONE);

        }
    }

    private String savePath = null;

    //    private void showShareDialog() {
    //
    //        if (!OkHttpUtil.checkNetworkState(this)) {
    //            Toast.makeText(this, "请检查你的网络是否已连接！", Toast.LENGTH_LONG).show();
    //            return;
    //        }
    //
    //        mShareDialog = new ShareDialog(NotePageActivityBase.this, mDesFile) {
    //            @Override
    //            public String saveFile(final String title, final String pwd, final String name) {
    //
    //                new Thread(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        long currentTime = System.currentTimeMillis();
    //                        String time = DateTimeUtil.getCurrentTime(currentTime);
    //
    //                        try {
    //                            mPagerAdapter.setWidthHeight(getScreenWidth(), getScreenHeight());
    //                            savePath = mPagerAdapter.saveNote(time, title, pwd, name);
    //                        } catch (Exception e) {
    //                            e.printStackTrace();
    //                            threadToast("请检查你的存储设备是否可用！" + e);
    //                            return;
    //                        }
    //
    //                        if (savePath == null || savePath.contains("保存出错了,错误原因")) {
    //                            threadToast("savePath！" + savePath);
    //                            return;
    //                        }
    //
    //                        File file = new File(savePath);
    //                        if (!file.exists()) {
    //                            threadToast("文件不存在 " + file.getPath());
    //                            return;
    //                        }
    //                        Map<String, String> paserm = new HashMap<String, String>();
    //                        paserm.put("name", OkHttpUtil.toUTF8(title
    //                                + "_" + currentTime));
    //                        paserm.put("pwd", OkHttpUtil.toUTF8(pwd.trim()));
    //                        paserm.put("api", OkHttpUtil.toUTF8("set"));
    //                        paserm.put("compere", OkHttpUtil.toUTF8(name));
    //                        paserm.put("time", OkHttpUtil.toUTF8(time.substring(0, time.indexOf("秒") + 1)));
    //                        //                        paserm.put("MeetingAddr", OkHttpUtil.toUTF8(
    //                        //                                mSpUtils.getString(Constants.MEETING_ADDR, Constants.MEETING_ADDR_NORMAL)));
    //
    //                        OkHttpUtil.uploadFile(file, Constants.SHARE_URL, paserm, null, new MyStringCallBack());
    //
    //                    }
    //                }).start();
    //
    //
    //                return savePath;
    //            }
    //
    //            @Override
    //            public void hidePopup() {
    //                finish();
    //            }
    //        };
    //        mShareDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
    //        mShareDialog.show();
    //    }
    //
    //    private void threadToast(final String str) {
    //        runOnUiThread(new Runnable() {
    //            @Override
    //            public void run() {
    //                Toast.makeText(NotePageActivityBase.this, str, Toast.LENGTH_LONG).show();
    //            }
    //        });
    //    }
    //
    //    class MyStringCallBack extends StringCallback {
    //        @Override
    //        public void onError(okhttp3.Call call, Exception e, int id) {
    //            mShareDialog.mButtonShare.setVisibility(View.VISIBLE);
    //            Toast.makeText(NotePageActivityBase.this, "上传出错 e = " + e, Toast.LENGTH_LONG).show();
    //            if (savePath != null) {
    //                File file = new File(savePath);
    //                if (file.exists())
    //                    file.delete();
    //            }
    //            Log.e("MainActity11", "上传出错 e = " + e);
    //            errorShareDialog();
    //        }
    //
    //        @Override
    //        public void onResponse(String response, int id) {
    //            Log.i("MainActity11", "响应 response = " + response);
    //            mShareDialog.mButtonShare.setVisibility(View.VISIBLE);
    //            if (response.contains("error||") || TextUtils.isEmpty(response)) {
    //                if (savePath != null) {
    //                    File file = new File(savePath);
    //                    if (file.exists())
    //                        file.delete();
    //                }
    //                errorShareDialog();
    //                Toast.makeText(NotePageActivityBase.this, "参数错误！", Toast.LENGTH_LONG).show();
    //            } else {
    //                mShareDialog.SHARE_STATE = mShareDialog.SHARE_SUCCEED;
    //                mShareDialog.mButtonShare.setImageResource(R.drawable.selector_dialog_share_btn_qr_code_succeed);
    //                Bitmap QRCode = QRCodeUtil.createQRCode(response, 300, 300);
    //                mShareDialog.mIvQRCode.setImageBitmap(QRCode);
    //                mShareDialog.mIvQRCode.setVisibility(View.VISIBLE);
    //                mShareDialog.mTvTitle.setVisibility(View.VISIBLE);
    //                mShareDialog.mEdtTitle.setVisibility(View.GONE);
    //                mShareDialog.mEdtName.setVisibility(View.GONE);
    //                mShareDialog.EdtPwd.setVisibility(View.GONE);
    //                mShareDialog.mLoadingView.setVisibility(View.GONE);
    //                mShareDialog.mTvHint.setText("扫描二维码查看记录");
    //                mShareDialog.deleteFile();
    //            }
    //        }
    //
    //        @Override
    //        public void inProgress(float progress, long total, int id) {
    //        }
    //
    //        @Override
    //        public boolean validateReponse(Response response, int id) {
    //            return super.validateReponse(response, id);
    //        }
    //    }
    //
    //    private void errorShareDialog() {
    //        mShareDialog.mEdtTitle.setVisibility(View.VISIBLE);
    //        mShareDialog.EdtPwd.setVisibility(View.VISIBLE);
    //        mShareDialog.mEdtName.setVisibility(View.VISIBLE);
    //        mShareDialog.mIvQRCode.setVisibility(View.GONE);
    //        mShareDialog.mTvHint.setVisibility(View.GONE);
    //        mShareDialog.mTvTitle.setVisibility(View.GONE);
    //        mShareDialog.mLoadingView.setVisibility(View.GONE);
    //        mShareDialog.mButtonShare.setImageResource(R.drawable.dialog_qrcode_btn_error);
    //        mShareDialog.SHARE_STATE = mShareDialog.SHARE_ERROR;
    //    }


    /**
     * 生成一个 透明的背景图片
     *
     * @return
     */
    private Drawable getDrawable() {
        ShapeDrawable bgdrawable = new ShapeDrawable(new OvalShape());
        bgdrawable.getPaint().setColor(getResources().getColor(R.color.transparency));
        return bgdrawable;
    }


    private void clickSave() {
        mSaveDialog = new SaveDialog(this, false, mDesFile) {
            @Override
            public void sure(String content, String pwd) {
                saveNote(content, pwd);
            }

        };
        mSaveDialog.show();
        mSaveDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                initWindow();
            }
        });

        initSaveDialogView(mSaveDialog);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //        PostilService.showView();
        NoteBeanConf.tuyaBeanList.clear();
        if (mDesFile != null) {
            CacheBitmapUtil.destoryCacheBitmap(mDesFile.listFiles());
        }

//        NoteBeanConf.bgLineBitmap = null;
//        NoteBeanConf.bgNormalBitmap = null;
//        NoteBeanConf.nullBitmap = null;
//        NoteBeanConf.saveBitmap = null;

    }


    long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 1500)  //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                //                System.exit(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 判断软键盘是否弹出
     */
    public boolean isSHowKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        View view = this.getWindow().peekDecorView();
        if (imm.hideSoftInputFromWindow(view.getWindowToken(), 0)) {
            imm.showSoftInput(view, 0);
            return true;
            //软键盘已弹出
        } else {
            return false;
            //软键盘未弹出
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //        PostilService.hideView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //        PostilService.showView();
    }
}
