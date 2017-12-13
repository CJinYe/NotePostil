package icox.cjy.notepostillibrary.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import icox.cjy.notepostillibrary.bean.TuyaViewBean;
import icox.cjy.notepostillibrary.constants.DrawPath;
import icox.cjy.notepostillibrary.constants.NoteBeanConf;
import icox.cjy.notepostillibrary.constants.NotePositConf;
import icox.cjy.notepostillibrary.utils.CacheBitmapUtil;
import icox.cjy.notepostillibrary.utils.SDcardUtil;

import static icox.cjy.notepostillibrary.constants.NotePositConf.DRAW_LINE_TYPE_EMPTY;
import static icox.cjy.notepostillibrary.constants.NotePositConf.DRAW_LINE_TYPE_JUST;
import static icox.cjy.notepostillibrary.constants.NotePositConf.DRAW_LINE_TYPE_JUST_TWO;
import static icox.cjy.notepostillibrary.constants.NotePositConf.DRAW_LINE_TYPE_WAVE;
import static icox.cjy.notepostillibrary.constants.NotePositConf.DRAW_LINE_TYPE_WAVE_TWO;
import static icox.cjy.notepostillibrary.constants.NotePositConf.DRAW_TYPE_CIRCLE;
import static icox.cjy.notepostillibrary.constants.NotePositConf.DRAW_TYPE_LINE;
import static icox.cjy.notepostillibrary.constants.NotePositConf.DRAW_TYPE_NORMAL;
import static icox.cjy.notepostillibrary.constants.NotePositConf.DRAW_TYPE_REC;


public class TuyaViewPage extends View {

    private final String TAG = "TuyaView";
    private Context context;
    public Bitmap mBitmap = null;
    private Bitmap mInBitmap = null;
    public Bitmap mSaveBitmap = null;
    private Canvas mCanvas;
    private Canvas mSaveCanvas;
    private Path mPath;
    private Paint mBitmapPaint;// 画布的画笔
    public Paint mPaint;// 真实的画笔
    private float mX, mY;// 临时点坐标
    private final float TOUCH_TOLERANCE = 2;
    // 保存Path路径的集合,用List集合来模拟栈
    public List<DrawPath> savePath = new ArrayList<>();
    // 保存已删除Path路径的集合
    private List<DrawPath> deletePath = new ArrayList<>();
    // 记录Path路径的对象
    private DrawPath mDrawPath;
    public int screenWidth, screenHeight;
    public int currentColor = Color.BLACK;//画笔颜色
    public int backgroundColor = Color.WHITE;//画布颜色
    public int currentSize = 5;//画笔大小
    public int currentStyle = 1;//画笔类型,1为正常,0为橡皮擦
    public final int PAINT_STYLE_ERASER = 0;
    public final int PAINT_STYLE_PAINT = 1;
    private boolean isRedo = false;//是否重做

    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF();
    ArrayList<Path> mPaths = new ArrayList<Path>();
    private InputMethodManager mImm;
    private long mStartTime;
    private long mEndTime;
    private float startX, startY, endX, endY;
    private Path mPathUp;
    private Path mPathDown;
    private Paint mPaintUp;
    private Paint mPaintDown;
    private float downX;
    private float downY;
    private Activity mNoteActivity;

    //是否更换了背景颜色
    public boolean mIsChangerBackground = false;
    private Matrix mMatrixTemp;
    private float mOriginalPivotX;
    private float mOriginalPivotY;
    private int mOldScreenWidth = 0;
    private Rect mRect;
    //橡皮擦的底图
    private BitmapShader mBitmapShaderEraser;

    public int BACKGROUND_TYPE = 1;
    private long mIsConfiCanvasTime;//是否是画过的回显

    public TuyaViewPage(Context context, Bitmap bitmap, Bitmap saveBitmap) {
        super(context);
        this.context = context;
        mBitmap = bitmap;
        mSaveBitmap = saveBitmap;
        mCanvas = new Canvas(mBitmap);
        mSaveCanvas = new Canvas(mSaveBitmap);
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mSaveCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mSaveCanvas.drawColor(backgroundColor);
        setBackgroundColor(backgroundColor);
        initCanvas();
    }

    public TuyaViewPage(Context context, Bitmap bitmap, Bitmap saveBitmap, Bitmap inBitmap) {
        super(context);
        this.context = context;
        mBitmap = bitmap;
        mInBitmap = inBitmap;
        mSaveBitmap = saveBitmap;
        mCanvas = new Canvas(mBitmap);
        mSaveCanvas = new Canvas(mSaveBitmap);
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mSaveCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mSaveCanvas.drawBitmap(inBitmap, 0, 0, null);
        mCanvas.drawBitmap(inBitmap, 0, 0, null);
        initCanvas();
        //        initRotate();
        //        setBackground(new BitmapDrawable(inBitmap));
    }

    private int isMeasure = 0;
    private long measureTime = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        screenHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (mOldScreenWidth == 0)
            mOldScreenWidth = screenWidth;

