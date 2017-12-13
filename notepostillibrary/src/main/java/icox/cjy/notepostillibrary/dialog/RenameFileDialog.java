package icox.cjy.notepostillibrary.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import icox.cjy.notepostillibrary.R;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-20 11:49
 */
public abstract class RenameFileDialog extends BaseDialog implements View.OnClickListener {

    private EditText mEditText;

    public RenameFileDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rename_file);
        mEditText = (EditText) findViewById(R.id.dialog_rename_edt);
        findViewById(R.id.dialog_rename_but_sure).setOnClickListener(this);
        findViewById(R.id.dialog_rename_but_cancel).setOnClickListener(this);
    }

    public abstract void sure(String name);

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_rename_but_sure) {
            sure(mEditText.getText().toString().trim());
            dismiss();
        } else if (id == R.id.dialog_rename_but_cancel) {
            dismiss();
        }
    }

    public void setEditText(String name){
        if (mEditText!=null && !TextUtils.isEmpty(name)){
            mEditText.setText(name);
            mEditText.setSelection(name.length());
        }
    }
}
