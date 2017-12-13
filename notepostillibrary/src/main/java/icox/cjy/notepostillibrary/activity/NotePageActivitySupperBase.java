package icox.cjy.notepostillibrary.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import icox.cjy.notepostillibrary.R;
import icox.cjy.notepostillibrary.adapter.PagerAdapter;
import icox.cjy.notepostillibrary.adapter.ScrawlPagerAdapter;
import icox.cjy.notepostillibrary.dialog.ColorPickerDialog;
import icox.cjy.notepostillibrary.dialog.RedoDialog;
import icox.cjy.notepostillibrary.views.NoPreloadViewPager;
import icox.cjy.notepostillibrary.views.TuyaViewPage;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-5-24 9:39
 * @des 多页涂鸦的Activity基类
 */
public abstract class NotePageActivitySupperBase extends BaseActivity {
    private InputMethodManager mImm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * 点击退出
     */
    public void goBack(){
        if (isSHowKeyboard(NotePageActivitySupperBase.this)) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                        0);
            }
        } else {
            goBackDialog();
        }
    }

    /**
     * 得到屏幕宽度
     * @return  屏幕宽度
     */
    public int getScreenWidth() {

//        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
//        getWindow().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
//        return mDisplayMetrics.widthPixels;

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        return point.x;
    }

    /**
     * 得到屏幕高度
     * @return  屏幕高度
     */
    public int getScreenHeight() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        return point.y;