        int rotate = 0;
        if (mInBitmap != null && screenWidth > mInBitmap.getWidth()) {
            rotate = 90;
        } else if (mInBitmap != null && screenWidth < mInBitmap.getWidth()) {
            rotate = 270;
        } else if (screenWidth < mBitmap.getWidth() - 100) {
            rotate = 270;
        } else if (screenWidth > mBitmap.getWidth()) {
            rotate = 90;
        }
        if (rotate != 0 && isMeasure < 1) {
            //            rotate(rotate, screenWidth, screenHeight);
            rotate(rotate);
            isMeasure++;
            measureTime = System.currentTimeMillis();
        }
    }


    public void initCanvas() {
        mNoteActivity = (Activity) context;
        mImm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mMatrixTemp = new Matrix();
        setPaintStyle();

        mOriginalPivotX = mBitmap.getWidth() / 2f;
        mOriginalPivotY = mBitmap.getHeight() / 2f;
    }

    //初始化画笔样式
    public void setPaintStyle() {
        mPaint = new Paint();
        mPaintUp = new Paint();
        mPaintDown = new Paint();
        mPaint = getInitPaint(mPaint);

        if (mInBitmap != null && !isPostil) {
            this.mBitmapShaderEraser = new BitmapShader(this.mInBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        }
        if (currentStyle == 1) {
            mPaint.setStrokeWidth(currentSize);
            mPaint.setColor(currentColor);
        } else {//橡皮擦
            mPaint.setStrokeWidth(70);
            DRAW_TYPE = DRAW_TYPE_NORMAL;
            if (mBitmapShaderEraser != null) {
                mPaint.setShader(mBitmapShaderEraser);
            } else {
                mPaint.setColor(backgroundColor);
            }
        }

        mPaintUp = getInitPaint(mPaintUp);
        mPaintDown = getInitPaint(mPaintDown);
        mPaintUp.setStrokeWidth(currentSize);
        mPaintDown.setStrokeWidth(currentSize);

        //设置虚线样式
        if (DRAW_TYPE == DRAW_TYPE_LINE && DRAW_LINE_TYPE == DRAW_LINE_TYPE_EMPTY) {
            mPaint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 100));
        }

    }

    private Paint getInitPaint(Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        paint.setStrokeCap(Paint.Cap.ROUND);// 形状
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setStrokeWidth(currentSize);
        paint.setColor(currentColor);
        return paint;
    }


    /**
     * 撤销
     * 撤销的核心思想就是将画布清空，
     * 将保存下来的Path路径最后一个移除掉，
     * 重新将路径画在画布上面。
     */
    public void undo() {
        if (savePath != null && savePath.size() > 0) {
            DrawPath drawPath = savePath.get(savePath.size() - 1);
            deletePath.add(drawPath);
            savePath.remove(savePath.size() - 1);
            if (mInBitmap != null && !isRedo) {
                setPaintStyle();
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                mSaveCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                mSaveCanvas.drawBitmap(mInBitmap, 0, 0, null);
                mCanvas.drawBitmap(mInBitmap, 0, 0, null);
                //                setBackground(new BitmapDrawable(mInBitmap));
                for (DrawPath drawPath1 : savePath) {
                    recoverPath(drawPath1);
                }
            } else {
                redrawOnBitmap();
            }
            invalidate();// 刷新
        }
    }

    private void recoverPath(DrawPath drawPath) {
        if (mInBitmap != null && !isPostil && drawPath.paint.getShader() != null) {
            this.mBitmapShaderEraser = new BitmapShader(this.mInBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            drawPath.paint.setShader(mBitmapShaderEraser);
        }
        switch (drawPath.DRAW_TYPE) {
            case DRAW_TYPE_NORMAL:
            case DRAW_TYPE_LINE:
                mCanvas.drawPath(drawPath.path, drawPath.paint);
                mSaveCanvas.drawPath(drawPath.path, drawPath.paint);
                if (drawPath.paintUp != null) {
                    mCanvas.drawPath(drawPath.pathUp, drawPath.paintUp);
                    mSaveCanvas.drawPath(drawPath.pathUp, drawPath.paintUp);
                    mCanvas.drawPath(drawPath.pathDown, drawPath.paintDown);
                    mSaveCanvas.drawPath(drawPath.pathDown, drawPath.paintDown);
                }
                break;
            case DRAW_TYPE_CIRCLE:
                mCanvas.drawCircle(drawPath.circleX, drawPath.circleY,
                        drawPath.circleRadius, drawPath.paint);
                mSaveCanvas.drawCircle(drawPath.circleX, drawPath.circleY,
                        drawPath.circleRadius, drawPath.paint);
                break;
            case DRAW_TYPE_REC:
                mCanvas.drawRect(drawPath.rect, drawPath.paint);
                mSaveCanvas.drawRect(drawPath.rect, drawPath.paint);
                break;
            default:
                break;
        }
    }

    private void recoverPath(int rotate, int width, int height) {

        if (rotate == 0) {
            for (DrawPath drawPath : savePath) {
                recoverPath(drawPath);
            }
            return;
        }

        Matrix matrix = new Matrix();
        float px = width / 2f, py = height / 2f;
        //　交换中心点的xy坐标
        float t = px;
        px = py;
        py = t;

        matrix.postRotate(rotate, px, py);
        if (Math.abs(rotate) == 90 || Math.abs(rotate) == 270) {
            matrix.postTranslate((py - px), -(py - px));
        }

        for (DrawPath drawPath : savePath) {

            if (mInBitmap != null && !isPostil && drawPath.paint.getShader() != null) {
                this.mBitmapShaderEraser = new BitmapShader(this.mInBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                drawPath.paint.setShader(mBitmapShaderEraser);
            }

            switch (drawPath.DRAW_TYPE) {
                case DRAW_TYPE_NORMAL:
                case DRAW_TYPE_LINE:
                    if (drawPath.rotate != rotate) {
                        drawPath.path.transform(matrix);
                    }
                    mCanvas.drawPath(drawPath.path, drawPath.paint);
                    mSaveCanvas.drawPath(drawPath.path, drawPath.paint);
                    if (drawPath.paintUp != null) {
                        if (drawPath.rotate != rotate) {
                            drawPath.pathUp.transform(matrix);
                            drawPath.pathDown.transform(matrix);
                        }
                        mCanvas.drawPath(drawPath.pathUp, drawPath.paintUp);
                        mSaveCanvas.drawPath(drawPath.pathUp, drawPath.paintUp);
                        mCanvas.drawPath(drawPath.pathDown, drawPath.paintDown);
                        mSaveCanvas.drawPath(drawPath.pathDown, drawPath.paintDown);
                    }
                    break;

                case DRAW_TYPE_CIRCLE:
                    if (rotate == 90 || rotate == 270) {
                        float x = drawPath.circleX;
                        drawPath.circleX = drawPath.circleY;
                        drawPath.circleY = x;
                    }

                    mCanvas.drawCircle(drawPath.circleX, drawPath.circleY,
                            drawPath.circleRadius, drawPath.paint);
                    mSaveCanvas.drawCircle(drawPath.circleX, drawPath.circleY,
                            drawPath.circleRadius, drawPath.paint);
                    break;

                case DRAW_TYPE_REC:
                    if (rotate == 90 || rotate == 270) {
                        int left = drawPath.rect.left;
                        int top = drawPath.rect.top;
                        int right = drawPath.rect.right;
                        int bottom = drawPath.rect.bottom;
                        drawPath.rect.set(top, left, bottom, right);
                    }
                    mCanvas.drawRect(drawPath.rect, drawPath.paint);
                    mSaveCanvas.drawRect(drawPath.rect, drawPath.paint);
                    break;
                default:
                    break;
            }


            drawPath.rotate = rotate;

        }
    }

    /**
     * 重做
     */
    public void redo() {
        isRedo = true;
        if (savePath != null && savePath.size() > 0) {
            savePath.clear();
            redrawOnBitmap();
        }

        if (mInBitmap != null) {
            redrawOnBitmap();
            mCanvas.drawBitmap(mInBitmap, 0, 0, null);
            mSaveCanvas.drawBitmap(mInBitmap, 0, 0, null);
        }
    }

    private void redrawOnBitmap() {
        setPaintStyle();

        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mSaveCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mSaveCanvas.drawColor(backgroundColor);

        for (DrawPath drawPath : savePath) {
            recoverPath(drawPath);
        }
        invalidate();// 刷新
    }

    private void refreshCanvas() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        options.inPurgeable = true;
        options.inInputShareable = true;
    }

    /**
     * 恢复，恢复的核心就是将删除的那条路径重新添加到savapath中重新绘画即可
     */
    public void recover() {
        if (deletePath.size() > 0) {
            //将删除的路径列表中的最后一个，也就是最顶端路径取出（栈）,并加入路径保存列表中
            DrawPath dp = deletePath.get(deletePath.size() - 1);
            savePath.add(dp);
            //将取出的路径重绘在画布上
            recoverPath(dp);
            //将该路径从删除的路径列表中去除
            deletePath.remove(deletePath.size() - 1);
            invalidate();
        }
    }

    private int mode = 0;
    private int longSize;
    private boolean isMulti = false;
    private boolean isWrite = false;

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ((mCentreTranX + mTransX) != 0) {
            float left = (mCentreTranX + mTransX) / (mPrivateScale * mScale);
            float top = (mCentreTranY + mTransY) / (mPrivateScale * mScale);
            // 画布和图片共用一个坐标系，只需要处理屏幕坐标系到图片（画布）坐标系的映射关系
            if (mPrivateScale * mScale != 0) {
                canvas.scale(mPrivateScale * mScale, mPrivateScale * mScale); // 缩放画布
            }

            if (!Float.isNaN(left) && !Float.isInfinite(left)) {
                canvas.translate(left, top); // 偏移画布
            }
        }

        canvas.save();

        // 将前面已经画过得显示出来
        canvas.drawBitmap(mBitmap, 0, 0, null);

        //                setBackground(new BitmapDrawable(mSaveBitmap));
        // 实时的显示
        if (mPathUp != null) {
            canvas.drawPath(mPathUp, mPaintUp);
        }
        if (mPathDown != null) {
            canvas.drawPath(mPathDown, mPaintDown);
        }
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }

        if (mLineTwoPath != null) {
            canvas.drawPath(mLineTwoPath, mPaint);
        }

        if (mRect != null) {
            canvas.drawRect(mRect, mPaint);
        }

        if (mX != 0 && mY != 0)
            switch (DRAW_TYPE) {
                case DRAW_TYPE_CIRCLE:
                    float radius =
                            Math.abs(mX - downX) > Math.abs(mY - downY)
                                    ? Math.abs(mX - downX)
                                    : Math.abs(mY - downY);

                    canvas.drawCircle(downX, downY,
                            radius
                            , mPaint);
                    break;
            }

        canvas.restore();
    }

    /**
     * 设置画画的样式
     *
     * @param type
     */
    public void setDrawType(int type) {
        DRAW_TYPE = type;
        setPaintStyle();
    }

    /**
     * 设置画直线的样式
     *
     * @param type
     */
    public void setDrawLineType(int type) {
        DRAW_LINE_TYPE = type;
        setPaintStyle();
    }

    private int touchType = 0;
    private final int TOUCH_TYPE_CLEAR = 3; //三点触摸,擦除
    private final int TOUCH_TYPE_ONE = 1; //单点触摸,画画
    private final int TOUCH_TYPE_ZOOM = 2; //两点触摸,放大缩小

    private float mNewDist, mOldDist;
    private float mOldScale = 0;
    private float mTouchSlop = 5;
    private final float mMaxScale = 4f; // 最大缩放倍数
    private final float mMinScale = 1f; // 最小缩放倍数
    private float mTouchCentreX, mTouchCentreY, mToucheCentreXOnGraffiti, mToucheCentreYOnGraffiti;

    private float mScale;
    private long firstTouchTime = 0;
    private long firstTouchPointerTime = 0;

    public int DRAW_TYPE = DRAW_TYPE_NORMAL;              //画画的样式

    public int DRAW_LINE_TYPE = DRAW_LINE_TYPE_JUST;         //画直线时的样式

    private Path mLineTwoPath;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //        if (!isMulti) {
        initEraser(event.getX(), event.getY());

        float x = toX(event.getX());
        float y = toY(event.getY());

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                //画的时候如果有软键盘则隐藏起来
                if (mImm != null) {
                    mImm.hideSoftInputFromWindow(mNoteActivity.getWindow().getDecorView().getWindowToken(),
                            0);
                }

                mode = 1;
                mStartTime = System.currentTimeMillis();
                firstTouchTime = System.currentTimeMillis();


                switch (DRAW_TYPE) {
                    case DRAW_TYPE_NORMAL:
                    case DRAW_TYPE_LINE:
                        // 每次down下去重新new一个Path
                        mPath = new Path();
                        //每一次记录的路径对象是不一样的
                        mPath.moveTo(x, y);
                        //如果是画线状态并且是双线
                        if (DRAW_TYPE == DRAW_TYPE_LINE &&
                                (DRAW_LINE_TYPE == DRAW_LINE_TYPE_JUST_TWO || DRAW_LINE_TYPE == DRAW_LINE_TYPE_WAVE_TWO)) {
                            mLineTwoPath = new Path();
                        }
                        break;
                    case DRAW_TYPE_CIRCLE:

                        break;
                    case DRAW_TYPE_REC:
                        mRect = new Rect(0, 0, 0, 0);
                        break;

                    default:
                        break;
                }
                mDrawPath = new DrawPath();

                mX = x;
                mY = y;
                startX = x;
                startY = y;
                downX = x;
                downY = y;
                longSize = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                //                if (mode != 2) {
                //                    if (touchType == 0 && TouchMeasureUtil.ThreeTouchDistance(event)) {
                //                        //修改画笔样式，改成橡皮擦状态
                //                        threeEraser();
                //                        canvasMovePath(x, y);
                //                    } else if (touchType == 0 && mode < 2) {
                //                        if (!isEraser) {
                //                            //修改画笔样式，改成画画状态
                //                            oneCanvasLine();
                //                        }
                //                        if (firstTouchPointerTime == 0 && System.currentTimeMillis() - firstTouchTime > 1000) {
                //                            touchType = TOUCH_TYPE_ONE;
                //                        } else {
                //                            if (firstTouchPointerTime - firstTouchTime > 1000) {
                //                                touchType = TOUCH_TYPE_ONE;
                //                            }
                //                        }
                //                        //开始画画
                //                        canvasMovePath(x, y);
                //                    } else if (touchType == 0 && !isEraser) {
                //                        //两点放大缩小
                //                        twoZoom(event);
                //                    } else if (touchType != TOUCH_TYPE_ZOOM) {
                //                        //开始画画
                //                        canvasMovePath(x, y);
                //                    }
                //                } else {
                //                    if (touchType == 0) {
                //                        twoZoom(event);
                //                    } else {
                //                        canvasMovePath(x, y);
                //                    }
                //                }

                canvasMovePath(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //                if (!isMulti) {//不是多点触控
                //                if (touchType != TOUCH_TYPE_ZOOM) {//不是多点触控
                //                    touchUp(x, y);
                //                }
                touchUp(x, y);

                mPath = null;// 重新置空
                mPathUp = null;
                mPathDown = null;
                mRect = null;
                mLineTwoPath = null;
                lastResult = 0;
                isMulti = false;//不是多点触控
                lastCalculateTime = 0;
                touchType = 0;
                mode = 0;
                firstTouchTime = 0;
                firstTouchPointerTime = 0;

                mX = 0;
                mY = 0;

                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //第二点触控抬起
                mode -= 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //第二点触控
                if (firstTouchPointerTime == 0)
                    firstTouchPointerTime = System.currentTimeMillis();
                mode += 1;
                mOldDist = spacing(event);// 两点按下时的距离
                mOldScale = getScale();
                mTouchCentreX = (event.getX(0) + event.getX(1)) / 2;// 不用减trans
                mTouchCentreY = (event.getY(0) + event.getY(1)) / 2;
                mToucheCentreXOnGraffiti = toX(mTouchCentreX);
                mToucheCentreYOnGraffiti = toY(mTouchCentreY);
                break;
        }

        //                        invalidate((int) (dirtyRect.left - currentSize),
        //                                (int) (dirtyRect.top - currentSize),
        //                                (int) (dirtyRect.right - currentSize),
        //                                (int) (dirtyRect.bottom - currentSize));

        lastTouchX = x;
        lastTouchY = y;

        return true;
    }

    /**
     * 触摸结束时把数据保存下来
     *
     * @param x
     * @param y
     */
    private void touchUp(float x, float y) {

        switch (DRAW_TYPE) {
            case DRAW_TYPE_NORMAL:  //正常状态
                float endX2 = (mX + x) / 2;
                float endY2 = (mY + y) / 2;
                mPath.quadTo(mX, mY, endX2, endY2);
                //把路径画到图片中
                mCanvas.drawPath(mPath, mPaint);
                mSaveCanvas.drawPath(mPath, mPaint);

                mEndTime = System.currentTimeMillis();
                endX = x;
                endY = y;
                if (currentStyle == 1) {//不是橡皮擦
                    calculate();
                    PathMeasure pathMeasure = new PathMeasure(mPath, false);
                    if (pathMeasure.getLength() < 60) {
                        mPath.addPath(mPathUp, 1.1f, 1.1f);
                        mPath.addPath(mPathDown, -1f, -1f);
                    }
                    mCanvas.drawPath(mPathUp, mPaintUp);
                    mCanvas.drawPath(mPathDown, mPaintDown);
                    mSaveCanvas.drawPath(mPathUp, mPaintUp);
                    mSaveCanvas.drawPath(mPathDown, mPaintDown);
                }

                //把路径保存到集合
                mDrawPath.path = mPath;
                mDrawPath.DRAW_TYPE = DRAW_TYPE_NORMAL;
                break;

            case DRAW_TYPE_LINE:    //画直线状态
                canvasMoveLine(x, y);
                if (mLineTwoPath != null)
                    mPath.addPath(mLineTwoPath);
                //把路径画到图片中
                mCanvas.drawPath(mPath, mPaint);
                mSaveCanvas.drawPath(mPath, mPaint);
                //把路径保存到集合
                mDrawPath.path = mPath;
                mDrawPath.DRAW_TYPE = DRAW_TYPE_LINE;
                break;
            case DRAW_TYPE_CIRCLE:  //画圆状态
                float radius = Math.abs(mX - downX) > Math.abs(mY - downY)
                        ? Math.abs(mX - downX) : Math.abs(mY - downY);
                mCanvas.drawCircle(downX, downY, radius, mPaint);
                mSaveCanvas.drawCircle(downX, downY, radius, mPaint);
                mDrawPath.circleX = downX;
                mDrawPath.circleY = downY;
                mDrawPath.circleRadius = radius;
                mDrawPath.DRAW_TYPE = DRAW_TYPE_CIRCLE;
                break;
            case DRAW_TYPE_REC:     //画矩形状态
                mCanvas.drawRect(mRect, mPaint);
                mSaveCanvas.drawRect(mRect, mPaint);
                mDrawPath.rect = mRect;
                mDrawPath.DRAW_TYPE = DRAW_TYPE_REC;
                break;

            default:
                break;
        }
        mDrawPath.paint = mPaint;
        savePath.add(mDrawPath);
    }

    /**
     * 把数据画到画布上
     *
     * @param x
     * @param y
     */
    private void canvasMovePath(float x, float y) {
        endX = x;
        endY = y;
        mEndTime = System.currentTimeMillis();

        if (DRAW_TYPE == DRAW_TYPE_NORMAL &&
                currentStyle == 1 &&
                (Math.abs(x - mX) > 3 || Math.abs(y - mY) > 3)) {
            calculate();
        }

        startX = x;
        startY = y;
        mStartTime = System.currentTimeMillis();

        switch (DRAW_TYPE) {
            case DRAW_TYPE_NORMAL:
                if (Math.abs(x - mX) > 3 || Math.abs(y - mY) > 3) {
                    float endX = (mX + x) / 2;
                    float endY = (mY + y) / 2;
                    mPath.quadTo(mX, mY, endX, endY);
                }
                mX = x;
                mY = y;
                break;
            case DRAW_TYPE_CIRCLE:
                mX = x;
                mY = y;
                break;

            case DRAW_TYPE_LINE:
                if (Math.abs(x - mX) > 3 || Math.abs(y - mY) > 3)
                    canvasMoveLine(x, y);
                break;

            case DRAW_TYPE_REC:
                mRect.left = (int) downX;
                mRect.top = (int) downY;
                mRect.right = (int) x;
                mRect.bottom = (int) y;
                //如果用户滑动区域是在开始区域的左边
                if (x < downX) {
                    mRect.left = (int) x;
                    mRect.right = (int) downX;
                }
                //如果用户滑动区域是在开始区域的上边
                if (y < downY) {
                    mRect.top = (int) y;
                    mRect.bottom = (int) downY;
                }

                break;
            default:
                break;
        }

        isWrite = true;

    }


    /**
     * 触摸移动时画直线状态下的处理
     *
     * @param x
     * @param y
     */
    private void canvasMoveLine(float x, float y) {
        float width = Math.abs(x - downX);

        switch (DRAW_LINE_TYPE) {
            case DRAW_LINE_TYPE_JUST:       //单直线
                mPath.reset();
                mPath.moveTo(downX, downY);
                mPath.lineTo(x, y);
                break;
            case DRAW_LINE_TYPE_JUST_TWO:   //双直线
                mPath.reset();
                mPath.moveTo(downX, downY);
                mPath.lineTo(x, downY);
                mLineTwoPath.reset();
                mLineTwoPath.moveTo(downX, downY + NotePositConf.DARW_LINE_TWO_DISTANCE);
                mLineTwoPath.lineTo(x, downY + NotePositConf.DARW_LINE_TWO_DISTANCE);
                //                mPath.addPath(mLineTwoPath);
                break;
            case DRAW_LINE_TYPE_EMPTY:      //虚线
                mPath.reset();
                mPath.moveTo(downX, downY);
                mPath.lineTo(x, y);
                break;
            case DRAW_LINE_TYPE_WAVE:       //波浪线
                mPath.moveTo(downX, downY);
                for (int i = (int) NotePositConf.DARW_LINE_WAVE_WIDTH; i < width; i += NotePositConf.DARW_LINE_WAVE_WIDTH * 2) {
                    if (x - downX > 0) {
                        float xx = downX + i;
                        mPath.quadTo(xx, downY - NotePositConf.DARW_LINE_WAVE_HEIGHT,
                                xx + NotePositConf.DARW_LINE_WAVE_WIDTH, downY);
                    } else {
                        float xx = downX - i;
                        mPath.quadTo(xx, downY - NotePositConf.DARW_LINE_WAVE_HEIGHT
                                , xx - NotePositConf.DARW_LINE_WAVE_WIDTH, downY);
                    }
                }
                break;
            case DRAW_LINE_TYPE_WAVE_TWO:   //双波浪线
                mPath.moveTo(downX, downY);
                mLineTwoPath.moveTo(downX, downY - NotePositConf.DARW_LINE_TWO_DISTANCE);
                for (int i = (int) NotePositConf.DARW_LINE_WAVE_WIDTH; i < width; i += NotePositConf.DARW_LINE_WAVE_WIDTH * 2) {
                    if (x - downX > 0) {
                        float xx = downX + i;
                        mPath.quadTo(xx, downY - NotePositConf.DARW_LINE_WAVE_HEIGHT,
                                xx + NotePositConf.DARW_LINE_WAVE_WIDTH, downY);
                        mLineTwoPath.quadTo(xx, downY - NotePositConf.DARW_LINE_WAVE_HEIGHT - NotePositConf.DARW_LINE_TWO_DISTANCE,
                                xx + NotePositConf.DARW_LINE_WAVE_WIDTH, downY - NotePositConf.DARW_LINE_TWO_DISTANCE);
                    } else {
                        float xx = downX - i;
                        mPath.quadTo(xx, downY - NotePositConf.DARW_LINE_WAVE_HEIGHT
                                , xx - NotePositConf.DARW_LINE_WAVE_WIDTH, downY);
                        mLineTwoPath.quadTo(xx, downY - NotePositConf.DARW_LINE_WAVE_HEIGHT - NotePositConf.DARW_LINE_TWO_DISTANCE,
                                xx - NotePositConf.DARW_LINE_WAVE_WIDTH, downY - NotePositConf.DARW_LINE_TWO_DISTANCE);
                    }
                }
                //                mPath.addPath(mLineTwoPath);
                break;

            default:
                break;
        }
    }


    /**
     * 两点放大缩小
     *
     * @param event
     */
    private void twoZoom(MotionEvent event) {
        touchType = TOUCH_TYPE_ZOOM;
        mNewDist = spacing(event);// 两点滑动时的距离
        if (Math.abs(mNewDist - mOldDist) >= mTouchSlop) {
            float scale = mNewDist / mOldDist;
            scale = mOldScale * scale;

            if (scale > mMaxScale) {
                scale = mMaxScale;
            }
            if (scale < mMinScale) { // 最小倍数
                scale = mMinScale;
            }
            // 围绕坐标(0,0)缩放图片
            setScale(scale);
            // 缩放后，偏移图片，以产生围绕某个点缩放的效果
            float transX = toTransX(mTouchCentreX, mToucheCentreXOnGraffiti);
            float transY = toTransY(mTouchCentreY, mToucheCentreYOnGraffiti);
            setTrans(transX, transY);
        }
    }

    /**
     * 三点触摸橡皮擦功能
     */
    private void threeEraser() {
        if (currentStyle == 1) {
            currentStyle = 0;
            setPaintStyle();
            mPaint.setStrokeWidth(100);
            //批注的时候用全透明作为橡皮擦色
            if (currentStyle == 0 && isPostil) {
                mPaint.setColor(Color.TRANSPARENT);
                mPaint.setAlpha(0);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            } else {
                //                mPaint.setShader(mBitmapShaderEraser);
            }
        }
        touchType = TOUCH_TYPE_CLEAR;
    }

    /**
     * 一点触摸画线
     */
    private void oneCanvasLine() {
        if (currentStyle == 0 && touchType != TOUCH_TYPE_CLEAR) {
            currentStyle = 1;
            setPaintStyle();
        }
    }


    // 计算两个触摸点的中点
    private PointF calMidPoint(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }


    private int mPrivateHeight, mPrivateWidth;// 图片在缩放mPrivateScale倍数的情况下，适应屏幕（mScale=1）时的大小（肉眼看到的在屏幕上的大小）
    private float mCentreTranX, mCentreTranY;// 图片在缩放mPrivateScale倍数的情况下，居中（mScale=1）时的偏移（肉眼看到的在屏幕上的偏移）
    private float mTransX = 0, mTransY = 0; // 图片在相对于居中时且在缩放mScale倍数的情况下的偏移量 （ 图片真实偏移量为　(mCentreTranX + mTransX)/mPrivateScale*mScale ）
    private float mPrivateScale = 1f; // 图片适应屏幕（mScale=1）时的缩放倍数

    /**
     * 缩放倍数，图片真实的缩放倍数为 mPrivateScale*mScale
     *
     * @param scale
     */
    public void setScale(float scale) {
        this.mScale = scale;
        judgePosition();
        resetMatrix();
        invalidate();
    }

    public float getScale() {
        return mScale;
    }

    public void setTrans(float transX, float transY) {
        mTransX = transX;
        mTransY = transY;
        judgePosition();
        resetMatrix();
        invalidate();
    }

    /**
     * 将屏幕触摸坐标x转换成在图片中的坐标
     */
    public float toX(float touchX) {
        float touch = (touchX - mCentreTranX - mTransX) / (mPrivateScale * mScale);
        if (Float.isNaN(touch) || Float.isInfinite(touch)) {
            touch = touchX;
        }
        return touch;
    }

    /**
     * 将屏幕触摸坐标y转换成在图片中的坐标
     */
    public float toY(float touchY) {
        float touch = (touchY - mCentreTranY - mTransY) / (mPrivateScale * mScale);
        if (Float.isNaN(touch) || Float.isInfinite(touch)) {
            touch = touchY;
        }
        return touch;
    }

    private void resetMatrix() {
        // 如果使用了自定义的橡皮擦底图，则需要调整矩阵
        if (currentStyle == PAINT_STYLE_ERASER) {
            //            mMatrixTemp.reset();
            //            mMatrixTemp.preScale(mBitmap.getWidth() * 1f, mBitmap.getHeight() * 1f);
            //            mBitmapShaderEraser.getLocalMatrix(mMatrixTemp);
        }
    }

    /**
     * 坐标换算
     * （公式由toX()中的公式推算出）
     *
     * @param touchX    触摸坐标
     * @param graffitiX 在涂鸦图片中的坐标
     * @return 偏移量
     */
    public final float toTransX(float touchX, float graffitiX) {
        return -graffitiX * (mPrivateScale * mScale) + touchX - mCentreTranX;
    }

    public final float toTransY(float touchY, float graffitiY) {
        return -graffitiY * (mPrivateScale * mScale) + touchY - mCentreTranY;
    }

    /**
     * 调整图片位置
     * <p>
     * 明白下面一点很重要：
     * 假设不考虑任何缩放，图片就是肉眼看到的那么大，此时图片的大小width =  mPrivateWidth * mScale ,
     * 偏移量x = mCentreTranX + mTransX，而view的大小为width = getWidth()。height和偏移量y以此类推。
     */
    private void judgePosition() {
        boolean changed = false;
        if (mPrivateWidth * mScale < getWidth()) { // 限制在view范围内
            if (mTransX + mCentreTranX < 0) {
                mTransX = -mCentreTranX;
                changed = true;
            } else if (mTransX + mCentreTranX + mPrivateWidth * mScale > getWidth()) {
                mTransX = getWidth() - mCentreTranX - mPrivateWidth * mScale;
                changed = true;
            }
        } else { // 限制在view范围外
            if (mTransX + mCentreTranX > 0) {
                mTransX = -mCentreTranX;
                changed = true;
            } else if (mTransX + mCentreTranX + mPrivateWidth * mScale < getWidth()) {
                mTransX = getWidth() - mCentreTranX - mPrivateWidth * mScale;
                changed = true;
            }
        }
        if (mPrivateHeight * mScale < getHeight()) { // 限制在view范围内
            if (mTransY + mCentreTranY < 0) {
                mTransY = -mCentreTranY;
                changed = true;
            } else if (mTransY + mCentreTranY + mPrivateHeight * mScale > getHeight()) {
                mTransY = getHeight() - mCentreTranY - mPrivateHeight * mScale;
                changed = true;
            }
        } else { // 限制在view范围外
            if (mTransY + mCentreTranY > 0) {
                mTransY = -mCentreTranY;
                changed = true;
            } else if (mTransY + mCentreTranY + mPrivateHeight * mScale < getHeight()) {
                mTransY = getHeight() - mCentreTranY - mPrivateHeight * mScale;
                changed = true;
            }
        }
        if (changed) {
            resetMatrix();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldw != 0 && w != oldw) {
            int rotate = 0;
            if (w > oldw) {
                rotate = 90;
            } else {
                rotate = 270;
            }
            screenWidth = w;
            screenHeight = h;
            if (isMeasure > 0 && System.currentTimeMillis() - measureTime > 500) {
                rotate(rotate, w, h);
            } else if (isMeasure == 1) {
                isMeasure++;
            } else {
                rotate(rotate, w, h);
            }
        }
        setBG();
    }


    private void setBG() {// 不用resize preview
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float nw = w * 1f / getWidth();
        float nh = h * 1f / getHeight();
        if (nw > nh) {
            mPrivateScale = 1 / nw;
            mPrivateWidth = getWidth();
            mPrivateHeight = (int) (h * mPrivateScale);
        } else {
            mPrivateScale = 1 / nh;
            mPrivateWidth = (int) (w * mPrivateScale);
            mPrivateHeight = getHeight();
        }
        // 使图片居中
        mCentreTranX = (getWidth() - mPrivateWidth) / 2f;
        mCentreTranY = (getHeight() - mPrivateHeight) / 2f;

        resetMatrix();
        //        initCanvas();
        invalidate();
    }

    public void rotate(int rotate, int width, int height) {
        Log.i("TESR_", "sizechang 旋转呀 = " + rotate);

        Matrix matrix = new Matrix();
        matrix.setRotate(rotate);
        // 围绕原地进行旋转
        if (mInBitmap != null) {

            if (mInBitmap.getWidth() != screenWidth)
                if (BACKGROUND_TYPE == 0) {//如果背景是有图背景
                    mInBitmap = Bitmap.createBitmap(mInBitmap, 0, 0, mInBitmap.getWidth(), mInBitmap.getHeight(), matrix, false);
                } else if (BACKGROUND_TYPE == 1) {//如果背景是纸质背景
                    mInBitmap = NoteBeanConf.createBgNormalBitmap(context.getResources(), screenWidth, screenHeight);
                } else if (BACKGROUND_TYPE == 2) {//如果背景是虚线
                    mInBitmap = NoteBeanConf.createBgLineBitmap(context.getResources(), screenWidth, screenHeight);
                }

            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
            mSaveBitmap = mBitmap;
            mCanvas = new Canvas(mBitmap);
            mSaveCanvas = new Canvas(mSaveBitmap);
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mSaveCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mCanvas.drawBitmap(mInBitmap, 0, 0, null);
            mSaveCanvas.drawBitmap(mInBitmap, 0, 0, null);
        } else {
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
            mSaveBitmap = mBitmap;
            mCanvas = new Canvas(mBitmap);
            mSaveCanvas = new Canvas(mSaveBitmap);
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mSaveCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }

        recoverPath(rotate, width, height);
    }

    public void rotate(int rotate) {
        Log.i("TESR_", "onmeasure 旋转呀 = " + rotate);

        Matrix matrix = new Matrix();
        matrix.setRotate(rotate);
        // 围绕原地进行旋转
        if (mInBitmap != null) {
            if (mBitmap.getWidth() != screenWidth) {

                if (BACKGROUND_TYPE == 0) {//如果背景是有图背景
                    mInBitmap = Bitmap.createBitmap(mInBitmap, 0, 0, mInBitmap.getWidth(), mInBitmap.getHeight(), matrix, false);
                } else if (BACKGROUND_TYPE == 1) {//如果背景是纸质背景
                    mInBitmap = NoteBeanConf.createBgNormalBitmap(context.getResources(), screenWidth, screenHeight);
                } else if (BACKGROUND_TYPE == 2) {//如果背景是虚线
                    mInBitmap = NoteBeanConf.createBgLineBitmap(context.getResources(), screenWidth, screenHeight);
                }

                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
                mSaveBitmap = mBitmap;
                mCanvas = new Canvas(mBitmap);
                mSaveCanvas = new Canvas(mSaveBitmap);
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                mSaveCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                mCanvas.drawBitmap(mInBitmap, 0, 0, null);
                mSaveCanvas.drawBitmap(mInBitmap, 0, 0, null);
            } else {
                mInBitmap = Bitmap.createBitmap(mInBitmap, 0, 0, mInBitmap.getWidth(), mInBitmap.getHeight(), matrix, false);
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                mSaveCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                mCanvas.drawBitmap(mInBitmap, 0, 0, null);
                mSaveCanvas.drawBitmap(mInBitmap, 0, 0, null);
            }
        } else {
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
            mSaveBitmap = mBitmap;
            mCanvas = new Canvas(mBitmap);
            mSaveCanvas = new Canvas(mSaveBitmap);
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mSaveCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
        //        if (isPostil) {
        //            recoverPath(0, screenWidth, screenHeight);
        //        } else
        if (mIsConfiCanvasTime == 0 || System.currentTimeMillis() - mIsConfiCanvasTime > 500) {
            recoverPath(rotate, screenWidth, screenHeight);
        } else {
            recoverPath(0, screenWidth, screenHeight);
        }
    }

    //上一次计算的时间,如果间隔太长就不执行
    private long lastCalculateTime = 0;

    private void calculate() {

        //如果两点相隔时间太长就返回
        if (lastCalculateTime != 0 && (System.currentTimeMillis() - lastCalculateTime) > 500) {
            lastCalculateTime = System.currentTimeMillis();
            mPathDown.moveTo(endX, endY);
            mPathUp.moveTo(endX, endY);
            return;
        }

        float velocity = velocityFrom2();
        float radius = (float) (controlPaint(velocity, currentSize) / 1.8);
        float ratio;
        if (screenWidth > screenHeight)
            ratio = (float) screenWidth / 1280;
        else
            ratio = (float) screenHeight / 1280;

        radius = radius * ratio;

        // 根据角度算出四边形的四个点
        float offsetX = (float) (radius * Math.sin(Math.atan((endY - startY) / (endX - startX))));
        float offsetY = (float) (radius * Math.cos(Math.atan((endY - startY) / (endX - startX))));

        offsetX = Math.abs(offsetX);
        offsetY = Math.abs(offsetY);
        if (Float.isNaN(offsetX) || Float.isInfinite(offsetX)) {
            offsetX = 1;
        }
        if (Float.isNaN(offsetY) || Float.isInfinite(offsetY)) {
            offsetY = 1;
        }

        float x1 = startX - offsetX;
        float y1 = startY + offsetY;

        float x2 = endX - offsetX;
        float y2 = endY + offsetY;

        float x3 = endX + offsetX;
        float y3 = endY - offsetY;

        float x4 = startX + offsetX;
        float y4 = startY - offsetY;

        if (mPathUp == null) {
            mPathUp = new Path();
            mPathUp.moveTo(x1, y1);
            //            mPathUp.lineTo(x2,y2);
            float endX1 = (x2 + x1) / 2;
            float endY1 = (y2 + y1) / 2;
            mPathUp.quadTo(x1, y1, endX1, endY1);

            mPathDown = new Path();
            mPathDown.moveTo(x4, y4);
            //            mPathDown.lineTo(x3,y3);
            float endX2 = (x3 + x4) / 2;
            float endY2 = (y4 + y3) / 2;
            mPathDown.quadTo(x4, y4, endX2, endY2);

            mDrawPath.paintUp = mPaintUp;
            mDrawPath.pathUp = mPathUp;
            mDrawPath.paintDown = mPaintDown;
            mDrawPath.pathDown = mPathDown;
        } else {
            float endX1 = (x2 + x1) / 2;
            float endY1 = (y2 + y1) / 2;
            mPathUp.quadTo(x1, y1, endX1, endY1);

            float endX2 = (x3 + x4) / 2;
            float endY2 = (y4 + y3) / 2;
            mPathDown.quadTo(x4, y4, endX2, endY2);
        }

        lastCalculateTime = System.currentTimeMillis();

    }

    private final float KEY_PAINT_WIDTH = 2.1f;
    private float lastResult = 0;

    private float controlPaint(double velocity, float paintSize) {
        //余弦函数
        //y=0.5*[cos(x*PI)+1]
        float result;
        if (velocity <= 0.2) {
            result = (float) ((float) velocity / 3);
        } else if (velocity < 0.5) {
            result = (float) ((float) velocity / 2);

        } else if (velocity < 0.7) {
            result = (float) ((float) velocity / 1.5);

        } else if (velocity < 0.9) {
            result = (float) ((float) velocity / 1);

        } else if (velocity < 1) {
            result = (float) ((float) velocity * 1.2);

        } else if (velocity <= 2) {
            result = (float) velocity * 1.5f;

        } else if (velocity > 2) {
            result = (float) ((float) velocity * 1.8);

        } else if (velocity > 3) {
            result = (float) (0.12 * paintSize * KEY_PAINT_WIDTH * (Math.cos(velocity * Math.PI) + 1));


        } else if (velocity > 4) {
            result = (float) (0.09 * paintSize * KEY_PAINT_WIDTH * (Math.cos(velocity * Math.PI) + 1));


        } else if (velocity > 5) {
            result = (float) (0.08 * paintSize * KEY_PAINT_WIDTH * (Math.cos(velocity * Math.PI) + 1));


        } else if (velocity > 6) {
            result = (float) (0.07 * paintSize * KEY_PAINT_WIDTH * (Math.cos(velocity * Math.PI) + 1));


        } else if (velocity > 7) {
            result = (float) (0.06 * paintSize * KEY_PAINT_WIDTH * (Math.cos(velocity * Math.PI) + 1));


        } else {
            result = (float) (0.05 * paintSize * KEY_PAINT_WIDTH * (Math.cos(velocity * Math.PI) + 1));

        }

        if (result > paintSize / 1.1) {
            result = (float) (paintSize / 1.1);
        }

        if (lastResult != 0) {
            if (lastResult > result * 3) {
                result = ((float) (lastResult / 3 > result * 2 ? lastResult / 3 : result * 2));
            } else if (lastResult < result / 3) {
                result = ((float) (lastResult * 3 < result / 2 ? result / 2 : lastResult * 3));
            }
        }
        lastResult = result;
        return result;
    }

    /**
     * 计算笔画的速度
     *
     * @return 速度
     */
    public float velocityFrom2() {
        float distanceTo = (float) Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));
        float velocity = distanceTo / (mEndTime - mStartTime);
        //        if (velocity != velocity)
        //            return 0f;
        return velocity;
    }

    /**
     * 更新橡皮擦的位置
     */
    public void initEraser(float x, float y) {
        if (currentStyle == 0 && mOnTouchEraser != null) {
            mOnTouchEraser.onTouchEraserListener(x, y);
        }
    }

    public List<DrawPath> getSavePaths() {

        //        for (DrawPath drawPath : savePath) {
        //            if (mOldScreenWidth == screenWidth) {
        //                drawPath.rotate = 0;
        //            }
        //        }
        //
        //        if (screenWidth < screenHeight && savePath.size() > 0) {
        //            for (DrawPath drawPath : savePath) {
        //                if (drawPath.rotate == 0) {
        //                    drawPath.rotate = 270;
        //                }
        //            }
        //        }

        return savePath;
    }


    public interface onTouchEraser {
        void onTouchEraserListener(float x, float y);
    }

    private onTouchEraser mOnTouchEraser;

    public void setOnTouchEraserListener(onTouchEraser onTouchEraser) {
        mOnTouchEraser = onTouchEraser;
    }


    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX;
        }
        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY;
        }
    }

    /**
     * Resets the dirty region when the motion event occurs.
     */
    private void resetDirtyRect(float eventX, float eventY) {
        // The lastTouchX and lastTouchY were set when the ACTION_DOWN
        // motion event occurred.
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);

    }

    //保存到sd卡
    public String saveToSDCard(String time, String content) throws Exception {
        mPaint.setColor(backgroundColor);
        mPaint.setStrokeWidth(3);
        mSaveCanvas.drawPoint(0, 0, mPaint);
        String sdPath = SDcardUtil.getBookNoteSavaPath();

        File f = new File(sdPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        String path = sdPath + "/" + content + "__" + time + ".png";
        File file = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSaveBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        return file.getPath();
    }

    //保存到sd卡
    public File saveToSDCard(int count, String content) {
        mPaint.setColor(backgroundColor);
        mPaint.setStrokeWidth(3);
        mSaveCanvas.drawPoint(0, 0, mPaint);
        String sdPath = Environment.getExternalStorageDirectory() + "/ScrawlNote";
        File f = new File(sdPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        String path = sdPath + "/" + content + "第" + count + "张.png";
        File file = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSaveBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        return file;
    }

    /**
     * 选择画布的颜色
     */
    public void selectorCanvasColor(int color) {
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mSaveCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (!isPostil) {
            mSaveCanvas.drawColor(color);
            for (DrawPath drawPath : savePath) {
                if (drawPath.paint.getColor() == backgroundColor || drawPath.paint.getColor() == Color.TRANSPARENT) {
                    drawPath.paint.setColor(color);
                }
                recoverPath(drawPath);
            }
            invalidate();// 刷新

            backgroundColor = color;
            setBackgroundColor(color);
            mIsChangerBackground = true;
        } else {
        }
    }

    /**
     * 选择画布的颜色
     */
    public void selectorCanvasColor(Bitmap bitmap) {
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mSaveCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (!isPostil) {
            mInBitmap = bitmap;
            mCanvas.drawBitmap(bitmap, 0, 0, null);
            mSaveCanvas.drawBitmap(bitmap, 0, 0, null);
            for (DrawPath drawPath : savePath) {
                //                if (drawPath.paint.getColor() == backgroundColor || drawPath.paint.getColor() == Color.TRANSPARENT) {
                //                    drawPath.paint.setColor(color);
                //                }
                recoverPath(drawPath);
            }
            invalidate();// 刷新
            mIsChangerBackground = true;
        } else {
        }
    }


    //选择画笔大小
    public void selectPaintSize(int which) {
        if (which < 1)
            which = 1;
        currentSize = which;
        setPaintStyle();
        mPaint.setStrokeWidth(which);
        mPaintUp.setStrokeWidth(which);
        mPaintDown.setStrokeWidth(which);
    }

    //设置画笔颜色
    public void selectPaintColor(int which) {
        currentStyle = 1;
        currentColor = which;
        mPaint = new Paint();
        mPaintUp = new Paint();
        mPaintDown = new Paint();
        isEraser = false;
        setPaintStyle();
    }

    private boolean isEraser = false;
    public boolean isPostil = false;

    //设置橡皮擦
    public void selectEraser() {
        isEraser = !isEraser;

        if (isEraser) {
            currentStyle = 0;
        } else {
            currentStyle = 1;
        }
        setPaintStyle();

        //// TODO: 2017-5-24 暂时考虑用背景颜色做橡皮擦
        //批注的时候用全透明作为橡皮擦色
        if ((backgroundColor == Color.parseColor("#00000000") && currentStyle == 0)
                || (currentStyle == 0 && isPostil)) {
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setAlpha(0);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
    }

    public void isPostil(boolean isPostil) {
        this.isPostil = isPostil;
    }

    /**
     * 根据缓存的信息,配置画布
     */
    public void setConstants(TuyaViewBean tuyaViewBean, int width, int height) {
        this.currentColor = tuyaViewBean.currentColor;
        this.backgroundColor = tuyaViewBean.backgroundColor;
        this.currentSize = tuyaViewBean.currentSize;
        this.currentStyle = tuyaViewBean.currentStyle;
        this.savePath = tuyaViewBean.savePaths;
        this.BACKGROUND_TYPE = tuyaViewBean.BACKGROUND_TYPE;
        isRedo = false;
        initCanvas();
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mSaveCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (tuyaViewBean.bitmapPath != null) {
            mInBitmap = CacheBitmapUtil.createBitmap(tuyaViewBean.bitmapPath.getPath());
            //            initRotate();
            if (mInBitmap == null) {
                mInBitmap = NoteBeanConf.createNullBitmap(screenWidth, screenHeight);
            }

            //如果是更换了背景,就全部变成背景色
            if (tuyaViewBean.isChangerBackground) {
                mSaveCanvas.drawColor(backgroundColor);
                setBackgroundColor(backgroundColor);
            } else {
                //                setBackground(new BitmapDrawable(mInBitmap));
                mSaveCanvas.drawBitmap(mInBitmap, 0, 0, null);
                mCanvas.drawBitmap(mInBitmap, 0, 0, null);
            }

        } else {
            if (!isPostil) {
                if (tuyaViewBean.BACKGROUND_TYPE == tuyaViewBean.BACKGROUND_NORMAL) {
                    mInBitmap = NoteBeanConf.createBgNormalBitmap(context.getResources(), screenWidth, screenHeight);
                } else if (tuyaViewBean.BACKGROUND_TYPE == tuyaViewBean.BACKGROUND_LINE) {
                    mInBitmap = NoteBeanConf.createBgLineBitmap(context.getResources(), screenWidth, screenHeight);
                } else {

                }

                mSaveCanvas.drawBitmap(mInBitmap, 0, 0, null);
                mCanvas.drawBitmap(mInBitmap, 0, 0, null);
            }
            //            mSaveCanvas.drawColor(backgroundColor);
            //            setBackgroundColor(backgroundColor);
        }

        //        if (savePath.size() > 0 && savePath.get(0).rotate != 0) {
        //            recoverPath(savePath.get(0).rotate, screenWidth, screenHeight);
        //            //            rotate(savePath.get(0).rotate, screenWidth, screenHeight);
        //        } else {

        for (DrawPath drawPath : savePath) {
            if (drawPath.paint.getColor() == backgroundColor) {
                drawPath.paint.setColor(backgroundColor);
            }

            //判断笔画保存时的屏幕宽度 以作旋转角度
            if (tuyaViewBean.saveScreenWidth == width)
                drawPath.rotate = 0;
            else {
                if (tuyaViewBean.saveScreenWidth > width) {
                    drawPath.rotate = 90;
                } else
                    drawPath.rotate = 270;
            }

            if (drawPath.rotate != 0) {
                Matrix matrix = new Matrix();
                float px = width / 2f, py = height / 2f;
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

            //                recoverPath(drawPath.rotate,screenWidth,screenHeight);

            recoverPath(drawPath);
            mIsConfiCanvasTime = System.currentTimeMillis();
        }
        //        }
        invalidate();// 刷新
    }

    /**
     * 新建画布时找到之前的信息
     */
    public void setConstants(int bgColor, int paintColor, int paintSize, int paintStyle) {
        if (paintColor != -1)
            this.currentColor = paintColor;
        if (bgColor != -1)
            this.backgroundColor = bgColor;
        if (paintSize != -1)
            this.currentSize = paintSize;
        if (paintStyle != -1)
            this.currentStyle = paintStyle;
        isRedo = false;
        initCanvas();
        if (mInBitmap == null) {
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mSaveCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mSaveCanvas.drawColor(backgroundColor);
            if (!isPostil)
                setBackgroundColor(backgroundColor);
        }
        invalidate();
    }

    public void setBackgroundType(int type) {
        BACKGROUND_TYPE = type;
    }

    public int getBackgroundType() {
        return BACKGROUND_TYPE;
    }
}
