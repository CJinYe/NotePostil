package icox.cjy.notepostillibrary.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import java.io.File;

import icox.cjy.notepostillibrary.R;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-20 11:16
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public abstract class MainMenuDialog extends BaseDialog implements View.OnClickListener {


    private final File mFile;

    public MainMenuDialog(@NonNull Context context, File file) {
        super(context);
        mFile = file;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_main_menu);
        findViewById(R.id.dialog_main_menu_delete).setOnClickListener(this);
        findViewById(R.id.dialog_main_menu_rename).setOnClickListener(this);
    }

    public abstract void clickMenuDeleteSure();

    public abstract void clickMenuRenameSure(String name);

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_main_menu_delete) {
            DeleteNoteDialog deleteNoteDialog = new DeleteNoteDialog(getContext()) {
                @Override
                public void sure() {
                    clickMenuDeleteSure();
                }
            };
            deleteNoteDialog.show();
        } else if (id == R.id.dialog_main_menu_rename) {
            RenameFileDialog renameFileDialog = new RenameFileDialog(getContext()) {
                @Override
                public void sure(String name) {
                    clickMenuRenameSure(name);
                }
            };
            renameFileDialog.show();
            renameFileDialog.setEditText(mFile.getName());
        }
        this.dismiss();
    }
}