//        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
//        getWindow().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
//        return mDisplayMetrics.heightPixels;
    }

    protected abstract void initColorPickerDialog(ColorPickerDialog colorPickerDialog);
    /**
     * 更改涂鸦的参数配置
     * @param tuyaViewPage 涂鸦的View
     * @param view  橡皮擦
     * @param isHideBg  是否隐藏背景颜色选择器
     */
    public void changerTuyaViewConf(final TuyaViewPage tuyaViewPage, final ImageView view,
                                    final boolean isHideBg) {
        ColorPickerDialog dialog = new ColorPickerDialog(
                NotePageActivitySupperBase.this, tuyaViewPage) {

            @Override
            protected void initColorPickerDialogView(ColorPickerDialog colorPickerDialog) {
                initColorPickerDialog(colorPickerDialog);
            }

            @Override
            public void sureClick(boolean isPaintColorChange) {
                tuyaViewPage.selectPaintColor(tuyaViewPage.currentColor);
                view.setVisibility(View.GONE);
            }
        };
        dialog.hideBackgroundView(isHideBg);
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //                        tuyaView.setBackgroundColor(tuyaView.backgroundColor);
                initWindow();
            }
        });
    }

    /**
     * 点击橡皮擦
     * @param tuyaViewPage  涂鸦View
     * @param view  橡皮擦
     */
    public void clickEraser(TuyaViewPage tuyaViewPage, ImageView view) {
        tuyaViewPage.selectEraser();
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        } else if (view.getVisibility() == View.GONE) {
            //                    mPagerAdapter.mTuyaViewMap.get(mNoteViewPager.getCurrentItem()).getTuyaView().selectEraser();
            view.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 点击上一页
     * @param nextButton    下一页的按钮
     * @param pageTv    页码的TextView
     * @param viewPager
     * @param pagerAdapter  适配器
     * @param ivEraser  橡皮擦
     */
    public void clickUpPage(ImageView nextButton, TextView pageTv,
                            NoPreloadViewPager viewPager, PagerAdapter pagerAdapter
            , ImageView ivEraser) {
        nextButton.setImageResource(R.drawable.selector_menu_but_next_pager);
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        initCreser(pagerAdapter, ivEraser);
        pageTv.setText((viewPager.getCurrentItem() + 1) + "/" + pagerAdapter.getCount());
    }

    /**
     * 点击上一页
     * @param nextButton    下一页的按钮
     * @param pageTv    页码的TextView
     * @param viewPager
     * @param pagerAdapter  适配器
     * @param ivEraser  橡皮擦
     */
    public void clickUpPage(ImageView nextButton, TextView pageTv,
                            NoPreloadViewPager viewPager, ScrawlPagerAdapter pagerAdapter
            , ImageView ivEraser) {
        nextButton.setImageResource(R.drawable.selector_menu_but_next_pager);
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        initCreser(pagerAdapter, ivEraser);
        pageTv.setText((viewPager.getCurrentItem() + 1) + "/" + pagerAdapter.getCount());
    }

    /**
     * 点击下一页一页
     * @param nextButton    下一页的按钮
     * @param pageTv    页码的TextView
     * @param viewPager
     * @param pagerAdapter  适配器
     * @param ivEraser  橡皮擦
     */
    public void clickNextPage(ImageView nextButton, TextView pageTv,
                              NoPreloadViewPager viewPager, ScrawlPagerAdapter pagerAdapter
            , ImageView ivEraser) {
        if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) {
            nextButton.setImageResource(R.drawable.selector_menu_but_add_pager);
            pagerAdapter.addCount();
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        } else {
            nextButton.setImageResource(R.drawable.selector_menu_but_next_pager);
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
        initCreser(pagerAdapter, ivEraser);
        pageTv.setText((viewPager.getCurrentItem() + 1) + "/" + pagerAdapter.getCount());
    }


    /**
     * 点击下一页一页
     * @param nextButton    下一页的按钮
     * @param pageTv    页码的TextView
     * @param viewPager
     * @param pagerAdapter  适配器
     * @param ivEraser  橡皮擦
     */
    public void clickNextPage(ImageView nextButton, TextView pageTv,
                              NoPreloadViewPager viewPager, PagerAdapter pagerAdapter
            , ImageView ivEraser) {
        if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) {
            nextButton.setImageResource(R.drawable.selector_menu_but_add_pager);
            pagerAdapter.addCount();
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        } else {
            nextButton.setImageResource(R.drawable.selector_menu_but_next_pager);
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
        initCreser(pagerAdapter, ivEraser);
        pageTv.setText((viewPager.getCurrentItem() + 1) + "/" + pagerAdapter.getCount());
    }

    /**
     * 点击清屏
     * @param pagerAdapter
     * @param viewPager
     */
    public void clickClear(final PagerAdapter pagerAdapter, final NoPreloadViewPager viewPager) {
        RedoDialog redoDialog = new RedoDialog(this) {
            @Override
            public void sure() {
                pagerAdapter.clear(viewPager.getCurrentItem());
            }
        };

        redoDialog.show();
        redoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                initWindow();
            }
        });
    }
    /**
     * 点击清屏
     * @param pagerAdapter
     * @param viewPager
     */
    public void clickClear(final ScrawlPagerAdapter pagerAdapter, final NoPreloadViewPager viewPager) {
        RedoDialog redoDialog = new RedoDialog(this) {
            @Override
            public void sure() {
                pagerAdapter.clear(viewPager.getCurrentItem());
            }
        };

        redoDialog.show();
        redoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                initWindow();
            }
        });
    }

    /**
     * 监听ViewPager滑动视图变化
     * @param viewPager
     * @param nextBut   下一页按钮
     */
    public void viewPagerScrollListener(final NoPreloadViewPager viewPager, final ImageButton nextBut){
        viewPager.setOnPageChangeListener(new NoPreloadViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) {
                    nextBut.setImageResource(R.drawable.selector_menu_but_add_pager);
                } else {
                    nextBut.setImageResource(R.drawable.selector_menu_but_next_pager);
                }
            }
        });
    }

    /**
     * 监听ViewPager滑动视图变化
     * @param viewPager
     * @param nextBut   下一页按钮
     */
    public void viewPagerScrollListener(final NoPreloadViewPager viewPager, final ImageView nextBut){
        viewPager.setOnPageChangeListener(new NoPreloadViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) {
                    nextBut.setImageResource(R.drawable.selector_menu_but_add_pager);
                } else {
                    nextBut.setImageResource(R.drawable.selector_menu_but_next_pager);
                }
            }
        });
    }

    public abstract void goBackDialog();

    private void initCreser(PagerAdapter pagerAdapter, ImageView ivEraser) {
        if (pagerAdapter.mNoteFragment.getTuyaView() != null) {
            if (pagerAdapter.mNoteFragment.getTuyaView().currentStyle == 0) {
                ivEraser.setVisibility(View.VISIBLE);
                pagerAdapter.mNoteFragment.getTuyaView().selectEraser();
            } else {
                ivEraser.setVisibility(View.GONE);
            }
        }
    }

    private void initCreser(ScrawlPagerAdapter pagerAdapter, ImageView ivEraser) {
        if (pagerAdapter.mNoteFragment.getTuyaView() != null) {
            if (pagerAdapter.mNoteFragment.getTuyaView().currentStyle == 0) {
                ivEraser.setVisibility(View.VISIBLE);
                pagerAdapter.mNoteFragment.getTuyaView().selectEraser();
            } else {
                ivEraser.setVisibility(View.GONE);
            }
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
