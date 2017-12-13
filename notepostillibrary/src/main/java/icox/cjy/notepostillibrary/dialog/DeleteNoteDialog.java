package icox.cjy.notepostillibrary.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import icox.cjy.notepostillibrary.R;


/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-4-5 14:45
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public abstract class DeleteNoteDialog extends BaseDialog implements View.OnClickListener {

    private Context mContext;
    private ImageView mIvTitle;
    private ImageView mIvTv;

    public DeleteNoteDialog(@NonNull Context context) {
        super(context);
        mContext = context;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete_file);
        initView();//初始化自定的布局/控件
    }

    private void initView() {
        //获取子控件
        ImageButton sure = (ImageButton) findViewById(R.id.dialog_delete_but_sure);
        ImageButton cancel = (ImageButton) findViewById(R.id.dialog_delete_but_cancel);
        sure.setOnClickListener(this);
        cancel.setOnClickListener(this);

        mIvTitle = (ImageView) findViewById(R.id.dialog_delete_title);
        mIvTv = (ImageView) findViewById(R.id.dialog_delete_tv);
    }

    public void setDianDuLayout() {
        if (mIvTitle != null && mIvTv != null) {
            mIvTitle.setImageResource(R.drawable.dialog_tv_tishi);
            mIvTv.setImageResource(R.drawable.dialog_diandu_close_tv);
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.dialog_delete_but_sure) {
            sure();
            dismiss();

        } else if (i == R.id.dialog_delete_but_cancel) {
            dismiss();
        }
    }

    public abstract void sure();
}
