package icox.cjy.notepostillibrary.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;

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
public abstract class RedoDialog extends BaseDialog implements View.OnClickListener{

    private Context mContext;

    public RedoDialog(@NonNull Context context) {
        super(context);
        mContext = context;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_go_back);
        initView();//初始化自定的布局/控件
    }


    private void initView() {
        //获取子控件
        ImageButton sure = (ImageButton) findViewById(R.id.dialog_redo_btn_sure);
        ImageButton cancel = (ImageButton) findViewById(R.id.dialog_redo_btn_cancel);
        sure.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.dialog_redo_btn_sure) {
            sure();
            dismiss();

        } else if (i == R.id.dialog_redo_btn_cancel) {
            dismiss();

        }
    }

    public abstract void sure();
}
