package icox.cjy.notepostil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import icox.cjy.notepostillibrary.activity.NotePageActivity;
import icox.cjy.notepostillibrary.activity.NotePostilMainActivity;
import icox.cjy.notepostillibrary.activity.PostilActivity;
import icox.cjy.notepostillibrary.constants.NoteBeanConf;
import icox.cjy.notepostillibrary.dialog.DeleteNoteDialog;
import icox.cjy.notepostillibrary.service.MusicService;
import icox.cjy.notepostillibrary.service.PostilSetvice;
import icox.cjy.notepostillibrary.utils.PopupUtils;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //        startActivity(new Intent(this, NotePostilMainActivity.class));
        //        finish();
        findViewById(R.id.main_button_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                                PopupUtils.showRecord(MainActivity.this);
            }
        });
        findViewById(R.id.main_button_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotePostilMainActivity.class);
                intent.putExtra("BookLocation", "第五课");
                intent.putExtra("BookName", "语文");
                startActivity(intent);
            }
        });
        findViewById(R.id.main_button_nex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotePageActivity.class);
                intent.putExtra("BookLocation", "第五课");
                intent.putExtra("BookName", "语文");
                startActivity(intent);
            }
        });
        findViewById(R.id.main_button_postil_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostilSetvice.class);
                intent.putExtra("BookLocation", "第五课");
                intent.putExtra("BookName", "语文");
                getWindow().getDecorView().setDrawingCacheEnabled(true);
                NoteBeanConf.PostilBitmap = getWindow().getDecorView().getDrawingCache();
                if (PostilSetvice.mPostilSetvice == null) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
            }
        });
        findViewById(R.id.main_button_postil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().getDecorView().setDrawingCacheEnabled(true);
                NoteBeanConf.PostilBitmap = getWindow().getDecorView().getDrawingCache();

                Intent intent = new Intent(MainActivity.this, PostilActivity.class);
                intent.putExtra("BookLocation", "第五课");
                intent.putExtra("BookName", "语文");
                startActivity(intent);


                //                String path = Environment.getExternalStorageDirectory() + "/" + "asdad第" + "张.png";
                //                File file = new File(path);
                //                FileOutputStream fos = null;
                //                try {
                //                    fos = new FileOutputStream(file);
                //                } catch (FileNotFoundException e) {
                //                    e.printStackTrace();
                //                }
                //
                //                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
        });


        findViewById(R.id.main_button_postil_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicService.mMusicService!=null){
                    DeleteNoteDialog dialog = new DeleteNoteDialog(MainActivity.this) {
                        @Override
                        public void sure() {
                            Intent intent = new Intent(getContext(), MusicService.class);
                            stopService(intent);
                            dismiss();
                        }
                    };
                    dialog.show();
                    dialog.setDianDuLayout();
                }
            }
        });
    }
}
