package icox.cjy.notepostillibrary.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import icox.cjy.notepostillibrary.R;


/**
 * Created by cjj on 2015/9/14.
 */
public class SnailBar extends SeekBar {

    public SnailBar(Context context) {
        super(context);
        init();
    }


    public SnailBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SnailBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setMax(70);
        this.setThumbOffset(dip2px(getContext(), 0));
//        this.setBackgroundResource(R.drawable.dialog_setting_seek_bg);
        int padding = dip2px(getContext(), (float) 20);
        this.setPadding(padding, padding, padding, padding);
        this.setProgressDrawable(getResources().getDrawable(R.drawable.dialog_setting_seek_bg));
    }


    //    @Override
    //    protected void onAttachedToWindow() {
    //        super.onAttachedToWindow();
    //        AnimationDrawable drawable = (AnimationDrawable)this.getThumb();
    //        drawable.start();
    //    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
