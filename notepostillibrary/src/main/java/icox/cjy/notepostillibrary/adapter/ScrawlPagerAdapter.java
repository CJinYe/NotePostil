package icox.cjy.notepostillibrary.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import icox.cjy.notepostillibrary.bean.NoteBean;
import icox.cjy.notepostillibrary.bean.TuyaViewBean;
import icox.cjy.notepostillibrary.constants.Constants;
import icox.cjy.notepostillibrary.constants.DrawPath;
import icox.cjy.notepostillibrary.constants.NoteBeanConf;
import icox.cjy.notepostillibrary.fragment.NoteFragment;
import icox.cjy.notepostillibrary.views.TuyaViewPage;

import static icox.cjy.notepostillibrary.constants.NoteBeanConf.nullBitmap;
import static icox.cjy.notepostillibrary.constants.NoteBeanConf.tuyaBeanList;
import static icox.cjy.notepostillibrary.constants.NotePositConf.DRAW_TYPE_CIRCLE;
import static icox.cjy.notepostillibrary.constants.NotePositConf.DRAW_TYPE_LINE;
import static icox.cjy.notepostillibrary.constants.NotePositConf.DRAW_TYPE_NORMAL;
import static icox.cjy.notepostillibrary.constants.NotePositConf.DRAW_TYPE_REC;
import static icox.cjy.notepostillibrary.utils.CacheBitmapUtil.createBitmap;
import static icox.cjy.notepostillibrary.utils.SDcardUtil.getPostilSavaPath;
import static icox.cjy.notepostillibrary.utils.SDcardUtil.getSavaPath;
import static icox.cjy.notepostillibrary.utils.Zip4jUtil.AddFilesWithAESEncryption;


