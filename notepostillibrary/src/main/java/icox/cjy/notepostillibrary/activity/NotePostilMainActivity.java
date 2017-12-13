package icox.cjy.notepostillibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

import butterknife.ButterKnife;
import icox.cjy.notepostillibrary.R;
import icox.cjy.notepostillibrary.adapter.NoteGridAdapter;
import icox.cjy.notepostillibrary.constants.Constants;
import icox.cjy.notepostillibrary.dialog.MainMenuDialog;
import icox.cjy.notepostillibrary.dialog.MusicDialog;
import icox.cjy.notepostillibrary.dialog.PwdDialog;
import icox.cjy.notepostillibrary.utils.Zip4jUtil;

/**
 * @author 陈锦业
 * @version $Rev$
 *          笔记批注的首页，查看已保存的笔记批注
 */
public class NotePostilMainActivity extends BaseActivity implements View.OnClickListener {
    GridView mMainNoteGv;
    private NoteGridAdapter mAdapter;
    private Context mContext;
    private String mBookLocation;
    private String mBookName;
    private LinearLayout mLayoutMain;
    private int mRotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mRotation = getWindowManager().getDefaultDisplay().getRotation();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note_postil_main);
        ButterKnife.inject(this);
        mContext = this;
        Constants.BOOK_LOCATION = mBookLocation = getIntent().getStringExtra("BookLocation");
        Constants.BOOK_NAME = mBookName = getIntent().getStringExtra("BookName");
        initConfiguration();
        initView();

    }

    private void initConfiguration() {
        mMainNoteGv = (GridView) findViewById(R.id.main_note_gv);
        mLayoutMain = (LinearLayout) findViewById(R.id.activity_main);
        findViewById(R.id.main_but_go_back).setOnClickListener(this);

        //        DisplayMetrics metrics = new DisplayMetrics();
        //        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        //        if (metrics.widthPixels>metrics.heightPixels){
        //            mMainNoteGv.setNumColumns(4);
        //            mLayoutMain.setBackgroundResource(R.drawable.main_landscape_background);
        //        }else {
        //            mMainNoteGv.setNumColumns(3);
        //            mLayoutMain.setBackgroundResource(R.drawable.main_portrait_background);
        //        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        setContentView(R.layout.activity_note_postil_main);
//        initConfiguration();
//        initView();
        super.onConfigurationChanged(newConfig);
        lockScreenRotation(mRotation);

    }

    private void initView() {
        mAdapter = new NoteGridAdapter(this);
        mMainNoteGv.setAdapter(mAdapter);
        mMainNoteGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final File file = mAdapter.mFiles.get(position);
                if (file.getPath().contains(".zip")) {
                    openNotePostil(file);
                } else {
                    openMusic(file);
                }
            }

        });

        mMainNoteGv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, int position, long id) {
                final File file = mAdapter.mFiles.get(position);

                MainMenuDialog mainMenuDialog = new MainMenuDialog(mContext, file) {
                    @Override
                    public void clickMenuDeleteSure() {
                        file.delete();
                        refreshAdapter();
                    }

                    @Override
                    public void clickMenuRenameSure(String name) {
                        String path = file.getAbsolutePath();
                        if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(name)) {
                            int lastIndexOf = path.lastIndexOf("/");
                            String path1 = path.substring(0, lastIndexOf + 1);
                            String suffix = path.substring(path.lastIndexOf("."), path.length());
                            File renameFile = new File(path1 + name + suffix);
                            boolean b = file.renameTo(renameFile);
                            if (b) {
                                // 刷新UI
                                refreshAdapter();
                            }
                        }
                    }
                };
                mainMenuDialog.show();
                return true;
            }
        });
    }

    private void openMusic(File file) {
        MusicDialog dialog = new MusicDialog(this, file);
        dialog.show();
    }

    private void openNotePostil(final File file) {
        final String fileName = file.getPath().replaceAll(".zip", "");
        final Intent intent = new Intent(mContext, NotePageActivity.class);
        try {
            final ZipFile zipFile = new ZipFile(file);
            //判断文件是否加密
            if (zipFile.isEncrypted()) {
                PwdDialog pwdDialog = new PwdDialog(mContext, file.getName()) {
                    @Override
                    public void sure(String pwd) {
                        try {
                            File fileUn = Zip4jUtil.unzip(file, fileName, pwd);
                            intent.putExtra("FilePath", fileUn.getPath());
                            startActivity(intent);
                        } catch (ZipException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "密码有误！", Toast.LENGTH_SHORT).show();
                            File file1 = new File(fileName);
                            if (file1.exists()) {
                                file1.delete();
                                refreshAdapter();
                            }
                        }
                    }
                };
                pwdDialog.show();
            } else {
                File fileUn = Zip4jUtil.unzip(file, fileName, "");
                intent.putExtra("FilePath", fileUn.getPath());
                startActivity(intent);
            }
        } catch (ZipException e) {
            e.printStackTrace();
            File file1 = new File(fileName);
            if (file1.exists()) {
                file1.delete();
                refreshAdapter();
            }
        }
    }

    private void refreshAdapter() {
        if (mMainNoteGv != null) {
            mAdapter = new NoteGridAdapter(this);
            mMainNoteGv.setAdapter(mAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.main_but_go_back) {
            finish();
        }
    }
}
