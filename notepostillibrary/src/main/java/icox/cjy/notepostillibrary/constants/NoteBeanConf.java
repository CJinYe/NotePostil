package icox.cjy.notepostillibrary.constants;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.SparseArray;

import icox.cjy.notepostillibrary.R;
import icox.cjy.notepostillibrary.bean.TuyaViewBean;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-5-8 9:21
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class NoteBeanConf {
    public static SparseArray<TuyaViewBean> tuyaBeanList = new SparseArray<>();
    public static SparseArray<Bitmap> fileBitmaps = new SparseArray<>();

    /**
     * 保存的上一页背景颜色
     */
    public static int saveNoteBgColor = -1;
    /**
     * 保存的上一页画笔颜色
     */
    public static int saveNotePaintColor = -1;
    /**
     * 保存的上一页画笔大小
     */
    public static int saveNotePaintSize = -1;
    /**
     * 保存的上一页画笔样式
     */
    public static int saveNotePaintStyle = -1;

    public static Bitmap nullBitmap = null;
    public static Bitmap saveBitmap = null;
    private static Bitmap fileBitmap = null;

    /**
     * 笔记空白横向背景图片
     */
    public static Bitmap bgNormalBitmap = null;
    /**
     * 笔记空白纵向背景图片
     */
    public static Bitmap bgNormalBitmapPortrait = null;
    /**
     * 笔记有线横向背景图片
     */
    public static Bitmap bgLineBitmap = null;
    /**
     * 笔记有线纵向背景图片
     */
    public static Bitmap bgLineBitmapPortrait = null;

    /**
     * 批注的时候传进来的底图，不然5.0以下没权限截取屏幕，只能截取当前activity的屏幕
     */
    public static Bitmap PostilBitmap = null;

    public static Bitmap createBgNormalBitmap(Resources res, int screenWidth, int screenHeight) {
        if (screenWidth > screenHeight) {
            if (bgNormalBitmap == null) {
                bgNormalBitmap = BitmapFactory.decodeResource(res, R.drawable.note_bg_normal).copy(Bitmap.Config.ARGB_4444, false);
                Matrix matrix;
                //            if (screenHeight > screenWidth) {
                //                matrix = new Matrix();
                //                matrix.setRotate(270);
                //                bgNormalBitmap = Bitmap.createBitmap(bgNormalBitmap, 0, 0, bgNormalBitmap.getWidth(), bgNormalBitmap.getHeight(), matrix, false);
                //            }
                if (bgNormalBitmap.getWidth() != screenWidth) {
                    float sx;
                    float sy;
                    matrix = new Matrix();
                    sx = (float) screenWidth / bgNormalBitmap.getWidth();
                    sy = (float) screenHeight / bgNormalBitmap.getHeight();
                    matrix.postScale(sx, sy);
                    bgNormalBitmap = Bitmap.createBitmap(bgNormalBitmap, 0, 0,
                            bgNormalBitmap.getWidth(), bgNormalBitmap.getHeight(), matrix, false);
                }
            }

            return bgNormalBitmap;
        } else {
            if (bgNormalBitmapPortrait == null) {
                bgNormalBitmapPortrait = BitmapFactory.decodeResource(res, R.drawable.note_bg_normal_portrait).copy(Bitmap.Config.ARGB_4444, false);
                Matrix matrix;
                if (bgNormalBitmapPortrait.getWidth() != screenWidth) {
                    float sx;
                    float sy;
                    matrix = new Matrix();
                    sx = (float) screenWidth / bgNormalBitmapPortrait.getWidth();
                    sy = (float) screenHeight / bgNormalBitmapPortrait.getHeight();
                    matrix.postScale(sx, sy);
                    bgNormalBitmapPortrait = Bitmap.createBitmap(bgNormalBitmapPortrait, 0, 0,
                            bgNormalBitmapPortrait.getWidth(), bgNormalBitmapPortrait.getHeight(), matrix, false);
                }
            }

            return bgNormalBitmapPortrait;
        }
    }

    public static Bitmap createBgLineBitmap(Resources res, int screenWidth, int screenHeight) {
        if (screenWidth > screenHeight) {
            if (bgLineBitmap == null) {
                bgLineBitmap = BitmapFactory.decodeResource(res, R.drawable.note_bg_line).copy(Bitmap.Config.ARGB_4444, false);
                Matrix matrix;
                if (bgLineBitmap.getWidth() != screenWidth) {
                    float sx;
                    float sy;
                    matrix = new Matrix();
                    sx = (float) bgLineBitmap.getWidth() / screenWidth;
                    sy = (float) bgLineBitmap.getHeight() / screenHeight;
                    matrix.postScale(sx, sy);
                    bgLineBitmap = Bitmap.createBitmap(bgLineBitmap, 0, 0, screenWidth, screenHeight, matrix, false);
                }
            }
            return bgLineBitmap;
        } else {
            if (bgLineBitmapPortrait == null) {
                bgLineBitmapPortrait = BitmapFactory.decodeResource(res, R.drawable.note_bg_line_portrait).copy(Bitmap.Config.ARGB_4444, false);
                Matrix matrix;
                if (bgLineBitmapPortrait.getWidth() != screenWidth) {
                    float sx;
                    float sy;
                    matrix = new Matrix();
                    sx = (float) bgLineBitmapPortrait.getWidth() / screenWidth;
                    sy = (float) bgLineBitmapPortrait.getHeight() / screenHeight;
                    matrix.postScale(sx, sy);
                    bgLineBitmapPortrait = Bitmap.createBitmap(bgLineBitmapPortrait, 0, 0, screenWidth, screenHeight, matrix, false);
                }
            }
            return bgLineBitmapPortrait;
        }
    }

    public static Bitmap createNullBitmap(int screenWidth, int screenHeight) {
        if (nullBitmap == null || nullBitmap.isRecycled()) {
            nullBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_4444);
        }
        return nullBitmap;
    }

    public static Bitmap createSaveBitmap(int screenWidth, int screenHeight) {
        if (saveBitmap == null || saveBitmap.isRecycled()) {
            saveBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_4444);
        }
        return saveBitmap;
    }

    public static Bitmap createFileBitmap(String path, int key) {
        Bitmap bitmap;
        if (fileBitmaps.get(key) == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
            options.inPurgeable = true;
            options.inInputShareable = true;
            bitmap = BitmapFactory.decodeFile(path, options);
            fileBitmaps.put(key, bitmap);
        } else {
            bitmap = fileBitmaps.get(key);
        }
        return bitmap;
    }


    public static Bitmap createBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        options.inPurgeable = true;
        options.inInputShareable = true;
        return BitmapFactory.decodeFile(null, options);
    }

    private static Bitmap mBitmap;


}