/**
 * @author Administrator
 * @version $Rev$
 * @time 2017-4-27 10:00
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class ScrawlPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "PagerAdapter1";
    private final Context mContext;
    private int mScreenWidth;
    private int mScreenHeight;
    private final boolean mIsPostil;
    private NoteBean mNoteBean;
    private String mZipPath;
    public SparseArray<NoteFragment> mTuyaViewMap = new SparseArray<>();
    private int size;
    public NoteFragment mNoteFragment;
    private ArrayList<File> mDesFiles = new ArrayList();
    private RelativeLayout.LayoutParams mParams;
    private ImageView mEraserView;
    private int mPosition;

    public ScrawlPagerAdapter(FragmentManager fm, Context context,
                              int screenWidth, int screenHeight,
                              File desFile, boolean isPostil) {
        super(fm);
        mContext = context;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        mIsPostil = isPostil;

        if (desFile != null && desFile.exists()) {
            mZipPath = desFile.getPath();
            File[] desFiles = desFile.listFiles();
            Collections.addAll(mDesFiles, desFiles);
            size = mDesFiles.size();
            if (size == 0)
                size = 1;
        } else {
            size = 1;
        }
    }

    public ScrawlPagerAdapter(FragmentManager fm, Context context,
                              int screenWidth, int screenHeight,
                              File desFile, boolean isPostil, NoteBean noteBean) {
        super(fm);
        mContext = context;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        mIsPostil = isPostil;
        mNoteBean = noteBean;
        if (desFile != null && desFile.exists()) {
            mDesFiles.add(desFile);
            size = mDesFiles.size();
        } else {
            size = 1;
        }
        size = 1;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        //        deleteBitmapToCache(mDesFiles[position].getPath());
        mTuyaViewMap.remove(position);
        //        CacheBitmapUtil.deleteBitmapToCache(mDesFiles[position].getPath());
    }

    @Override
    public int getItemPosition(Object object) {
        return ScrawlPagerAdapter.POSITION_NONE;
    }


    @Override
    public Fragment getItem(int position) {

        mNoteFragment = new NoteFragment();
        mPosition = position;


        //在原有的图片上做修改
        if (mDesFiles != null && position < mDesFiles.size()) {
            TuyaViewPage tuyaView;
            Bitmap bitmap = createBitmap(mDesFiles.get(position).getPath());
            if (bitmap != null && !bitmap.isRecycled()) {
                tuyaView = new TuyaViewPage(mContext,
                        NoteBeanConf.createNullBitmap(mScreenWidth, mScreenHeight),
                        NoteBeanConf.createSaveBitmap(mScreenWidth, mScreenHeight),
                        bitmap);
                //设置背景颜色,跟橡皮擦有关联
                //                tuyaView.backgroundColor = getBitmapBackground(bitmap);
                tuyaView.backgroundColor = Color.WHITE;

                tuyaView.setBackgroundType(0);
            } else {
                tuyaView = new TuyaViewPage(mContext, NoteBeanConf.createNullBitmap(mScreenWidth, mScreenHeight),
                        NoteBeanConf.createSaveBitmap(mScreenWidth, mScreenHeight));
            }

            setEraserListener(tuyaView);

            mNoteFragment.setTuyaView(tuyaView);

            mTuyaViewMap.put(position, mNoteFragment);
            mNoteFragment.setKey(position, mDesFiles.get(position));
            return mNoteFragment;
        }

        TuyaViewPage tuyaView;
        if (mIsPostil) {
            tuyaView = new TuyaViewPage(mContext, NoteBeanConf.createNullBitmap(mScreenWidth, mScreenHeight),
                    NoteBeanConf.createSaveBitmap(mScreenWidth, mScreenHeight));
            tuyaView.selectorCanvasColor(Color.TRANSPARENT);
            tuyaView.isPostil(true);
            //如果批注的绘画信息不为空,则显示出来
            //            if (PostilService.postilList.get(0) != null) {
            //                tuyaView.setConstants(PostilService.postilList.get(0));
            //            }
            mNoteFragment.setPostil(true);
        } else {
            tuyaView = new TuyaViewPage(mContext, NoteBeanConf.createNullBitmap(mScreenWidth, mScreenHeight),
                    NoteBeanConf.createSaveBitmap(mScreenWidth, mScreenHeight),
                    NoteBeanConf.createBgNormalBitmap(mContext.getResources(), mScreenWidth, mScreenHeight));
        }

        setEraserListener(tuyaView);
        mNoteFragment.setTuyaView(tuyaView);
        mTuyaViewMap.put(position, mNoteFragment);
        mNoteFragment.setKey(position, null);
        return mNoteFragment;

    }

    @Override
    public int getCount() {
        return size;
    }

    public int addCount() {
        //        if (Runtime.getRuntime().maxMemory() / 1.13 <= Runtime.getRuntime().totalMemory()) {
        //            Toast.makeText(mContext, "超标了!!!!", Toast.LENGTH_LONG).show();
        //            return 0;
        //        }

        //如果添加图片超过内存值 则不做操作
        if (Runtime.getRuntime().maxMemory() / 1.15 <= nullBitmap.getByteCount() * size / 1.5) {
            Toast.makeText(mContext, "超标了!!!!", Toast.LENGTH_SHORT).show();
            return 0;
        }
        size++;
        notifyDataSetChanged();
        return size;
    }

    private void setEraserListener(TuyaViewPage tuyaViewPage) {
        tuyaViewPage.setOnTouchEraserListener(new TuyaViewPage.onTouchEraser() {
            @Override
            public void onTouchEraserListener(float x, float y) {
                if (mEraserView.getVisibility() == View.VISIBLE) {
                    int x1 = (int) x;
                    int y1 = (int) y;
                    int width = mEraserView.getWidth() / 2;
                    int height = (int) (mEraserView.getHeight() / 1.8);
                    mParams.setMargins(x1 - width, y1 - height, width - x1 - width,
                            height - y1 - height);
                    mEraserView.setLayoutParams(mParams);
                }
            }
        });
    }

    public void setEraserView(ImageView eraserView) {
        ViewGroup.MarginLayoutParams marginLayoutParams =
                new ViewGroup.MarginLayoutParams(eraserView.getLayoutParams());
        mParams = new RelativeLayout.LayoutParams(marginLayoutParams);
        mEraserView = eraserView;
    }

    public void setPaintSize(int currentItem, int paintSize) {
        mTuyaViewMap.get(currentItem).getTuyaView().selectPaintSize(paintSize);
    }


    /**
     * 保存所有图片
     *
     * @param currentItem
     * @param time  时间
     * @param content   标题
     * @param pwd   密码
     * @return
     * @throws Exception
     */
    private String saveZipPath = "";
    private ArrayList<File> mSavePaths;

    public String saveNote(String time, String content, String pwd, String name) {

        mNoteFragment.saveBitmap();

        mSavePaths = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            File file = saveBitmap(content, i);
            mSavePaths.add(file);
        }

        saveZipPath = AddFilesWithAESEncryption(time, content, pwd, mSavePaths, name);

        if (!saveZipPath.contains("保存出错了,错误原因")) {
            for (int i = 0; i < mSavePaths.size(); i++) {
                mSavePaths.get(i).delete();
            }
        }

        return saveZipPath;
    }

    public void savePostil(final String time, Bitmap screenBitmap) throws Exception {

        mNoteFragment.saveBitmap();
        final ArrayList<File> savePats = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            File file = savePostilBitmap(time, i, screenBitmap);
            savePats.add(file);
        }

        //        String zipPath = "";
        //        zipPath = AddFilesWithAESEncryption(time, content, pwd, savePats, null);
        //
        //        if (!zipPath.contains("保存出错了,错误原因"))
        //            for (File file : savePats) {
        //                file.delete();
        //            }
    }

    public void savePostil(final String time, String content, String pwd, Bitmap screenBitmap) throws Exception {

        mNoteFragment.saveBitmap();
        final ArrayList<File> savePats = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            File file = savePostilBitmap(content, i, screenBitmap);
            savePats.add(file);
        }

        String zipPath = "";
        zipPath = AddFilesWithAESEncryption(time, content, pwd, savePats, null);

        if (!zipPath.contains("保存出错了,错误原因"))
            for (File file : savePats) {
                file.delete();
            }
    }

    public void setWidthHeight(int width, int height) {
        //        mScreenWidth = width;
        //        mScreenHeight = height;
    }

    private File savePostilBitmap(String name, int position, Bitmap screenBitmap) throws Exception {
        //获得缓存的笔记信息
        TuyaViewBean tuyaViewBean = null;
        if (tuyaBeanList.size() > position) {
            tuyaViewBean = tuyaBeanList.get(position);
        }

        Bitmap bitmap = Bitmap.createBitmap(screenBitmap.getWidth(), screenBitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        //已经保存了的图片
        Bitmap inBitmap = null;
        if (tuyaViewBean != null && tuyaViewBean.bitmapPath != null) {
            //如果缓存里面存在当前页面的图片则用该图片作为背景图
            inBitmap = createBitmap(tuyaViewBean.bitmapPath.getPath());
        } else if (mDesFiles != null && mDesFiles.size() > position) {
            //如果加载进来的图片里面存在当前页面的图片则用该图片作为背景图
            inBitmap = createBitmap(mDesFiles.get(position).getPath());
        } else {
            //如果都没有，则代表是新创建的页面，用当前屏幕来做背景图
            inBitmap = screenBitmap;
        }
        if (inBitmap != null)
            canvas.drawBitmap(inBitmap, 0, 0, null);

        if (tuyaViewBean != null) {
            //将保存起来的绘制数据画到图片上
            for (DrawPath drawPath : tuyaViewBean.savePaths) {

                //判断笔画保存时的屏幕宽度 以作旋转角度
                if (tuyaViewBean.saveScreenWidth == mScreenWidth)
                    drawPath.rotate = 0;
                else {
                    if (tuyaViewBean.saveScreenWidth > mScreenWidth) {
                        drawPath.rotate = 270;
                    } else
                        drawPath.rotate = 90;
                }

                if (drawPath.rotate != 0 && drawPath.path != null) {
                    Matrix matrix = new Matrix();
                    float px = bitmap.getWidth() / 2f, py = bitmap.getHeight() / 2f;
                    //　交换中心点的xy坐标
                    float t = px;
                    px = py;
                    py = t;

                    matrix.postRotate(drawPath.rotate, px, py);
                    if (Math.abs(drawPath.rotate) == 90 || Math.abs(drawPath.rotate) == 270) {
                        matrix.postTranslate((py - px), -(py - px));
                    }

                    drawPath.path.transform(matrix);
                    if (drawPath.pathUp != null && drawPath.pathDown != null) {
                        drawPath.pathUp.transform(matrix);
                        drawPath.pathDown.transform(matrix);
                    }
                }

                switch (drawPath.DRAW_TYPE) {
                    case DRAW_TYPE_NORMAL:
                    case DRAW_TYPE_LINE:
                        canvas.drawPath(drawPath.path, drawPath.paint);
                        if (drawPath.pathUp != null && drawPath.pathDown != null) {
                            canvas.drawPath(drawPath.pathUp, drawPath.paintUp);
                            canvas.drawPath(drawPath.pathDown, drawPath.paintDown);
                        }
                        break;

                    case DRAW_TYPE_CIRCLE:
                        if (drawPath.rotate == 90 || drawPath.rotate == 270) {
                            float x = drawPath.circleX;
                            drawPath.circleX = drawPath.circleY;
                            drawPath.circleY = x;
                        }

                        canvas.drawCircle(drawPath.circleX, drawPath.circleY,
                                drawPath.circleRadius, drawPath.paint);
                        break;

                    case DRAW_TYPE_REC:
                        if (drawPath.rotate == 90 || drawPath.rotate == 270) {
                            int left = drawPath.rect.left;
                            int top = drawPath.rect.top;
                            int right = drawPath.rect.right;
                            int bottom = drawPath.rect.bottom;
                            drawPath.rect.set(top, left, bottom, right);
                        }
                        canvas.drawRect(drawPath.rect, drawPath.paint);
                        break;
                    default:
                        break;
                }

                Paint paint = new Paint();
                paint.setColor(tuyaViewBean.backgroundColor);
                paint.setStrokeWidth(3);
                canvas.drawPoint(0, 0, paint);
            }
        }

        String sdPath = getPostilSavaPath().getPath();
        String path = sdPath + "/" + Constants.BOOK_LOCATION + "_" + name + "_第" + (position + 1) + "张.png";
        if (mDesFiles != null && mDesFiles.size() > position) {
            path = mDesFiles.get(position).getPath();
        }
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        Log.i("TEST_", "path = " + path + "    " + file.exists());

        return file;
    }

    /**
     * 保存已绘制的图片
     *
     * @param content  标题
     * @param position 索引  @return
     * @throws Exception
     */
    public File saveBitmap(String content, int position) {

        TuyaViewBean tuyaBean = null;

        if (tuyaBeanList.size() > position) {
            tuyaBean = tuyaBeanList.get(position);
        }
        //        Bitmap bitmap = NoteBeanConf.createSaveBitmap(mScreenWidth, mScreenHeight);

        Bitmap bitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        Bitmap inBitmap = null;

        //保存的绘制信息不为空,是在图片上面做操作的
        if (null != tuyaBean && tuyaBean.bitmapPath != null) {
            inBitmap = createBitmap(tuyaBean.bitmapPath.getPath());
            //如果背景颜色没有变化,就用之前的bitmap做背景
            if (inBitmap != null) {
                //没有画布颜色
                if (!tuyaBean.isChangerBackground) {
                    canvas = new Canvas(bitmap);
                    canvas.drawBitmap(inBitmap, 0, 0, null);

                } else {
                    canvas.drawColor(tuyaBean.backgroundColor);
                }
            } else {
                canvas.drawColor(tuyaBean.backgroundColor);
            }
        } else if (mDesFiles != null && mDesFiles.size() > position) {
            inBitmap = createBitmap(mDesFiles.get(position).getPath());
            if (inBitmap != null) {
                canvas = new Canvas(bitmap);
                canvas.drawBitmap(inBitmap, 0, 0, null);
            } else {
                canvas.drawColor(Color.WHITE);
            }

        } else {//如果都未空,则用背景色作为背景
            if (tuyaBean != null && tuyaBean.BACKGROUND_TYPE == tuyaBean.BACKGROUND_NORMAL) {
                canvas.drawBitmap(NoteBeanConf.createBgNormalBitmap(mContext.getResources(), mScreenWidth, mScreenHeight), 0, 0, null);
            } else if (tuyaBean != null && tuyaBean.BACKGROUND_TYPE == tuyaBean.BACKGROUND_LINE) {
                canvas.drawBitmap(NoteBeanConf.createBgLineBitmap(mContext.getResources(), mScreenWidth, mScreenHeight), 0, 0, null);
            } else {
                canvas.drawColor(tuyaBean.backgroundColor);
            }
        }

        if (tuyaBean != null) {
            //            int rotate = 0;
            //            //            if (tuyaBeanList.size() > i && tuyaBeanList.get(i).savePaths.size() > 0)
            //            //                rotate = tuyaBeanList.get(i).savePaths.get(0).rotate;
            //            //            if (rotate != 0) {
            //            if (tuyaBean.savePaths.size() > 0 && tuyaBean.savePaths.get(0).rotate != 0) {
            //                rotate = tuyaBean.savePaths.get(0).rotate;
            //            } else {
            //                if (inBitmap != null && mScreenWidth > inBitmap.getWidth()) {
            //                    rotate = 270;
            //                } else if (inBitmap != null && mScreenWidth < inBitmap.getWidth()) {
            //                    rotate = 90;
            //                } else if (mScreenWidth > bitmap.getWidth()) {
            //                    rotate = 270;
            //                } else if (mScreenWidth < bitmap.getWidth()) {
            //                    rotate = 90;
            //                }
            //            }
            //
            //            if (rotate != 0 && rotate <= 90) {
            //                Matrix matrix = new Matrix();
            //                matrix.setRotate(rotate);
            //                if (mPosition == position) {
            //                    // 围绕原地进行旋转
            //                    if (inBitmap != null && inBitmap.getWidth() != width) {
            //                        Log.i("tuyaview", "save = inBitmap " + rotate);
            //
            //                        inBitmap = Bitmap.createBitmap(inBitmap, 0, 0, inBitmap.getWidth(), inBitmap.getHeight(), matrix, false);
            //                        canvas = new Canvas(bitmap);
            //                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            //                        canvas.drawBitmap(inBitmap, 0, 0, null);
            //                    } else if (bitmap.getWidth() != width) {
            //                        Log.i("tuyaview", "save =  " + rotate);
            //                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            //                        canvas = new Canvas(bitmap);
            //                    }
            //                }
            //            }


            //将保存起来的绘制数据画到图片上
            for (DrawPath drawPath : tuyaBean.savePaths) {

                //判断笔画保存时的屏幕宽度 以作旋转角度
                if (tuyaBean.saveScreenWidth == mScreenWidth)
                    drawPath.rotate = 0;
                else {
                    if (tuyaBean.saveScreenWidth > mScreenWidth) {
                        drawPath.rotate = 270;
                    } else
                        drawPath.rotate = 90;
                }

                if (drawPath.rotate != 0) {
                    Matrix matrix = new Matrix();
                    float px = bitmap.getWidth() / 2f, py = bitmap.getHeight() / 2f;
                    //　交换中心点的xy坐标
                    float t = px;
                    px = py;
                    py = t;

                    matrix.postRotate(drawPath.rotate, px, py);
                    if (Math.abs(drawPath.rotate) == 90 || Math.abs(drawPath.rotate) == 270) {
                        matrix.postTranslate((py - px), -(py - px));
                    }

                    drawPath.path.transform(matrix);
                    if (drawPath.pathUp != null && drawPath.pathDown != null) {
                        drawPath.pathUp.transform(matrix);
                        drawPath.pathDown.transform(matrix);
                    }
                }

                canvas.drawPath(drawPath.path, drawPath.paint);
                if (drawPath.pathUp != null && drawPath.pathDown != null) {
                    canvas.drawPath(drawPath.pathUp, drawPath.paintUp);
                    canvas.drawPath(drawPath.pathDown, drawPath.paintDown);
                }
                Paint paint = new Paint();
                paint.setColor(tuyaBean.backgroundColor);
                paint.setStrokeWidth(3);
                canvas.drawPoint(0, 0, paint);
            }
        }

        String sdPath = null;
        try {
            sdPath = getSavaPath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String path = sdPath + "/" + content + "第" + (position + 1) + "张.png";
        File file = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        return file;
    }

    /**
     * 保存已绘制的图片
     *
     * @param content 标题
     * @return
     * @throws Exception
     */
    public File saveBitmap(String content, String time) throws Exception {
        String path = mTuyaViewMap.get(0).getTuyaView().saveToSDCard(time, content);
        File file = new File(path);
        return file;
    }

    public void clear(int currentItem) {
        mTuyaViewMap.get(currentItem).getTuyaView().redo();
    }

    public void setPaintColor(int currentItem, int white) {
        mTuyaViewMap.get(currentItem).getTuyaView().selectPaintColor(white);
    }

    public void undo(int currentItem) {
        mTuyaViewMap.get(currentItem).getTuyaView().undo();
    }

    /**
     * 点击文字
     *
     * @param currentItem
     */
    public void onClickType(int currentItem) {
        //        mTuyaViewMap.get(currentItem).clickType();
        mNoteFragment.clickType();
    }


}
