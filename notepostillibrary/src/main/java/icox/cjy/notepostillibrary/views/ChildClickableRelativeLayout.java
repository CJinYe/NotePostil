package icox.cjy.notepostillibrary.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-6-6 15:56
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class ChildClickableRelativeLayout extends RelativeLayout {
    //子控件是否可以接受点击事件
    private boolean childClickable = true;
    public ChildClickableRelativeLayout(Context context) {
        super(context);
    }

    public ChildClickableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildClickableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !childClickable;
    }

    public void setChildClickable(boolean childClickable){
        this.childClickable = childClickable;
    }
}
