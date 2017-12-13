package icox.cjy.notepostillibrary.utils;

import android.content.Context;
import android.content.Intent;

import icox.cjy.notepostillibrary.dialog.RecordDialog;
import icox.cjy.notepostillibrary.service.AudioRecordService;

/**
 * @author 陈锦业
 * @version $Rev$
 * @time 2017-11-16 16:17
 */
public class PopupUtils {

    public static void showRecord(final Context context) {

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

}
