package icox.cjy.notepostillibrary.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import icox.cjy.notepostillibrary.R;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-18 17:09
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class RecordDialog extends BaseDialog implements View.OnClickListener {

    private ImageView mIvTitle;
    private ImageView mIvIcon;
    private LinearLayout mLayouSave;
    private LinearLayout mLayouStart;

    public RecordDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_record);
        findViewById(R.id.dialog_record_cancel).setOnClickListener(this);
        findViewById(R.id.dialog_record_sure).setOnClickListener(this);
        findViewById(R.id.dialog_record_save).setOnClickListener(this);
        findViewById(R.id.dialog_record_no_save).setOnClickListener(this);
        findViewById(R.id.dialog_record_keep).setOnClickListener(this);

        mIvTitle = (ImageView) findViewById(R.id.dialog_record_iv_title);
        mIvIcon = (ImageView) findViewById(R.id.dialog_record_iv_icon);
        mLayouSave = (LinearLayout) findViewById(R.id.dialog_record_layout_save);
        mLayouStart = (LinearLayout) findViewById(R.id.dialog_record_layout_start);
    }

    public void setLayoutSave() {
        mIvIcon.setImageResource(R.drawable.dialog_record_iv_save);
        mIvTitle.setImageResource(R.drawable.dialog_record_tv_save);
        mLayouSave.setVisibility(View.VISIBLE);
        mLayouStart.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_record_cancel || id == R.id.dialog_record_keep) {
            dismiss();
        } else if (id == R.id.dialog_record_sure) {
            if (mClickStartListener != null)
                mClickStartListener.clickStartSure();
        } else if (id == R.id.dialog_record_save) {
            if (mClickSaveListener != null)
                mClickSaveListener.clickSave();
        } else if (id == R.id.dialog_record_no_save) {
            if (mClickSaveListener != null)
                mClickSaveListener.clickNoSave();
        }
    }

    private ClickStartListener mClickStartListener;

    public void setClickStartListener(ClickStartListener listener) {
        mClickStartListener = listener;
    }

    private ClickSaveListener mClickSaveListener;

    public void setClickSaveListener(ClickSaveListener clickSaveListener) {
        mClickSaveListener = clickSaveListener;
    }

    public interface ClickStartListener {
        void clickStartSure();
    }

    public interface ClickSaveListener {
        void clickNoSave();

        void clickSave();
    }

}
