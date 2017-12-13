package icox.cjy.notepostillibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import icox.cjy.notepostillibrary.activity.NotePageActivity;
import icox.cjy.notepostillibrary.activity.NotePostilMainActivity;
import icox.cjy.notepostillibrary.activity.PostilActivity;
import icox.cjy.notepostillibrary.constants.NoteBeanConf;
import icox.cjy.notepostillibrary.dialog.DeleteNoteDialog;
import icox.cjy.notepostillibrary.dialog.RecordDialog;
import icox.cjy.notepostillibrary.service.AudioRecordService;
import icox.cjy.notepostillibrary.service.MusicService;
import icox.cjy.notepostillibrary.service.PostilSetvice;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-23 11:41
 */
public class NotePostilUtils {
    /**
     * 打开录音笔记进行录音
     *
     * @param context      上下文
     * @param bookLocation 课本位置
     * @param bookName     课本名称
     */
    public static void openRecord(final Context context, String bookLocation, String bookName) {
        RecordDialog dialog = new RecordDialog(context);
        dialog.setClickStartListener(new RecordDialog.ClickStartListener() {
            @Override
            public void clickStartSure() {
                Intent intent = new Intent(context, AudioRecordService.class);
                intent.putExtra("BookLocation", "第五课");
                intent.putExtra("BookName", "语文");
                context.startService(intent);
            }
        });
        dialog.show();
    }

    /**
     * 打开笔记的首页
     *
     * @param context      上下文
     * @param bookLocation 课本位置
     * @param bookName     课本名称
     */
    public static void openNoteMain(Context context, String bookLocation, String bookName) {
        Intent intent = new Intent(context, NotePostilMainActivity.class);
        intent.putExtra("BookLocation", bookLocation);
        intent.putExtra("BookName", bookName);
        context.startActivity(intent);
    }

    /**
     * 新建笔记
     *
     * @param context      上下文
     * @param bookLocation 课本位置
     * @param bookName     课本名称
     */
    public static void newNote(Context context, String bookLocation, String bookName) {
        Intent intent = new Intent(context, NotePageActivity.class);
        intent.putExtra("BookLocation", bookLocation);
        intent.putExtra("BookName", bookName);
        context.startActivity(intent);
    }

    /**
     * 打开批注左上角按钮
     *
     * @param context      上下文
     * @param bookLocation 课本位置
     * @param bookName     课本名称
     */
    public static void openPostilFloat(Context context, String bookLocation, String bookName) {
        Intent intent = new Intent(context, PostilSetvice.class);
        intent.putExtra("BookLocation", bookLocation);
        intent.putExtra("BookName", bookName);
        Activity activity = (Activity) context;
        activity.getWindow().getDecorView().setDrawingCacheEnabled(true);
        NoteBeanConf.PostilBitmap = activity.getWindow().getDecorView().getDrawingCache();
        if (PostilSetvice.mPostilSetvice == null) {
            context.startService(intent);
        } else {
            context.stopService(intent);
        }
    }

    /**
     * 新建批注
     *
     * @param context      上下文
     * @param bookLocation 课本位置
     * @param bookName     课本名称
     */
    public static void newPostil(Context context, String bookLocation, String bookName) {
        Activity activity = (Activity) context;
        activity.getWindow().getDecorView().setDrawingCacheEnabled(true);
        NoteBeanConf.PostilBitmap = activity.getWindow().getDecorView().getDrawingCache();

        Intent intent = new Intent(context, PostilActivity.class);
        intent.putExtra("BookLocation", bookLocation);
        intent.putExtra("BookName", bookName);
        context.startActivity(intent);
    }

    /**
     * 如正在播放笔记录音
     * 点击点读的时候弹出是否关闭播放对话框
     *
     * @param context
     */
    public static void openIsMusicStart(final Context context) {
        if (MusicService.mMusicService != null) {
            DeleteNoteDialog dialog = new DeleteNoteDialog(context) {
                @Override
                public void sure() {
                    Intent intent = new Intent(getContext(), MusicService.class);
                    context.stopService(intent);
                    dismiss();
                }
            };
            dialog.show();
            dialog.setDianDuLayout();
        }
    }


}
