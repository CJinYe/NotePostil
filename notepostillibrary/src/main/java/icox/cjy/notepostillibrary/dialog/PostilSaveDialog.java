package icox.cjy.notepostillibrary.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;

import icox.cjy.notepostillibrary.R;
import icox.cjy.notepostillibrary.utils.EditextUtil;


/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-4-5 14:45
 */
public abstract class PostilSaveDialog extends BaseDialog implements View.OnClickListener {

    private String mName;
    private File mDesFile;
    private String mCurrentTime;
    private Context mContext;
    public EditText mText;
    public EditText mPwd;

    public PostilSaveDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_postil_save);
        initView();//初始化自定的布局/控件
    }

    private void initView() {
        //获取子控件
        ImageButton sure = (ImageButton) findViewById(R.id.dialog_postil_save_but_sure);
        ImageButton cancel = (ImageButton) findViewById(R.id.dialog_postil_save_but_cancel);
        mText = (EditText) findViewById(R.id.dialog_postil_save_edt);

        //不允许输入特殊字符
        EditextUtil.setEditTextInhibitInputSpeChat(32, mText);
        //不自动弹出键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sure.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.dialog_postil_save_but_sure) {
            dismiss();
            sure(mText.getText().toString().trim(), null);

        } else if (i == R.id.dialog_postil_save_but_cancel) {
            dismiss();

        }
    }

    public abstract void sure(String content, String pwd);
}
